package in.omerjerk.preferenceseditor;

import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	
	private Context context;
	private String packageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = getApplicationContext();
		
		SharedPreferences settings = getSharedPreferences("PREFS", 0);
	    boolean isRooted = settings.getBoolean("ROOT_STATUS", false);
	    if(!isRooted){
	    	new checkRootAsyncTask().execute();
	    } else {
	    	createUI();
	    }
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_about:
	            DialogFragment aboutD = new AboutDialog();
	            aboutD.show(getSupportFragmentManager(), "ABOUT_DIALOG");
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
public class CopyFilesAsyncTask extends AsyncTask<Void, Void, Void> {
		
	LoadingDialog dialog;
		protected void onPreExecute(){
			dialog = new LoadingDialog();
			Bundle args = new Bundle();
			args.putString("MESSAGE", "Loading...");
		    dialog.setArguments(args);
			dialog.show(getSupportFragmentManager(), "LOADING_DIALOG");
			//dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			//File sdcard = 
			// TODO Auto-generated method stub
			Process p = null;
			try {
				File sdcard = Environment.getExternalStorageDirectory();
				File targetFolder = new File(sdcard, "/PreferencesEditor/" + packageName );
				if(!targetFolder.exists()){
					targetFolder.mkdirs();
				}
				p = Runtime.getRuntime().exec("su");
				
				DataOutputStream os = new DataOutputStream(p.getOutputStream());
				// Remounting /system as read+write
				os.writeBytes("mount -o rw,remount /data\n");
				
				//os.writeBytes("mkdir " + Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ packageName);
				// Copying file to SD Card
				os.writeBytes("cp -f -R /data/data/" + packageName + "/shared_prefs/* " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/PreferencesEditor/" + packageName + "/\n");
				os.writeBytes("exit\n");
				os.flush();
				p.waitFor();
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return null;
		}
		
		protected void onPostExecute(Void v){
			dialog.dismiss();
			Toast.makeText(context, "All files loaded!", Toast.LENGTH_SHORT).show();
			
			chooserDialogFinal chooserDF = new chooserDialogFinal();
			Bundle args = new Bundle();
		    args.putString("PACKAGE_NAME", packageName);
		    chooserDF.setArguments(args);
			chooserDF.show(getSupportFragmentManager(), "CHOOSER_DIALOG");
			//System.out.println("");
		}
	}

	public class checkRootAsyncTask extends AsyncTask<Void, Void, Boolean>{
		
		LoadingDialog dialog;
		protected void onPreExecute(){
			dialog = new LoadingDialog();
			Bundle args = new Bundle();
			args.putString("MESSAGE", "Checking Root Access...");
		    dialog.setArguments(args);
			dialog.show(getSupportFragmentManager(), "LOADING_DIALOG");
			//dialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Void... voids ){
			boolean result = Utils.checkRoot();
			return result;
		}
		
		protected void onPostExecute(Boolean b){
			
			SharedPreferences settings = getSharedPreferences("PREFS", 0);
		    SharedPreferences.Editor editor = settings.edit();
		    
			if(b){
				createUI();
				
				editor.putBoolean("ROOT_STATUS", true);
				Utils.showToast(context,"ROOTED !!");
				Log.d("fuck", "DAfuq !! it's rooted");
			    editor.commit();
			    
			} else {
				
				TextView noRootMessage = (TextView) findViewById(R.id.noRootMessage);
				noRootMessage.setVisibility(View.VISIBLE);
				Utils.showToast(context,"No Root!");
				
			    editor.putBoolean("ROOT_STATUS", false);
			    editor.commit();
								
			}
			
			dialog.dismiss();
			
		}
		
	}
	
	private void createUI(){
		
		//Do rest of work
		final PackageManager pm = context.getPackageManager();
		
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		List<Drawable> icons = new ArrayList<Drawable>();
		final List<String> packageNames = new ArrayList<String>();
		
		try{
		for (ApplicationInfo packageInfo : packages) {
		    //Log.d("LOG", "Installed package :" + packageInfo.packageName);
		    //Log.d("LOG", "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
		    packageNames.add(packageInfo.packageName);
		    //Log.d("TEST", "Installed PAckage : " + packageNames.get(i));
		    icons.add(context.getPackageManager().getApplicationIcon(packageInfo.packageName));
		    //i++;
		}} catch(Exception e){
			Toast.makeText(context, (CharSequence) e, Toast.LENGTH_SHORT).show();
		}
		
		ListView mainList = (ListView) findViewById(R.id.mainList);
		mainList.setVisibility(View.VISIBLE);
		
		mainList.setAdapter(new CustomAdapter(context, packageNames, icons));
		
		mainList.setOnItemClickListener(new OnItemClickListener(){
			@Override 
			public void onItemClick(AdapterView<?> parent, View v,int position, long id){ 
				
				File sdcard = Environment.getExternalStorageDirectory();
				File homeFolder = new File(sdcard , "/PreferencesEditor/");
				if(!homeFolder.exists()){
					homeFolder.mkdirs();
					System.out.println("Making Folder");
				} 
				
				packageName = packageNames.get(position);				
				new CopyFilesAsyncTask().execute();
		    }
		});
	}

}
