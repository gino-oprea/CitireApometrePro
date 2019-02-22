package com.gino.citireapometre_pro.DB;



public class Persoana
{
	private long id;
	private String nume;
	private String bloc;
	private String scara;
	private String etaj;
	private long id_locuinta;
	private String nrPersoane;
	private String email;
	
	public long getId()
	{
		return id;
	}
	public void setId(long id)
	{
		this.id = id;
	}
	public String getName()
	{
		return nume;
	}
	public void setName(String nume)
	{
		this.nume = nume;
	}
	public String getBloc()
	{
		return bloc;
	}
	public void setBloc(String bloc)
	{
		this.bloc = bloc;
	}
	public String getScara()
	{
		return scara;
	}
	public void setScara(String scara)
	{
		this.scara = scara;
	}
	public String getEtaj()
	{
		return etaj;
	}
	public void setEtaj(String etaj)
	{
		this.etaj = etaj;
	}
	public long getIdLocuinta()
	{
		return id_locuinta;
	}
	public void setIdLocuinta(long id_locuinta)
	{
		this.id_locuinta = id_locuinta;
	}
	public String getNrPersoane()
	{
		return nrPersoane;
	}
	public void setNrPersoane(String nrPersoane)
	{
		this.nrPersoane = nrPersoane;
	}
	public String getEmail()
	{
		return email;
	}
	public void setEmail(String email)
	{
		this.email = email;
	}
}
