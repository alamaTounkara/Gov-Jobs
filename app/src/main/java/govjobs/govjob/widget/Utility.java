package govjobs.govjob.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import govjobs.govjob.util.Constants;

/**
 * Created by alamatounkara on 9/4/15.
 */
public class Utility {

    /**
     * create an alarm manager and that will be called every half and hour
     * @param context
     */
    public static void updateWithAlarmManager(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = setPendingIntent(context);
      //  alarmManager.cancel(pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(),AlarmManager.INTERVAL_HALF_HOUR, pendingIntent );
    }

    /**
     * set our intent and encapsulate it in a PendingIntent
     * @param context
     * @return PendingIntent
     */
    public static PendingIntent setPendingIntent(Context context){
        Intent intent = new Intent(context, GovJobAppWidgetProvider.class);
        intent.setAction(Constants.ACTION_ALARM_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    /**
     * Cancel the alarm
     * @param context
     */
    public static void cancelAlarmManager(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(setPendingIntent(context));
    }


    /**
     * get the number of job to display on the widget from our sharepreferences.
     * If user didn't choose a number of job to display on the widget, the default job list to
     * display will be 50.
     */
    public static int getNumberOfJobToDisplay(Context context) {
        Constants constants = new Constants(context);
        String number_job = constants.JOB_LIST_PREFERENCE.getString(Constants.NUMBER_OF_JOB_ON_WIDGET, "50");
        if ((number_job != null) && (!number_job.equals(""))) {
            return Integer.parseInt(number_job);
        }
        return 50;//default number of job to display on the widget
    }
}
