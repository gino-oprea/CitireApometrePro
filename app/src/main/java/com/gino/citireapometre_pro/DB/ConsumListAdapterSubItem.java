package com.gino.citireapometre_pro.DB;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gino.citireapometre_pro.R;

public class ConsumListAdapterSubItem extends ArrayAdapter<Consum>
{

	private List<Consum> consumuri;
	private int resourceId;
	private Context context;
	public ConsumListAdapterSubItem(Context context, int resource, List<Consum> values)
    {
	    super(context, resource, values);
	    this.context=context;
	    this.resourceId=resource;
	    this.consumuri=values;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View row = convertView;
		ConsumSubItemHolder holder = null;

		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(resourceId, parent, false);

		holder = new ConsumSubItemHolder();
		holder.consum = consumuri.get(position);
		holder.lblCameraItem = (TextView)row.findViewById(R.id.lblNumeCameraItem);
		holder.lblReceItem = (TextView)row.findViewById(R.id.lblReceItem);
		holder.lblCaldaItem = (TextView)row.findViewById(R.id.lblCaldaItem);
		holder.lblCaldaItem.setTag(holder.consum);
		holder.lblReceItem.setTag(holder.consum);			

		row.setTag(holder);
		

		setupItem(holder);
		return row;
	}
//	@Override
//	public boolean isEnabled(int position)
//	{
//		return false;
//	}

	private void setupItem(ConsumSubItemHolder holder) 
	{
		holder.lblCameraItem.setText(holder.consum.getNumeCamera());
		holder.lblCaldaItem.setText(holder.consum.getConsumCalda().toString());
		holder.lblReceItem.setText(holder.consum.getConsumRece().toString());
	}

	public static class ConsumSubItemHolder 
	{
		Consum consum;
		TextView lblCameraItem;		
		TextView lblCaldaItem;
		TextView lblReceItem;
	}
	
}
