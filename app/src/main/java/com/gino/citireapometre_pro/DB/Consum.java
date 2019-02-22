package com.gino.citireapometre_pro.DB;

public class Consum
{
	private long id;
	private long id_camera;
	private String nume_camera;
	private Float consum_rece;
	private Float consum_calda;
	private String data;
	
	public long getId()
	{
		return id;
	}
	public void setId(long id)
	{
		this.id = id;
	}
	public long getIdCamera()
	{
		return id_camera;
	}
	public void setIdCamera(long id_camera)
	{
		this.id_camera = id_camera;
	}
	public String getNumeCamera()
	{
		return nume_camera;
	}
	public void setNumeCamera(String nume_camera)
	{
		this.nume_camera = nume_camera;
	}
	public Float getConsumRece()
	{
		return consum_rece;
	}
	public void setConsumRece(Float consumRece)
	{
		this.consum_rece = consumRece;
	}
	public Float getConsumCalda()
	{
		return consum_calda;
	}
	public void setConsumCalda(Float consumCalda)
	{
		this.consum_calda = consumCalda;
	}
	public String getData()
	{
		return data;
	}
	public void setData(String data)
	{
		this.data = data;
	}
}
