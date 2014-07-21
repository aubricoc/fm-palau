package cat.aubricoc.palaudenoguera.festamajor.fragment;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cat.aubricoc.palaudenoguera.festamajor2014.R;

@SuppressLint("InflateParams")
public class ProgramaFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_programa, container,
				false);

		ViewGroup dies = (ViewGroup) rootView.findViewById(R.id.programa_list);

		showDies(inflater, dies);

		return rootView;
	}

	private void showDies(LayoutInflater inflater, ViewGroup viewGroup) {
		int iterDia = 0;
		boolean noMore = false;
		while (!noMore) {
			iterDia++;
			Integer diaStringId = getStringId("dia_" + iterDia);
			if (diaStringId == null) {
				noMore = true;
			} else {
				View diaView = inflater.inflate(R.layout.section_dia_programa,
						null);
				TextView textView = (TextView) diaView
						.findViewById(R.id.programa_dia);
				textView.setText(diaStringId);
				ViewGroup actes = (ViewGroup) diaView
						.findViewById(R.id.programa_actes_list);
				showActes(inflater, actes, iterDia);
				viewGroup.addView(diaView);
			}
		}
	}

	private void showActes(LayoutInflater inflater, ViewGroup viewGroup,
			int iterDia) {
		int iterActe = 0;
		boolean noMore = false;
		while (!noMore) {
			iterActe++;
			Integer horaStringId = getStringId("dia_" + iterDia + "_acte_"
					+ iterActe + "_hora");
			if (horaStringId == null) {
				noMore = true;
			} else {
				View acteView = inflater.inflate(
						R.layout.section_acte_programa, null);
				TextView textView = (TextView) acteView
						.findViewById(R.id.acte_hora);
				textView.setText(horaStringId);
				setTextField(acteView, R.id.acte_nom, "dia_" + iterDia
						+ "_acte_" + iterActe + "_nom");
				setTextField(acteView, R.id.acte_descripcio, "dia_" + iterDia
						+ "_acte_" + iterActe + "_descripcio");
				ViewGroup grups = (ViewGroup) acteView
						.findViewById(R.id.programa_grups_list);
				showGrups(inflater, grups, iterDia, iterActe);
				viewGroup.addView(acteView);
			}
		}
	}

	private void showGrups(LayoutInflater inflater, ViewGroup viewGroup,
			int iterDia, int iterActe) {
		int iterGrup = 0;
		boolean noMore = false;
		while (!noMore) {
			iterGrup++;
			Integer grupStringId = getStringId("dia_" + iterDia + "_acte_"
					+ iterActe + "_grup_" + iterGrup);
			if (grupStringId == null) {
				noMore = true;
			} else {
				TextView grupView = (TextView) inflater.inflate(
						R.layout.text_grup_programa, null);
				grupView.setText(grupStringId);
				Typeface font = Typeface.createFromAsset(getActivity()
						.getAssets(), "PermanentMarker.ttf");
				grupView.setTypeface(font);
				viewGroup.addView(grupView);
			}
		}
	}

	private Integer getStringId(String key) {
		try {
			Field field = R.string.class.getField(key);
			return (int) field.get(null);
		} catch (NoSuchFieldException e) {
			return null;
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(e);
		}
	}

	private void setTextField(View layout, int textFieldId, String key) {
		Integer stringId = getStringId(key);
		TextView textView = (TextView) layout.findViewById(textFieldId);
		if (stringId == null) {
			textView.setVisibility(View.GONE);
		} else {
			textView.setText(stringId);
			textView.setVisibility(View.VISIBLE);
		}
	}
}