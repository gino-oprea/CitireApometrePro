package com.gino.citireapometre_pro.DB;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.gino.citireapometre_pro.Common;
import com.gino.citireapometre_pro.R;

public class ConsumListAdapter extends ArrayAdapter<ConsumListHelper>
{

	private List<ConsumListHelper> consumHelper;
	private int resourceId;
	private Context context;
	public ConsumListAdapter(Context context, int resource, List<ConsumListHelper> consumHelper)
    {
	    super(context, resource, consumHelper);
	    this.context=context;
	    this.resourceId=resource;
	    this.consumHelper=consumHelper;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View row = convertView;
		ConsumHelperHolder holder = null;

		holder = new ConsumHelperHolder();
		holder.consumListHelper = consumHelper.get(position);
		
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(resourceId, parent, false);
		
		

		
		
			
		holder.lblData=(TextView)row.findViewById(R.id.lblData);
		holder.lblData.setTag(holder.consumListHelper);
		holder.listConsum = (ListView)row.findViewById(R.id.listConsumItem);		
		final ListView lv = (ListView)row.findViewById(R.id.listConsumItem);
		holder.listConsum.setTag(holder.consumListHelper);		
		
		TextView lblTotalCalda = (TextView)row.findViewById(R.id.lblTotalCalda);
		TextView lblTotalRece = (TextView)row.findViewById(R.id.lblTotalRece);
		
		TextView lblConsumCaldaLuna = (TextView)row.findViewById(R.id.lblConsumCaldaLuna);
		TextView lblConsumReceLuna = (TextView)row.findViewById(R.id.lblConsumReceLuna);
		
		float consumCalda = CalculateTotal(consumHelper.get(position).getListConsum(), "Calda");
		float consumRece = CalculateTotal(consumHelper.get(position).getListConsum(), "Rece");
		
		lblTotalCalda.setText(String.format("%.1f", consumCalda));
		lblTotalRece.setText(String.format("%.1f", consumRece));
		
		if(position+1<consumHelper.size())
		{
			consumCalda = consumCalda - CalculateTotal(consumHelper.get(position + 1).getListConsum(), "Calda");
			consumRece = consumRece - CalculateTotal(consumHelper.get(position + 1).getListConsum(), "Rece");
		}		
		lblConsumCaldaLuna.setText(String.format("%.1f", consumCalda));
		lblConsumReceLuna.setText(String.format("%.1f", consumRece));
		

		row.setTag(holder.consumListHelper);
		
		holder.listConsum.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{			
				setItemColor(lv);
			}			
		});
		holder.listConsum.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				setItemColor(lv);
				//onlongclick se propaga pana la parinte si se trateaza acolo opendialog
				return false;
			}
		});

		setupItem(holder);
		
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();		
		
		
		int screenHeight=display.getHeight();
		int spacer = screenHeight * 4/100;
		
		
		row.setLayoutParams(new ListView.LayoutParams(
				ListView.LayoutParams.WRAP_CONTENT,
				spacer + holder.listConsum.getMeasuredHeight() + holder.listConsum.getMeasuredHeight() + getListViewHeight(holder.listConsum)));
						
		
		return row;
	}	
	
	public void setItemColor(ListView lv)
	{
		
		LinearLayout ll =(LinearLayout) lv.getParent().getParent().getParent();
		if(!ll.isSelected())
		{
    		ListView lvParent = (ListView) ll.getParent();//listView-ul parinte suprem
    		
    		for (int i = 0; i < lvParent.getChildCount(); i++) 
    		{
    			LinearLayout llChild = (LinearLayout)lvParent.getChildAt(i);			
    			  llChild.setSelected(false);	        
    	    }		
    		ll.setSelected(true);
		}
		
	}
	public float CalculateTotal(List<Consum> consumuri, String tip)
	{
		float total = 0;
		if (tip == "Calda")
        {			
	        for (int i = 0; i < consumuri.size(); i++)
            {
	            total+=consumuri.get(i).getConsumCalda();
            }
        }
		if(tip == "Rece")
		{
			for (int i = 0; i < consumuri.size(); i++)
            {
	            total+=consumuri.get(i).getConsumRece();
            }
		}
		return total;
	}

	private void setupItem(ConsumHelperHolder holder) 
	{
		holder.lblData.setText(buildDateString(holder.consumListHelper.getData()));
		
		ApometreDataSource datasource = new ApometreDataSource(this.context);
		datasource.open();
		
		List<Consum> values = datasource.getConsum(holder.consumListHelper.getIdLocuinta(),holder.consumListHelper.getData());
  		ConsumListAdapterSubItem adapter=new ConsumListAdapterSubItem(context, R.layout.consum_list_subitem, values); 			
  		
  		holder.listConsum.setAdapter(adapter);  
  		
  		holder.lblData.setHeight(getListViewHeight(holder.listConsum));
	}
	
	private int getListViewHeight(ListView list) 
	{
		ConsumListAdapterSubItem adapter = (ConsumListAdapterSubItem) list.getAdapter();

        int listviewHeight = 0;

        list.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED), 
                     MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        listviewHeight = list.getMeasuredHeight() * adapter.getCount() + (adapter.getCount() * list.getDividerHeight())-2;//-2 e din cauza separatorului de consum cred;

        return listviewHeight;
  }
	public String buildDateString(String numericDate)
	{
		String date =numericDate.substring(0,2) + "." + Common.getLuna(context, numericDate.substring(3,5)) + "." + numericDate.substring(6);
		return date;		
	}
//	public String getLuna(String NrLuna)
//	{
//		String luna="Ian";
//		if(NrLuna.equals("01"))
//			luna="Ian";
//		if(NrLuna.equals("02"))
//			luna="Feb";
//		if(NrLuna.equals("03"))
//			luna="Mar";
//		if(NrLuna.equals("04"))
//			luna="Apr";
//		if(NrLuna.equals("05"))
//			luna="Mai";
//		if(NrLuna.equals("06"))
//			luna="Iun";
//		if(NrLuna.equals("07"))
//			luna="Iul";
//		if(NrLuna.equals("08"))
//			luna="Aug";
//		if(NrLuna.equals("09"))
//			luna="Sep";
//		if(NrLuna.equals("10"))
//			luna="Oct";
//		if(NrLuna.equals("11"))
//			luna="Nov";
//		if(NrLuna.equals("12"))
//			luna="Dec";	
//		
//		return luna;
//	}	

	public static class ConsumHelperHolder 
	{
	  public ConsumListHelper consumListHelper;
	  public ListView listConsum;
	  public TextView lblData;
	}
}