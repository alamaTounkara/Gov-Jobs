package govjobs.govjob.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import govjobs.govjob.util.Constants;
import govjobs.govjob.ui.JobDetailsActivity;
import govjobs.govjob.ui.JobList;
import govjobs.govjob.R;

/**
 * Created by alamatounkara on 8/9/15.
 */
public class GovJobAppWidgetProvider extends AppWidgetProvider {
    private static final String LOG = "MyAppWidgetProvider";
    /**
     * onEnabled(): An instance of AlarmManager is created here to start the repeating timer
     * and register the intent with the AlarmManager.  As this method gets called at the very
     * first instance of widget installation, it helps to set repeating alarm  only once.
     *
     * @param context
     */
    int count;

    /**
     * FetchDataService fetches data,and send broadcast to GobJobAppWidgetProvider, this
     * broadcast will be received by WidgetProvider onReceive which in turn
     * updates the widget
     */
    // private PendingIntent service = null;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        int mCount = appWidgetIds.length;
        if(mCount>0) {
            //start a service that will download our data from the net
            Intent serviceIntent = new Intent(context, FetchDataService.class);
            serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //serviceIntent.setData(Uri.parse("uri::govjobs.govjob.widgetService"));

            //serviceIntent.
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[0]);

            //    serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            serviceIntent.putExtra(Constants.NUMBER_OF_JOB_ON_WIDGET, Utility.getNumberOfJobToDisplay(context));
            context.startService(serviceIntent);
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
        Intent activityIntent = new Intent(context, JobList.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.wigetSearchImageBtn, pendingIntent);
        /************Start the activity with an intent when the search icon on the widget is clicked*****/


        /************START JOB DESCRIPTION ACTTIVITY WHEN A PARTICULAR LISTVIEW ITEM IS CLICKED*****/
        // This section makes it possible for items to have individualized behavior.
        // It does this by setting up a pending intent template. Individuals items of a collection
        // cannot set up their own pending intents. Instead, the collection as a whole sets
        // up a pending intent template, and the individual items set a fillInIntent
        // to create unique behavior on an item-by-item basis.
        Intent itemInWidgetIntent = new Intent(context, JobDetailsActivity.class);

        // Set the action for the intent.
        // When the user touches a particular view, it will have the effect of
        // STARTING A JobDetailsActivity activity, ACTION_INDIVIDUAL_ITEM_IN_WIDGET.
        itemInWidgetIntent.setAction(Constants.ACTION_INDIVIDUAL_ITEM_IN_WIDGET);
        itemInWidgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        upadateIntent.setData(Uri.parse(upadateIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent itemInWidgetPendingIntent = PendingIntent.getActivity(context, 0, itemInWidgetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.widget_listView, itemInWidgetPendingIntent);
        /************END START JOB DESCRIPTION ACTTIVITY WHEN A PARTICULAR LISTVIEW ITEM IS CLICKED*****/

        return remoteViews;
    }


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        /**
         * creating inexact repeating alarms using AlarmManager.
         * Also we need to reschedule the alarms since they are lost on reboot. So we create a
         * broadcast receiver for boot completed and register it in the manifest.
         */
        count = 0;
        Utility.updateWithAlarmManager(context);
    }

    /**
     * Cancel the alarm when the last instance of the widget is removed
     *
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Utility.cancelAlarmManager(context);
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
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        if (intent != null) {
            if (intent.getAction().equals(Constants.ACTION_SERVICE_FINISH_FECTH)) {
                ComponentName componentName = new ComponentName(context, GovJobAppWidgetProvider.class);
                int[] widgetIds = manager.getAppWidgetIds(componentName);
                for (int i = 0; i < widgetIds.length; i++) {

//                    int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                            AppWidgetManager.INVALID_APPWIDGET_ID);
                    int appWidgetId = widgetIds[i];

                    //UPDATE WIDGET
                    RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);
                    manager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_listView);

                    manager.updateAppWidget(appWidgetId, remoteViews);
                }

            } else if (intent.getAction().equals(Constants.ACTION_INDIVIDUAL_ITEM_IN_WIDGET)) {
                /**
                 * Checks to see whether the intent's action is ACTION_INDIVIDUAL_ITEM_IN_WIDGET.
                 * If it is, the app widget  start the JobDescription activiy for the current item.
                 * this intent is sent from onUpdate()  setPendingIntentTemplate
                 */
                int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);

                String jsonData = intent.getStringExtra(Constants.JSON_DATA_FOR_JOBDETAILS_KEY);

                Intent jobDetailsIntent = new Intent(context, JobDetailsActivity.class);
                jobDetailsIntent.putExtra(Constants.JSON_DATA_FOR_JOBDETAILS_KEY, jsonData);
                jobDetailsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(jobDetailsIntent);

            } else if (intent.getAction().equals(Constants.ACTION_ALARM_UPDATE)) {
                ComponentName componentName = new ComponentName(context, GovJobAppWidgetProvider.class);
                int[] widgetIds = manager.getAppWidgetIds(componentName);
                if (widgetIds.length > 0) {
                    onUpdate(context, manager, widgetIds);
                }
            }
        }
    }

}
