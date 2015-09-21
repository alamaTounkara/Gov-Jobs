package govjobs.govjob.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import govjobs.govjob.R;

public class Prefs extends PreferenceActivity {
    /*
    * The preferences portion of an application should be ran as a separate Activity that extends
     * the PreferenceActivity class. In the PreferenceActivity, a PreferenceScreen object should be
     * the root element of the layout. The PreferenceScreen contains Preference elements such as a
     * CheckBoxPreference, EditTextPreference, ListPreference, PreferenceCategory, or RingtonePreference.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PF()).commit();


    }

     public static class PF extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);

        }
    }

}