package com.gino.citireapometre_pro.DB;

import java.util.List;

public class ConsumListHelper
{
	long idLocuinta;
	private List<Consum> listConsum;
	private String date;

	public long getIdLocuinta()
	{
		return idLocuinta;
	}

	public void setIdLocuinta(long idLocuinta)
	{
		this.idLocuinta = idLocuinta;
	}

	public String getData()
	{
		return date;
	}

	public void setData(String date)
	{
		this.date = date;
	}

	public List<Consum> getListConsum()
	{
		return listConsum;
	}

	public void setListConsum(List<Consum> listConsum)
	{
		this.listConsum = listConsum;
	}
}
