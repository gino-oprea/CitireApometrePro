package com.gino.citireapometre_pro.DB;

public class Locuinta
{
    private long id;
    private String name;
    private String bloc = "";
    private String scara = "";
    private String etaj = "";
    private String apartament = "";
    private String nrPersoane = "";
    private String email = "";

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
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getBloc()
    {
        return bloc != null ? bloc : "";
    }

    public void setBloc(String bloc)
    {
        this.bloc = bloc;
    }

    public String getScara()
    {
        return scara != null ? scara : "";
    }

    public void setScara(String scara)
    {
        this.scara = scara;
    }

    public String getEtaj()
    {
        return etaj != null ? etaj : "";
    }

    public void setEtaj(String etaj)
    {
        this.etaj = etaj;
    }

    public String getApartament()
    {
        return apartament != null ? apartament : "";
    }

    public void setApartament(String apartament)
    {
        this.apartament = apartament;
    }

    public String getNrPersoane()
    {
        return nrPersoane != null ? nrPersoane : "";
    }

    public void setNrPersoane(String nrPersoane)
    {
        this.nrPersoane = nrPersoane;
    }

    public String getEmail()
    {
        return email != null ? email : "";
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
