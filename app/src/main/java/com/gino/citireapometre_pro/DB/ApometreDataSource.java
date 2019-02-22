package com.gino.citireapometre_pro.DB;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class ApometreDataSource
{
	private SQLiteDatabase	database;
	private MySQLiteHelper dbHelper;
	private String[] ColumnsLocuinte = {MySQLiteHelper.COLUMN_TBLLOCUINTE_ID,
			MySQLiteHelper.COLUMN_TBLLOCUINTE_NUME,
			MySQLiteHelper.COLUMN_TBLLOCUINTE_APARTAMENT,
			MySQLiteHelper.COLUMN_TBLLOCUINTE_BLOC,
			MySQLiteHelper.COLUMN_TBLLOCUINTE_SCARA,
			MySQLiteHelper.COLUMN_TBLLOCUINTE_ETAJ,
			MySQLiteHelper.COLUMN_TBLLOCUINTE_NRPERSOANE,
			MySQLiteHelper.COLUMN_TBLLOCUINTE_EMAIL};
	private String[] ColumnsCamere={MySQLiteHelper.COLUMN_TBLCAMERE_ID,
			MySQLiteHelper.COLUMN_TBLCAMERE_IDLOCUINTA,
			MySQLiteHelper.COLUMN_TBLCAMERE_NUME};
	private String[] ColumnsConsum={MySQLiteHelper.COLUMN_TBLCONSUM_ID,
			MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA,
			MySQLiteHelper.COLUMN_TBLCONSUM_RECE,
			MySQLiteHelper.COLUMN_TBLCONSUM_CALDA,
			MySQLiteHelper.COLUMN_TBLCONSUM_DATA};
	
	public ApometreDataSource(Context context)
	{
		dbHelper=new MySQLiteHelper(context);
	}
	
	public void open() throws SQLException 
	{
	    database = dbHelper.getWritableDatabase();
	    database.disableWriteAheadLogging();
	}

	public void close() 
	{
	  dbHelper.close();
	}

	public List<Locuinta> getAllLocuinte()
	{
		List<Locuinta> locuinte = new ArrayList<Locuinta>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCUINTE, ColumnsLocuinte, null, null, null, null, MySQLiteHelper.COLUMN_TBLLOCUINTE_NUME + " ASC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			Locuinta locuinta = cursorToLocuinta(cursor);
			locuinte.add(locuinta);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return locuinte;
	}

	public Locuinta createLocuinta(Locuinta locuinta)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_TBLLOCUINTE_NUME, locuinta.getName());
		values.put(MySQLiteHelper.COLUMN_TBLLOCUINTE_APARTAMENT,locuinta.getApartament());
		values.put(MySQLiteHelper.COLUMN_TBLLOCUINTE_BLOC, locuinta.getBloc());
		values.put(MySQLiteHelper.COLUMN_TBLLOCUINTE_ETAJ, locuinta.getEtaj());
		values.put(MySQLiteHelper.COLUMN_TBLLOCUINTE_SCARA, locuinta.getScara());
		values.put(MySQLiteHelper.COLUMN_TBLLOCUINTE_NRPERSOANE, locuinta.getNrPersoane());
		values.put(MySQLiteHelper.COLUMN_TBLLOCUINTE_EMAIL, locuinta.getEmail());

		long insertId = database.insert(MySQLiteHelper.TABLE_LOCUINTE, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCUINTE, ColumnsLocuinte, MySQLiteHelper.COLUMN_TBLLOCUINTE_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Locuinta newLocuinta = cursorToLocuinta(cursor);
		cursor.close();
		return newLocuinta;
	}

	public void updateLocuinta(Locuinta locuinta)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_TBLLOCUINTE_NUME, locuinta.getName());
		values.put(MySQLiteHelper.COLUMN_TBLLOCUINTE_APARTAMENT,locuinta.getApartament());
		values.put(MySQLiteHelper.COLUMN_TBLLOCUINTE_BLOC, locuinta.getBloc());
		values.put(MySQLiteHelper.COLUMN_TBLLOCUINTE_ETAJ, locuinta.getEtaj());
		values.put(MySQLiteHelper.COLUMN_TBLLOCUINTE_SCARA, locuinta.getScara());
		values.put(MySQLiteHelper.COLUMN_TBLLOCUINTE_NRPERSOANE, locuinta.getNrPersoane());
		values.put(MySQLiteHelper.COLUMN_TBLLOCUINTE_EMAIL, locuinta.getEmail());

		int rowsUpdated = database.update(MySQLiteHelper.TABLE_LOCUINTE, values,
				MySQLiteHelper.COLUMN_TBLLOCUINTE_ID + " = " + Long.toString(locuinta.getId())
				, null);
	}

	public void deleteLocuinta(Locuinta locuinta)
	{
		long id = locuinta.getId();
		String nume = locuinta.getName();
		System.out.println("Locuinta " + nume+ " a fost stearsa");

		List<Camera> camere = getAllCamere(id);
		//List<String> idCamere = camere.stream().map(c->Long.toString(c.getId())).collect(Collectors.toList());//lambda incompatibil cu android 6
		List<String> idCamere=new ArrayList<String>();
		for(int i = 0; i<camere.size(); i++)
		{
			idCamere.add(Long.toString(camere.get(i).getId()));
		}

		database.delete(MySQLiteHelper.TABLE_CONSUM, MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + " IN ("+ TextUtils.join(",", idCamere)+")", null);
		database.delete(MySQLiteHelper.TABLE_CAMERE, MySQLiteHelper.COLUMN_TBLCAMERE_ID + " IN ("+ TextUtils.join(",", idCamere)+")", null);
		database.delete(MySQLiteHelper.TABLE_LOCUINTE, MySQLiteHelper.COLUMN_TBLLOCUINTE_ID + " = " + id, null);
	}


	public Camera createCamera(long idLocuinta,String nume)
	{
	    ContentValues values = new ContentValues();
	    values.put(MySQLiteHelper.COLUMN_TBLCAMERE_IDLOCUINTA, idLocuinta);
	    values.put(MySQLiteHelper.COLUMN_TBLCAMERE_NUME, nume);
	    long insertId = database.insert(MySQLiteHelper.TABLE_CAMERE, null, values);
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_CAMERE, ColumnsCamere, MySQLiteHelper.COLUMN_TBLCAMERE_ID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    Camera newCamera = cursorToCamera(cursor);
	    cursor.close();
	    return newCamera;
	 }
	public void updateCamera(Camera camera)
	{
		ContentValues values = new ContentValues();			
		values.put(MySQLiteHelper.COLUMN_TBLCAMERE_NUME, camera.getName());
		
		int rowsUpdated = database.update(MySQLiteHelper.TABLE_CAMERE, values, 
				MySQLiteHelper.COLUMN_TBLCAMERE_ID + " = " + Long.toString(camera.getId())
				, null);
	}
	public void deleteCamera(Camera camera) 
	{
	    long id = camera.getId();
	    String nume = camera.getName();
	    System.out.println("Camera " + nume+ " a fost stearsa");
	    database.delete(MySQLiteHelper.TABLE_CONSUM, MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + " = " + id, null);
	    database.delete(MySQLiteHelper.TABLE_CAMERE, MySQLiteHelper.COLUMN_TBLCAMERE_ID + " = " + id, null);	    
	}
	
	public List<Camera> getAllCamere(long idLocuinta)
	{
	    List<Camera> camere = new ArrayList<Camera>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_CAMERE, ColumnsCamere, MySQLiteHelper.COLUMN_TBLCAMERE_IDLOCUINTA + " = " + idLocuinta, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) 
	    {
	    	Camera camera = cursorToCamera(cursor);
	      camere.add(camera);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return camere;
	}

	private Locuinta cursorToLocuinta(Cursor cursor)
	{
		Locuinta locuinta=new Locuinta();
		locuinta.setId(cursor.getLong(0));
		locuinta.setName(cursor.getString(1));
		locuinta.setApartament(cursor.getString(2));
		locuinta.setBloc(cursor.getString(3));
		locuinta.setScara(cursor.getString(4));//nu stiu de ce indexul de coloana e asa dar altfel se inverseaza scara cu etajul!!!!
		locuinta.setEtaj(cursor.getString(5));
		locuinta.setNrPersoane(cursor.getString(6));
		locuinta.setEmail(cursor.getString(7));
		return  locuinta;
	}
	
	private Camera cursorToCamera(Cursor cursor) 
	{
		Camera camera = new Camera();
		camera.setId(cursor.getLong(0));
		camera.setIdLocation(cursor.getLong(1));
		camera.setName(cursor.getString(2));
	    return camera;
	}


	///asta se va apela pt fiecare camera in parte
	public void createConsum(long idCamera,Float consumCalda, Float consumRece, String data)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA, idCamera);
		values.put(MySQLiteHelper.COLUMN_TBLCONSUM_CALDA, consumCalda);
		values.put(MySQLiteHelper.COLUMN_TBLCONSUM_RECE, consumRece);
		values.put(MySQLiteHelper.COLUMN_TBLCONSUM_DATA, data);
		
		//List<Consum> list = getAllConsum();
		String sql="SELECT *  FROM " + MySQLiteHelper.TABLE_CONSUM +  
	    		" WHERE " + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + " = '" + data + "'" +
	    		" AND " + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + " = " + Long.toString(idCamera) +
	    		" ORDER BY " + MySQLiteHelper.COLUMN_TBLCONSUM_DATA;
		Cursor cursor=database.rawQuery(sql, null);
		
		///trebuie verificat daca mai exista inregistrat consum pt camera respectiva in aceeasi data si daca da, sa se faca update
//		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONSUM, ColumnsConsum, 
//				MySQLiteHelper.COLUMN_TBLCONSUM_DATA + " = " + data + " AND " + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + " = " + Long.toString(idCamera), 
//				null, null, null, null);
		
		if(cursor.getCount() == 0)
		{
			long insertId=database.insert(MySQLiteHelper.TABLE_CONSUM, null, values);
			
			String sqlQuery="SELECT " +
		    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_ID + "," +
		    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + "," +
		    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_RECE + "," +
		    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_CALDA + "," +
		    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + "," +
		    		MySQLiteHelper.TABLE_CAMERE + "." + MySQLiteHelper.COLUMN_TBLCAMERE_NUME +
		    		
	 	    		" FROM " + MySQLiteHelper.TABLE_CONSUM + " INNER JOIN " + MySQLiteHelper.TABLE_CAMERE + 
		    		" ON " + MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + " = "
		    		+ MySQLiteHelper.TABLE_CAMERE + "." + MySQLiteHelper.COLUMN_TBLCAMERE_ID + 
		    		" WHERE " + MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_ID + " = " + Long.toString(insertId) +
		    		" ORDER BY " + MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_DATA;
			//cursor=database.query(MySQLiteHelper.TABLE_CONSUM, ColumnsConsum, MySQLiteHelper.COLUMN_TBLCONSUM_ID + " = " + Long.toString(insertId), null, null, null, null);
			cursor = database.rawQuery(sqlQuery, null);		
			cursor.moveToFirst();
			
			Consum consum = cursorToConsum(cursor);
		}
		else 
		{
			values = new ContentValues();			
			values.put(MySQLiteHelper.COLUMN_TBLCONSUM_CALDA, consumCalda);
			values.put(MySQLiteHelper.COLUMN_TBLCONSUM_RECE, consumRece);			
			int rowsUpdated = database.update(MySQLiteHelper.TABLE_CONSUM, values, 
					MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + " = " + Long.toString(idCamera) + " AND " + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + " = '" + data + "'"
					, null);		
		}		
		cursor.close();		
	}
	//asta se apeleaza la edit
	public void updateConsum (long idConsum, String consumCalda, String consumRece) 
	{
		ContentValues values = new ContentValues();		
		values.put(MySQLiteHelper.COLUMN_TBLCONSUM_CALDA, consumCalda);
		values.put(MySQLiteHelper.COLUMN_TBLCONSUM_RECE, consumRece);			
		
		database.update(MySQLiteHelper.TABLE_CONSUM, values, MySQLiteHelper.COLUMN_TBLCONSUM_ID + " = " + Long.toString(idConsum), null);		
	}
	//se sterg toate consumurile din data respectiva
	public void deleteConsum(long idLocuinta,String data)
	{
		List<Camera> camere = getAllCamere(idLocuinta);
		//List<String> idCamere = camere.stream().map(c->Long.toString(c.getId())).collect(Collectors.toList());
		List<String> idCamere=new ArrayList<String>();
		for(int i = 0; i<camere.size(); i++)
		{
			idCamere.add(Long.toString(camere.get(i).getId()));
		}

	   int x = database.delete(MySQLiteHelper.TABLE_CONSUM, MySQLiteHelper.COLUMN_TBLCONSUM_DATA + " = '" + data + "' AND "
			   + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + " in ("+ TextUtils.join(",", idCamere)+")", null);
	   int test=x;
	}
	public List<Consum> getAllConsum(long idLocuinta)
	{
	    List<Consum> consumuri = new ArrayList<Consum>();

		List<Camera> camere = getAllCamere(idLocuinta);
		//List<String> idCamere = camere.stream().map(c->Long.toString(c.getId())).collect(Collectors.toList());
		List<String> idCamere=new ArrayList<String>();
		for(int i = 0; i<camere.size(); i++)
		{
			idCamere.add(Long.toString(camere.get(i).getId()));
		}

		String sqlQuery="SELECT " +
	    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_ID + "," +
	    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + "," +
	    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_RECE + "," +
	    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_CALDA + "," +
	    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + "," +
	    		MySQLiteHelper.TABLE_CAMERE + "." + MySQLiteHelper.COLUMN_TBLCAMERE_NUME +
	    		
 	    		" FROM " + MySQLiteHelper.TABLE_CONSUM + " INNER JOIN " + MySQLiteHelper.TABLE_CAMERE + 
	    		" ON " + MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + " = "
	    		+ MySQLiteHelper.TABLE_CAMERE + "." + MySQLiteHelper.COLUMN_TBLCAMERE_ID +
				" WHERE " + MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + " IN ("+ TextUtils.join(",", idCamere)+")" +
	    		" GROUP BY " + MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_DATA;
	    
	    
	    //Cursor cursor = database.query(MySQLiteHelper.TABLE_CONSUM, ColumnsConsum, null, null, MySQLiteHelper.COLUMN_TBLCONSUM_DATA, null, null);
	    Cursor cursor = database.rawQuery(sqlQuery, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) 
	    {
	      Consum consum = cursorToConsum(cursor);
	      consumuri.add(consum);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return consumuri;
	}
	public List<Consum> getConsum(long idLocuinta, String data)
	{
	    List<Consum> consumuri = new ArrayList<Consum>();

		List<Camera> camere = getAllCamere(idLocuinta);
		//List<String> idCamere = camere.stream().map(c->Long.toString(c.getId())).collect(Collectors.toList());
		List<String> idCamere=new ArrayList<String>();
		for(int i = 0; i<camere.size(); i++)
		{
			idCamere.add(Long.toString(camere.get(i).getId()));
		}

	    String sqlQuery="SELECT " +
	    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_ID + "," +
	    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + "," +
	    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_RECE + "," +
	    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_CALDA + "," +
	    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + "," +
	    		MySQLiteHelper.TABLE_CAMERE + "." + MySQLiteHelper.COLUMN_TBLCAMERE_NUME +
	    		
 	    		" FROM " + MySQLiteHelper.TABLE_CONSUM + " INNER JOIN " + MySQLiteHelper.TABLE_CAMERE + 
	    		" ON " + MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + " = "
	    		+ MySQLiteHelper.TABLE_CAMERE + "." + MySQLiteHelper.COLUMN_TBLCAMERE_ID + 
	    		" WHERE " + MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + " = '" + data + "'"+
				" AND " + MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + " IN ("+ TextUtils.join(",", idCamere)+")";
	    
	    
	    //Cursor cursor = database.query(MySQLiteHelper.TABLE_CONSUM, ColumnsConsum, null, null, MySQLiteHelper.COLUMN_TBLCONSUM_DATA, null, null);
	    Cursor cursor = database.rawQuery(sqlQuery, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) 
	    {
	      Consum consum = cursorToConsum(cursor);
	      consumuri.add(consum);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return consumuri;
	}
	public List<ConsumListHelper> getConsumListHelper(long idLocuinta ,String Tip)
	{
		List<Camera> camere = getAllCamere(idLocuinta);
		//List<String> idCamere = camere.stream().map(c->Long.toString(c.getId())).collect(Collectors.toList());
		List<String> idCamere=new ArrayList<String>();
		for(int i = 0; i<camere.size(); i++)
		{
			idCamere.add(Long.toString(camere.get(i).getId()));
		}

	    List<ConsumListHelper> clhList = new ArrayList<ConsumListHelper>();

	    List<String> DateUnice=new ArrayList<String>();
//	    String sqlQuery1="SELECT DISTINCT " +
//	    		MySQLiteHelper.COLUMN_TBLCONSUM_DATA + 	    		
// 	    		" FROM " + MySQLiteHelper.TABLE_CONSUM +
// 	    		" ORDER BY " + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + " ASC";
	    
	    String sqlQuery1="";
	    if(Tip.equals("Lista"))
		{
			sqlQuery1 = "SELECT DISTINCT " +
					MySQLiteHelper.COLUMN_TBLCONSUM_DATA +
					" FROM " + MySQLiteHelper.TABLE_CONSUM +
					" WHERE " + MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + " IN (" + TextUtils.join(",", idCamere) + ")" +
					" ORDER BY substr(" + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + ", 7, 4) || " +
					"substr(" + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + ", 4, 2) || " +
					"substr(" + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + ", 1, 2) DESC";
		}
	    if(Tip.equals("Grafic"))
	    {
	    	String subSelect="SELECT DISTINCT " + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + 	    		
	 	    		" FROM " + MySQLiteHelper.TABLE_CONSUM +
					" WHERE " + MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + " IN (" + TextUtils.join(",", idCamere) + ")" +
	 	    		" ORDER BY substr(" + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + ", 7, 4) || " +
	 	    		"substr(" + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + ", 4, 2) || " +
	 	    		"substr(" + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + ", 1, 2) DESC LIMIT 13";
	    	
	    	sqlQuery1="SELECT DISTINCT " +
		    		MySQLiteHelper.COLUMN_TBLCONSUM_DATA + 	    		
	 	    		" FROM " + //MySQLiteHelper.TABLE_CONSUM +
	 	    		"(" + subSelect + ")" +
	 	    		" ORDER BY substr(" + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + ", 7, 4)," +
	 	    		"substr(" + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + ", 4, 2)," +
	 	    		"substr(" + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + ", 1, 2) ASC";
	    }
	    
	    ////toate datele unice
	    Cursor cursor = database.rawQuery(sqlQuery1, null);
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) 
	    {	      
	      DateUnice.add(cursor.getString(0));
	      cursor.moveToNext();
	    }
	    
	    for (int i = 0; i < DateUnice.size(); i++)
        {
	        String data = DateUnice.get(i);
	        List<Consum> consumuri = new ArrayList<Consum>();
	        
	        String sqlQuery2="SELECT " +
		    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_ID + "," +
		    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + "," +
		    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_RECE + "," +
		    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_CALDA + "," +
		    		MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + "," +
		    		MySQLiteHelper.TABLE_CAMERE + "." + MySQLiteHelper.COLUMN_TBLCAMERE_NUME +
		    		
	 	    		" FROM " + MySQLiteHelper.TABLE_CONSUM + " INNER JOIN " + MySQLiteHelper.TABLE_CAMERE + 
		    		" ON " + MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + " = "
		    		+ MySQLiteHelper.TABLE_CAMERE + "." + MySQLiteHelper.COLUMN_TBLCAMERE_ID +
		    		" WHERE " + MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_DATA + " = '" + data + "'" +
					" AND " + MySQLiteHelper.TABLE_CONSUM + "." + MySQLiteHelper.COLUMN_TBLCONSUM_IDCAMERA + " IN ("+ TextUtils.join(",", idCamere)+")";;
	        
	        cursor = database.rawQuery(sqlQuery2, null);
		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) 
		    {
		      Consum consum = cursorToConsum(cursor);		      
		      consumuri.add(consum);
		      cursor.moveToNext();
		    }
		    ConsumListHelper clh = new ConsumListHelper();
		    clh.setIdLocuinta(idLocuinta);
		    clh.setData(data);
		    clh.setListConsum(consumuri);	
		    clhList.add(clh);
        }   
	    return clhList;
	}
		
	private Consum cursorToConsum(Cursor cursor)
	{
		Consum consum = new Consum();
		consum.setId(cursor.getLong(0));
		consum.setIdCamera(cursor.getLong(1));
		consum.setConsumRece(cursor.getFloat(2));
		consum.setConsumCalda(cursor.getFloat(3));
		consum.setData(cursor.getString(4));
		consum.setNumeCamera(cursor.getString(5));
		
		return consum;
	}
}

