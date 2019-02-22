package com.gino.citireapometre_pro;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import com.gino.citireapometre_pro.DB.ApometreDataSource;
import com.gino.citireapometre_pro.DB.Consum;
import com.gino.citireapometre_pro.DB.ConsumListAdapter;
import com.gino.citireapometre_pro.DB.ConsumListHelper;
import com.gino.citireapometre_pro.DB.CitireListAdapter.CitireHolder;
import com.gino.citireapometre_pro.DB.Locuinta;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnPageChangeListener//,OnTabChangeListener
{
    public Spinner ddlLocations;
    MyPageAdapter pageAdapter;
    public ViewPager mViewPager;
    //private TabHost mTabHost;
    private ApometreDataSource datasource;
    private Activity activity=this;
    int currentPosition = 0;

    final int MY_PERMISSIONS_REQUEST_READ_WRITE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setLocale();
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(R.string.Info_permisiuni)
                        .setCancelable(true)
                        .setPositiveButton(R.string.OK, (DialogInterface dialog, int id) ->
                        {
                            dialog.dismiss();
                            //ask for permission first
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_WRITE);
                        });

                AlertDialog alert = builder.create();
                alert.show();


        }

        ddlLocations = findViewById(R.id.ddlLocation);


        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        // Tab Initialization
        //initialiseTabHost();//comentate de test

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pageAdapter);
        //mViewPager.setOnPageChangeListener((OnPageChangeListener) MainActivity.this);
        mViewPager.addOnPageChangeListener((OnPageChangeListener) MainActivity.this);


        SetUpTabSelection(0);

        datasource = new ApometreDataSource(this);
        datasource.open();

        loadLocations(null);

        ddlLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                for (int i = 0; i < pageAdapter.fragments.size(); i++)
                {
                    FragmentLifecycle fragment = (FragmentLifecycle) pageAdapter.fragments.get(i);
                    fragment.onLocationChange((Locuinta)ddlLocations.getSelectedItem());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
//        ActionBar bar = getActionBar();
//        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#75AFAC")));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_READ_WRITE:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else
                {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    finish();
                    moveTaskToBack(true);
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void loadLocations(Locuinta selectedLocation)
    {
        List<Locuinta> locuinte = datasource.getAllLocuinte();
        ArrayAdapter<Locuinta> adapter = new ArrayAdapter<Locuinta>(this, R.layout.spinner_item, locuinte);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        ddlLocations.setAdapter(adapter);

        if (selectedLocation != null)
        {
            for (int position = 0; position < adapter.getCount(); position++)
            {
                if(adapter.getItem(position).getId() == selectedLocation.getId())
                {
                    ddlLocations.setSelection(position);
                    return;
                }
            }
        }
    }

    public void btnCitireClick(View v)
    {
        SetUpTabSelection(0);
        this.mViewPager.setCurrentItem(0);
    }
    public void btnConsumClick(View v)
    {
        SetUpTabSelection(1);
        this.mViewPager.setCurrentItem(1);
    }
    public void btnGraficClick(View v)
    {
        SetUpTabSelection(2);
        this.mViewPager.setCurrentItem(2);
    }
    public void btnSetariClick(View v)
    {
        SetUpTabSelection(3);
        this.mViewPager.setCurrentItem(3);
    }
    public void SetUpTabSelection(int index)
    {
        Button btnCitire=(Button)findViewById(R.id.btnCitire);
        Button btnConsum=(Button)findViewById(R.id.btnConsum);
        Button btnGrafic=(Button)findViewById(R.id.btnGrafic);
        Button btnSetari=(Button)findViewById(R.id.btnSetari);

        switch (index)
        {
            case 0:
                btnCitire.setSelected(true);
                btnConsum.setSelected(false);
                btnGrafic.setSelected(false);
                btnSetari.setSelected(false);
                break;
            case 1:
                btnCitire.setSelected(false);
                btnConsum.setSelected(true);
                btnGrafic.setSelected(false);
                btnSetari.setSelected(false);
                break;
            case 2:
                btnCitire.setSelected(false);
                btnConsum.setSelected(false);
                btnGrafic.setSelected(true);
                btnSetari.setSelected(false);
                break;
            case 3:
                btnCitire.setSelected(false);
                btnConsum.setSelected(false);
                btnGrafic.setSelected(false);
                btnSetari.setSelected(true);
                break;
        }

    }
    public void setUpValuesForEdit(ConsumListHelper clh)
    {
        List<Consum> consumuri = clh.getListConsum();
        String data = clh.getData();

        String[] splitDate = data.split("-");

        String zi = splitDate[0];
        String luna = splitDate[1];
        String an = splitDate[2];
        // set current date into textview
        String dataProcesata=zi + "-" + Common.getLuna(this, luna) + "-" + an;

        TextView tvData = (TextView) findViewById(R.id.lblDataSetata);
        tvData.setText(dataProcesata);

        ListView lvCitire = (ListView) findViewById(R.id.listCitire);

        for (int i = 0; i < lvCitire.getChildCount(); i++)
        {
            View row = lvCitire.getChildAt(i);
            CitireHolder ch = (CitireHolder)row.getTag();
            for (int j = 0; j < consumuri.size(); j++)
            {
                if (consumuri.get(j).getIdCamera() == ch.camera.getId())
                {
                    ch.txtCalda.setText(consumuri.get(j).getConsumCalda().toString());
                    ch.txtRece.setText(consumuri.get(j).getConsumRece().toString());
                    break;
                }
            }
        }
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

    // Manages the Page changes, synchronizing it with Tabs
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2)
    {
        int pos = this.mViewPager.getCurrentItem();
        SetUpTabSelection(pos);
        //this.mTabHost.setCurrentTab(pos);//comentat de test
    }
    @Override
    public void onPageScrollStateChanged(int arg0)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int newPosition)
    {
        FragmentLifecycle fragmentToShow = (FragmentLifecycle)pageAdapter.getItem(newPosition);
        fragmentToShow.onResumeFragment((Locuinta)ddlLocations.getSelectedItem());

        currentPosition = newPosition;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.menuBackup:
                Common.exportDB(activity);
                return true;
            case R.id.menuRestore:
                Common.importDB(activity);
                //trebuie intai sa sari la fragmentul de consum
                SetUpTabSelection(1);
                mViewPager.setCurrentItem(1);
                RefreshTabelConsum();
                return true;
            case R.id.menuAbout:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.menuLimba:
                final Dialog dialog = new Dialog(this);
                dialog.setTitle(this.getResources().getString(R.string.Limba));
                dialog.setContentView(R.layout.dialog_limba);
                dialog.show();
                final RadioGroup rgLimba = (RadioGroup)dialog.findViewById(R.id.rgLimba);

                RadioButton rbEnglish = (RadioButton)dialog.findViewById(R.id.rbEnglish);
                RadioButton rbRomana = (RadioButton)dialog.findViewById(R.id.rbRomana);
                RadioButton rbGermana = (RadioButton)dialog.findViewById(R.id.rbGermana);

                SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                String lang = sharedPref.getString("Limba", "");
                if(lang.equals("en"))
                    rbEnglish.setChecked(true);
                if(lang.equals("ro"))
                    rbRomana.setChecked(true);
                if(lang.equals("de"))
                    rbGermana.setChecked(true);
                if(lang.equals(""))
                {
                    lang = Locale.getDefault().getLanguage();
                    if(lang.equals("en"))
                        rbEnglish.setChecked(true);
                    else
                    if(lang.equals("ro"))
                        rbRomana.setChecked(true);
                    if(lang.equals("de"))
                        rbGermana.setChecked(true);
                    else
                        rbEnglish.setChecked(true);
                }


                rgLimba.setOnCheckedChangeListener(new OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId)
                    {
                        int selectedId = rgLimba.getCheckedRadioButtonId();

                        if(selectedId==R.id.rbEnglish)
                        {
                            SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("Limba", "en");
                            editor.commit();

                            setLocale();

//						Intent refresh = new Intent(activity, MainActivity.class);
//						refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//						startActivity(refresh);
                            finish();
                            startActivity(getIntent());

                            dialog.dismiss();
                        }
                        if(selectedId==R.id.rbRomana)
                        {
                            SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("Limba", "ro");
                            editor.commit();

                            setLocale();

//						Intent refresh = new Intent(activity, MainActivity.class);
//						refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//						startActivity(refresh);

                            finish();
                            startActivity(getIntent());

                            dialog.dismiss();
                        }
                        if(selectedId==R.id.rbGermana)
                        {
                            SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("Limba", "de");
                            editor.commit();

                            setLocale();

//						Intent refresh = new Intent(activity, MainActivity.class);
//						refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//						startActivity(refresh);

                            finish();
                            startActivity(getIntent());

                            dialog.dismiss();
                        }
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void setLocale()
    {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String lang = sharedPref.getString("Limba", "");
        if(!lang.equals(""))
        {
            Locale myLocale = new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.setLocale(myLocale);
            res.updateConfiguration(conf, dm);
        }
    }
    public class MyPageAdapter extends FragmentStatePagerAdapter
    {
        private List<Fragment> fragments;
        public MyPageAdapter(FragmentManager fm)
        {
            super(fm);

            this.fragments = new ArrayList<Fragment>();
            fragments.add(new CitireActivity());
            fragments.add(new TabelActivity());
            fragments.add(new GraficFragment());
            fragments.add(new SetariFragmentActivity());
        }

        @Override
        public Fragment getItem(int position)
        {
            return fragments.get(position);
        }

        @Override
        public int getCount()
        {
            return 4;
        }
    }


    //////////////
    //////////
    ///////EVENIMENTE DIN FRAGMENTE

//    public void btnEditClick(View v)
//    {
//        final Camera camera=(Camera)v.getTag();
//
//        final Dialog dialog = new Dialog(activity);
//        dialog.setTitle(activity.getResources().getString(R.string.Editeaza));
//        dialog.setContentView(R.layout.dialog_edit_camera);
//
//        Button btnSave = (Button)dialog.findViewById(R.id.btnSaveEditCamera);
//        final EditText txtNumeCamera = (EditText)dialog.findViewById(R.id.txtEditCamera);
//
//        txtNumeCamera.setText(camera.getName());
//
//
//        btnSave.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                if(!txtNumeCamera.getText().toString().equals(""))
//                {
//                    dialog.dismiss();
//
//                    datasource.open();
//
//                    ListView listSetari=(ListView)findViewById(R.id.listSetari);
//                    CamereListAdapter adapter = (CamereListAdapter) listSetari.getAdapter();
//
//                    camera.setName(txtNumeCamera.getText().toString());
//                    datasource.updateCamera(camera);//aici se face update
//
//                    adapter.notifyDataSetChanged();
//
//                    //RefreshTabelConsum();
//                    Toast.makeText(activity, activity.getResources().getString(R.string.Salvare_efectuata),
//                            Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//
//
//        dialog.setCancelable(true);
//        dialog.show();
//    }

//    public void btnDeleteClick(View v)
//    {
//        showConfirmDialog(v);
//
//    }
    public void RefreshTabelConsum()
    {
        datasource = new ApometreDataSource(this);
        datasource.open();
        List<ConsumListHelper> values = datasource.getConsumListHelper(1,"Lista");
        ConsumListAdapter adapter=new ConsumListAdapter(this, R.layout.consum_list_item, values);
        ListView listConsum=(ListView)findViewById(R.id.listConsum);
        listConsum.setAdapter(adapter);

    }

//    public void showConfirmDialog(final View v)
//    {
//        final Camera camera=(Camera)v.getTag();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(this.getResources().getString(R.string.StergetiCamera)+" \"" + camera.getName() + "\"?")
//                .setCancelable(true)
//                .setPositiveButton(this.getResources().getString(R.string.Da), new DialogInterface.OnClickListener()
//                {
//                    public void onClick(DialogInterface dialog, int id)
//                    {
//                        datasource.open();
//
//                        ListView listSetari=(ListView)findViewById(R.id.listSetari);
//                        CamereListAdapter adapter = (CamereListAdapter) listSetari.getAdapter();
//                        if (adapter.getCount() > 0)
//                        {
//                            datasource.deleteCamera(camera);
//                            adapter.remove(camera);
//                            //refreshGraph();
//                        }
//                        adapter.notifyDataSetChanged();
//
//                        //RefreshTabelConsum();
//                    }
//                })
//                .setNegativeButton(this.getResources().getString(R.string.Nu), new DialogInterface.OnClickListener()
//                {
//                    public void onClick(DialogInterface dialog, int id)
//                    {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }


//    //importing database
//    private void importDB()
//    {
//        // TODO Auto-generated method stub
//
//        try {
//            File sd = Environment.getExternalStorageDirectory();
//            File data  = Environment.getDataDirectory();
//
//            if (sd.canWrite())
//            {
//                String backupDBPath  = "/" + activity.getResources().getString(R.string.CitireApometre) +
//                        "/Apometre.db";
//                File  backupDB= getDatabasePath("Apometre.db");//new File(data, currentDBPath);
//                File currentDB  = new File(sd, backupDBPath);
//
//                FileChannel src = new FileInputStream(currentDB).getChannel();
//                FileChannel dst = new FileOutputStream(backupDB).getChannel();
//                dst.transferFrom(src, 0, src.size());
//                src.close();
//                dst.close();
//                Toast.makeText(getBaseContext(), activity.getResources().getString(R.string.Restaurare_efectuata),
//                        Toast.LENGTH_LONG).show();
//
//            }
//        } catch (Exception e) {
//
//            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
//                    .show();
//
//        }
//    }
//    //exporting database
//    private void exportDB()
//    {
//        // TODO Auto-generated method stub
//
//        try {
//
//            createPDFDirectory(activity.getResources().getString(R.string.CitireApometre));
//
//            File sd = Environment.getExternalStorageDirectory();
//            File data = Environment.getDataDirectory();
//
//            if (sd.canWrite()) {
//
//                String backupDBPath  = "/" + activity.getResources().getString(R.string.CitireApometre) +
//                        "/Apometre.db";
//                File currentDB = getDatabasePath("Apometre.db");//new File(data, currentDBPath);
//                File backupDB = new File(sd, backupDBPath);
//
//                FileChannel src = new FileInputStream(currentDB).getChannel();
//                FileChannel dst = new FileOutputStream(backupDB).getChannel();
//                dst.transferFrom(src, 0, src.size());
//                src.close();
//                dst.close();
//                Toast.makeText(getBaseContext(),
//                        activity.getResources().getString(R.string.Backup_efectuat_la) +
//                                backupDB.toString(),
//                        Toast.LENGTH_LONG).show();
//
//            }
//        } catch (Exception e) {
//
//            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
//                    .show();
//
//        }
//    }
//    public void createPDFDirectory(String directoryName)
//    {
//        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + directoryName);
//        boolean success = true;
//        if (!folder.exists())
//        {
//            success = folder.mkdir();
//        }
//    }

    @Override
    public void onResume()
    {
        datasource.open();
        super.onResume();
    }

    @Override
    public void onPause()
    {
        datasource.close();
        super.onPause();
    }


}



