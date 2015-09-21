package govjobs.govjob.task;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import govjobs.govjob.util.ParserAdapter;
import govjobs.govjob.util.CallBackInterface;

/**
 *
 */
/*
Java has two kinds of classes for input and output (I/O): streams and readers/writers.

Streams (InputStream, OutputStream and everything that extends these) are for reading and writing
    binary data from files, the network, or whatever other device.

Readers and writers are for reading and writing text (characters). They are a layer on top of
    streams, that converts binary data (bytes) to characters and back, using a character encoding.

Reading data from disk byte-by-byte is very inefficient. One way to speed it up is to use a buffer:
instead of reading one byte at a time, you read a few thousand bytes at once, and put them in a buffer,
 in memory. Then you can look at the bytes in the buffer one by one.
 Created by alamatounkara on 7/27/15.
 */

public class Task extends AsyncTask<String, Integer, ArrayList<HashMap<String, String>>> {

    ParserAdapter mParserAdapter;
    int count = 0;
    // the length of our content
   // private int contentTotallength = -1; // return -1 when things go bad
  //  private int downloadCurrentSize = 0;// byte size of the ile
    private CallBackInterface mCallBackInterface = null;



    public Task(CallBackInterface activity) {
        mCallBackInterface = activity;
        mParserAdapter = new ParserAdapter();
    }

    //fragment onAttach() will call this method to past it the new Activity created
    public void onAttach(CallBackInterface activity) {
        this.mCallBackInterface = activity;
    }

    //fragment onDettach() will call this method to past it the Activity destroyed
    public void onDetach() {
        this.mCallBackInterface = null;
    }

    @Override
    protected void onPreExecute() {// run on main thread

        // we have a determinate number therefore we will have to change the
        // default undeterninate to false
        if (this.mCallBackInterface == null) {
            //mActivity might be null for sometime when your
            //sreen orientation is changing. because of that,
            ///we are using the if condition other we get a null pointer exception.
            //when our background thread is accessing that activity
        } else {
            this.mCallBackInterface.onPreExecute();
        }

    }


    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {
        //Jobs are searchable by keyword, location, agency, schedule, or any combination of these.
       String url1 = urls[0];

        URL url = null;
        URLConnection urlConnection = null; // getting the connection
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;// for reading the url content
        //final result
        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        BufferedReader bufferedReader = null;
        try {
            url = new URL(url1);

            urlConnection = url.openConnection();
            //file  total size
            //contentTotallength = urlConnection.getContentLength();
            // http type of connection
            httpURLConnection = (HttpURLConnection) urlConnection;
            //An InputStream reads raw octet (8 bit)-byte.Good us for image like file
            inputStream = httpURLConnection.getInputStream();
            //while InputStreamReader reads as char, good used for character
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            //The Java.io.BufferedReader class reads text from a character-input stream, buffering
            //characters so as to provide for the efficient reading of characters, arrays, and lines
            //The buffer is a storage container which stores part or all of the streamed data and
            // feeds this to the output device.
            bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String read;
            while ((read = bufferedReader.readLine()) != null) {
                stringBuffer.append(read);
            }
            result = mParserAdapter.processJSON(stringBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static final String LOG = "MyAppWidgetProvider";

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> result) {// run on main thread
        Log.d(LOG, "Task==>>onPostExecute()- result: " + result.size());

        if (result != null) {// @param result is the @return value
            if (this.mCallBackInterface == null) {
                //mActivity might be null for sometime when your
                //sreen orientation is changing. because of that,
                ///we are using the if condition other we get a null pointer exception.
                //when our background thread is accessing that activity

            } else {
                this.mCallBackInterface.onPostExecute(result);
            }

        }

    }


}
