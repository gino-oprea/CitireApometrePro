package com.gino.citireapometre_pro;

import com.gino.citireapometre_pro.DB.Locuinta;

public interface FragmentLifecycle
{	
	public void onResumeFragment(Locuinta location);
	public void onLocationChange(Locuinta location);
}
