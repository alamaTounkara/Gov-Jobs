package govjobs.govjob.widget;

import android.app.Activity;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import govjobs.govjob.CallBackInterface;
import govjobs.govjob.Constants;
import govjobs.govjob.NonUIFragmentForTask;
import govjobs.govjob.Task;

public class FetchDataService extends Service implements CallBackInterface {


    private static final String USA_JOB_BASE_URL = "https://data.usajobs.gov/api/jobs?NumberOfJobs=";
    private static final String LOG = "MyAppWidgetProvider";
    public static ArrayList<HashMap<String, String>> mResult = new ArrayList<HashMap<String, String>>();
    public Task mMyTask;
    private Activity mContext;
    private NonUIFragmentForTask mNonUITaskFragment;
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private int mNumberOfJobToFetch;//default

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
            try {
                Log.d(LOG, "AppWidgetManager.EXTRA_APPWIDGET_ID: " + intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID));
                mAppWidgetId = intent.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
                if(mAppWidgetId!=0)

                mNumberOfJobToFetch = intent.getIntExtra(Constants.NUMBER_OF_JOB_ON_WIDGET, 100);
            } catch (Exception e) {
                Log.d(LOG, "" + e);
            }

            manageSearch();//fetch data from our URL
        }


        return super.onStartCommand(intent, flags, startId);
    }


    //helper method for job search
    private void manageSearch() {
        Log.d(LOG, "USA_JOB_BASE_URL + mNumberOfJobToFetch: " + USA_JOB_BASE_URL + mNumberOfJobToFetch);
        startTaskOnResume(USA_JOB_BASE_URL + mNumberOfJobToFetch);// we defined startTask() in mNonUITaskFragment
        populateWidget();
    }


    public void startTaskOnResume(String url) {
        /*this is used because of shared preferences.
        /*by using startTask() alone, after editing the sharepreferences, the activiy will not get
         * updating. becaus mMytask will just be cancelled.
         *
         * Now with this new method, onResume in activity will used a method that will call this
         * method to cancel the current task and run a new one.
         */

        if (mMyTask != null) {// cancel the task, even if its runnig
            mMyTask.cancel(true);
            Log.d("mMyTask", "startTaskOnResume-mMyTask: cancel");

        }
        mMyTask = new Task(this);
        mMyTask.execute(url);
        Log.d("ch", "startTask()");
        Log.d("mMyTask", "startTaskOnResume-mMyTask: new task");

    }


    //send a broadcast, which will be caught by our appWidgetProvider
    public void populateWidget() {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_SERVICE_FINISH_FECTH);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        sendBroadcast(intent);
        this.stopSelf();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onPostExecute(ArrayList<HashMap<String, String>> result) {
        mResult = result;
    }


}
