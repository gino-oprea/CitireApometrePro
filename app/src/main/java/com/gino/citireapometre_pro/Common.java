package com.gino.citireapometre_pro;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class Common
{
	//importing database
	public static void importDB(Context context)
	{
		// TODO Auto-generated method stub

		try {
			File sd = Environment.getExternalStorageDirectory();
			File data  = Environment.getDataDirectory();

			if (sd.canWrite())
			{
				String backupDBPath  = "/" + context.getResources().getString(R.string.CitireApometre) +
						"/Apometre_Pro.db";
				File  backupDB= context.getDatabasePath("Apometre_Pro.db");//new File(data, currentDBPath);
				File currentDB  = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Toast.makeText(context, context.getResources().getString(R.string.Restaurare_efectuata),
						Toast.LENGTH_LONG).show();

			}
		} catch (Exception e) {

			Toast.makeText(context, e.toString(), Toast.LENGTH_LONG)
					.show();

		}
	}
	//exporting database
	public static void exportDB(Context context)
	{
		// TODO Auto-generated method stub

		try {
			createPDFDirectory(context.getResources().getString(R.string.CitireApometre));

			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {

				String backupDBPath  = "/" + context.getResources().getString(R.string.CitireApometre) +
						"/Apometre_Pro.db";
				File currentDB = context.getDatabasePath("Apometre_Pro.db");//new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Toast.makeText(context,
						context.getResources().getString(R.string.Backup_efectuat_la) +
								backupDB.toString(),
						Toast.LENGTH_LONG).show();

			}
		} catch (Exception e) {

			Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	public static void createPDFDirectory(String directoryName)
	{
		File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + directoryName);
		boolean success = true;
		if (!folder.exists())
		{
			success = folder.mkdir();
		}
	}
	public static String getLuna(Context context,String NrLuna)
	{
		String luna=context.getResources().getString(R.string.Ian);//"Ian";
		if(NrLuna.equals("01"))
			luna=context.getResources().getString(R.string.Ian);//"Ian";
		if(NrLuna.equals("02"))
			luna=context.getResources().getString(R.string.Feb);//"Feb";
		if(NrLuna.equals("03"))
			luna=context.getResources().getString(R.string.Mar);//"Mar";
		if(NrLuna.equals("04"))
			luna=context.getResources().getString(R.string.Apr);//"Apr";
		if(NrLuna.equals("05"))
			luna=context.getResources().getString(R.string.Mai);//"Mai";
		if(NrLuna.equals("06"))
			luna=context.getResources().getString(R.string.Iun);//"Iun";
		if(NrLuna.equals("07"))
			luna=context.getResources().getString(R.string.Iul);//"Iul";
		if(NrLuna.equals("08"))
			luna=context.getResources().getString(R.string.Aug);//"Aug";
		if(NrLuna.equals("09"))
			luna=context.getResources().getString(R.string.Sep);//"Sep";
		if(NrLuna.equals("10"))
			luna=context.getResources().getString(R.string.Oct);//"Oct";
		if(NrLuna.equals("11"))
			luna=context.getResources().getString(R.string.Nov);//"Nov";
		if(NrLuna.equals("12"))
			luna=context.getResources().getString(R.string.Dec);//"Dec";	
		
		return luna;
	}
	public static String getLunaFullName(Context context, String NrLuna)
	{
		String luna=context.getResources().getString(R.string.Ianuarie);//"IANUARIE";
		if(NrLuna.equals("01"))
			luna=context.getResources().getString(R.string.Decembrie);//"DECEMEBRIE";
		if(NrLuna.equals("02"))
			luna=context.getResources().getString(R.string.Ianuarie);//"IANUARIE";
		if(NrLuna.equals("03"))
			luna=context.getResources().getString(R.string.Februarie);//"FEBRUARIE";
		if(NrLuna.equals("04"))
			luna=context.getResources().getString(R.string.Martie);//"MARTIE";
		if(NrLuna.equals("05"))
			luna=context.getResources().getString(R.string.Aprilie);//"APRILIE";
		if(NrLuna.equals("06"))
			luna=context.getResources().getString(R.string.Maii);//"MAI";
		if(NrLuna.equals("07"))
			luna=context.getResources().getString(R.string.Iunie);//"IUNIE";
		if(NrLuna.equals("08"))
			luna=context.getResources().getString(R.string.Iulie);//"IULIE";
		if(NrLuna.equals("09"))
			luna=context.getResources().getString(R.string.August);//"AUGUST";
		if(NrLuna.equals("10"))
			luna=context.getResources().getString(R.string.Septembrie);//"SEPTEMBRIE";
		if(NrLuna.equals("11"))
			luna=context.getResources().getString(R.string.Octombrie);//"OCTOMBRIE";
		if(NrLuna.equals("12"))
			luna=context.getResources().getString(R.string.Noiembrie);//"NOIEMBRIE";	
		
		return luna;
	}
	public static String getLunaNumber(Context context, String Luna)
	{
		String luna="01";
		if(Luna.equals(context.getResources().getString(R.string.Ian)))
			luna="01";
		if(Luna.equals(context.getResources().getString(R.string.Feb)))
			luna="02";
		if(Luna.equals(context.getResources().getString(R.string.Mar)))
			luna="03";
		if(Luna.equals(context.getResources().getString(R.string.Apr)))
			luna="04";
		if(Luna.equals(context.getResources().getString(R.string.Mai)))
			luna="05";
		if(Luna.equals(context.getResources().getString(R.string.Iun)))
			luna="06";
		if(Luna.equals(context.getResources().getString(R.string.Iul)))
			luna="07";
		if(Luna.equals(context.getResources().getString(R.string.Aug)))
			luna="08";
		if(Luna.equals(context.getResources().getString(R.string.Sep)))
			luna="09";
		if(Luna.equals(context.getResources().getString(R.string.Oct)))
			luna="10";
		if(Luna.equals(context.getResources().getString(R.string.Nov)))
			luna="11";
		if(Luna.equals(context.getResources().getString(R.string.Dec)))
			luna="12";	
		
		return luna;
	}
}
