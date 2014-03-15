package in.omerjerk.preferenceseditor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class chooserDialogFinal extends DialogFragment{
	
	private Context context;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		context = getActivity();
		
		final String packageName = getArguments().getString("PACKAGE_NAME");
	    
	    LayoutInflater factory = LayoutInflater.from(context);
		View listView = factory.inflate(R.layout.chooser_dialog_list_view, null);
		ListView chooseFileListView = (ListView) listView.findViewById(R.id.chooseFilesListView);
		File sdcard = Environment.getExternalStorageDirectory();
		File targetFolder = new File(sdcard, "/PreferencesEditor/" + packageName );
		final List<String> fileNames = new ArrayList<String>();
		for (File file : targetFolder.listFiles()) {
		    if (file.isFile()) {
		        fileNames.add(file.getName());
		    }
		}
		chooseFileListView.setAdapter(new ArrayAdapter<String>(context,  android.R.layout.simple_list_item_1, fileNames));
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
	    builder = new AlertDialog.Builder(context);
	    builder.setTitle("Choose a file to edit")
	           .setView(listView);
	    
	    chooseFileListView.setOnItemClickListener(new OnItemClickListener(){
			@Override 
			public void onItemClick(AdapterView<?> parent, View v,int position, long id){ 
				
				Intent intent = new Intent(context, EditorActivity.class);
				intent.putExtra("PACKAGE_NAME", packageName);
				intent.putExtra("FILE_NAME", fileNames.get(position));
				startActivity(intent);
		    }
		});
	    
	    return builder.show();
	}

}
