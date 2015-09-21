package govjobs.govjob.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alamatounkara on 7/27/15.
 */
public class ParserAdapter {

    private static final String JOB_TOTAL = "TotalJobs";
    private static final String JOB_ID = "DocumentID";
    private static final String JOB_POSITION_TITLE = "JobTitle";
    private static final String JOB_ORGANISATION_NAME = "OrganizationName";
    private static final String JOB_AGENCY = "AgencySubElement";
    private static final String JOB_WHO_MAY_APPLY = "WhoMayApplyText";
    private static final String JOB_PAY_PLAN = "PayPlan";
    private static final String JOB_SERIES = "Series";
    private static final String JOB_GRADE = "Grade";
    private static final String JOB_WORK_SCHEDULE = "WorkSchedule";
    private static final String JOB_WORK_TYPE = "WorkType";

    private static final String JOB_MINIMUM_SALARY = "SalaryMin";
    private static final String JOB_MAXIMUM_SALARY = "SalaryMax";
    private static final String JOB_SALARY_BASIS = "SalaryBasis";

    private static final String JOB_START_DATE = "StartDate";
    private static final String JOB_END_DATE = "EndDate";
    private static final String JOB_LOCATIONS = "Locations";
    private static final String JOB_JOB_SUMMARY = "JobSummary";
    private static final String JOB_APPLY_URL = "ApplyOnlineURL";
    private static JSONArray mJsonArray;//json array
    private static JSONObject mJsonObject;//json object


    private static String mJotalJobs = null;//FIRST STRING IN THE JSON OBJECT


    ArrayList<HashMap<String, String>> results;

    public ParserAdapter() {
        results = new ArrayList<HashMap<String, String>>();
    }


    public static JSONObject getMyJsonArray(int position) {
        try {
            return mJsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static JSONObject getMyJsonObject() {
        return mJsonObject;
    }

    public static String getJobTotal() {
        return mJotalJobs;
    }

    /*
    *Android provides four different classes to manipulate JSON data. These classes are JSONArray,
    *JSONObject,JSONStringer and JSONTokenizer.
    *The first step is to identify the fields in the JSON data in which you are interested
    */
    public ArrayList<HashMap<String, String>> processJSON(StringBuffer stringBuffer) {

        //create a new hashmap in every iterration and put our data inside
        HashMap<String, String> currentValue = null;
        try {
            // step1----JSON Parsing
            //For parsing a JSON object, we will create an object of class JSONObject and specify a
            // string containing JSON data to it.
            JSONObject jsonObject = new JSONObject(stringBuffer.toString());

            mJotalJobs = jsonObject.getString("TotalJobs");//FIRST STRING IN THE JSON OBJECT


            // getting the json object
            JSONArray jsonArray = jsonObject.getJSONArray("JobData");
            mJsonArray = jsonArray;//this JsonArray will be passed to joblist activity to send via intent to another another
            mJsonObject = jsonObject;//this JsonArray will be passed to the widget service

            // step2----getting specific object type from our json Array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject subJsonObject = jsonArray.getJSONObject(i);
                //create a new hashmap in every iterration and put our data inside
                currentValue = new HashMap<String, String>();

                //getting all item from json object
                currentValue.put("id", subJsonObject.getString(JOB_ID));
                currentValue.put("positionTitle", subJsonObject.getString(JOB_POSITION_TITLE));
                currentValue.put("orgName", subJsonObject.getString(JOB_ORGANISATION_NAME));
                currentValue.put("minimum", subJsonObject.getString(JOB_MINIMUM_SALARY));
                currentValue.put("maximum", subJsonObject.getString(JOB_MAXIMUM_SALARY));
                currentValue.put("startDate", subJsonObject.getString(JOB_START_DATE));
                currentValue.put("endDate", subJsonObject.getString(JOB_END_DATE));
                currentValue.put("url", subJsonObject.getString(JOB_APPLY_URL));
                currentValue.put("JOB_LOCATIONS", subJsonObject.getString(JOB_LOCATIONS));

                currentValue.put("agency", subJsonObject.getString(JOB_AGENCY));
                currentValue.put("JOB_WHO_MAY_APPLY", subJsonObject.getString(JOB_WHO_MAY_APPLY));
                currentValue.put("JOB_PAY_PLAN ", subJsonObject.getString(JOB_PAY_PLAN));
                currentValue.put("JOB_SERIES", subJsonObject.getString(JOB_SERIES));
                currentValue.put("JOB_GRADE", subJsonObject.getString(JOB_GRADE));
                currentValue.put("JOB_WORK_TYPE", subJsonObject.getString(JOB_WORK_TYPE));
                currentValue.put("JOB_WORK_SCHEDULE", subJsonObject.getString(JOB_WORK_SCHEDULE));
                currentValue.put("JOB_JOB_SUMMARY", subJsonObject.getString(JOB_JOB_SUMMARY));
                currentValue.put("JOB_APPLY_URL", subJsonObject.getString(JOB_APPLY_URL));
                if ((currentValue != null) && (!currentValue.isEmpty())) {
                    results.add(currentValue);//arraylist of object hashmap
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;

    }

}
