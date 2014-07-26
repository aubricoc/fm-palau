package cat.aubricoc.palaudenoguera.festamajor.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;
import cat.aubricoc.palaudenoguera.festamajor2014.R;

public class InfoDialog {

	private Context context;

	private String text;

	private Dialog dialog;

	public InfoDialog(Context context, String text) {
		this.context = context;
		this.text = text;
	}

	public Dialog show() {
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(true);
		dialog.setContentView(R.layout.dialog_info);

		TextView textView = (TextView) dialog.findViewById(R.id.info_text);
		textView.setText(text);

		dialog.show();

		return dialog;
	}
}
