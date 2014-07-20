package cat.aubricoc.palaudenoguera.festamajor.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cat.aubricoc.palaudenoguera.festamajor.activity.Activity;
import cat.aubricoc.palaudenoguera.festamajor.db.DatabaseHelper;
import cat.aubricoc.palaudenoguera.festamajor.db.EntityInfo;
import cat.aubricoc.palaudenoguera.festamajor.db.EntityInfo.FieldInfo;
import cat.aubricoc.palaudenoguera.festamajor.db.enums.Entity;
import cat.aubricoc.palaudenoguera.festamajor.utils.Constants;
import cat.aubricoc.palaudenoguera.festamajor.utils.StringUtils;

public abstract class Dao<T, K> {

	private static final String DATE_FORMAT = "yyyyMMddHHmmssSSSZ";

	protected EntityInfo entityInfo;

	protected SQLiteDatabase database;
	protected DatabaseHelper dbHelper;

	protected Dao(Class<?> entity) {
		entityInfo = EntityInfo.getEntityInfo(entity);
		dbHelper = new DatabaseHelper(Activity.CURRENT_CONTEXT);
	}

	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		if (!database.isReadOnly()) {
			database.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	private void close() {
		dbHelper.close();
	}

	public long create(T object) {
		synchronized (Constants.LOCK_DATABASE) {
			open();
			try {
				ContentValues values = getInsertValues(object);

				long id = database.insert(getTableName(), null, values);

				if (isAutoIncrementId()) {
					setAutoIncrementId(object, id);

					Log.i(Constants.PROJECT_NAME, "Created "
							+ object.getClass().getSimpleName() + " with id "
							+ id);

				} else {
					String[] ids = getIdentifierWhereValues(object);
					Log.i(Constants.PROJECT_NAME, "Created "
							+ object.getClass().getSimpleName() + " with id "
							+ StringUtils.join(ids, ","));
				}

				return id;
			} finally {
				close();
			}
		}
	}

	public void update(T object) {
		synchronized (Constants.LOCK_DATABASE) {
			open();
			try {
				ContentValues values = getInsertValues(object);
				String[] ids = getIdentifierWhereValues(object);
				database.update(getTableName(), values, getIdentifierWhere(),
						ids);

				Log.i(Constants.PROJECT_NAME, "Updated "
						+ object.getClass().getSimpleName() + " with id "
						+ StringUtils.join(ids, ","));
			} finally {
				close();
			}
		}
	}

	public void delete(T object) {
		synchronized (Constants.LOCK_DATABASE) {
			open();
			try {
				String[] ids = getIdentifierWhereValues(object);
				database.delete(getTableName(), getIdentifierWhere(), ids);

				Log.i(Constants.PROJECT_NAME, "Deleted "
						+ object.getClass().getSimpleName() + " with id "
						+ StringUtils.join(ids, ","));
			} finally {
				close();
			}
		}
	}

	public long createIfNotExists(T object) {
		synchronized (Constants.LOCK_DATABASE) {
			if (!exists(getIdentifier(object))) {
				return create(object);
			}
			return -1;
		}
	}

	public void create(List<T> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		synchronized (Constants.LOCK_DATABASE) {
			open();
			try {
				String className = null;
				database.beginTransaction();
				for (T object : list) {
					if (className == null) {
						className = object.getClass().getSimpleName();
					}
					ContentValues values = getInsertValues(object);

					long id = database.insert(getTableName(), null, values);

					if (isAutoIncrementId()) {
						setAutoIncrementId(object, id);
					}
				}
				database.setTransactionSuccessful();

				Log.i(Constants.PROJECT_NAME, "Created " + list.size() + " "
						+ className + " at once");

			} finally {
				database.endTransaction();
				close();
			}
		}
	}

	public void update(List<T> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		synchronized (Constants.LOCK_DATABASE) {
			open();
			try {
				String className = null;
				database.beginTransaction();
				for (T object : list) {
					if (className == null) {
						className = object.getClass().getSimpleName();
					}
					ContentValues values = getInsertValues(object);
					String[] ids = getIdentifierWhereValues(object);
					database.update(getTableName(), values,
							getIdentifierWhere(), ids);
				}
				database.setTransactionSuccessful();

				Log.i(Constants.PROJECT_NAME, "Updated " + list.size() + " "
						+ className + " at once");

			} finally {
				database.endTransaction();
				close();
			}
		}
	}

	public void delete(List<T> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		synchronized (Constants.LOCK_DATABASE) {
			open();
			try {
				String className = null;
				database.beginTransaction();
				for (T object : list) {
					if (className == null) {
						className = object.getClass().getSimpleName();
					}
					String[] ids = getIdentifierWhereValues(object);
					database.delete(getTableName(), getIdentifierWhere(), ids);
				}
				database.setTransactionSuccessful();

				Log.i(Constants.PROJECT_NAME, "Deleted " + list.size() + " "
						+ className + " at once");

			} finally {
				database.endTransaction();
				close();
			}
		}
	}

	public List<T> getAll() {
		return getAll(null);
	}

	public List<T> getAll(String orderBy) {
		synchronized (Constants.LOCK_DATABASE) {
			open();
			try {
				List<T> list = new ArrayList<T>();

				Cursor cursor = database.query(getTableName(), getColumns(),
						null, null, null, null, orderBy == null ? getOrderBy()
								: orderBy);

				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					T object = cursorToObject(cursor);
					list.add(object);
					cursor.moveToNext();
				}
				cursor.close();
				return list;
			} finally {
				close();
			}
		}
	}

	protected List<T> getBy(String whereClause, String... whereArgs) {
		return getBy(whereClause, null, whereArgs);
	}

	protected List<T> getBy(String whereClause, String orderBy,
			String... whereArgs) {
		synchronized (Constants.LOCK_DATABASE) {
			open();
			try {
				List<T> list = new ArrayList<T>();

				Cursor cursor = database.query(getTableName(), getColumns(),
						whereClause, whereArgs, null, null,
						orderBy == null ? getOrderBy() : orderBy);

				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					T object = cursorToObject(cursor);
					list.add(object);
					cursor.moveToNext();
				}
				cursor.close();
				return list;
			} finally {
				close();
			}
		}
	}

	protected List<T> getByQuery(String query, String... whereArgs) {
		synchronized (Constants.LOCK_DATABASE) {
			open();
			try {
				List<T> list = new ArrayList<T>();

				Cursor cursor = database.rawQuery(query, whereArgs);

				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					T object = cursorToObject(cursor);
					list.add(object);
					cursor.moveToNext();
				}
				cursor.close();
				return list;
			} finally {
				close();
			}
		}
	}

	public T getById(K id) {
		synchronized (Constants.LOCK_DATABASE) {
			open();
			try {
				Cursor cursor = database.query(getTableName(), getColumns(),
						getIdentifierWhere(), getWhereValues(id), null, null,
						null);

				cursor.moveToFirst();
				if (!cursor.isAfterLast()) {
					T rowObject = cursorToObject(cursor);
					cursor.close();
					return rowObject;
				}
				cursor.close();
				return null;

			} finally {
				close();
			}
		}
	}

	public boolean exists(K id) {
		T result = getById(id);
		return result != null;
	}

	protected String getTableName() {
		return entityInfo.getTableName();
	}

	protected boolean isAutoIncrementId() {
		return entityInfo.getPK().isAutoincremental();
	}

	protected String[] getColumns() {
		String[] columns = new String[entityInfo.getFields().size()];
		int iter = 0;
		for (FieldInfo fieldInfo : entityInfo.getFields()) {
			columns[iter] = fieldInfo.getColumnName();
			iter++;
		}
		return columns;
	}

	@SuppressWarnings("unchecked")
	protected T cursorToObject(Cursor cursor) {
		try {
			T object = (T) entityInfo.getEntityClass().newInstance();
			int iter = 0;
			for (FieldInfo fieldInfo : entityInfo.getFields()) {
				if (!cursor.isNull(iter)) {
					Object val = null;
					if (fieldInfo.getDatabaseType().equals("string")) {
						String value = cursor.getString(iter);
						val = fromStringTo(fieldInfo.getField().getType(),
								value);
					} else if (fieldInfo.getDatabaseType().equals("integer")) {
						Long value = cursor.getLong(iter);
						val = fromLongTo(fieldInfo.getField().getType(), value);
					} else if (fieldInfo.getDatabaseType().equals("real")) {
						Double value = cursor.getDouble(iter);
						val = fromDoubleTo(fieldInfo.getField().getType(),
								value);
					} else {
						throw new IllegalArgumentException(
								"Unkown database type '"
										+ fieldInfo.getDatabaseType() + "'.");
					}
					fieldInfo.getField().setAccessible(true);
					fieldInfo.getField().set(object, val);
				}
				iter++;
			}
			return object;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	protected void setAutoIncrementId(T object, long id) {
		if (entityInfo.getPK().isAutoincremental()) {
			try {
				Object idObject = fromLongTo(entityInfo.getPK().getField()
						.getType(), id);
				entityInfo.getPK().getField().setAccessible(true);
				entityInfo.getPK().getField().set(object, idObject);
			} catch (Exception e) {
				throw new IllegalArgumentException("Can't insert ID '" + id
						+ "' on entity '"
						+ entityInfo.getEntityClass().getSimpleName() + "'", e);
			}
		}
	}

	protected String getIdentifierWhere() {
		return entityInfo.getPK().getColumnName() + "=?";
	}

	protected String[] getWhereValues(K id) {
		return new String[] { id.toString() };
	}

	protected String[] getIdentifierWhereValues(T object) {
		return getWhereValues(getIdentifier(object));
	}

	@SuppressWarnings("unchecked")
	protected K getIdentifier(T object) {
		try {
			entityInfo.getPK().getField().setAccessible(true);
			Object id = entityInfo.getPK().getField().get(object);
			return (K) id;
		} catch (Exception e) {
			throw new IllegalArgumentException("Can't get ID from entity '"
					+ entityInfo.getEntityClass().getSimpleName() + "'", e);
		}
	}

	protected ContentValues getInsertValues(T object) {
		ContentValues values = new ContentValues();

		try {
			for (FieldInfo fieldInfo : entityInfo.getFields()) {
				if (!fieldInfo.isPK() || !fieldInfo.isAutoincremental()) {
					fieldInfo.getField().setAccessible(true);
					Object val = fieldInfo.getField().get(object);
					if (fieldInfo.getDatabaseType().equals("string")) {
						String value = null;
						if (val != null) {
							value = toStringFrom(
									fieldInfo.getField().getType(), val);
						}
						values.put(fieldInfo.getColumnName(), value);
					} else if (fieldInfo.getDatabaseType().equals("integer")) {
						Long value = null;
						if (val != null) {
							value = toLongFrom(fieldInfo.getField().getType(),
									val);
						}
						values.put(fieldInfo.getColumnName(), value);
					} else if (fieldInfo.getDatabaseType().equals("real")) {
						Double value = null;
						if (val != null) {
							value = toDoubleFrom(
									fieldInfo.getField().getType(), val);
						}
						values.put(fieldInfo.getColumnName(), value);
					} else {
						throw new IllegalArgumentException(
								"Unkown database type '"
										+ fieldInfo.getDatabaseType() + "'.");
					}

				}
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return values;
	}

	protected String getOrderBy() {
		if (entityInfo.getOrderBy() != null) {
			return entityInfo.getOrderBy().getColumnName();
		}
		return entityInfo.getPK().getColumnName();
	}

	protected Object fromStringTo(Class<?> type, String value) {
		if (type.equals(String.class)) {
			return fromStringToString(value);
		} else if (type.equals(Date.class)) {
			return fromStringToDate(value);
		} else if (type.equals(Character.class)) {
			return fromStringToCharacter(value);
		} else if (type.isEnum()) {
			return fromStringToEnum(type, value);
		} else if (type.isAnnotationPresent(Entity.class)) {
			try {
				EntityInfo fkEntityInfo = EntityInfo.getEntityInfo(type);
				Object fkObject = type.newInstance();
				Field fkPkField = fkEntityInfo.getPK().getField();
				Object id = fromStringTo(fkPkField.getType(), value);
				fkPkField.setAccessible(true);
				fkPkField.set(fkObject, id);
				return fkObject;
			} catch (Exception e) {
				throw new IllegalArgumentException("Can't convert '" + value
						+ "' to entity '" + type.getSimpleName() + "'", e);
			}
		}
		throw new IllegalArgumentException("Unkown java type '"
				+ type.getSimpleName() + "' matching database type 'string'.");
	}

	protected Object fromLongTo(Class<?> type, Long value) {
		if (type.equals(Long.class)) {
			return fromLongToLong(value);
		} else if (type.equals(Integer.class)) {
			return fromLongToInteger(value);
		} else if (type.equals(Short.class)) {
			return fromLongToShort(value);
		} else if (type.equals(Byte.class)) {
			return fromLongToByte(value);
		} else if (type.equals(BigInteger.class)) {
			return fromLongToBigInteger(value);
		} else if (type.equals(Boolean.class)) {
			return fromLongToBoolean(value);
		} else if (type.isAnnotationPresent(Entity.class)) {
			try {
				EntityInfo fkEntityInfo = EntityInfo.getEntityInfo(type);
				Object fkObject = type.newInstance();
				Field fkPkField = fkEntityInfo.getPK().getField();
				Object id = fromLongTo(fkPkField.getType(), value);
				fkPkField.setAccessible(true);
				fkPkField.set(fkObject, id);
				return fkObject;
			} catch (Exception e) {
				throw new IllegalArgumentException("Can't convert '" + value
						+ "' to entity '" + type.getSimpleName() + "'", e);
			}
		}
		throw new IllegalArgumentException("Unkown java type '"
				+ type.getSimpleName() + "' matching database type 'integer'.");
	}

	protected Object fromDoubleTo(Class<?> type, Double value) {
		if (type.equals(Double.class)) {
			return fromDoubleToDouble(value);
		} else if (type.equals(Float.class)) {
			return fromDoubleToFloat(value);
		} else if (type.equals(BigDecimal.class)) {
			return fromDoubleToBigDecimal(value);
		} else if (type.isAnnotationPresent(Entity.class)) {
			try {
				EntityInfo fkEntityInfo = EntityInfo.getEntityInfo(type);
				Object fkObject = type.newInstance();
				Field fkPkField = fkEntityInfo.getPK().getField();
				Object id = fromDoubleTo(fkPkField.getType(), value);
				fkPkField.setAccessible(true);
				fkPkField.set(fkObject, id);
				return fkObject;
			} catch (Exception e) {
				throw new IllegalArgumentException("Can't convert '" + value
						+ "' to entity '" + type.getSimpleName() + "'", e);
			}
		}
		throw new IllegalArgumentException("Unkown java type '"
				+ type.getSimpleName() + "' matching database type 'real'.");
	}

	protected String toStringFrom(Class<?> type, Object value) {
		if (type.equals(String.class)) {
			return fromStringToString(value);
		} else if (type.equals(Date.class)) {
			return fromDateToString(value);
		} else if (type.equals(Character.class)) {
			return fromCharacterToString(value);
		} else if (type.isEnum()) {
			return fromEnumToString(type, value);
		} else if (type.isAnnotationPresent(Entity.class)) {
			try {
				EntityInfo fkEntityInfo = EntityInfo.getEntityInfo(type);
				Field fkPkField = fkEntityInfo.getPK().getField();
				fkPkField.setAccessible(true);
				Object fkId = fkPkField.get(value);
				return toStringFrom(fkPkField.getType(), fkId);
			} catch (Exception e) {
				throw new IllegalArgumentException("Can't convert entity '"
						+ type.getSimpleName() + "' to String", e);
			}
		}
		throw new IllegalArgumentException("Unkown java type '"
				+ type.getSimpleName() + "' matching database type 'string'.");
	}

	protected Long toLongFrom(Class<?> type, Object value) {
		if (type.equals(Long.class)) {
			return fromLongToLong(value);
		} else if (type.equals(Integer.class)) {
			return fromIntegerToLong(value);
		} else if (type.equals(Short.class)) {
			return fromShortToLong(value);
		} else if (type.equals(Byte.class)) {
			return fromByteToLong(value);
		} else if (type.equals(BigInteger.class)) {
			return fromBigIntegerToLong(value);
		} else if (type.equals(Boolean.class)) {
			return fromBooleanToLong(value);
		} else if (type.isAnnotationPresent(Entity.class)) {
			try {
				EntityInfo fkEntityInfo = EntityInfo.getEntityInfo(type);
				Field fkPkField = fkEntityInfo.getPK().getField();
				fkPkField.setAccessible(true);
				Object fkId = fkPkField.get(value);
				return toLongFrom(fkPkField.getType(), fkId);
			} catch (Exception e) {
				throw new IllegalArgumentException("Can't convert entity '"
						+ type.getSimpleName() + "' to Long", e);
			}
		}
		throw new IllegalArgumentException("Unkown java type '"
				+ type.getSimpleName() + "' matching database type 'integer'.");
	}

	protected Double toDoubleFrom(Class<?> type, Object value) {
		if (type.equals(Double.class)) {
			return fromDoubleToDouble(value);
		} else if (type.equals(Float.class)) {
			return fromFloatToDouble(value);
		} else if (type.equals(BigDecimal.class)) {
			return fromBigDecimalToDouble(value);
		} else if (type.isAnnotationPresent(Entity.class)) {
			try {
				EntityInfo fkEntityInfo = EntityInfo.getEntityInfo(type);
				Field fkPkField = fkEntityInfo.getPK().getField();
				fkPkField.setAccessible(true);
				Object fkId = fkPkField.get(value);
				return toDoubleFrom(fkPkField.getType(), fkId);
			} catch (Exception e) {
				throw new IllegalArgumentException("Can't convert entity '"
						+ type.getSimpleName() + "' to Double", e);
			}
		}
		throw new IllegalArgumentException("Unkown java type '"
				+ type.getSimpleName() + "' matching database type 'real'.");
	}

	protected String fromStringToString(Object value) {
		return ((String) value).trim();
	}

	@SuppressLint("SimpleDateFormat")
	protected Date fromStringToDate(Object value) {
		try {
			return new SimpleDateFormat(DATE_FORMAT).parse((String) value);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Can't convert '" + value
					+ "' to a Date", e);
		}
	}

	@SuppressLint("SimpleDateFormat")
	protected String fromDateToString(Object value) {
		return new SimpleDateFormat(DATE_FORMAT).format((Date) value);
	}

	protected Character fromStringToCharacter(Object value) {
		return ((String) value).trim().charAt(0);
	}

	protected String fromCharacterToString(Object value) {
		return ((Character) value).toString();
	}

	protected Object fromStringToEnum(Class<?> enumType, Object value) {
		try {
			Method valueOfMethod = enumType.getMethod("valueOf", Class.class,
					String.class);
			return valueOfMethod.invoke(null, enumType, value);
		} catch (Exception e) {
			throw new IllegalArgumentException("Can't convert '" + value
					+ "' to enum '" + enumType.getSimpleName() + "'", e);
		}
	}

	protected String fromEnumToString(Class<?> enumType, Object value) {
		return ((Enum<?>) value).name();
	}

	protected Long fromLongToLong(Object value) {
		return (Long) value;
	}

	protected Integer fromLongToInteger(Object value) {
		return ((Long) value).intValue();
	}

	protected Long fromIntegerToLong(Object value) {
		return ((Integer) value).longValue();
	}

	protected Short fromLongToShort(Object value) {
		return ((Long) value).shortValue();
	}

	protected Long fromShortToLong(Object value) {
		return ((Short) value).longValue();
	}

	protected Byte fromLongToByte(Object value) {
		return ((Long) value).byteValue();
	}

	protected Long fromByteToLong(Object value) {
		return ((Byte) value).longValue();
	}

	protected BigInteger fromLongToBigInteger(Object value) {
		return BigInteger.valueOf((Long) value);
	}

	protected Long fromBigIntegerToLong(Object value) {
		return ((BigInteger) value).longValue();
	}

	protected Boolean fromLongToBoolean(Object value) {
		return ((Long) value) > 0;
	}

	protected Long fromBooleanToLong(Object value) {
		return ((Boolean) value) ? 1L : 0L;
	}

	protected Double fromDoubleToDouble(Object value) {
		return (Double) value;
	}

	protected Float fromDoubleToFloat(Object value) {
		return ((Double) value).floatValue();
	}

	protected Double fromFloatToDouble(Object value) {
		return ((Float) value).doubleValue();
	}

	protected BigDecimal fromDoubleToBigDecimal(Object value) {
		return BigDecimal.valueOf((Double) value);
	}

	protected Double fromBigDecimalToDouble(Object value) {
		return ((BigDecimal) value).doubleValue();
	}
}
