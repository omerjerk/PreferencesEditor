package in.omerjerk.preferenceseditor;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter{

	private Context context;
	private final List<String> packageNames;
	private final List<Drawable> icons;
	
	public CustomAdapter (Context context, List<String> pNames, List<Drawable> iIcons) {
		
		this.context = context;
		this.packageNames = pNames;
		this.icons = iIcons;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		//return 0;
		return packageNames.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView;
		  
			rowView = new View(context);
 
			// get layout from mobile.xml
			rowView = inflater.inflate(R.layout.list, null);
 
			// set value into textview
			TextView textView = (TextView) rowView
					.findViewById(R.id.label); 
			textView.setText(packageNames.get(position));
 
			// set image based on selected text
			ImageView imageView = (ImageView) rowView
					.findViewById(R.id.logo);
 
			imageView.setImageDrawable(icons.get(position));
		
 
		return rowView;
	}
	
}