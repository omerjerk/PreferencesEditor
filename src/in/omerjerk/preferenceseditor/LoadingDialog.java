package in.omerjerk.preferenceseditor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

public class LoadingDialog extends DialogFragment {
	
	private Context context;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		context = getActivity();
		
		final String message = getArguments().getString("MESSAGE");
			    
	    LayoutInflater factory = LayoutInflater.from(context);
		View view = factory.inflate(R.layout.loading_dialog, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
	    builder = new AlertDialog.Builder(context);
	    builder.setTitle(message)
	           .setView(view);
	    
	    return builder.show();
	}

}
