package com.gino.citireapometre_pro.DB;

import java.util.List;

import com.gino.citireapometre_pro.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class CitireListAdapter extends ArrayAdapter<Camera>
{

	private List<Camera> camere;
	private int resourceId;
	private Context context;
	public CitireListAdapter(Context context, int resource, List<Camera> values)
    {
	    super(context, resource, values);
	    // TODO Auto-generated constructor stub
	    this.context=context;
	    this.resourceId=resource;
	    this.camere=values;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View row = convertView;
		CitireHolder holder = null;

		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(resourceId, parent, false);

		holder = new CitireHolder();
		holder.camera = camere.get(position);
		holder.txtCalda = (EditText)row.findViewById(R.id.txtCalda);
		holder.txtRece = (EditText)row.findViewById(R.id.txtRece);
		holder.txtCalda.setTag(holder.camera);
		holder.txtRece.setTag(holder.camera);//nu cred ca e nevoie de set tag
		holder.lblCamera = (TextView)row.findViewById(R.id.lblCamera);		

		row.setTag(holder);
		

		setupItem(holder);
		return row;
	}

	private void setupItem(CitireHolder holder) 
	{
		holder.lblCamera.setText(holder.camera.getName());
	}

	public static class CitireHolder 
	{
		public Camera camera;
		public TextView lblCamera;		
		public EditText txtCalda;
		public EditText txtRece;
	}
	
}
