package com.gino.citireapometre_pro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.gino.citireapometre_pro.DB.ApometreDataSource;
import com.gino.citireapometre_pro.DB.Camera;
import com.gino.citireapometre_pro.DB.Consum;
import com.gino.citireapometre_pro.DB.ConsumListAdapter;
import com.gino.citireapometre_pro.DB.ConsumListHelper;
import com.gino.citireapometre_pro.DB.Locuinta;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class TabelActivity extends Fragment implements FragmentLifecycle
{	
	private ApometreDataSource datasource;
	FloatingActionButton btnAddCitire;
	ListView listConsum;
	private File pdfFile;
	private String filename = "Apometre.pdf";
	private String filepath = "Apometre";
	private BaseFont bfBold;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = (View) inflater.inflate(R.layout.activity_tabel, container, false);		
		
		filename = getActivity().getResources().getString(R.string.CitireApometre)+".pdf";
		filepath = getActivity().getResources().getString(R.string.CitireApometre);
		
		datasource = new ApometreDataSource(this.getActivity());
		datasource.open();			
		

  		listConsum=(ListView)view.findViewById(R.id.listConsum);
  		btnAddCitire = view.findViewById(R.id.btnAddCitire);

  		if(((MainActivity) getActivity()).ddlLocations.getSelectedItem()!=null)
		{
			long idLocuinta = ((Locuinta) ((MainActivity) getActivity()).ddlLocations.getSelectedItem()).getId();
			loadListConsum(idLocuinta);
		}

		btnAddCitire.setOnClickListener((View v) ->
		{
			MainActivity mainActivity = (MainActivity) getActivity();
			mainActivity.SetUpTabSelection(0);
			mainActivity.mViewPager.setCurrentItem(0);
		});

  		
  		listConsum.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3)
			{
				if(!view.isSelected())
					view.setSelected(true);
			}  			
		});
  		listConsum.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,int arg2, long arg3)
			{
				if(!view.isSelected())
					view.setSelected(true);
				ConsumListHelper clh = (ConsumListHelper)view.getTag();
				
				Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
				vibe.vibrate(50);
				
				showConfirmDialog(clh);
				return false;
			}  			
		});  		
  		
		return view;
	}
	public void loadListConsum(long idLocuinta)
	{
		List<ConsumListHelper> values = datasource.getConsumListHelper(idLocuinta,"Lista");
		ConsumListAdapter adapter=new ConsumListAdapter(this.getActivity(), R.layout.consum_list_item, values);
		listConsum.setAdapter(adapter);
	}
	public void showConfirmDialog(final ConsumListHelper clh)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		builder.setMessage(getActivity().getResources().getString(R.string.EditConsum))
		       .setCancelable(true)
		       .setPositiveButton(getActivity().getResources().getString(R.string.Editare), new DialogInterface.OnClickListener() 
		       {
		           public void onClick(DialogInterface dialog, int id) 
		           {
		                MainActivity mainActivity = (MainActivity)getActivity();
		                mainActivity.SetUpTabSelection(0);
		                mainActivity.mViewPager.setCurrentItem(0);
		                mainActivity.setUpValuesForEdit(clh);
		           }
		       })
		       .setNeutralButton(getActivity().getResources().getString(R.string.Export_PDF), new DialogInterface.OnClickListener()
		       {
				
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						Locuinta locuinta = (Locuinta) ((MainActivity) getActivity()).ddlLocations.getSelectedItem();

						List<ConsumListHelper> clhList = datasource.getConsumListHelper(locuinta.getId(),"Lista");
						ConsumListHelper previousClh = getPreviousConsum(clh, clhList);
						if(previousClh!=null)
						{
							ProgressDialog loading = ProgressDialog.show(getActivity(), "",
									"Loading. Please wait...", true);

							filename = getActivity().getResources().getString(R.string.CitireApometre) + "_" + locuinta.getName() + ".pdf";
							generatePDF(previousClh.getListConsum(), clh.getListConsum(), locuinta);
							openPDF(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filepath + "/" + filename);

							loading.dismiss();
							Toast.makeText(getActivity(),
									getActivity().getResources().getString(R.string.Fisierul_a_fost_salvat_pe_cardul_de_memorie_in_directorul) + " " +
											filepath, Toast.LENGTH_LONG).show();
						}
						else 
						{
							Toast toast = Toast.makeText(getActivity(),
									getActivity().getResources().getString(R.string.Nu_exista_date_pe_luna_precedenta) + ".", Toast.LENGTH_LONG);
							toast.show();
						}
					}
		       })
		       .setNegativeButton(getActivity().getResources().getString(R.string.Sterge), new DialogInterface.OnClickListener()
			   {
				   public void onClick(DialogInterface dialog, int id)
				   {
					   datasource.open();
					   //ListView listConsum=(ListView)getActivity().findViewById(R.id.listConsum);
					   ConsumListAdapter adapter = (ConsumListAdapter) listConsum.getAdapter();
					   if (adapter.getCount() > 0)
					   {
						   long idLocuinta = ((Locuinta) ((MainActivity) getActivity()).ddlLocations.getSelectedItem()).getId();
						   datasource.deleteConsum(idLocuinta, clh.getData());
						   adapter.remove(clh);
					   }
					   adapter.notifyDataSetChanged();

					   Common.exportDB(getActivity());
				   }
			   });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
//	public String getLuna(String NrLuna)
//	{
//		String luna="IANUARIE";
//		if(NrLuna.equals("01"))
//			luna="DECEMEBRIE";
//		if(NrLuna.equals("02"))
//			luna="IANUARIE";
//		if(NrLuna.equals("03"))
//			luna="FEBRUARIE";
//		if(NrLuna.equals("04"))
//			luna="MARTIE";
//		if(NrLuna.equals("05"))
//			luna="APRILIE";
//		if(NrLuna.equals("06"))
//			luna="MAI";
//		if(NrLuna.equals("07"))
//			luna="IUNIE";
//		if(NrLuna.equals("08"))
//			luna="IULIE";
//		if(NrLuna.equals("09"))
//			luna="AUGUST";
//		if(NrLuna.equals("10"))
//			luna="SEPTEMBRIE";
//		if(NrLuna.equals("11"))
//			luna="OCTOMBRIE";
//		if(NrLuna.equals("12"))
//			luna="NOIEMBRIE";	
//		
//		return luna;
//	}
	
	public void generatePDF(List<Consum> consumVechi, List<Consum> consumNou, Locuinta locuinta)
	{
		if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) 
		{  
		 Toast toast =Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.Nu_se_poate_accesa_cardul_de_memorie)+"!", Toast.LENGTH_SHORT);
		 toast.show();
		 return;
		} 
		 else 
		{
		  createPDFDirectory(filepath);
		  File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filepath);
		  pdfFile = new File(folder, filename);
		}
		 //create a new document
		Document document = new Document();
		
		try 
		{
		  
		 PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
		 document.open();		    
		 
		
				   
		PdfPTable tabelRece = generateTabelConsum(datasource.getAllCamere(locuinta.getId()), consumVechi, consumNou, getActivity().getResources().getString(R.string.Rece));
		PdfPTable tabelCalda = generateTabelConsum(datasource.getAllCamere(locuinta.getId()), consumVechi, consumNou, getActivity().getResources().getString(R.string.Calda));
		//document.add(tabelRece); 
		//document.add(tabelCalda);
		String shortDots = ".......";
		String longDots = "....................";
		//Persoana persoana = datasource.GetPersoana();
		
		String data=consumNou.get(0).getData();
		String indexLuna = data.substring(3,5);
		
		String text = getActivity().getResources().getString(R.string.Consum_contorizat_in_apartament_pe_luna)+" " +
						//Common.getLunaFullName(getActivity(), indexLuna) + " , "+
						getActivity().getResources().getString(R.string.An)+" " + data.substring(6) + ", "+
						getActivity().getResources().getString(R.string.Bloc)+" "+shortDots+","; 
		String text2 = getActivity().getResources().getString(R.string.Scara)+" "+shortDots+", "+
						getActivity().getResources().getString(R.string.Etaj)+" "+shortDots+", "+
						getActivity().getResources().getString(R.string.Apartament)+" "+shortDots+", "+
						getActivity().getResources().getString(R.string.NrPersoane)+" "+shortDots;
		
		if(locuinta!=null)
		{
			String bloc=locuinta.getBloc().trim().equals("")?shortDots:locuinta.getBloc();
			String etaj=locuinta.getEtaj().trim().equals("")?shortDots:locuinta.getEtaj();
			String scara=locuinta.getScara().trim().equals("")?shortDots:locuinta.getScara();
			String apartament=locuinta.getApartament().trim().equals("")?shortDots:locuinta.getApartament();
			String nrPersoane=locuinta.getNrPersoane().trim().equals("")?shortDots:locuinta.getNrPersoane();

			text = getActivity().getResources().getString(R.string.Consum_contorizat_in_apartament_pe_luna)+" " +
					//Common.getLunaFullName(getActivity(), indexLuna) + " , "+
					getActivity().getResources().getString(R.string.An)+" " +
					data.substring(6) + ", "+getActivity().getResources().getString(R.string.Bloc)+" "+bloc+",";
			text2 = getActivity().getResources().getString(R.string.Scara)+" "+scara+", "+
					getActivity().getResources().getString(R.string.Etaj)+" "+etaj+", "+
					getActivity().getResources().getString(R.string.Apartament)+" "+apartament+", "+
					getActivity().getResources().getString(R.string.NrPersoane)+" "+nrPersoane;
		}
		
		PdfContentByte cb = docWriter.getDirectContent();
		writeText(cb, document.leftMargin(), 800, text);
		writeText(cb, document.leftMargin(), 780, text2);
		
		tabelRece.writeSelectedRows(0, -1, document.leftMargin(), 750, docWriter.getDirectContent());
		tabelCalda.writeSelectedRows(0, -1, document.leftMargin() + tabelRece.getTotalWidth() + 30, 750, docWriter.getDirectContent());
		 
		String text3=getActivity().getResources().getString(R.string.Data_citirii)+": "+
						consumNou.get(0).getData();
		String text4=getActivity().getResources().getString(R.string.Semnatura);
		writeText(cb, document.leftMargin(), 750 - tabelRece.getTotalHeight() - 20, text3);
		writeText(cb, 300, 750 - tabelCalda.getTotalHeight() - 20, text4);
		
		     
		    
		document.close();
	  } 
	  catch(Exception e)
	  {
	   e.printStackTrace();
	  }		 
	 }
	public PdfPTable generateTabelConsum(List<Camera> camere, List<Consum> consumVechi, List<Consum> consumNou, String Tip) throws DocumentException 
	{
		float cellWidth=80;
		Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
		
		PdfPTable tabel = new PdfPTable(3);
		tabel.setTotalWidth(new float[]{cellWidth,cellWidth,cellWidth});
		tabel.setLockedWidth(true);  
		PdfPCell cell;
		
		if(Tip.equals(getActivity().getResources().getString(R.string.Rece)))
			cell = new PdfPCell(new Phrase(getActivity().getResources().getString(R.string.Apa_rece),font));
		else
			cell = new PdfPCell(new Phrase(getActivity().getResources().getString(R.string.Apa_calda),font));
		
		cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
		cell.setColspan(3);
		tabel.addCell(cell);
		
		cell = new PdfPCell(new Phrase("",font));		
		tabel.addCell(cell);
		cell = new PdfPCell(new Phrase(getActivity().getResources().getString(R.string.Index_vechi),font));		
		tabel.addCell(cell);
		cell = new PdfPCell(new Phrase(getActivity().getResources().getString(R.string.Index_nou),font));		
		tabel.addCell(cell);	
		
		for (int i = 0; i < camere.size(); i++)
		{			
			//crearea randurilor cu consumul pe fiecare camera						
			cell = new PdfPCell(new Phrase(camere.get(i).getName(),font));			
			tabel.addCell(cell);
			tabel.addCell(Float.toString(getConsumByIdCamera(camere.get(i).getId(), consumVechi, Tip)));//index vechi
			tabel.addCell(Float.toString(getConsumByIdCamera(camere.get(i).getId(), consumNou, Tip)));//index nou			
		}		
		///total 
		cell = new PdfPCell(new Phrase(getActivity().getResources().getString(R.string.Total),font));		
		tabel.addCell(cell);
		float totalVechi=calculateTotal(consumVechi, Tip);
		float totalNou=calculateTotal(consumNou, Tip);
		cell = new PdfPCell(new Phrase(String.format("%.1f", totalVechi),font));		
		tabel.addCell(cell);
		cell = new PdfPCell(new Phrase(String.format("%.1f", totalNou),font));		
		tabel.addCell(cell);		
		
		//consum
		cell = new PdfPCell(new Phrase(getActivity().getResources().getString(R.string.Consum),font));	
		tabel.addCell(cell);
		cell = new PdfPCell(new Phrase(String.format("%.1f", totalNou-totalVechi),font));
		cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
		cell.setColspan(2);
		tabel.addCell(cell);		
		return tabel;
	}
	public float calculateTotal(List<Consum> consum, String Tip)
	{
		float total=0;
		for (int i = 0; i < consum.size(); i++)
		{			
			if (Tip.equals(getActivity().getResources().getString(R.string.Calda)))
			{
				total+=consum.get(i).getConsumCalda();
			}
			else 
			{
				total+=consum.get(i).getConsumRece();
			}			
		}
		return total;
	}
	public ConsumListHelper getPreviousConsum(ConsumListHelper consumCurent,List<ConsumListHelper> consumuri)
	{
		int currentPosition=0;
		for (int i = 0; i < consumuri.size(); i++)
		{
			///trebuie gasita pozitia consumului curent in lista totala
			if (consumCurent.getData().equals(consumuri.get(i).getData()))
			{
				currentPosition=i;
				break;
			}
		}
		if (currentPosition+1<consumuri.size())
		{
			return consumuri.get(currentPosition + 1);
		}
		else 
		{
			return null;					
		}
	}
	public float getConsumByIdCamera(long idCamera,List<Consum> consumuri, String tip)
	{
		float consum=0;
		for (int i = 0; i < consumuri.size(); i++)
		{
			if (idCamera == consumuri.get(i).getIdCamera())
			{
				if (tip==getActivity().getResources().getString(R.string.Rece))
				{
					consum=consumuri.get(i).getConsumRece();
				}
				if (tip==getActivity().getResources().getString(R.string.Calda))
				{
					consum=consumuri.get(i).getConsumCalda();
				}
			}
		}		
		return consum;
	}
	
		 
		 private void writeText(PdfContentByte cb, float x, float y, String text) throws DocumentException, IOException
		 {	
		  BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		  cb.beginText();
		  cb.setFontAndSize(bf, 13);
		  cb.setTextMatrix(x,y);
		  cb.showText(text.trim());
		  cb.endText(); 
		 
		 }
		 
	
		 private static boolean isExternalStorageReadOnly() 
		 {  
			  String extStorageState = Environment.getExternalStorageState();  
			  if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState))
			  {  
			   return true;  
			  }  
			  return false;  
		 }  
			 
		 private static boolean isExternalStorageAvailable()
		 {  
		  String extStorageState = Environment.getExternalStorageState();  
		  if (Environment.MEDIA_MOUNTED.equals(extStorageState)) 
		  {  
		   return true;  
		  }  
		  return false;  
		 } 
	
		 public void createPDFDirectory(String directoryName)
		 {
			 File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + directoryName);
			 boolean success = true;
			 if (!folder.exists()) 
			 {
			     success = folder.mkdir();
			 }			  
		 }

		 public void openPDF(String FilePath)
		 {
			 File pdfFile = new File(FilePath); 
	            if(pdfFile.exists()) 
	            {
	                Uri path = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".fileprovider", pdfFile);//Uri.fromFile(pdfFile);
	                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
	                pdfIntent.setDataAndType(path, "application/pdf");
	                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

	                try
	                {
	                    startActivity(pdfIntent);
	                }
	                catch(ActivityNotFoundException e)
	                {
	                    Toast.makeText(getActivity(), 
	                    		getActivity().getResources().getString(R.string.Nu_e_instalata_nici_o_aplicatie_pentru_a_vedea_fisierul_PDF)+ 
	                    		"." +
	                    		getActivity().getResources().getString(R.string.Fisierul_a_fost_salvat_pe_cardul_de_memorie_in_directorul)+ " " + 
	                filepath, Toast.LENGTH_LONG).show();
	                    
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

			loadListConsum(location.getId());
		}
	}
}
