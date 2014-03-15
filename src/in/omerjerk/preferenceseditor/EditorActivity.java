package in.omerjerk.preferenceseditor;

import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class EditorActivity extends Activity {
	
	private Context context;
	private String packageName;
	private String fileName;
	ArrayList<EditText> data;
	ArrayList<String> name;
	ArrayList<String> dataType;
	//NodeList nodes;
	//Document doc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.table_layout);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent intent = getIntent();
		packageName = intent.getStringExtra("PACKAGE_NAME");
		fileName = intent.getStringExtra("FILE_NAME");
		
		File sdcard = Environment.getExternalStorageDirectory();
		File targetFile = new File(sdcard, "/PreferencesEditor/" + packageName + "/" + fileName);
		
		context = getApplicationContext();
		try {
			TableLayout tLayout = (TableLayout) findViewById(R.id.rootTable);
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();  
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();  
			   
			Document doc = documentBuilder.parse(targetFile);
			Element rootElement = doc.getDocumentElement();
			 
			NodeList nodes = rootElement.getChildNodes();
			System.out.println("Node Length :" + nodes.getLength()); 
			
			data = new ArrayList<EditText>();
			dataType = new ArrayList<String>();
			name = new ArrayList<String>();
			TextView tView = (TextView)LayoutInflater.from(context).inflate(R.layout.tv, null);
			EditText eText = (EditText)LayoutInflater.from(context).inflate(R.layout.et, null);
			TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
			for(int i=0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if(node instanceof Element){
					Element child = (Element) node;
					
					TableRow tRow = new TableRow(context);
					tRow.setLayoutParams(rowParams);
					
					tView.setText(child.getAttribute("name"));
					if(node.getNodeName().equals("string")){
						eText.setText(child.getTextContent());
					} else {
						eText.setText(child.getAttribute("value"));
					}
					eText.setTextColor(getResources().getColor(R.color.black));
					tRow.addView(tView);
					tRow.addView(eText);
					tLayout.addView(tRow, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f));
					data.add(eText);
					name.add(child.getAttribute("name"));
					dataType.add(node.getNodeName());
				}
			}
			
			//Log.d("EDITOR_ACTIVITY", "X  :  " + nodeList.getLength());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	public class copyFileAsyncTask extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			try{
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("map");
				doc.appendChild(rootElement);
				for(int i = 0; i < data.size() ; ++i){
					Element element;
					if(dataType.get(i).equals("string")){
						element = doc.createElement("string");
						element.appendChild(doc.createTextNode(data.get(i).getText().toString()));
						
					} else {
						element = doc.createElement(dataType.get(i));
						
						// set attribute to staff element
						Attr attr = doc.createAttribute("value");
						attr.setValue(data.get(i).getText().toString());
						element.setAttributeNode(attr);
						
					}
					Attr nameAttr = doc.createAttribute("name");
					nameAttr.setValue(name.get(i));
					element.setAttributeNode(nameAttr);
					rootElement.appendChild(element);
					
				}
				
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				
				File sdcard = Environment.getExternalStorageDirectory();
				File newFolder = new File(sdcard, "/PreferencesEditor/" + packageName + "/NEW/");
				if(!newFolder.exists()){
					newFolder.mkdirs();
				}
				StreamResult result = new StreamResult(new File(newFolder, fileName + ".new"));
		 
				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);
		 
				transformer.transform(source, result);
			} catch (Exception e){
				e.printStackTrace();
			}
			 
			System.out.println("Done");
			Process process = null;
			DataOutputStream os = null;
			Log.d("AsyncTask", "Entering the try block");
			try {
				process = Runtime.getRuntime().exec("su");
				os = new DataOutputStream(process.getOutputStream());
				os.writeBytes("mount -o rw,remount /data\n");
				os.writeBytes("cp -f " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/PreferencesEditor/"+ packageName + "/NEW/" + fileName + ".new" + " /data/data/" + packageName + "/shared_prefs/" + fileName + "\nucj");
				os.writeBytes("chmod 755 /data/data/" + packageName + "/shared_prefs/" + fileName + "\n");
				os.writeBytes("exit\n");
				os.flush();
				process.waitFor();
				os.close();
				
			} catch(Exception e){
				e.printStackTrace();
			} finally {
	            try {
	                if (os != null) {
	                    os.close();
	                }
	                process.destroy();
	            } catch (Exception e) {
	            	e.printStackTrace();
	            }
	        }
			//Toast.makeText(context, "APP Hacked Successfully!", Toast.LENGTH_SHORT).show();
			return null;
		}
		
		protected void onPostExecute(Void v){
			Toast.makeText(context, "APP Hacked Successfully!", Toast.LENGTH_SHORT).show();
			//return null;
			
		}
		
	}
	
	public void commit(View v) {
		for(int i =0; i < data.size(); ++i){
			Log.d("CommitButton", data.get(i).getText().toString());
		}
		
		new copyFileAsyncTask().execute();
	}

}
