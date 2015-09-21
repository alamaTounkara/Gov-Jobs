package govjobs.govjob.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import govjobs.govjob.R;
import govjobs.govjob.ui.JobDetailsActivity;
import govjobs.govjob.ui.UserSettingsAdapter;

/**
 * Created by alamatounkara on 7/31/15.
 */
public class AccountManger implements AdapterView.OnItemClickListener {


    public final String USER_ID = "user_ID";

    public final String KEEP_LOGIN = "keep_user_login";
    public Button mSignUpBtn, mLoginBtn, mLoginOnDrawerBtn, msignUpOnDrawerBtn;
    public EditText mLoginEmailEditTxt, mLoginPwdlEditTxt;
    Constants mConstants;
    private EditText mSignUpFullNameEditTxt, mSignUpEmailEditTxt, mSignUpPwdEditTxt;
    private Activity mActivity;
    private ListView mListview;
    private TextView welcomeTxt;
    private TextView numbJobSavedTxt;
    private LinearLayout logindrawerLisContent;
    private LinearLayout signupdrawerLisContent;
    private LinearLayout userBoardLisContent;
    private Button mUnsave;
    private Button mSaved;
    private JSONObject mJSONObject;
    private boolean mIsChecked;
    private long mUserID;
    private ArrayList<HashMap<Integer, String>> mUserData;
    private SQLiteDatabaseAdapter mSQLiteDatabaseAdapter;

    private Form mForm; // Form used for validation


    public AccountManger(Activity activity, Context c) {


        mConstants = new Constants(activity);
        mSQLiteDatabaseAdapter = new SQLiteDatabaseAdapter(c);

        mActivity = activity;

        //login elements
        mLoginBtn = (Button) mActivity.findViewById(R.id.loginBtn);
        msignUpOnDrawerBtn = (Button) mActivity.findViewById(R.id.signUpOnLoginBtn);
        mLoginEmailEditTxt = (EditText) mActivity.findViewById(R.id.emailInput);
        mLoginPwdlEditTxt = (EditText) mActivity.findViewById(R.id.pwdInput);
        /**
         * This will allow user to click on the Imei.Login on the keyboard to launch the search
         */
        if(mLoginPwdlEditTxt != null){
            mLoginPwdlEditTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null) && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) ||
                            (actionId == EditorInfo.IME_ACTION_GO))) {
                        AccountManger.this.loginUser();
                        return true;
                    }
                    return false;
                }
            });
        }

        //sign up elements
        mSignUpBtn = (Button) mActivity.findViewById(R.id.signUpBtn);
        mSignUpFullNameEditTxt = (EditText) mActivity.findViewById(R.id.signupNameInput);
        mSignUpEmailEditTxt = (EditText) mActivity.findViewById(R.id.emailSignupInput);
        mSignUpPwdEditTxt = (EditText) mActivity.findViewById(R.id.pwdSignupInput);
        /**
         * This will allow user to click on the Imei.Login on the keyboard to launch the search
         */
        if(mSignUpPwdEditTxt!= null) {
            mSignUpPwdEditTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null) && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) ||
                            (actionId == EditorInfo.IME_ACTION_GO))) {
                        AccountManger.this.signUpUser();
                        return true;
                    }
                    return false;
                }
            });
        }
        mLoginOnDrawerBtn = (Button) mActivity.findViewById(R.id.loginOnSignUpBtn);
        mListview = (ListView) mActivity.findViewById(R.id.listView2);
        welcomeTxt = (TextView) mActivity.findViewById(R.id.welcomeNameLbl);
        numbJobSavedTxt = (TextView) mActivity.findViewById(R.id.numberOfJobTxt);

        logindrawerLisContent = (LinearLayout) mActivity.findViewById(R.id.logindrawerLisContent);
        signupdrawerLisContent = (LinearLayout) mActivity.findViewById(R.id.signupdrawerLisContent);
        userBoardLisContent = (LinearLayout) mActivity.findViewById(R.id.userBoardLisContent);

        mUnsave = (Button) mActivity.findViewById(R.id.saveTxt);//the star dark favorite icon
        mSaved = (Button) mActivity.findViewById(R.id.savedTxt);//the shiny favorite icon
        mIsChecked = false;//if remember login was checked
        mUserID = mConstants.JOB_LIST_PREFERENCE.getLong(USER_ID, -1);


    }


    public static final String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impossibru!
        }
    }


    /**
     * show the registration page
     */
    public void showRegisterPage() {//show register page
        signupdrawerLisContent.setVisibility(View.VISIBLE);
        logindrawerLisContent.setVisibility(View.GONE);
        userBoardLisContent.setVisibility(View.GONE);
    }


    /**
     * show the login page
     */
    public void showLoginPage() {//show login page
        logindrawerLisContent.setVisibility(View.VISIBLE);
        signupdrawerLisContent.setVisibility(View.GONE);
        userBoardLisContent.setVisibility(View.GONE);

    }


    /**
     * show the dashboard
     */
    public void showDashboard() {//show login page
        userBoardLisContent.setVisibility(View.VISIBLE);
        signupdrawerLisContent.setVisibility(View.GONE);
        logindrawerLisContent.setVisibility(View.GONE);
    }




    /**
     * singup a new user to our database
     */
    public void signUpUser() {
        initSignUpValidationForm(); //initializing form element
        FormUtils.hideKeyboard(mActivity, mSignUpPwdEditTxt);
        if (mForm.isValid()) {
            String name = mSignUpFullNameEditTxt.getText().toString();
            String email = mSignUpEmailEditTxt.getText().toString();
            String pass = mSignUpPwdEditTxt.getText().toString();
            pass = md5(pass);
            long id = mSQLiteDatabaseAdapter.insertUser(name, email, pass);
            if (id > -1) {
                mListview.setAdapter(null);
               // mListview.clearChoices();
                saveInSharedprefences(id, true); //keep user login by default
                mIsChecked = true;
                mUserID = id;
                showFormattedUserDashBoard(id, name);
            } else {
                Toast.makeText(mActivity, "Your account was not create, please try again!", Toast.LENGTH_LONG).show();
            }
        }else {
            //Toast.makeText(mActivity, "You need to fill up everything!", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * login the user
     *
     * @return return the user id
     */
    public long loginUser() {
        initLoginValidationForm();//initializing form element
        FormUtils.hideKeyboard(mActivity, mLoginEmailEditTxt);
        if (mForm.isValid()) {
        String email = mLoginEmailEditTxt.getText().toString();
        String pass = mLoginPwdlEditTxt.getText().toString();

            pass = md5(pass);
            long userId = mSQLiteDatabaseAdapter.selectUser(email, pass);
            mUserID = userId;
            showFormattedUserDashBoard(userId);
            return userId;
        }

        Toast.makeText(mActivity, "You need to fill up everyting!", Toast.LENGTH_LONG).show();
        return -1;
    }


    /**
     * show a formatted representation of user dashboard
     * ALL the user saved job are shown.
     *
     * @param userId: user id od the user
     */
    public void showFormattedUserDashBoard(long userId) {
        if (userId > -1) {
            showDashboard();
            try {
                mListview.setAdapter(null);//first empty the list
                mUserData = getUserHashMapData((int) userId);

                if (mUserData != null) { //index 0, will always be our user name

                    Iterator<Map.Entry<Integer, String>> user = mUserData.remove(0).entrySet().iterator();
                    Map.Entry<Integer, String> entry = user.next();
                    //index 0, will always be our user name we remove it before we
                    // pass the rest of our mUserData to a listview
                    welcomeTxt.setText("Welcome " + entry.getValue());
                    numbJobSavedTxt.setText("You have " + mUserData.size() + " job(s) saved");
                    showUserData(mUserData);//show data
                } else {
                    String name = mSQLiteDatabaseAdapter.selectUserName((int) userId);
                    welcomeTxt.setText("Welcome " + name);
                    numbJobSavedTxt.setText("You dont have any job saved");
                }

                if (mIsChecked != false) {
                    saveInSharedprefences(userId, true);
                }
            } catch (Exception e) {
                Log.d("UserData", "" + e);
            }

        } else {
            Toast.makeText(mActivity, "Your account was not found, please try again!", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * show a formatted representation of user dashboard
     * This will be used after user sign up
     * beside the welcome message,No data will be shown as user just sign up
     *
     * @param userId: user id od the user
     * @param name    is the user name
     */
    public void showFormattedUserDashBoard(long userId, String name) {
        if (userId > -1) {
            showDashboard();
            welcomeTxt.setText("Welcome " + name);
            numbJobSavedTxt.setText("You dont have any job saved!");

        } else {
            Toast.makeText(mActivity, "Your account was not found, please try again!", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * getter for user id
     *
     * @return userid
     */
    public long getUserID() {
        return mUserID;
    }


    /**
     * getting all the jobs save by that user
     *
     * @param userid: user id od the user
     * @return ArrayList<String> contain all user data
     */
    public ArrayList<String> getUserData(int userid) {
        //index 0, will always be our user name
        return mSQLiteDatabaseAdapter.selectUserData(userid);
    }


    /**
     * getting all the jobs save by that user
     *
     * @param userid: user id od the user
     * @return ArrayList<HashMap<Integer,String>> containing user data. the Key of the
     * hashmap is the job unique id, and the value is the string representing the job object
     */
    public ArrayList<HashMap<Integer, String>> getUserHashMapData(int userid) {
        //index 0, will always be our user name
        return mSQLiteDatabaseAdapter.selectUserHashMapData(userid);
    }


    /**
     * Used by the listview to show our user saved jobs
     *
     * @param data a list of data containning user jobs
     */
    public void showUserData(ArrayList<HashMap<Integer, String>> data) {
        mListview.setAdapter(null);//first empty the list
        mListview.setAdapter(new UserSettingsAdapter(mActivity, data, numbJobSavedTxt, this, getUserID()));
        mListview.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            mJSONObject = new JSONObject(((Map.Entry<Integer, String>) mUserData.get(position)).getValue());
            Intent intent = new Intent(mActivity, JobDetailsActivity.class);
            intent.putExtra("JsonArray", mJSONObject.toString());
            intent.putExtra("isSaved", "true");
            mActivity.startActivity(intent);
        } catch (JSONException e) {
            Log.e("json conversion", "" + e.fillInStackTrace());
            Log.e("json conversion", "" + e);
        }

    }





    /**
     * save a userid in our sharepreferences for session purpose
     *
     * @param userId    for a user
     * @param keepLogin a boolean and indecating if we should keep the user login, usualy by checking
     *                  the 'save login checkbox
     */
    public void saveInSharedprefences(long userId, boolean keepLogin) {
        // mEditor.putString(USER_NAME, name);
        mConstants.EDITOR.putLong(USER_ID, userId);
        mConstants.EDITOR.putBoolean(KEEP_LOGIN, keepLogin);
        mConstants.EDITOR.commit();
    }


    /**
     * check if this user chose to keep is login seesion
     *
     * @return a boolean indicating true or false based on success
     */
    public boolean isKeepLogin() {
        boolean check = false;
        try {
            check = mConstants.JOB_LIST_PREFERENCE.getBoolean(KEEP_LOGIN, false);
        } catch (Exception e) {
            Log.d("account", "" + e);
        }
        return check;
    }


    /**
     * kill user session in sharepreference
     */
    public void killKeepLogin() {
        // SharedPreferences.Editor editor = JOB_LIST_PREFERENCES.edit();
        try {
            mConstants.EDITOR.putBoolean(KEEP_LOGIN, false);
            mConstants.EDITOR.commit();
            mListview.clearChoices();
        } catch (Exception e) {
            Log.d("account", "" + e);
        }
    }


    /**
     * logout the user
     */
    public void logOut() {
        killKeepLogin();
        showLoginPage();
    }


    /**
     * check if user is login
     *
     * @return a boolean indicating true or false based on success
     */
    public boolean isUserLogin() {
        try {
            if (mUserID > -1) return true;
        } catch (Exception e) {
            Log.d("account", "" + e);
        }
        return false;
    }


    /**
     * save a job our database
     *
     * @param jsonObj representing the job data that will be stringfy and put in our database
     */
    public void saveJob(JSONObject jsonObj) {

        if (isUserLogin() != false) {

            if (mSaved.getVisibility() != View.VISIBLE) {//checking if this item was already added to the favorite
                long id = mSQLiteDatabaseAdapter.insertData((int) mUserID, jsonObj);
                if (id > -1) {
                    mUnsave.setVisibility(View.GONE);
                    mSaved.setVisibility(View.VISIBLE);
                    Toast.makeText(mActivity, "This job was saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "This job was not saved! Please try again later!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mActivity, "You already added this item to your favorite!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(mActivity, "You need to signin in order to save jobs!", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * delete this specific job from the database
     *
     * @param userId       that is login
     * @param dataPosition in our database
     */
    public boolean deleteJob(long userId, int dataPosition) {
        try {
            if ((userId > -1) && (dataPosition > -1)) {
                long count = mSQLiteDatabaseAdapter.deleteFromTable(userId, dataPosition);
                if (count > -1) {

                    return true;
                } else {
                    Toast.makeText(mActivity, "Could not delete this job, please try again later!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(mActivity, "You can not delete this job.You are not login!", Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (Exception e) {
            Log.d("AccountManger", "DELETE JOB EXCEPTION" + e);
        }
        return false;
    }


    /**
     * delete this specific job from the database
     *
     * @param userId that is login
     * @param data   string data representing the job object
     */
    public boolean deleteJob(long userId, String data) {
        try {
            if ((userId > -1) && (data != null)) {
                long count = mSQLiteDatabaseAdapter.deleteFromTable(userId, data);

                if (count > -1) {

                    return true;
                } else {
                    Toast.makeText(mActivity, "Could not delete this job, please try again later!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(mActivity, "You can not delete this job.You are not login!", Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (Exception e) {
            Log.d("AccountManger", "DELETE JOB EXCEPTION" + e);
        }
        return false;
    }


    public void saveLoginCheck(boolean ok) {
        if (ok == true) {
            mIsChecked = true;
        } else {
            mIsChecked = false;
        }
    }


    private void initLoginValidationForm() {
        mForm = Form.create();
        mForm.addField(Field.using(mLoginEmailEditTxt).validate(NotEmpty.build()).validate(IsEmail.build()));
        mForm.addField(Field.using(mLoginPwdlEditTxt).validate(NotEmpty.build()));
        mForm.errorHandler(new EditTextErrorHandler());
    }



    private void initSignUpValidationForm() {
        mForm = Form.create();
        mForm.addField(Field.using(mSignUpFullNameEditTxt).validate(NotEmpty.build()));
        mForm.addField(Field.using(mSignUpEmailEditTxt).validate(NotEmpty.build()).validate(IsEmail.build()));
        mForm.addField(Field.using(mSignUpPwdEditTxt).validate(NotEmpty.build()));
        mForm.errorHandler(new EditTextErrorHandler());
    }


}
