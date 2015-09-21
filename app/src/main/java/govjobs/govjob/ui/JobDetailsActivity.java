package govjobs.govjob.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import govjobs.govjob.R;
import govjobs.govjob.util.AccountManger;
import govjobs.govjob.util.Constants;


public class JobDetailsActivity extends AppCompatActivity {

    private final String IS_IT_A_SAVED_JOB = "isSaved";
    @InjectView(R.id.titleTxt)
    TextView mTitleTxt;
    @InjectView(R.id.deptNameTxt)
    TextView mCompanyTxt;
    @InjectView(R.id.cityStateTxt)
    TextView mStateTxt;
    @InjectView(R.id.openPeriodTxtValue)
    TextView mDateTxt;
    @InjectView(R.id.saveTxt)
    TextView mSaveTxt;
    @InjectView(R.id.agencyTxtValue)
    TextView agencyTxt;
    @InjectView(R.id.salarayRangeValueTxt)
    TextView mSalaryTxt;
    @InjectView(R.id.positionInfoValueTxt)
    TextView mWorkTypeTxt;
    @InjectView(R.id.requirementTxt)
    TextView mRequrementTxt;
    @InjectView(R.id.summaryTxt)
    TextView mSummaryTxt;
    @InjectView(R.id.saveTxt)
    Button mUnsave;
    @InjectView(R.id.savedTxt)
    Button mSaved;
    private String mSave;
    private JSONObject mJsonObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobdetails);


        ButterKnife.inject(this);

        try {
            Intent intent = getIntent();
            mJsonObj = new JSONObject(intent.getStringExtra(Constants.JSON_DATA_FOR_JOBDETAILS_KEY));
            mSave = intent.getStringExtra(IS_IT_A_SAVED_JOB);

            processJSON(mJsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * This will save the job in our database when user clicked the save icon
     *
     * @param view save icon vutton
     */
    public void saveJob(View view) {
        new AccountManger(this, this).saveJob(mJsonObj);
    }


    /**
     * Android provides four different classes to manipulate JSON data. These classes are JSONArray,
     * JSONObject,JSONStringer and JSONTokenizer.
     * The first step is to identify the fields in the JSON data in which you are interested
     */
    public void processJSON(JSONObject jsonObject) {
        try {

            //getting all item from json object
            mTitleTxt.setText(jsonObject.getString(Constants.JOB_POSITION_TITLE));
            mCompanyTxt.setText(jsonObject.getString(Constants.JOB_ORGANISATION_NAME));
            mSalaryTxt.setText(jsonObject.getString(Constants.JOB_MINIMUM_SALARY) + " - " +
                    jsonObject.getString(Constants.JOB_MAXIMUM_SALARY));

            mDateTxt.setText(jsonObject.getString(Constants.JOB_START_DATE) + " to " +
                    jsonObject.getString(Constants.JOB_END_DATE));

            if (mSave != null) {
                if (mSave.equals("true")) {


                    mUnsave.setVisibility(View.GONE);
                    mSaved.setVisibility(View.VISIBLE);
                }
            }

            mStateTxt.setText(jsonObject.getString(Constants.JOB_LOCATIONS));
            agencyTxt.setText(jsonObject.getString(Constants.JOB_AGENCY));

            mRequrementTxt.setText(jsonObject.getString(Constants.JOB_WHO_MAY_APPLY));

            mWorkTypeTxt.setText(jsonObject.getString(Constants.JOB_WORK_SCHEDULE) + " - " +
                    jsonObject.getString(Constants.JOB_WORK_TYPE));

            mSummaryTxt.setText(jsonObject.getString(Constants.JOB_JOB_SUMMARY));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * APPLY button on job detail page to allow allow user to go fill up the
     * job application.
     *
     * @param view is the 'APPLY' button that was clicked
     * @throws JSONException
     */
    public void applyJob(View view) throws JSONException {
        Intent intent = new Intent(this, JobApplication.class);
        intent.putExtra(Constants.JOB_ID, mJsonObj.getString(Constants.JOB_ID).toString());
        startActivity(intent);
    }


    /**
     * SHARE button on job detail page to allow allow user to go share the job
     *
     * @param v is the 'Share' button that was clicked
     * @throws JSONException
     */
    public void shareJob(View v) throws JSONException {
        String url = mJsonObj.getString(Constants.JOB_APPLY_URL);
        //create a new intent
        Intent intent = new Intent();
        //Set and action (what do you want to do?)
        intent.setAction(Intent.ACTION_SEND);

        Uri jobUrl = Uri.parse(url);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Job opportunity");
        intent.setType("text/plain");//VERY IMPORTANT

        intent.putExtra(Intent.EXTRA_TEXT, "Hi, I just came across this new job opportunity,"
                + " i thought you might be interested .... \n\n"
                + "Position title: " + mJsonObj.getString(Constants.JOB_POSITION_TITLE) + "\n\n"
                + "Location: " + mJsonObj.getString(Constants.JOB_LOCATIONS) + "\n\n"
                + "Who may apply: " + mJsonObj.getString(Constants.JOB_WHO_MAY_APPLY) + "\n\n"
                + "Salary range: " + mJsonObj.getString(Constants.JOB_MINIMUM_SALARY)
                + " - " + mJsonObj.getString(Constants.JOB_MAXIMUM_SALARY) + "\n\n" + jobUrl + "\n");

        //This displays a dialog with a list of apps that respond to the intent passed to the
        // createChooser() method and uses the supplied text as the dialog title.
        Intent chooser = Intent.createChooser(intent, "Share Job");

        //check to see if there is at least one app in our phone that can take care of the intent.
        // this is useful becasue if there is no app that can take care of our intent,
        // our app will crash. PackageManager manages all intent in the android system
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}





