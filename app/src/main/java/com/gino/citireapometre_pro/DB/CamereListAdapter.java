package com.gino.citireapometre_pro.DB;

import java.util.List;

import com.gino.citireapometre_pro.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class CamereListAdapter extends ArrayAdapter<Camera>
{
	private List<Camera> camere;
	private int layoutResourceId;
	private Context context;
	public CamereListAdapter(Context context, int layoutResourceId, List<Camera> camere)
	{
		super(context, layoutResourceId, camere);
		this.context=context;
		this.layoutResourceId=layoutResourceId;
		this.camere=camere;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View row = convertView;
		CamereHolder holder = null;

		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);

		holder = new CamereHolder();
		holder.camera = camere.get(position);
		holder.deleteButton = (ImageButton)row.findViewById(R.id.btnDelete);
		holder.deleteButton.setTag(holder.camera);
		holder.editButton = (ImageButton)row.findViewById(R.id.btnEdit);
		holder.editButton.setTag(holder.camera);

		holder.name = (TextView)row.findViewById(R.id.txtNumeCamera);		

		row.setTag(holder);
		

		setupItem(holder);
		return row;
	}

	private void setupItem(CamereHolder holder) 
	{
		holder.name.setText(holder.camera.getName());		
	}

	public static class CamereHolder 
	{
		Camera camera;
		TextView name;		
		ImageButton deleteButton;
		ImageButton editButton;
	}

}
