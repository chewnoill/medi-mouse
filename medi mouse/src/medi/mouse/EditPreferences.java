package medi.mouse;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class EditPreferences extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.get_auth);
		
		PreferenceManager.setDefaultValues(this,R.xml.prefs, false);
		addPreferencesFromResource(R.xml.prefs);
		
	}

}


