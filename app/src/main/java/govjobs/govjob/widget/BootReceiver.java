package govjobs.govjob.widget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by alamatounkara on 9/4/15.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            ComponentName componentName = new ComponentName(context, GovJobAppWidgetProvider.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] widgetIds = appWidgetManager.getAppWidgetIds(componentName);
            if (widgetIds.length > 0) {
                for (int i = 0; i < widgetIds.length; i++) {
                    //start a service that will download our data from the net
                    Intent serviceIntent = new Intent(context, FetchDataService.class);
                    //serviceIntent.
                    serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetIds[i]);
                    serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startService(serviceIntent);

                    /**
                     * We get the refresh interval provided by the user from shared preferences and use it for
                     * creating inexact repeating alarms using AlarmManager.
                     * Also we need to reschedule the alarms since they are lost on reboot. So we create a
                     * broadcast receiver for boot completed and register it in the manifest.
                     */
                    Utility.updateWithAlarmManager(context);

                }
            }

        }
    }
}
