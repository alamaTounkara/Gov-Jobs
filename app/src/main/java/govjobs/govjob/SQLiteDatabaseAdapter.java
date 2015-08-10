package govjobs.govjob;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class SQLiteOpenHelper
 *  A helper class to manage database creation and version management.
 *  You create a subclass implementing onCreate(SQLiteDatabase), onUpgrade(SQLiteDatabase, int, int) 
 *  and optionally onOpen(SQLiteDatabase), and this class takes care of opening the database if it exists,
 *  creating it if it does not, and upgrading it as necessary. Transactions are used to make sure the
 *  database is always in a sensible state.
 */

public class SQLiteDatabaseAdapter {

    private SQLiteHelper mSQLiteHelper;

    public SQLiteDatabaseAdapter(Context context) {
        mSQLiteHelper = new SQLiteHelper(context);

    }


    /**
     * Insert user in the table TABLE_USERS
     * @param name: name of the user
     * @param email: email of the user
     * @param password: password of the user
     * @return user unique id
     */
    public long insertUser(String name, String email, String password) {

        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(mSQLiteHelper.COLUMN_NAME, name);
        contentValues.put(mSQLiteHelper.COLUMN_EMAIL, email);
        contentValues.put(mSQLiteHelper.COLUMN_PASSWORD, password);

        Log.d("DB", "insertData()-id : After try-catch");

        long id = db.insert(mSQLiteHelper.TABLE_USERS, null, contentValues);
        Log.d("DB", "insertData()-id : " + id);
        return id;// return column id
    }



    /**
     * Insert job data(json object) in the table TABLE_DATA
     * @param userId: user id that the data belongs to.
     * @param jsonObject: is a jsonObject represent the job data itself.
     * We will transform this in to string by using toString() and insert it in the database
     * @return row number of the object
     */
    public long insertData(int userId, JSONObject jsonObject) {
        if ((jsonObject != null)&&(userId >=0)) {
            SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(mSQLiteHelper.COLUMN_DATA_from_USER, userId);
            contentValues.put(mSQLiteHelper.JOB_DATA, jsonObject.toString());

            return db.insert(mSQLiteHelper.TABLE_DATA, null, contentValues);
        }
        return -1;
    }



    /**
     * Selecting name and pass from the table TABLE_USERS
     * @param email: user email address
     * @param pass: user password
     * @return a unique user id
     */
    public long selectUser(String email, String pass) {
        if((email != null) &&(pass != null)) {
            long userid = -1; //-1 mean there was no user found
            SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

            String[] columns = {mSQLiteHelper.COLUMN_ID};
            String[] condition={email, pass};
            Cursor cursor = db.query(mSQLiteHelper.TABLE_USERS, columns,
                    mSQLiteHelper.COLUMN_EMAIL + " = ? AND " +
                            mSQLiteHelper.COLUMN_PASSWORD + " = ?", condition, null, null, null);

            while (cursor.moveToNext()) {
                userid= cursor.getInt(cursor.getColumnIndex(mSQLiteHelper.COLUMN_ID));
            }
            return userid;
        }
        return -1;//unsuccessful
    }





    /**
     * Selecting user name and pass from the table TABLE_USERS
     * @param email: user email address
     * @param pass: user password
     * @return string array with with lenght=2. 1 is email, 1 pass
     */
    public String[] selectUserNamePass(String email, String pass) {
        if((email != null) &&(pass != null)) {
          //size = 2 cuz we are expecting only 1 name and 1 pass. if there is more, we have duplicate rows
            String [] user = new String[2];
            SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

            // select _id,name,pwd from Students;
            String[] columns = {mSQLiteHelper.COLUMN_EMAIL, mSQLiteHelper.COLUMN_PASSWORD};
            String[] condition={email, pass};
            Cursor cursor = db.query(mSQLiteHelper.TABLE_USERS, columns,
                    mSQLiteHelper.COLUMN_EMAIL + " = ? AND " +
                            mSQLiteHelper.COLUMN_PASSWORD + " = ?", condition, null, null, null);

           int i = 0;
            while (cursor.moveToNext()) {
                user[0]= cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_EMAIL));
                user[1]= cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_PASSWORD));
                i++;
            }
            if(i>1) return null; //we have duplicate row
            return user;//return user array with 2 value - name and pass
        }
        return null;
    }



    /*
    * Selecting user name  the table TABLE_USERS
    * @param userId: user id
    * @return user name
    */
    public String selectUserName(int userId) {
        if((userId>-1)) {
            //size = 2 cuz we are expecting only 1 name and 1 pass. if there is more, we have duplicate rows
            SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

            // select _id,name,pwd from Students;
            String[] columns = {mSQLiteHelper.COLUMN_NAME};
            Cursor cursor = db.query(mSQLiteHelper.TABLE_USERS, columns,
                    mSQLiteHelper.COLUMN_ID + " = +"+userId,null, null, null, null);

            int i = 0;
            String name=null;
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_NAME));
                i++;
            }
            if(i>1) return null; //we have duplicate row
            return name;//return user array with 2 value - name and pass
        }
        return null;
    }


    /*
     * Selecting user data from the table TABLE_DATA
     * @param userid: user id od the user
     * @return ArrayList<String> contain all user data
     */
    public ArrayList<String> selectUserData(int userid) {
        //select data from user,data where data.userid = userid and user.id=userid
        if((userid >=0)) {
            SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

            String[] columns = {mSQLiteHelper.JOB_DATA,mSQLiteHelper.COLUMN_NAME};
            //  int[] condition={userid};

            Cursor cursor = db.query(mSQLiteHelper.TABLE_USERS + ", " + mSQLiteHelper.TABLE_DATA,
                    columns, mSQLiteHelper.TABLE_DATA + "." + mSQLiteHelper.COLUMN_DATA_from_USER
                            + " = " + userid + " AND " + mSQLiteHelper.TABLE_USERS + "." + mSQLiteHelper.COLUMN_ID + " = " + userid
                    , null, null, null, null);

            ArrayList<String> data = new ArrayList<String>();
           int i = 0;
            while (cursor.moveToNext()) {
                if(i ==0) {
                    //data index 0, will always be our user name
                    data.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_NAME)));
                }
                i++;
                data.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_DATA)));//object
            }
            if(data.size()>1)return data;

        }
        return null;

    }


    /**
    * Selecting user data from the table TABLE_DATA
    * @param userid: user id od the user
    * @return ArrayList<HashMap<Integer,String>> contain all user data
    */
    public ArrayList<HashMap<Integer,String>> selectUserHashMapData(int userid) {
        //select data from user,data where data.userid = userid and user.id=userid
        if((userid >=0)) {
            SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

            String[] columns = {mSQLiteHelper.JOB_DATA,mSQLiteHelper.COLUMN_NAME,
                    mSQLiteHelper.COLUMN_DATA_ID,mSQLiteHelper.TABLE_DATA+"."+ mSQLiteHelper.COLUMN_DATA_from_USER};

            Cursor cursor = db.query(mSQLiteHelper.TABLE_USERS + ", " + mSQLiteHelper.TABLE_DATA,
                    columns, mSQLiteHelper.TABLE_DATA + "." + mSQLiteHelper.COLUMN_DATA_from_USER
                            + " = " + userid + " AND " + mSQLiteHelper.TABLE_USERS + "." + mSQLiteHelper.COLUMN_ID + " = " + userid
                    , null, null, null, null);

            HashMap<Integer,String> currenValue;
            ArrayList<HashMap<Integer,String>> data = new ArrayList<HashMap<Integer,String>>();
            int i = 0;
            while (cursor.moveToNext()) {
                if(i ==0) {
                    HashMap<Integer,String> currenUserValue = new HashMap<Integer,String>();
                    //data index 0, will always be our user name
                    currenUserValue.put(cursor.getInt(cursor.getColumnIndex(mSQLiteHelper.COLUMN_DATA_from_USER)),
                            cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_NAME)));
                    Log.d("AccountManger", "mSQLiteHelper.COLUMN_DATA_from_USER: " + currenUserValue);
                    data.add(currenUserValue);//object
                }
                i++;
                currenValue = new HashMap<Integer,String>();
                currenValue.put(cursor.getInt(cursor.getColumnIndex(mSQLiteHelper.COLUMN_DATA_ID)),
                        cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_DATA)));

                data.add(currenValue);//object
            }
            if(data.size()>1)return data;

        }
        return null;
    }
















//    public long insertData(String name, String email, String password, JSONObject jsonObject, String isSavedBedore) {
//
//        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        Log.d("DB", "insertData()-id : before try-catch");
//
//        contentValues.put(mSQLiteHelper.COLUMN_NAME, name);
//        contentValues.put(mSQLiteHelper.COLUMN_PASSWORD, password);
//        contentValues.put(mSQLiteHelper.COLUMN_EMAIL, email);
////            Log.d("account", "" + mSQLiteHelper.COLUMN_EMAIL + " : " + email);
////            Log.d("account", "" + mSQLiteHelper.COLUMN_PASSWORD + " : " + password);
//////            Log.d("DB", "json: " + jsonObject.getString(mSQLiteHelper.JOB_POSITION_TITLE) + " -  " +
////                    jsonObject.getString(mSQLiteHelper.JOB_ORGANISATION_NAME) + " -  " +
////                    jsonObject.getString(mSQLiteHelper.JOB_AGENCY) + " -  " +
////                    jsonObject.getString(mSQLiteHelper.JOB_WHO_MAY_APPLY) + " -  " +
////                    jsonObject.getString(mSQLiteHelper.JOB_WORK_SCHEDULE) + " -  " +
////                    jsonObject.getString(mSQLiteHelper.JOB_WORK_TYPE) + " -  " +
////                    jsonObject.getString(mSQLiteHelper.JOB_MINIMUM_SALARY) + " -  " +
////                    jsonObject.getString(mSQLiteHelper.JOB_MAXIMUM_SALARY) + " -  " +
////                    jsonObject.getString(mSQLiteHelper.JOB_START_DATE) + " -  " +
////                    jsonObject.getString(mSQLiteHelper.JOB_END_DATE) + " -  " +
////                    jsonObject.getString(mSQLiteHelper.JOB_LOCATIONS) + " -  " +
////                    jsonObject.getString(mSQLiteHelper.JOB_JOB_SUMMARY) + " -  " +
////                    jsonObject.getString(mSQLiteHelper.JOB_APPLY_URL));
//
//
//        if (jsonObject != null) {
//            //contentValues.put(mSQLiteHelper.JOB_TOTAL, isSavedBedore);
//            contentValues.put(mSQLiteHelper.JOB_DATA, jsonObject.toString());
//
////
////                contentValues.put(mSQLiteHelper.JOB_POSITION_TITLE, jsonObject.getString(mSQLiteHelper.JOB_POSITION_TITLE));
////                contentValues.put(mSQLiteHelper.JOB_ORGANISATION_NAME, jsonObject.getString(mSQLiteHelper.JOB_ORGANISATION_NAME));
////                contentValues.put(mSQLiteHelper.JOB_AGENCY, jsonObject.getString(mSQLiteHelper.JOB_AGENCY));
////                contentValues.put(mSQLiteHelper.JOB_WHO_MAY_APPLY, jsonObject.getString(mSQLiteHelper.JOB_WHO_MAY_APPLY));
////                contentValues.put(mSQLiteHelper.JOB_WORK_SCHEDULE, jsonObject.getString(mSQLiteHelper.JOB_WORK_SCHEDULE));
////                contentValues.put(mSQLiteHelper.JOB_WORK_TYPE, jsonObject.getString(mSQLiteHelper.JOB_WORK_TYPE));
////                contentValues.put(mSQLiteHelper.JOB_MINIMUM_SALARY, jsonObject.getString(mSQLiteHelper.JOB_MINIMUM_SALARY));
////                contentValues.put(mSQLiteHelper.JOB_MAXIMUM_SALARY, jsonObject.getString(mSQLiteHelper.JOB_MAXIMUM_SALARY));
////                contentValues.put(mSQLiteHelper.JOB_START_DATE, jsonObject.getString(mSQLiteHelper.JOB_START_DATE));
////                contentValues.put(mSQLiteHelper.JOB_END_DATE, jsonObject.getString(mSQLiteHelper.JOB_END_DATE));
////                contentValues.put(mSQLiteHelper.JOB_LOCATIONS, jsonObject.getString(mSQLiteHelper.JOB_LOCATIONS));
////                contentValues.put(mSQLiteHelper.JOB_JOB_SUMMARY, jsonObject.getString(mSQLiteHelper.JOB_JOB_SUMMARY));
////                contentValues.put(mSQLiteHelper.JOB_APPLY_URL, jsonObject.getString(mSQLiteHelper.JOB_APPLY_URL));
//        }
//        Log.d("DB", "insertData()-id : After try-catch");
//
//        long id = db.insert(mSQLiteHelper.TABLE_USERS, null, contentValues);
//        Log.d("DB", "insertData()-id : " + id);
//        return id;// return column id
//    }

//    public String selectData(String email, String pass) {
//
//        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
//
//        // select _id,name,pwd from Students;
//        String[] columns = {mSQLiteHelper.COLUMN_EMAIL, mSQLiteHelper.COLUMN_PASSWORD};
//        Cursor cursor = db.query(mSQLiteHelper.TABLE_USERS, null, null, null,
//                null, null, null);
//        StringBuffer buffer = new StringBuffer();
//        while (cursor.moveToNext()) {
//            int id = cursor.getInt(cursor
//                    .getColumnIndex(mSQLiteHelper.COLUMN_ID));
//            String name = cursor.getString(cursor
//                    .getColumnIndex(mSQLiteHelper.COLUMN_NAME));
//            String password = cursor.getString(cursor
//                    .getColumnIndex(mSQLiteHelper.COLUMN_PASSWORD));
//            buffer.append(id + " " + name + " " + pass + "\n");
//        }
//
//        return buffer.toString();
//    }
//
//    public String selectDataWhere(String nameArg) {
//
//        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
//
//        // select name,pwd from Students where name= 'nameArg';
//        String[] columns = {mSQLiteHelper.COLUMN_NAME,
//                mSQLiteHelper.COLUMN_PASSWORD};
//        Cursor cursor = db.query(mSQLiteHelper.TABLE_USERS, columns,
//                mSQLiteHelper.COLUMN_NAME + "=" + "'" + nameArg + "'", null,
//                null, null, null);
//        StringBuffer buffer = new StringBuffer();
//        while (cursor.moveToNext()) {
//            String name = cursor.getString(cursor
//                    .getColumnIndex(mSQLiteHelper.COLUMN_NAME));
//            String pass = cursor.getString(cursor
//                    .getColumnIndex(mSQLiteHelper.COLUMN_PASSWORD));
//            buffer.append(name + " " + pass + "\n");
//        }
//
//        return buffer.toString();
//    }
//
//    public ArrayList<ArrayList<String>> selectDataWhere(String email, String passArg, boolean withUserObect) {
//
//        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
//        Cursor cursor = db.query(mSQLiteHelper.TABLE_USERS, null,
//                mSQLiteHelper.COLUMN_EMAIL + "=" + "'" + email + "'", null,
//                null, null, null);
//
//
//        Log.d("check", "mSQLiteHelper.COLUMN_PASSWORD: " + cursor);
//
//        if (cursor != null) {
//            Log.d("check", "cursor.getCount():" + cursor.getCount());
//        }
//
//
//        ArrayList<String> user;
//        ArrayList<ArrayList<String>> userItem = new ArrayList<>();
//        String[] cur = cursor.getColumnNames();
//        // Log.d("account", "cursor: " + cursor.getColumnCount()+" and :"+ cursor.getColumnNames().toString()+" and "+cursor.moveToNext());
//        //Log.d("account", "cursor: " + cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_NAME))+" , "+cur[0] + ", " + cur[1] + ", " + cur[2] + ", " + cur[3] + ", " + cur[4] );
//        Log.d("pd", "NAME: " + selectData(email, passArg));
//       // int id = 0;
//        while (cursor.moveToNext()) {
//            user = new ArrayList<String>();
//            user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_NAME)));//name
//            user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_EMAIL)));//email
//            user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_PASSWORD)));//pass
//           // user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_TOTAL)));//object
//            user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_DATA)));//object
//            Log.d("account", "user.add(): " + user);
//            userItem.add(user);
//        }
//
//        Log.d("account", "USER-ITEM-ARRAYLIST: " + userItem.size());//email)));
//
//        return userItem;
//    }
//

















//
//
//
//
//    public ArrayList<ArrayList<String>> selectDataWhere(String email, String passArg, boolean withUserObect) {
//
//        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
//        // String[] columns = {mSQLiteHelper.COLUMN_NAME, mSQLiteHelper.COLUMN_EMAIL, mSQLiteHelper.COLUMN_PASSWORD};
//
//        // select _id from Students where name= 'nameArg' and pass = 'passArg';
//        // String[] selectionArgs = {email, passArg};
//        //Log.d("account", "columns: " + columns);
//
//        Log.d("account", "" + mSQLiteHelper.COLUMN_EMAIL + "=" + "'" + email + "' and "
//                + mSQLiteHelper.COLUMN_PASSWORD + "=" + "'" + passArg + "'");
//        // Log.d("account", "mSQLiteHelper.COLUMN_PASSWORD: " + passArg);
//
//        String countQuery = "SELECT  * FROM " + mSQLiteHelper.TABLE_USERS+"  WHERE "
//                 + mSQLiteHelper.COLUMN_PASSWORD+" = \""+passArg+ "\"";
//
//        Cursor cursor = db.rawQuery(countQuery, null);
//        Log.d("check", "mSQLiteHelper.COLUMN_PASSWORD: " + cursor);
//
//        if (cursor != null) {
//            Log.d("check", "Count: " + countQuery);
//            //cursor.close();
//        }
//
//
////        // fist question mark(?) corresponds to first object in selectionArgs
////        // array(nameArg).
////        // second question mark(?) corresponds to second object in selectionArgs
////        // array (passArg).
////        Cursor cursor = db.query(mSQLiteHelper.TABLE_USERS, null,
////                mSQLiteHelper.COLUMN_EMAIL+ "=" + "'" + email + "' and "
////                        + mSQLiteHelper.COLUMN_PASSWORD+ "=" + "'" + passArg + "'", null,
////                null, null, null);
//
//        ArrayList<String> user;
//        ArrayList<ArrayList<String>> userItem = new ArrayList<>();
//        String[] cur = cursor.getColumnNames();
//        // Log.d("account", "cursor: " + cursor.getColumnCount()+" and :"+ cursor.getColumnNames().toString()+" and "+cursor.moveToNext());
//        Log.d("account", "cursor: " + cur[0] + ", " + cur[1] + ", " + cur[2] + ", " + cur[3] + ", " + cur[4] );
//
//       // Log.d("account", "USER-ARRAYLIST: " + user);//email)));
//       // userItem.add(user);
//       // Log.d("account", "USER-ITEM-ARRAYLIST: " + userItem);//email)));
//
//         Log.d("pd", "NAME: " + selectData(email, passArg));
//
//
//        int id = 0;
//
//        //if(withUserObect==true) {
//        //Log.d("account", "before while (cursor.moveToNext()): " + cursor.moveToNext());//email)));
//
//        while (cursor.moveToNext()) {
//            user = new ArrayList<String>();
////            Log.d("DB", "json: " + cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_POSITION_TITLE)) + " -  " +
////                    cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_ORGANISATION_NAME)) + " -  " +
////                    cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_AGENCY)) + " -  " +
////                    cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_WHO_MAY_APPLY)) + " -  " +
////                    cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_WORK_SCHEDULE)) + " -  " +
////                    cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_WORK_TYPE)) + " -  " +
////                    cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_MINIMUM_SALARY)) + " -  " +
////                    cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_MAXIMUM_SALARY)) + " -  " +
////                    cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_START_DATE)) + " -  " +
////                    cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_END_DATE)) + " -  " +
////                    cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_LOCATIONS)) + " -  " +
////                    cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_JOB_SUMMARY)) + " -  " +
////                    cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_APPLY_URL)) + "\n");
//
////                Log.d("pd", "NAME: " + cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_NAME)));
////                Log.d("pd", "EMAIL: " + cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_EMAIL)));//email)));
////                Log.d("pd", "PASS: " + cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_PASSWORD)));//email)));
////                Log.d("pd", "TITLE: " + cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_POSITION_TITLE)));//email)));
////
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_NAME)));//name
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_EMAIL)));//email
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_PASSWORD)));//pass
////
////
////                // Log.d("account", "cursor: " +cursor.getColumnIndex(mSQLiteHelper.COLUMN_NAME)+" value: "+cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_NAME)));
//                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_NAME)));//name
//                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_EMAIL)));//email
//                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_PASSWORD)));//pass
//            user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_TOTAL)));//object
//            user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_DATA)));//object
//
////            user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_POSITION_TITLE)));//object
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_ORGANISATION_NAME)));//object
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_AGENCY)));//object
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_WHO_MAY_APPLY)));//object
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_WORK_SCHEDULE)));//object
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_WORK_TYPE)));//object
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_MINIMUM_SALARY)));//object
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_MAXIMUM_SALARY)));//object
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_START_DATE)));//object
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_END_DATE)));//object
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_LOCATIONS)));//object
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_JOB_SUMMARY)));//object
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_APPLY_URL)));//object
//                Log.d("account", "user.add(): " + user);
//
//                userItem.add(user);
//                //Log.d("account", "UseriTEM: " + userItem.size());
//            // Log.d("account", "while (cursor.moveToNext()): " + cursor.moveToNext());//email)));
//
//        }
////        } else{
////            while (cursor.moveToNext()) {
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_NAME)));//name
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_EMAIL)));//email
////                user.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_PASSWORD)));//pass
////
////                userItem.add(user);
////                id++;
////            }
////
////        }
//
////        if (id > 1) {
////            user = null;
////        }
//
//        //Log.d("account", "USER-ARRAYLIST: " + user.size());//email)));
//       // userItem.add(user);
//        Log.d("account", "USER-ITEM-ARRAYLIST: " + userItem.size());//email)));
//
//        return userItem;
//    }


    public int deleteFromTable(long userId, int dataPostion) {

        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

        // delete from Students where data_id= dataPostion and userid = userId;

        int count = db.delete(mSQLiteHelper.TABLE_DATA,
                mSQLiteHelper.COLUMN_DATA_ID + "= "+dataPostion+" AND "
                        + mSQLiteHelper.COLUMN_DATA_from_USER + "= "+(int)userId, null);
        return count;
    }

    public int deleteFromTable(long userId, String data) {

        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

        // delete from Students where data_id= dataPostion and userid = userId;

        int count = db.delete(mSQLiteHelper.TABLE_DATA,
                mSQLiteHelper.JOB_DATA + "= "+data+" AND "
                        + mSQLiteHelper.COLUMN_DATA_from_USER + "= "+(int)userId, null);
        return count;
    }



    public int updateName(String oldValue, String newValue) {

        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(mSQLiteHelper.COLUMN_NAME, newValue);

        // update table Students set name="newValue" where name= 'oldValue';
        String[] selectionArgs = {oldValue};
        int count = db.update(mSQLiteHelper.TABLE_USERS, values,
                mSQLiteHelper.COLUMN_NAME + "=?", selectionArgs);
        return count;
    }

	

	/*
     * Class SQLiteOpenHelper A helper class to manage database creation and
	 * version management. You create a subclass implementing
	 * onCreate(SQLiteDatabase), onUpgrade(SQLiteDatabase, int, int) and
	 * optionally onOpen(SQLiteDatabase), and this class takes care of opening
	 * the database if it exists, creating it if it does not, and upgrading it
	 * as necessary. Transactions are used to make sure the database is always
	 * in a sensible state.
	 */

    static class SQLiteHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "GovJobDBsql";
        //user table
        private static final String TABLE_USERS = "Users";
        private static final int DATABASE_VERSION = 7;
        private static final String COLUMN_ID = "_id";
        private static final String COLUMN_NAME = "Name";
        private static final String COLUMN_PASSWORD = "Pwd";
        private static final String COLUMN_EMAIL = "Email";
        private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS
                + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " VARCHAR(225), "
                + COLUMN_PASSWORD + " VARCHAR(225), "
                + COLUMN_EMAIL + " VARCHAR(225));";


        //data table
        private static final String TABLE_DATA= "Data";
        private static final String COLUMN_DATA_ID = "Data_id";
        private static final String COLUMN_DATA_from_USER = "_id";//foreign key that reference _id in user table
        private static final String JOB_DATA = "data";
        private static final String CREATE_TABLE_DATA = "CREATE TABLE " + TABLE_DATA
                + " (" + COLUMN_DATA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COLUMN_DATA_from_USER + " INTEGER, "
                + JOB_DATA+ " TEXT,"
                +"FOREIGN KEY("+COLUMN_DATA_from_USER+") REFERENCES "
                +TABLE_USERS+"("+COLUMN_ID+"));";


         // private static final String JOB_TOTAL = "TotalJobSave";
//        private static final String JOB_POSITION_TITLE = "JobTitle";
//        private static final String JOB_ORGANISATION_NAME = "OrganizationName";
//        private static final String JOB_AGENCY = "AgencySubElement";
//        private static final String JOB_WHO_MAY_APPLY = "WhoMayApplyText";
//        private static final String JOB_WORK_SCHEDULE = "WorkSchedule";
//        private static final String JOB_WORK_TYPE = "WorkType";
//        private static final String JOB_MINIMUM_SALARY = "SalaryMin";
//        private static final String JOB_MAXIMUM_SALARY = "SalaryMax";
//        private static final String JOB_START_DATE = "StartDate";
//        private static final String JOB_END_DATE = "EndDate";
//        private static final String JOB_LOCATIONS = "Locations";
//        private static final String JOB_JOB_SUMMARY = "JobSummary";
//        private static final String JOB_APPLY_URL = "ApplyOnlineURL";

//
//        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_USERS
//                + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + COLUMN_NAME + " VARCHAR(225), "
//                + COLUMN_PASSWORD + " VARCHAR(225),"
//                + COLUMN_EMAIL + " VARCHAR(225),"
//                + JOB_DATA  + " TEXT,"
//                + JOB_TOTAL + " VARCHAR(225) "+
////                + JOB_POSITION_TITLE + " VARCHAR(225),"
////                + JOB_ORGANISATION_NAME + " VARCHAR(225), "
////                + JOB_AGENCY + " VARCHAR(225),"
////                + JOB_WHO_MAY_APPLY + " VARCHAR(225), "
////                + JOB_WORK_SCHEDULE + " VARCHAR(225),"
////                + JOB_WORK_TYPE + " VARCHAR(225), "
////                + JOB_MINIMUM_SALARY + " VARCHAR(225),"
////                + JOB_MAXIMUM_SALARY + " VARCHAR(225),"
////                + JOB_START_DATE + " VARCHAR(225), "
////                + JOB_END_DATE + " VARCHAR(225),"
////                + JOB_LOCATIONS + " VARCHAR(225),"
////                + JOB_JOB_SUMMARY + " VARCHAR(225), "
////                + JOB_APPLY_URL + " VARCHAR(225) " +
//                ");";


        private static final String DROP_TABLE_USERS = "DROP TABLE IF EXISTS "
                + TABLE_USERS;
        private static final String DROP_TABLE_DATA = "DROP TABLE IF EXISTS "
                + TABLE_DATA;


        public SQLiteHelper(Context context) {
            // SQLiteOpenHelper(Context context, String name,
            // SQLiteDatabase.CursorFactory factory, int version)
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d("SQLITE HELPER", "Constructor: SQLiteHelper()");

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                // execSQL() @return void, therefore dont use it with any sql
                // statement(i.e: select or anything like) that return values
                db.execSQL(CREATE_TABLE_USERS);
                db.execSQL(CREATE_TABLE_DATA);

                Log.d("SQLITE HELPER", "onCreate()");
            } catch (SQLException e) {
                Log.d("DB", "" + e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_TABLE_USERS);
                db.execSQL(DROP_TABLE_DATA);

                Log.d("SQLITE HELPER", "onUpgrade()");
                onCreate(db);
            } catch (SQLException e) {
                Log.d("DB", "" + e);
            }

        }

    }

}
