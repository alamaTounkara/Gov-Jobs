package govjobs.govjob;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class JobDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.titleTxt)
    TextView mTitleTxt;
//    @InjectView(R.id.jobStatusTxt)
//    TextView statusTxt;
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
    private final String IS_IT_A_SAVED_JOB = "isSaved";

    private JSONObject mJsonObj;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobdetails);


        ButterKnife.inject(this);

//        mTitleTxt = (TextView) findViewById(R.id.titleTxt);
//        statusTxt = (TextView) findViewById(R.id.jobStatusTxt);
//        mCompanyTxt = (TextView) findViewById(R.id.deptNameTxt);
//        mStateTxt = (TextView) findViewById(R.id.cityStateTxt);
//        mDateTxt = (TextView) findViewById(R.id.openPeriodTxtValue);
//        mSaveTxt = (TextView) findViewById(R.id.saveTxt);
//        agencyTxt = (TextView) findViewById(R.id.agencyTxtValue);
//        mSalaryTxt = (TextView) findViewById(R.id.salarayRangeValueTxt);
//        mWorkTypeTxt = (TextView) findViewById(R.id.positionInfoValueTxt);
//        mRequrementTxt = (TextView) findViewById(R.id.requirementTxt);
//        mSummaryTxt = (TextView) findViewById(R.id.summaryTxt);


        try {
            Intent intent = getIntent();
            mJsonObj = new JSONObject(intent.getStringExtra("JsonArray"));
            mSave = intent.getStringExtra(IS_IT_A_SAVED_JOB);

            processJSON(mJsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("JSONObject1", "JSONObject: " + mJsonObj.toString());


    }


    /**
     * This will save the job in our database when user clicked the save icon
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
        Log.d("ApplyDialogBox", "applyJob(View view) called");

        //ApplyDialogBox dialog =new ApplyDialogBox();
        //dialog.show(getFragme
        // ntManager(),"dialogTag");

//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mJsonObj.getString(Constants.JOB_APPLY_URL).toString()));
//
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "You need to install a browser in your device to apply!", Toast.LENGTH_LONG).show();
//        }

        //return true;

        Log.d("ApplyDialogBox", "applyJob(View view) finished");
//
        Intent intent= new Intent(this, JobApplication.class);
        intent.putExtra(Constants.JOB_ID,mJsonObj.getString(Constants.JOB_ID).toString());
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
        intent.setAction(intent.ACTION_SEND);

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
            startActivity(chooser);
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_job_details, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            /**
//             *To Send or Share an image, use the ACTION_SEND action and
//             * and give the Uri of the image to putExtra() as second argument.
//             */
//            //create a new intent
//            Intent intent = new Intent();
//            //Set and action (what do you want to do?)
//            intent.setAction(intent.ACTION_SEND);
//            // "android.ressource:/<package_name>/fileId" : tell android to look into
//            // your own android app ressource and inside the package_name and get the file with fileId
//            Uri picUri = Uri.parse("android.ressource:/com.example.drawable/" + R.mipmap.ic_launcher);
//            intent.putExtra(Intent.EXTRA_STREAM, picUri);//we used Predifined constants but we could also create ours
//            intent.putExtra(Intent.EXTRA_TEXT, "Hi i attached this PICTURE");
//            //we dont have to specify "from" field as android will use
//            //your email you used to log into the device
//
//            //VERY IMPORTANT: WE MUST SPECIFY A MIME TYPE FOR IMAGE OR OUR APP WILL EITHER CRASH
//            //OR THE IMAGE WILL NOT BE SENT
//            intent.setType("image/*");//this means: the file is type "image" with all "*" formats(png, jpeg and so on)
//            //This displays a dialog with a list of apps that respond to the intent passed to the
//            // createChooser() method and uses the supplied text as the dialog title.
//            Intent chooser = Intent.createChooser(intent, "Send Picture");
//
//            //check to see if there is at least one app in our phone that can take care of the intent.
//            // this is useful becasue if there is no app that can take care of our intent,
//            // our app will crash. PackageManager manages all intent in the android system
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(chooser);
//                Log.d("Send Image", "Image WAS SENT");
//            }
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}





