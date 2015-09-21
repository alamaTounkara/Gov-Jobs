package govjobs.govjob.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import govjobs.govjob.R;
import govjobs.govjob.util.AccountManger;
import govjobs.govjob.util.CallBackInterface;
import govjobs.govjob.util.Constants;
import govjobs.govjob.util.CustomBaseAdapter;
import govjobs.govjob.util.Form;
import govjobs.govjob.util.NonUIFragmentForTask;
import govjobs.govjob.util.ParserAdapter;


public class JobList extends AppCompatActivity implements CallBackInterface, AdapterView.OnItemClickListener {


    /**
     * These constants will be used to create the query string to send to the api
     */
    private static final String JOB_TOTAL = "TotalJobs";
    private final String USA_JOB_BASE_URL = "https://data.usajobs.gov/api/jobs?NumberOfJobs=250";
    private final String TITLE = "Title";
    private final String MINIMUM_SALARY = "MinSalary";
    private final String MAXIMUM_SALARY = "MaxSalary";
    private final String LOCATION_ID = "LocationID";
    private final String LOCATION_NAME = "LocationName";
    private final String SENIOR_EXECUTIVE_SERVICE = "SES";
    private final String KEYWORD = "Keyword";

    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;
    @InjectView(R.id.progressLinearLayout)
    LinearLayout mProgressLinearLayout;
    @InjectView(R.id.listView)
    ListView mListView;
    @InjectView(R.id.searchEditText)
    EditText mSearchEditText;
    @InjectView(R.id.noResultTxt)
    TextView mNoresultMsgTextView;
    @InjectView(R.id.numbOfJobTxt)
    TextView mNumbResultTextView;
    @InjectView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.appToolbar)
    Toolbar mToolbar;
    //query string array
    String[] mQueryParam = {KEYWORD, TITLE, LOCATION_NAME, LOCATION_ID, SENIOR_EXECUTIVE_SERVICE, MAXIMUM_SALARY, MINIMUM_SALARY};
    String[] mSearchWords;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    Constants constants;
    //Our navigation drawer fragment
    private NavDrawerFragment mNavDrawerFragment;
    private Form mForm; // Form used for validation

    //android.support.v4.app.ActionBarDrawerToggle mDrawerToggle;
    private boolean mIsUserLogin = false;
    private NonUIFragmentForTask mNonUITaskFragment;
    private Bundle mSaveInstanceState;//getting a reference to the saveInstanceStae object
    private JSONObject mJSONObject;
    private AccountManger mAccountManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joblist);
        ButterKnife.inject(this);//initializing all the inputs
        setSupportActionBar(mToolbar);//setting the toolbar

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);


        constants = new Constants(this);
        mSaveInstanceState = savedInstanceState; //getting a reference to the bundle and use it in search function
        mSearchWords = new String[7]; //all search parameters will be put in to this
        mAccountManger = new AccountManger(this, this);

        /**
         * This will allow user to click on the Imei.search on the keyboard to launch the search
         */
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null) && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) ||
                        (actionId == EditorInfo.IME_ACTION_SEARCH))) {
                    manageSearch(mSearchWords);
                    return true;
                }
                return false;
            }
        });


//        changeTitle("GOV JOBS");//making "GOV JOBS" our app title
        //setUpDrawer();
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        mNavDrawerFragment = (NavDrawerFragment)manager.findFragmentById(R.id.nav_drawer_frag);
        mNavDrawerFragment.setUpDrawer(R.id.nav_drawer_frag, mDrawerLayout, mToolbar, mAccountManger);

        //showing the 3vertival home drawer icon(called hamburger icon)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //making our App icon+name on actionBar clickable (may be used as link or some other action)
        getSupportActionBar().setHomeButtonEnabled(true);




       /*
         * we make a fragment undestructable by adding "setRetainInstance(true)
		 * in onActivityCreated() method in the fragment. In this app,we dont
		 * want the fragment to be destroy when the screen is rotate(even when
		 * the activity with which it is associated get destroyed). By doing so
		 * onDestroy() will never be called but onDestroyView() will be called.
		 * onCreate() will be called only once when the fragment is being
		 * created the first time.
		 */
        if (savedInstanceState == null) {// first time this activity is being
            // created
            mNonUITaskFragment = new NonUIFragmentForTask();
            getFragmentManager().beginTransaction().add(mNonUITaskFragment, "NonUIFragment").commit();

        } else {// DONT CREATE IT, JUST FIND IT
            mNonUITaskFragment = (NonUIFragmentForTask) getFragmentManager().findFragmentByTag("Non UI Fragment");
        }

        // deciding whether or not we want the progress to show per default when
        // the program starts
        if ((mNonUITaskFragment != null)
                && (mNonUITaskFragment.mMyTask != null)
                && (mNonUITaskFragment.mMyTask.getStatus() == AsyncTask.Status.RUNNING)) {
            mProgressLinearLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
    }



    /**
     * show registration  page
     */
    public void goToRegisterPage(View v) {
        mAccountManger.showRegisterPage();
    }


    /**
     * show login page
     */
    public void goToLoginPage(View v) {
        mAccountManger.showLoginPage();
    }

    /**
     * Login user when this button is clicked
     */
    public void loginUser(View v) {
        mAccountManger.loginUser();
    }

    /**
     * sign up user
     */
    public void signUpUser(View v) {
        mAccountManger.signUpUser();
    }

    /**
     * Logout user
     */
    public void logUserOut(View view) {
        mAccountManger.logOut();
    }


    @Override
    protected void onResume() {

        super.onResume();
        SharedPreferences searchCriteria = PreferenceManager.getDefaultSharedPreferences(this);
        String sss1 = searchCriteria.getString("job_title", null);
        String sss2 = searchCriteria.getString("location_name", null);
        String sss3 = searchCriteria.getString("zip_code", null);
        Boolean sss4 = searchCriteria.getBoolean("senior_level", false);
        String sss5 = searchCriteria.getString("max_salary", null);
        String sss6 = searchCriteria.getString("min_salary", null);

        mSearchWords[2] = searchCriteria.getString("location_name", null);
        mSearchWords[3] = searchCriteria.getString("zip_code", null);
        mSearchWords[4] = String.valueOf(searchCriteria.getBoolean("senior_level", false));
        mSearchWords[5] = searchCriteria.getString("max_salary", null);
        mSearchWords[6] = searchCriteria.getString("min_salary", null);

        manageSearch(mSearchWords);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSaveInstanceState = outState; //getting a reference to the bundle and use it in search function
    }

    //helper method for job search
    private void manageSearch(String[] ar) {
        // mSearchWords =ar;
        mSearchWords[0] = mSearchEditText.getText().toString();
        //if ((mSearchWords[0] != "") && (mSearchWords[0] != null) && (!mSearchWords[0].isEmpty())) {
        if (mSaveInstanceState == null) {// first time this activity is being
            // created
            mNonUITaskFragment = new NonUIFragmentForTask();
            getFragmentManager().beginTransaction()
                    .add(mNonUITaskFragment, "Non UI Fragment").commit();

        } else {// DONT CREATE IT, JUST FIND IT
            mNonUITaskFragment = (NonUIFragmentForTask) getFragmentManager()
                    .findFragmentByTag("Non UI Fragment");

        }

        // deciding whether or not we want the progress to show per default when
        // the program starts
        if ((mNonUITaskFragment != null)
                 /* && (mNonUITaskFragment.mMyTask == null)
                  && (mNonUITaskFragment.mMyTask.getStatus() != AsyncTask.Status.RUNNING)*/) {

            Uri uri = Uri.parse(USA_JOB_BASE_URL);
            Uri.Builder uriBuilder = uri.buildUpon();

            for (int i = 0; i < mSearchWords.length; i++) {
                if (mSearchWords[i] != null) {
                    uriBuilder.appendQueryParameter(mQueryParam[i], mSearchWords[i]);
                }
            }

            String myUrl = uriBuilder.build().toString();

            mNonUITaskFragment.startTaskOnResume(myUrl);// we defined startTask() in mNonUITaskFragment
        }
    }


    public void searchJobs(View view) {
        view.setFocusable(true);

        /**
         * changing the background of imageview when onfocus
         */
        view.setOnTouchListener(new View.OnTouchListener() {
            private Rect rect;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(Color.argb(50, 50, 50, 50));
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    manageSearch(mSearchWords);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(Color.argb(0, 0, 0, 0));
                }
                return false;
            }
        });

    }


    @Override
    public void onPreExecute() {
        // we are iniatializing mAdapter here because,if we had done in
        // onProgressUpdate(), the publishProgress(day) inside the for loop
        // located in doInBackground() will have called onProgressUpdate()
        // in each loop, hence iniatializing mAdapter in each loop, which is
        // was not a good idea
        //this.mAdapter = (ArrayAdapter<String>) mListView.getAdapter();
        //this.mCustomBaseAdapter  = new CustomBaseAdapter(th);
        //we have a determinate number therefore we will have to change the
        //default undeterninate to false
        mProgressLinearLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void onPostExecute(ArrayList<HashMap<String, String>> result) {
        if ((result.size() != 0) && (result != null)) {//we have results
            if (mListView.getVisibility() == View.GONE) {//if listView is invisble
                mListView.setVisibility(View.VISIBLE);
            }
            int numbjob = Integer.parseInt(ParserAdapter.getJobTotal());//number of job
            if (numbjob == 1) {//if we have only one
                mNumbResultTextView.setText(numbjob + " job found");
            } else {//we have multiple jobs
                mNumbResultTextView.setText(numbjob + " jobs found");
            }

            mListView.setAdapter(new CustomBaseAdapter(this, result));
            mProgressLinearLayout.setVisibility(View.GONE);
            mListView.setOnItemClickListener(this);
        } else {//no result
            mProgressLinearLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            mNoresultMsgTextView.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mJSONObject = ParserAdapter.getMyJsonArray(position);
        Intent intent = new Intent(this, JobDetailsActivity.class);
        intent.putExtra(Constants.JSON_DATA_FOR_JOBDETAILS_KEY, mJSONObject.toString());
        startActivity(intent);
    }


    public void showFilterOptions(View view) {
        Intent intent = new Intent(this, Prefs.class);
        startActivity(intent);
    }




    public void saveLoginCheck(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            mAccountManger.saveLoginCheck(true);
            //Toast.makeText(this, "Save login was checked", Toast.LENGTH_SHORT).show();
        } else {
            mAccountManger.saveLoginCheck(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}



