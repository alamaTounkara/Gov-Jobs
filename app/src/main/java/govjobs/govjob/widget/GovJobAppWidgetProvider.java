package govjobs.govjob.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import govjobs.govjob.Constants;
import govjobs.govjob.JobList;
import govjobs.govjob.R;

/**
 * Created by alamatounkara on 8/9/15.
 */
public class GovJobAppWidgetProvider extends AppWidgetProvider {
    private static final String LOG = " MyAppWidgetProvider";

    /**
     * onEnabled(): An instance of AlarmManager is created here to start the repeating timer
     * and register the intent with the AlarmManager.  As this method gets called at the very
     * first instance of widget installation, it helps to set repeating alarm  only once.
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,FetchDataService.class );
       // intent.setAction(Constants.ACTION_SERVICE_FINISH_FECTH);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC,System.currentTimeMillis(), 1000*3,pendingIntent);
    }

    /**
     * Cancel the alarm when the last instance of the widget is removed
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        Intent intent = new Intent(context,  GovJobAppWidgetProvider.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        super.onDisabled(context);
    }


    /**
     * FetchDataService fetches data,and send broadcast to GobJobAppWidgetProvider, this
     * broadcast will be received by WidgetProvider onReceive which in turn
     * updates the widget
     */
    private PendingIntent service = null;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        int mCount = appWidgetIds.length;
        for (int i = 0; i < mCount; i++) {



            //call the service on every millisecond (30min-set in our appwidgetifo xml file) to
            //fetch new data and  broad cast it to the widget, which will receive it onReceive()
            Intent serviceIntent = new Intent(context.getApplicationContext(), FetchDataService.class);
            //let send the intent with appWidget ID
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.EXTRA_APPWIDGET_ID);
            context.startService(serviceIntent);
            Log.d(LOG, "onUpdate() called");


//            RemoteViews remoteViews = updateWidgetListView(context, appWidgetIds[i]);
//            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        //getting the layout of our widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.gov_job_widget);
        //send an intent to the CollectionService in order to provide us the adapter for the listview
        Intent upadateIntent = new Intent(context.getApplicationContext(), CollectionService.class);
        //let send the intent with appWidget ID
        upadateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.EXTRA_APPWIDGET_ID);
        //setting a Uri for this Intent
        upadateIntent.setData(Uri.parse(upadateIntent.toUri(Intent.URI_INTENT_SCHEME)));
        //setting up the adapter for the listview
        remoteViews.setRemoteAdapter(R.id.widget_listView, upadateIntent);

        //display something else in case the lisview is empty
        remoteViews.setEmptyView(R.id.widget_listView, R.id.empty_view);



        /************Start the activity with an intent when the search icon on the widget is clicked*****/
        Intent activityIntent= new Intent(context, JobList.class);
        PendingIntent pendingIntent =PendingIntent.getActivity(context,0,activityIntent,0);
        remoteViews.setOnClickPendingIntent(R.id.wigetSearchImageBtn,pendingIntent);
        /************Start the activity with an intent when the search icon on the widget is clicked*****/

        return remoteViews;
    }


    /**
     * The FetchData service fetches data and send broadcast after finish.
     * This will receive the broadcast sent by our FetchData service as set in our Manifest.xml file.
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent != null) {
            Log.d(LOG, "onReceive() method was called with intent action: " + intent.getAction());
            if (intent.getAction().equals(Constants.ACTION_SERVICE_FINISH_FECTH)) {
                int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                //UPDATE WIDGET
                RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);

                manager.updateAppWidget(appWidgetId, remoteViews);

            }
        }
    }

}
