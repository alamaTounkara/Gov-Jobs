package govjobs.govjob.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;

import govjobs.govjob.util.Constants;
import govjobs.govjob.util.EditTextErrorHandler;
import govjobs.govjob.util.Field;
import govjobs.govjob.util.Form;
import govjobs.govjob.util.FormUtils;
import govjobs.govjob.util.InRange;
import govjobs.govjob.R;


/**
 * Creating an App Widget Configuration Activity:
 * If you would like the user to configure settings when he or she adds a new App Widget, you can
 * create an App Widget configuration Activity. This Activity will be automatically launched by the
 * App Widget host and allows the user to configure available settings for the App Widget at create-time,
 * such as the App Widget color, size, update period or other functionality settings.
 * <p/>
 * The configuration Activity should be declared as a normal Activity in the Android manifest file.
 * However, it will be launched by the App Widget host with the ACTION_APPWIDGET_CONFIGURE action,
 * so the Activity needs to accept this Intent.
 * <p/>
 * two important things to remember when you implement the Activity:
 * <p/>
 * 1: The App Widget host calls the configuration Activity and the configuration Activity should always
 * return a result. The result should include the App Widget ID passed by the Intent that launched
 * the Activity (saved in the Intent extras as EXTRA_APPWIDGET_ID).
 * 2: The onUpdate() method will not be called when the App Widget is created (the system will not
 * send the ACTION_APPWIDGET_UPDATE broadcast when a configuration Activity is launched). It is the
 * responsibility of the configuration Activity to request an update from the AppWidgetManager when
 * the App Widget is first created. However, onUpdate() will be called for subsequent updates—it is
 * only skipped the first time.
 * <p/>
 * Here's a summary of the procedure to properly update the App Widget and close the configuration Activity:
 * A: First, get the App Widget ID from the Intent that launched the Activity.
 * B: Perform your App Widget configuration.
 * C: When the configuration is complete, get an instance of the AppWidgetManager by calling getInstance(Context).
 * D: Update the App Widget with a RemoteViews layout by calling updateAppWidget(int, RemoteViews).
 * E: Finally, create the return Intent, set it with the Activity result, and finish the Activity:
 */
public class WidgetConfig extends AppCompatActivity {

    //    private static final String PREFS_NAME = "govjobs.govjob.widget.GovJobWidget";
//    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final String LOG = "MyAppWidgetProvider";
    EditText mNumbJobConfigTxt;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    // Form used for validation
    private Form mForm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

        mNumbJobConfigTxt = (EditText) findViewById(R.id.numbJobConfigTxt);

        //A: First, get the App Widget ID from the Intent that launched the Activity.
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            mAppWidgetId = extra.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }


        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
// Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED);


        //B: Perform your App Widget configuration.

    }


    public void setUpWidget(View view) {
        initLoginValidationForm();//initializing form element
        FormUtils.hideKeyboard(this, mNumbJobConfigTxt);
        if (mForm.isValid()) {



            // C: When the configuration is complete, get an instance of the AppWidgetManager
            //by calling getInstance(Context).
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            //D: Update the App Widget with a RemoteViews layout by calling updateAppWidget(int, RemoteViews).
            RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.gov_job_widget);
            manager.updateAppWidget(mAppWidgetId, remoteViews);

            // E: Finally, create the return Intent, set it with the Activity result, and finish the Activity:
            /**this intent is essential to show the widget if this intent is not in*/
            Intent updateWidgetIntent = new Intent();
            updateWidgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    mAppWidgetId);

            //let save the number of job to display in the sharedpreference
            String numb_job = mNumbJobConfigTxt.getText().toString();
            saveInSharedprefences(numb_job);

            //start a service that will download our data from the net
            Intent serviceIntent = new Intent(this, FetchDataService.class);
            //serviceIntent.
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            //    serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            serviceIntent.putExtra(Constants.NUMBER_OF_JOB_ON_WIDGET,
                    Integer.parseInt(numb_job));
            startService(serviceIntent);

            //finish the activity
            setResult(RESULT_OK, updateWidgetIntent);

            finish();
        }



    }



    /**
     * save number of job to display on the widget in our sharepreferences
     *
     * @param number_job is the number of job user will choose to display on the widget
     */
    public void saveInSharedprefences(String number_job) {
        Constants  constants = new Constants(this);
        constants.EDITOR.putString(Constants.NUMBER_OF_JOB_ON_WIDGET, number_job);
        constants.EDITOR.commit();
    }



    private void initLoginValidationForm() {
        mForm = Form.create();
        mForm.addField(Field.using(mNumbJobConfigTxt).validate(InRange.build(0, 120)));
        mForm.errorHandler(new EditTextErrorHandler());
    }

}
