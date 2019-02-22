package com.gino.citireapometre_pro;



import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity
{
	private TextView txtAppInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		txtAppInfo = (TextView)findViewById(R.id.txtAppInfo);
		StringBuilder htmlInfo = new StringBuilder();
        htmlInfo.append("<br /><br />");
        htmlInfo.append("<b>"+this.getResources().getString(R.string.DezvoltataDe)+"</b> Gino Oprea<br /><br />");
        htmlInfo.append("<b>"+this.getResources().getString(R.string.E_mail)+"</b> gino.oprea24@gmail.com<br /><br />");
        htmlInfo.append("<b>"+this.getResources().getString(R.string.Telefon)+"</b>+40 0723676067<br />");              
        txtAppInfo.setText(Html.fromHtml(htmlInfo.toString()));  
	}
	
}
