package com.gino.citireapometre_pro;

import java.util.List;

import com.gino.citireapometre_pro.DB.ApometreDataSource;
import com.gino.citireapometre_pro.DB.Camera;
import com.gino.citireapometre_pro.DB.CamereListAdapter;
import com.gino.citireapometre_pro.DB.Locuinta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SetariFragmentActivity extends Fragment implements FragmentLifecycle
{
	private ApometreDataSource datasource;
	FloatingActionButton btnAddLocuinta;
	FloatingActionButton btnAddCamera;
	Locuinta selectedLocuinta;
	ListView lstLocuinte;
	ListView listSetari;
	Button btnCamere;
	Button btnLocuinte;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.activity_setari_fragment, container, false);
		btnAddLocuinta = view.findViewById(R.id.btnAddLocuinta);
		btnAddCamera = view.findViewById(R.id.btnAddCamera);
		lstLocuinte = view.findViewById(R.id.listLocuinte);

		datasource = new ApometreDataSource(this.getActivity());
		datasource.open();
		//////////
		///////// Regiunea legata de Camere
		//salveaza si la enter
//		TextView txtCamera = (TextView) view.findViewById(R.id.txtNumeCamera);
//		txtCamera.setOnKeyListener(new OnKeyListener()
//		{
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event)
//			{
//				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
//				{
//					AddCamera();
//					return true;
//				}
//				return false;
//			}
//		});
		btnCamere = (Button) view.findViewById(R.id.btnCamere);
		btnLocuinte = (Button) view.findViewById(R.id.btnLocuinte);
		btnLocuinte.setSelected(true);
		btnCamere.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SetUpTabs(1);
			}
		});
		btnLocuinte.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SetUpTabs(2);
			}
		});

		listSetari = (ListView) view.findViewById(R.id.listSetari);

		if (((MainActivity) getActivity()).ddlLocations.getSelectedItem() != null)
			loadCamere(((Locuinta) ((MainActivity) getActivity()).ddlLocations.getSelectedItem()).getId());

//		final ImageButton btnAdd = (ImageButton) view.findViewById(R.id.add);
//		btnAdd.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				AddCamera();
//			}
//		});

		btnAddCamera.setOnClickListener((View v)->
		{
			OpenSaveCameraDialog("add", null);
		});

		listSetari.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) ->
		{
			Camera camera = (Camera)parent.getAdapter().getItem(position);
			OpenSaveCameraDialog("edit",camera);
		});

		////////////
		/////////
		////sfarsit regiune camere


		////////
		//////
		////regiune Locuinte
		List<Locuinta> locuinte = datasource.getAllLocuinte();
		ArrayAdapter<Locuinta> locuinteAdapter = new ArrayAdapter<Locuinta>(this.getActivity(), R.layout.list_item, locuinte);
		lstLocuinte.setAdapter(locuinteAdapter);

		lstLocuinte.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) ->
		{
			((MainActivity) getActivity()).ddlLocations.setSelection(position);
			selectedLocuinta = (Locuinta) parent.getAdapter().getItem(position);
			loadCamere(selectedLocuinta.getId());
			SetUpTabs(1);
		});
		lstLocuinte.setOnItemLongClickListener((AdapterView<?> parent, View v, int position, long id) ->
		{
			Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
			vibe.vibrate(50);

			Locuinta locuinta = (Locuinta) parent.getAdapter().getItem(position);
			OpenSaveLocuintaDialog("edit", locuinta);
			return true;
		});

		btnAddLocuinta.setOnClickListener((View v) ->
		{
			OpenSaveLocuintaDialog("add", null);
		});
		///sfarsit regiune locuinte

		return view;
	}

	public void loadCamere(long idLocuinta)
	{
		List<Camera> values = datasource.getAllCamere(idLocuinta);
		//CamereListAdapter adapter = new CamereListAdapter(this.getActivity(), R.layout.camera_list_item, values);
		ArrayAdapter<Camera> adapter = new ArrayAdapter<Camera>(this.getActivity(),R.layout.list_item, values);
		listSetari.setAdapter(adapter);
	}


	public void OpenSaveLocuintaDialog(String mode, Locuinta locuintaEdit)//"add" sau "edit"
    {
//        final Dialog dialog = new Dialog(getActivity());
//        dialog.setTitle(getActivity().getResources().getString(R.string.Editeaza));
//        dialog.setContentView(R.layout.dialog_add_edit_locuinta);

		String dialogTitle = getActivity().getResources().getString(R.string.Add_locuinta);
		if(mode=="edit")
			dialogTitle = getActivity().getResources().getString(R.string.Edit_locuinta);

		final AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setTitle(dialogTitle)
				.setView(R.layout.dialog_add_edit_locuinta)
				.create();

		dialog.setCancelable(true);
		dialog.show();

        ImageButton btnSave = dialog.findViewById(R.id.btnSave);
        ImageButton btnDelete = dialog.findViewById(R.id.btnDelete);

        if(mode == "add")
		{
			dialog.findViewById(R.id.spacer_buttons_locuinta).setVisibility(View.GONE);
			btnDelete.setVisibility(View.GONE);
		}
        else
		{
			dialog.findViewById(R.id.spacer_buttons_locuinta).setVisibility(View.VISIBLE);
			btnDelete.setVisibility(View.VISIBLE);
		}

        EditText txtNumeLocuinta = dialog.findViewById(R.id.txtNume);
        EditText txtBloc = dialog.findViewById(R.id.txtBloc);
        EditText txtScara = dialog.findViewById(R.id.txtScara);
        EditText txtEtaj = dialog.findViewById(R.id.txtEtaj);
        EditText txtApartament = dialog.findViewById(R.id.txtApartament);
        EditText txtNrPersoane = dialog.findViewById(R.id.txtNrPersoane);
        EditText txtEmail = dialog.findViewById(R.id.txtEmail);

        //for edit
		if(mode=="edit" && locuintaEdit != null)
		{
			txtNumeLocuinta.setText(locuintaEdit.getName());
			txtBloc.setText(locuintaEdit.getBloc());
			txtScara.setText(locuintaEdit.getScara());
			txtEtaj.setText(locuintaEdit.getEtaj());
			txtApartament.setText(locuintaEdit.getApartament());
			txtNrPersoane.setText(locuintaEdit.getNrPersoane());
			txtEmail.setText(locuintaEdit.getEmail());
		}


        btnSave.setOnClickListener((View v1)->
		{
			dialog.dismiss();
			//datasource.open();

			Locuinta loc = SaveLocuinta(dialog, mode, locuintaEdit);

			((MainActivity)getActivity()).loadLocations(loc);
		});

		btnDelete.setOnClickListener((View v) ->
		{
			dialog.dismiss();
			//datasource.open();

			datasource.deleteLocuinta(locuintaEdit);
			ArrayAdapter<Locuinta> adapter = (ArrayAdapter<Locuinta>) lstLocuinte.getAdapter();
			adapter.remove(locuintaEdit);
			adapter.notifyDataSetChanged();

			((MainActivity)getActivity()).loadLocations(null);

			Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.Salvare_efectuata),
					Toast.LENGTH_SHORT).show();
		});

    }
	public Locuinta SaveLocuinta(Dialog dialog, String mode,Locuinta locuinta)//"add" sau "edit"
    {
		EditText txtNumeLocuinta = dialog.findViewById(R.id.txtNume);
		EditText txtBloc = dialog.findViewById(R.id.txtBloc);
		EditText txtScara = dialog.findViewById(R.id.txtScara);
		EditText txtEtaj = dialog.findViewById(R.id.txtEtaj);
		EditText txtApartament = dialog.findViewById(R.id.txtApartament);
		EditText txtNrPersoane = dialog.findViewById(R.id.txtNrPersoane);
		EditText txtEmail = dialog.findViewById(R.id.txtEmail);

		if(!txtNumeLocuinta.getText().toString().equals(""))
		{
			if(locuinta == null)
				locuinta = new Locuinta();

			locuinta.setName(txtNumeLocuinta.getText().toString());
			locuinta.setBloc(txtBloc.getText().toString());
			locuinta.setScara(txtScara.getText().toString());
			locuinta.setEtaj(txtEtaj.getText().toString());
			locuinta.setApartament(txtApartament.getText().toString());
			locuinta.setNrPersoane(txtNrPersoane.getText().toString());
			locuinta.setEmail(txtEmail.getText().toString());


			ArrayAdapter<Locuinta> adapter = (ArrayAdapter<Locuinta>) lstLocuinte.getAdapter();

			if(mode == "add")
			{
				Locuinta newLocuinta = datasource.createLocuinta(locuinta);
				adapter.add(newLocuinta);

				adapter.sort((Locuinta lhs, Locuinta rhs) ->
				{
						return lhs.toString().compareTo(rhs.toString());
				});

				locuinta = newLocuinta;
			}
			else
			{
				datasource.updateLocuinta(locuinta);
			}

			adapter.notifyDataSetChanged();

			Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.Salvare_efectuata),
					Toast.LENGTH_SHORT).show();
		}
		return locuinta;
    }

	public void SetUpTabs(int tab)
	{
		LinearLayout layoutCamere = (LinearLayout) this.getActivity().findViewById(R.id.layoutCamere);
		LinearLayout layoutLocuinte = (LinearLayout) this.getActivity().findViewById(R.id.layoutLocuinte);
		switch (tab)
		{
			case 1:
			{
				btnLocuinte.setSelected(false);
				btnCamere.setSelected(true);
				layoutLocuinte.setVisibility(View.GONE);
				layoutCamere.setVisibility(View.VISIBLE);
				break;
			}
			case 2:
			{
				btnCamere.setSelected(false);
				btnLocuinte.setSelected(true);
				layoutCamere.setVisibility(View.GONE);
				layoutLocuinte.setVisibility(View.VISIBLE);
				break;
			}
		}
	}

	public void OpenSaveCameraDialog(String mode, Camera cameraEdit)//"add" sau "edit"
	{
//		final Dialog dialog = new Dialog(getActivity());
//		dialog.setTitle(getActivity().getResources().getString(R.string.Editeaza));
//		dialog.setContentView(R.layout.dialog_edit_camera);

		String dialogTitle = getActivity().getResources().getString(R.string.Add_camera);
		if(mode=="edit")
			dialogTitle = getActivity().getResources().getString(R.string.Edit_camera);

		final AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setTitle(dialogTitle)
				.setView(R.layout.dialog_edit_camera)
				.create();

		dialog.setCancelable(true);
		dialog.show();

		ImageButton btnSave =  dialog.findViewById(R.id.btnSaveEditCamera);
		ImageButton btnDelete = dialog.findViewById(R.id.btnDeleteCamera);

		if(mode == "add")
		{
			dialog.findViewById(R.id.spacer_buttons_camera).setVisibility(View.GONE);
			btnDelete.setVisibility(View.GONE);
		}
		else
		{
			dialog.findViewById(R.id.spacer_buttons_camera).setVisibility(View.VISIBLE);
			btnDelete.setVisibility(View.VISIBLE);
		}

	 	EditText txtNumeCamera = (EditText) dialog.findViewById(R.id.txtEditCamera);

		//for edit
		if(mode=="edit" && cameraEdit != null)
		{
			txtNumeCamera.setText(cameraEdit.getName());
		}

		btnSave.setOnClickListener((View v) ->
		{
			if (!txtNumeCamera.getText().toString().equals(""))
			{
				dialog.dismiss();
				SaveCamera(dialog, mode, cameraEdit);
			}
		});
		btnDelete.setOnClickListener((View v) ->
		{
			dialog.dismiss();
			//datasource.open();

			datasource.deleteCamera(cameraEdit);
			ArrayAdapter<Camera> adapter = (ArrayAdapter<Camera>) listSetari.getAdapter();
			adapter.remove(cameraEdit);
			adapter.notifyDataSetChanged();

			Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.Salvare_efectuata),
					Toast.LENGTH_SHORT).show();
		});
	}
	public void SaveCamera(Dialog dialog, String mode,Camera camera)//"add" sau "edit"
	{
		EditText txtNumeCamera = dialog.findViewById(R.id.txtEditCamera);

		if(!txtNumeCamera.getText().toString().equals(""))
		{
			if(camera == null)
				camera = new Camera();

			camera.setName(txtNumeCamera.getText().toString());

			ArrayAdapter<Camera> adapter = (ArrayAdapter<Camera>) listSetari.getAdapter();

			long idLocuinta=((Locuinta) ((MainActivity) getActivity()).ddlLocations.getSelectedItem()).getId();

			if(mode == "add")
			{
				Camera newCamera = datasource.createCamera(idLocuinta,camera.getName());
				adapter.add(newCamera);
			}
			else
			{
				datasource.updateCamera(camera);
			}
			adapter.notifyDataSetChanged();

			Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.Salvare_efectuata),
					Toast.LENGTH_SHORT).show();
		}
	}


	public void AddCamera()
	{
		TextView txtCamera = (TextView) this.getActivity().findViewById(R.id.txtNumeCamera);
		if (!txtCamera.getText().toString().equals(""))
		{
			datasource.open();
			ListView listSetari = (ListView) this.getActivity().findViewById(R.id.listSetari);
			CamereListAdapter adapter = (CamereListAdapter) listSetari.getAdapter();
			Camera Camera = null;


			// save the new Camera to the database
			if(selectedLocuinta!=null)
			{
				Camera = datasource.createCamera(selectedLocuinta.getId(), txtCamera.getText().toString());
				adapter.add(Camera);
				txtCamera.setText("");
			}
		}
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
