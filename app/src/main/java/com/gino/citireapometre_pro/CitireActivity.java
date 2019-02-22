package com.gino.citireapometre_pro;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.gino.citireapometre_pro.DB.ApometreDataSource;
import com.gino.citireapometre_pro.DB.Camera;
import com.gino.citireapometre_pro.DB.CitireListAdapter;
import com.gino.citireapometre_pro.DB.ConsumListAdapter;
import com.gino.citireapometre_pro.DB.ConsumListHelper;
import com.gino.citireapometre_pro.DB.Locuinta;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CitireActivity extends Fragment implements FragmentLifecycle
{
	private ApometreDataSource datasource;
	ListView listCitire;

	private boolean flashLightStatus = false;
	private boolean hasCameraFlash;

	private int year;
	private int month;
	private int day;

	String date;

	final int MY_PERMISSIONS_REQUEST_CAMERA = 11;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
	{
		View view = (View) inflater.inflate(R.layout.activity_citire, container, false);

		datasource = new ApometreDataSource(this.getActivity());
		datasource.open();

		listCitire = view.findViewById(R.id.listCitire);
		if (((MainActivity) getActivity()).ddlLocations.getSelectedItem() != null)
			loadCamere(((Locuinta) ((MainActivity) getActivity()).ddlLocations.getSelectedItem()).getId());

		FloatingActionButton btnSaveCitire = (FloatingActionButton) view.findViewById(R.id.btnSaveCitire);
		btnSaveCitire.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				for (int i = 0; i < listCitire.getChildCount(); i++)
				{
					LinearLayout layout = (LinearLayout) listCitire.getChildAt(i);
					EditText txtCalda = (EditText) layout.getChildAt(1);
					EditText txtRece = (EditText) layout.getChildAt(2);

					Float calda = null;
					Float rece = null;
					if (!txtCalda.getText().toString().trim().matches(""))
						calda = Float.valueOf(txtCalda.getText().toString());
					if (!txtRece.getText().toString().trim().matches(""))
						rece = Float.valueOf(txtRece.getText().toString());
					Camera camera = (Camera) txtRece.getTag();

					TextView lblDataSetata = (TextView) getActivity().findViewById(R.id.lblDataSetata);
//					String zi =Integer.toString(day).length()==1?("0"+Integer.toString(day)):Integer.toString(day);
//					String luna =Integer.toString(month+1).length()==1?("0"+Integer.toString(month+1)):Integer.toString(month+1);
//					String data=zi + "-" + luna + "-" + Integer.toString(year);
					String data = lblDataSetata.getText().toString();
					String[] splitDate = data.split("-");
					String zi = splitDate[0];
					String luna = splitDate[1];
					String an = splitDate[2];
					// set current date into textview
					String dataProcesata = zi + "-" + Common.getLunaNumber(getActivity(), luna) + "-" + an;
					datasource.createConsum(camera.getId(), calda, rece, dataProcesata);

					txtCalda.setText("");
					txtRece.setText("");
				}

				RefreshTabelConsum();

				MainActivity mainActivity = (MainActivity) getActivity();
				mainActivity.SetUpTabSelection(1);
				mainActivity.mViewPager.setCurrentItem(1);

				Toast toast = Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.Consumul_a_fost_salvat), Toast.LENGTH_SHORT);
				toast.show();

				Common.exportDB(getActivity());
			}
		});

		setCurrentDateOnView(view);

		ImageButton btnModifica = (ImageButton) view.findViewById(R.id.btnModificaData);
		btnModifica.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				//showDatePickerDialog();
				showDatePickerDialogNew();
			}
		});


		boolean hasCameraFlash = getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		FloatingActionButton btnLanterna = view.findViewById(R.id.btnLanterna);
		btnLanterna.setOnClickListener((View v) ->
		{
			if (hasCameraFlash)
			{
				if (flashLightStatus)
					flashLightOff();
				else
					flashLightOn();
			}
			else
			{
				Toast.makeText(getActivity(), "No flash available on your device", Toast.LENGTH_SHORT).show();
			}
		});


		return view;
	}

	private void flashLightOn()
	{
		CameraManager cameraManager = (CameraManager)getActivity().getSystemService(Context.CAMERA_SERVICE);

		try {
			String cameraId = cameraManager.getCameraIdList()[0];
			cameraManager.setTorchMode(cameraId, true);
			flashLightStatus = true;
		}
		catch (CameraAccessException e)
		{
		}
	}
	private void flashLightOff()
	{
		CameraManager cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
		try
		{
			String cameraId = cameraManager.getCameraIdList()[0];
			cameraManager.setTorchMode(cameraId, false);
			flashLightStatus = false;
		}
		catch (CameraAccessException e)
		{
		}
	}

	public void loadCamere(long idLocuinta)
	{
		List<Camera> values = datasource.getAllCamere(idLocuinta);
		CitireListAdapter adapter=new CitireListAdapter(this.getActivity(), R.layout.citire_list_item, values);
		listCitire.setAdapter(adapter);
	}

	public void RefreshTabelConsum()
	{
		datasource = new ApometreDataSource(this.getActivity());
		datasource.open();

		long idLocuinta = ((Locuinta)((MainActivity)getActivity()).ddlLocations.getSelectedItem()).getId();

		List<ConsumListHelper> values = datasource.getConsumListHelper(idLocuinta,"Lista");
  		ConsumListAdapter adapter=new ConsumListAdapter(this.getActivity(), R.layout.consum_list_item, values);
  		ListView listConsum=(ListView)getActivity().findViewById(R.id.listConsum);	  		
  		listConsum.setAdapter(adapter);
  		
	}

	public void showDatePickerDialogNew()
	{
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_calendar_picker);

		final CalendarView calendarView = (CalendarView) dialog.findViewById(R.id.calendarPicker);
		ImageButton btnSelectDate = dialog.findViewById(R.id.btnOK);
//		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

		calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
		{
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth)
			{
				String zi =Integer.toString(dayOfMonth).length()==1?("0"+Integer.toString(dayOfMonth)):Integer.toString(dayOfMonth);
				String luna =Integer.toString(month+1).length()==1?("0"+Integer.toString(month+1)):Integer.toString(month+1);
				date= zi + "-" + Common.getLuna(getActivity(), luna) + "-" + Integer.toString(year);
			}
		});

		btnSelectDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (date != null && date!="")
				{
					TextView lblDataSetata = (TextView) getActivity().findViewById(R.id.lblDataSetata);
					lblDataSetata.setText(date);
				}
				dialog.dismiss();
			}
		});
//		btnCancel.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//			}
//		});
		dialog.show();
	}


	// display current date
	public void setCurrentDateOnView(View view) 
	{			
		TextView lblDataSetata = (TextView)view.findViewById(R.id.lblDataSetata);
		
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		
 
		// set current date into textview
		String zi =Integer.toString(day).length()==1?("0"+Integer.toString(day)):Integer.toString(day);
		String luna =Integer.toString(month+1).length()==1?("0"+Integer.toString(month+1)):Integer.toString(month+1);
		String data= zi + "-" + Common.getLuna(getActivity(), luna) + "-" + Integer.toString(year);
		lblDataSetata.setText(data.toString());		
	}

	public static Date getDate(int year, int month, int day)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
	}
	
	@Override
    public void onResumeFragment(Locuinta location)
    {
			    
    }

	@Override
	public void onLocationChange(Locuinta location)
	{
		if(this.getActivity()!=null)
		{
			if (datasource == null)
				datasource = new ApometreDataSource(this.getActivity());
			datasource.open();

			loadCamere(location.getId());
		}
	}

}
