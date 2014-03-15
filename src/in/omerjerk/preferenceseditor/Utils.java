package in.omerjerk.preferenceseditor;

import java.io.DataOutputStream;
import java.io.IOException;

import android.content.Context;
import android.widget.Toast;

public class Utils {
	
	public static boolean checkRoot(){
		Process p = null;
		try{
			// Preform su to get root privledges
			p = Runtime.getRuntime().exec("su");
			
			// Attempt to write a file to a root-only
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("mount -o rw,remount /system\n");
			os.writeBytes("echo \"Do I have root?\" >/system/etc/temporary.txt\n");
			
			// Close the terminal
			os.writeBytes("exit\n");
			os.flush();
			
			try{
				p.waitFor();
				if(p.exitValue() != 225){
					return true;
				} else {
					return false;
				}
			} catch(InterruptedException e){
				return false;
			}
		} catch(IOException e){
			return false;
		}
	}
	
	public static void showToast(Context c,String s){
		Toast.makeText(c, s, Toast.LENGTH_SHORT).show();
	}

}
