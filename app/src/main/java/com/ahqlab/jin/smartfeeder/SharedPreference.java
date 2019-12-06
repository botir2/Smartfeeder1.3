package com.ahqlab.jin.smartfeeder;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference
{
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;

	private static final String CONTROL_SETTINGS = "control_settings";

	private static final String ID = "id";
	private static final String PW = "password";

	public SharedPreference(Context context)
	{
		pref = context.getSharedPreferences(CONTROL_SETTINGS, Context.MODE_PRIVATE);
		editor = pref.edit();
		commit();
	}

	public void commit()
	{
		editor.commit();
	}

	public String getIDString()
	{
		return pref.getString(ID, "");
	}

	public void setIDString( String value )
	{
		editor.putString(ID, value);
		commit();
	}

	public String getPWString()
	{
		return pref.getString(PW, "");
	}

	public void setPWString( String value )
	{
		editor.putString(PW, value);
		commit();
	}

}