package govjobs.govjob.util;

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
 * A helper class to manage database creation and version management.
 * You create a subclass implementing onCreate(SQLiteDatabase), onUpgrade(SQLiteDatabase, int, int)
 * and optionally onOpen(SQLiteDatabase), and this class takes care of opening the database if it exists,
 * creating it if it does not, and upgrading it as necessary. Transactions are used to make sure the
 * database is always in a sensible state.
 */

public class SQLiteDatabaseAdapter {

    private SQLiteHelper mSQLiteHelper;

    public SQLiteDatabaseAdapter(Context context) {
        mSQLiteHelper = new SQLiteHelper(context);

    }


    /**
     * Insert user in the table TABLE_USERS
     *
     * @param name:     name of the user
     * @param email:    email of the user
     * @param password: password of the user
     * @return user unique id
     */
    public long insertUser(String name, String email, String password) {

        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(mSQLiteHelper.COLUMN_NAME, name);
        contentValues.put(mSQLiteHelper.COLUMN_EMAIL, email);
        contentValues.put(mSQLiteHelper.COLUMN_PASSWORD, password);

        long id = db.insert(mSQLiteHelper.TABLE_USERS, null, contentValues);
        return id;// return column id
    }


    /**
     * Insert job data(json object) in the table TABLE_DATA
     *
     * @param userId:     user id that the data belongs to.
     * @param jsonObject: is a jsonObject represent the job data itself.
     *                    We will transform this in to string by using toString() and insert it in the database
     * @return row number of the object
     */
    public long insertData(int userId, JSONObject jsonObject) {
        if ((jsonObject != null) && (userId >= 0)) {
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
     *
     * @param email: user email address
     * @param pass:  user password
     * @return a unique user id
     */
    public long selectUser(String email, String pass) {
        if ((email != null) && (pass != null)) {
            long userid = -1; //-1 mean there was no user found
            SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

            String[] columns = {mSQLiteHelper.COLUMN_ID};
            String[] condition = {email, pass};
            Cursor cursor = db.query(mSQLiteHelper.TABLE_USERS, columns,
                    mSQLiteHelper.COLUMN_EMAIL + " = ? AND " +
                            mSQLiteHelper.COLUMN_PASSWORD + " = ?", condition, null, null, null);

            while (cursor.moveToNext()) {
                userid = cursor.getInt(cursor.getColumnIndex(mSQLiteHelper.COLUMN_ID));
            }
            return userid;
        }
        return -1;//unsuccessful
    }


    /**
     * Selecting user name and pass from the table TABLE_USERS
     *
     * @param email: user email address
     * @param pass:  user password
     * @return string array with with lenght=2. 1 is email, 1 pass
     */
    public String[] selectUserNamePass(String email, String pass) {
        if ((email != null) && (pass != null)) {
            //size = 2 cuz we are expecting only 1 name and 1 pass. if there is more, we have duplicate rows
            String[] user = new String[2];
            SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

            // select _id,name,pwd from Students;
            String[] columns = {mSQLiteHelper.COLUMN_EMAIL, mSQLiteHelper.COLUMN_PASSWORD};
            String[] condition = {email, pass};
            Cursor cursor = db.query(mSQLiteHelper.TABLE_USERS, columns,
                    mSQLiteHelper.COLUMN_EMAIL + " = ? AND " +
                            mSQLiteHelper.COLUMN_PASSWORD + " = ?", condition, null, null, null);

            int i = 0;
            while (cursor.moveToNext()) {
                user[0] = cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_EMAIL));
                user[1] = cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_PASSWORD));
                i++;
            }
            if (i > 1) return null; //we have duplicate row
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
        if ((userId > -1)) {
            //size = 2 cuz we are expecting only 1 name and 1 pass. if there is more, we have duplicate rows
            SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

            // select _id,name,pwd from Students;
            String[] columns = {mSQLiteHelper.COLUMN_NAME};
            Cursor cursor = db.query(mSQLiteHelper.TABLE_USERS, columns,
                    mSQLiteHelper.COLUMN_ID + " = +" + userId, null, null, null, null);

            int i = 0;
            String name = null;
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_NAME));
                i++;
            }
            if (i > 1) return null; //we have duplicate row
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
        if ((userid >= 0)) {
            SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

            String[] columns = {mSQLiteHelper.JOB_DATA, mSQLiteHelper.COLUMN_NAME};
            //  int[] condition={userid};

            Cursor cursor = db.query(mSQLiteHelper.TABLE_USERS + ", " + mSQLiteHelper.TABLE_DATA,
                    columns, mSQLiteHelper.TABLE_DATA + "." + mSQLiteHelper.COLUMN_DATA_from_USER
                            + " = " + userid + " AND " + mSQLiteHelper.TABLE_USERS + "." + mSQLiteHelper.COLUMN_ID + " = " + userid
                    , null, null, null, null);

            ArrayList<String> data = new ArrayList<String>();
            int i = 0;
            while (cursor.moveToNext()) {
                if (i == 0) {
                    //data index 0, will always be our user name
                    data.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_NAME)));
                }
                i++;
                data.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_DATA)));//object
            }
            if (data.size() > 1) return data;

        }
        return null;

    }


    /**
     * Selecting user data from the table TABLE_DATA
     *
     * @param userid: user id od the user
     * @return ArrayList<HashMap<Integer,String>> contain all user data
     */
    public ArrayList<HashMap<Integer, String>> selectUserHashMapData(int userid) {
        //select data from user,data where data.userid = userid and user.id=userid
        if ((userid >= 0)) {
            SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

            String[] columns = {mSQLiteHelper.JOB_DATA, mSQLiteHelper.COLUMN_NAME,
                    mSQLiteHelper.COLUMN_DATA_ID, mSQLiteHelper.TABLE_DATA + "." + mSQLiteHelper.COLUMN_DATA_from_USER};

            Cursor cursor = db.query(mSQLiteHelper.TABLE_USERS + ", " + mSQLiteHelper.TABLE_DATA,
                    columns, mSQLiteHelper.TABLE_DATA + "." + mSQLiteHelper.COLUMN_DATA_from_USER
                            + " = " + userid + " AND " + mSQLiteHelper.TABLE_USERS + "." + mSQLiteHelper.COLUMN_ID + " = " + userid
                    , null, null, null, null);

            HashMap<Integer, String> currenValue;
            ArrayList<HashMap<Integer, String>> data = new ArrayList<HashMap<Integer, String>>();
            int i = 0;
            while (cursor.moveToNext()) {
                if (i == 0) {
                    HashMap<Integer, String> currenUserValue = new HashMap<Integer, String>();
                    //data index 0, will always be our user name
                    currenUserValue.put(cursor.getInt(cursor.getColumnIndex(mSQLiteHelper.COLUMN_DATA_from_USER)),
                            cursor.getString(cursor.getColumnIndex(mSQLiteHelper.COLUMN_NAME)));
                    data.add(currenUserValue);//object
                }
                i++;
                currenValue = new HashMap<Integer, String>();
                currenValue.put(cursor.getInt(cursor.getColumnIndex(mSQLiteHelper.COLUMN_DATA_ID)),
                        cursor.getString(cursor.getColumnIndex(mSQLiteHelper.JOB_DATA)));

                data.add(currenValue);//object
            }
            if (data.size() > 1) return data;

        }
        return null;
    }


    public int deleteFromTable(long userId, int dataPostion) {

        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

        // delete from Students where data_id= dataPostion and userid = userId;

        int count = db.delete(mSQLiteHelper.TABLE_DATA,
                mSQLiteHelper.COLUMN_DATA_ID + "= " + dataPostion + " AND "
                        + mSQLiteHelper.COLUMN_DATA_from_USER + "= " + (int) userId, null);
        return count;
    }

    public int deleteFromTable(long userId, String data) {

        SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

        // delete from Students where data_id= dataPostion and userid = userId;

        int count = db.delete(mSQLiteHelper.TABLE_DATA,
                mSQLiteHelper.JOB_DATA + "= " + data + " AND "
                        + mSQLiteHelper.COLUMN_DATA_from_USER + "= " + (int) userId, null);
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
        private static final String TABLE_DATA = "Data";
        private static final String COLUMN_DATA_ID = "Data_id";
        private static final String COLUMN_DATA_from_USER = "_id";//foreign key that reference _id in user table
        private static final String JOB_DATA = "data";
        private static final String CREATE_TABLE_DATA = "CREATE TABLE " + TABLE_DATA
                + " (" + COLUMN_DATA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DATA_from_USER + " INTEGER, "
                + JOB_DATA + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_DATA_from_USER + ") REFERENCES "
                + TABLE_USERS + "(" + COLUMN_ID + "));";

        private static final String DROP_TABLE_USERS = "DROP TABLE IF EXISTS "
                + TABLE_USERS;
        private static final String DROP_TABLE_DATA = "DROP TABLE IF EXISTS "
                + TABLE_DATA;


        public SQLiteHelper(Context context) {
            // SQLiteOpenHelper(Context context, String name,
            // SQLiteDatabase.CursorFactory factory, int version)
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                // execSQL() @return void, therefore dont use it with any sql
                // statement(i.e: select or anything like) that return values
                db.execSQL(CREATE_TABLE_USERS);
                db.execSQL(CREATE_TABLE_DATA);
            } catch (SQLException e) {
                Log.d("DB", "" + e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_TABLE_USERS);
                db.execSQL(DROP_TABLE_DATA);
                onCreate(db);
            } catch (SQLException e) {
                Log.d("DB", "" + e);
            }

        }

    }

}
