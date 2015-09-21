package govjobs.govjob.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by alamatounkara on 8/7/15.
 */
public class Constants  {
    /**
     * These constants will be used to create the query string to send to the api
     */
    public static final String JOB_TOTAL = "TotalJobs";
    public static final String USA_JOB_BASE_URL = "https://data.usajobs.gov/api/jobs?NumberOfJobs=250";
    public static final String TITLE = "Title";
    public static final String MINIMUM_SALARY = "MinSalary";
    public static final String MAXIMUM_SALARY = "MaxSalary";
    public static final String LOCATION_ID = "LocationID";
    public static final String LOCATION_NAME = "LocationName";
    public static final String SENIOR_EXECUTIVE_SERVICE = "SES";
    public static final String KEYWORD = "Keyword";





    public static final String SHARE_PREFERENCE_FILE = "govjobs.govjob_preferences";
    public static final String NUMBER_OF_JOB_ON_WIDGET = "NUMBER_OF_ON_WIDGET";
    public static final String JOB_ID = "DocumentID";
    public static final String JOB_POSITION_TITLE = "JobTitle";
    public static final String JOB_ORGANISATION_NAME = "OrganizationName";
    public static final String JOB_AGENCY = "AgencySubElement";
    public static final String JOB_WHO_MAY_APPLY = "WhoMayApplyText";
    public static final String JOB_PAY_PLAN = "PayPlan";
    public static final String JOB_SERIES = "Series";
    public static final String JOB_GRADE = "Grade";
    public static final String JOB_WORK_SCHEDULE = "WorkSchedule";
    public static final String JOB_WORK_TYPE = "WorkType";
    public static final String JOB_MINIMUM_SALARY = "SalaryMin";
    public static final String JOB_MAXIMUM_SALARY = "SalaryMax";
    public static final String JOB_SALARY_BASIS = "SalaryBasis";
    public static final String JOB_START_DATE = "StartDate";
    public static final String JOB_END_DATE = "EndDate";
    public static final String JOB_LOCATIONS = "Locations";
    public static final String JOB_JOB_SUMMARY = "JobSummary";
    public static final String JOB_APPLY_URL = "ApplyOnlineURL";
    public static final String ACTION_WIDGET_START_SERVICE = "govjobs.govjob.ACTION_WIDGET_START_SERVICE";
    public static final String ACTION_WIDGET_START_JOBLIST_ACTIVITY = "govjobs.govjob.ACTION_WIDGET_START_JOBLIST_ACTIVITY";
    public static final String ACTION_SERVICE_FINISH_FECTH = "govjobs.govjob.ACTION_SERVICE_FINISH_FECTH";
    public static final String ACTION_ALARM_UPDATE = "govjobs.govjob.ACTION_ALARM_UPDATE";
    public static final String ACTION_INDIVIDUAL_ITEM_IN_WIDGET = "govjobs.govjob.ACTION_INDIVIDUAL_ITEM_IN_WIDGET";
    public static final String ACTION_INDIVIDUAL_ITEM_IN_WIDGET_POSITION = "govjobs.govjob.ACTION_INDIVIDUAL_ITEM_IN_WIDGET_POSITION";
    public static final String JSON_DATA_FOR_JOBDETAILS_KEY = "govjobs.govjob.JsonArray";
    public final SharedPreferences JOB_LIST_PREFERENCE;
    public final SharedPreferences.Editor  EDITOR;



    // public static final String ACTION_WIDGET_START_SERVICE = "govjobs.govjob.ACTION_WIDGET_START_SERVICE";


    public Constants(Context context){
        JOB_LIST_PREFERENCE = context.getSharedPreferences(SHARE_PREFERENCE_FILE, Context.MODE_PRIVATE);
        EDITOR = JOB_LIST_PREFERENCE.edit();

    }

}
