package com.gino.citireapometre_pro.DB;

public class Camera 
{
	private long id;
	private long id_locuinta;
	private String name;
	
	public long getId()
	{
		return id;
	}
	public void setId(long id)
	{
		this.id = id;
	}

	public long getIdLocation(){ return id_locuinta; }
	public void setIdLocation(long id_locuinta){ this.id_locuinta = id_locuinta; }
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
