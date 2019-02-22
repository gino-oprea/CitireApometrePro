package com.gino.citireapometre_pro.DB;

import com.gino.citireapometre_pro.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper 
{
	public static final String TABLE_LOCUINTE="tblLocuinte";
	public static final String COLUMN_TBLLOCUINTE_ID="_id";
	public static final String COLUMN_TBLLOCUINTE_NUME="Nume";
	public static final String 	COLUMN_TBLLOCUINTE_BLOC="Bloc";
	public static final String 	COLUMN_TBLLOCUINTE_SCARA="Scara";
	public static final String 	COLUMN_TBLLOCUINTE_ETAJ="Etaj";
	public static final String 	COLUMN_TBLLOCUINTE_APARTAMENT="Apartament";
	public static final String 	COLUMN_TBLLOCUINTE_NRPERSOANE="NrPersoane";
	public static final String 	COLUMN_TBLLOCUINTE_EMAIL="Email";

	public static final String TABLE_CAMERE="tblCamere";
	public static final String 	COLUMN_TBLCAMERE_ID="_id";
	public static final String 	COLUMN_TBLCAMERE_IDLOCUINTA="id_Locuinta";
	public static final String 	COLUMN_TBLCAMERE_NUME="Nume";
	
	public static final String TABLE_CONSUM="tblConsum";
	public static final String 	COLUMN_TBLCONSUM_ID="_id";
	public static final String 	COLUMN_TBLCONSUM_IDCAMERA="id_Camera";
	public static final String 	COLUMN_TBLCONSUM_RECE="Rece";
	public static final String 	COLUMN_TBLCONSUM_CALDA="Calda";
	public static final String 	COLUMN_TBLCONSUM_DATA="Data";
	
	private static final String DATABASE_NAME="Apometre_Pro.db";
	private static final int DATABASE_VERSION = 1;
	
	private static Context context;

	
	// Database creation sql statement
	  private static final String DATABASE_CREATE_TBLLOCUINTE = "create table " + TABLE_LOCUINTE
			+ "(" + COLUMN_TBLLOCUINTE_ID + " integer primary key autoincrement, "
			+ COLUMN_TBLLOCUINTE_NUME + " text not null, "
			+ COLUMN_TBLLOCUINTE_APARTAMENT + " text, "
			+ COLUMN_TBLLOCUINTE_BLOC + " text, "
			+ COLUMN_TBLLOCUINTE_ETAJ + " text, "
			+ COLUMN_TBLLOCUINTE_SCARA + " text, "
			+ COLUMN_TBLLOCUINTE_NRPERSOANE + " text, "
			+ COLUMN_TBLLOCUINTE_EMAIL + " text)";

	  private static final String DATABASE_CREATE_TBLCAMERE = "create table "   + TABLE_CAMERE
			  + "(" + COLUMN_TBLCAMERE_ID + " integer primary key autoincrement, "
			  + COLUMN_TBLCAMERE_IDLOCUINTA + " integer not null, "
			  + COLUMN_TBLCAMERE_NUME  + " text not null)";
			  
	  private static final String DATABASE_CREATE_TBLCONSUM = "create table " + TABLE_CONSUM 
			  + "(" + COLUMN_TBLCONSUM_ID  + " integer primary key autoincrement, " 
			  + COLUMN_TBLCONSUM_IDCAMERA  + " integer, "
			  + COLUMN_TBLCONSUM_RECE + " real, "
			  + COLUMN_TBLCONSUM_CALDA + " real, "
			  + COLUMN_TBLCONSUM_DATA + " text)";
	  
	  public MySQLiteHelper(Context context)
	  {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);  
		this.context=context;
	  }
	  
		  
	  
	  
	@Override
	public void onCreate(SQLiteDatabase database)
	{
		try
		{
			database.execSQL(DATABASE_CREATE_TBLLOCUINTE);
			//insereaza Locuinta 1 default
			ContentValues valuesLocation = new ContentValues();
			valuesLocation.put(MySQLiteHelper.COLUMN_TBLLOCUINTE_NUME, context.getResources().getString(R.string.Locuinta));
			long insertIdLocuinta = database.insert(MySQLiteHelper.TABLE_LOCUINTE, null, valuesLocation);

			database.execSQL(DATABASE_CREATE_TBLCAMERE);
			//insereaza Bucatarie si Baie default
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.COLUMN_TBLCAMERE_IDLOCUINTA,insertIdLocuinta);
			values.put(MySQLiteHelper.COLUMN_TBLCAMERE_NUME, context.getResources().getString(R.string.Bucatarie));
			long insertId = database.insert(MySQLiteHelper.TABLE_CAMERE, null, values);

			ContentValues values2 = new ContentValues();
			values2.put(MySQLiteHelper.COLUMN_TBLCAMERE_IDLOCUINTA,insertIdLocuinta);
			values2.put(MySQLiteHelper.COLUMN_TBLCAMERE_NUME, context.getResources().getString(R.string.Baie));
			long insertId2 = database.insert(MySQLiteHelper.TABLE_CAMERE, null, values2);

			//////////////////////////////////////
			database.execSQL(DATABASE_CREATE_TBLCONSUM);
		} catch (Exception e)
		{
			// TODO: handle exception
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) 
	{
		Log.w(MySQLiteHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		
		String dropSqlString1 = "DROP TABLE IF EXISTS " + TABLE_CONSUM;
		String dropSqlString2 = "DROP TABLE IF EXISTS " + TABLE_CAMERE;
		String dropSqlString3 = "DROP TABLE IF EXISTS " + TABLE_LOCUINTE;
		//database.execSQL(dropSqlString1);
		//database.execSQL(dropSqlString2);
		//database.execSQL(dropSqlString3);
		onCreate(database);
	}

}
