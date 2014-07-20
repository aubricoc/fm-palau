package cat.aubricoc.palaudenoguera.festamajor.db;

import java.util.ArrayList;
import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.db.EntityInfo.FieldInfo;

public class DatabaseReflection {

	private static final DatabaseReflection INSTANCE = new DatabaseReflection();

	private DatabaseReflection() {
		super();
	}

	public static DatabaseReflection getInstance() {
		return INSTANCE;
	}

	public List<String> prepareCreateTables(Class<?>... entities) {

		List<String> createTables = new ArrayList<String>();
		for (Class<?> entity : entities) {
			createTables.add(prepareCreateTable(entity));
		}

		return createTables;
	}

	private String prepareCreateTable(Class<?> entity) {
		EntityInfo entityInfo = EntityInfo.getEntityInfo(entity);
		String sql = "create table " + entityInfo.getTableName() + " (";
		boolean first = true;
		for (FieldInfo fieldInfo : entityInfo.getFields()) {
			if (first) {
				first = false;
			} else {
				sql += ", ";
			}
			sql += fieldInfo.getColumnName() + " "
					+ fieldInfo.getDatabaseType();
			if (fieldInfo.isPK()) {
				sql += " primary key";
			} else if (!fieldInfo.isNullable()) {
				sql += " not null";
			}
			if (fieldInfo.isAutoincremental()) {
				sql += " autoincrement";
			}
		}
		for (FieldInfo fieldInfo : entityInfo.getFields()) {
			if (fieldInfo.isFK()) {
				sql += ", foreign key(" + fieldInfo.getColumnName()
						+ ") REFERENCES "
						+ fieldInfo.getForeignKeyTo().getTableName() + "("
						+ fieldInfo.getForeignKeyTo().getPK().getColumnName()
						+ ")";
				if (fieldInfo.isFkDeleteOnCascade()) {
					sql += " ON DELETE CASCADE";
				}
			}
		}
		sql += ");";
		return sql;
	}
}
