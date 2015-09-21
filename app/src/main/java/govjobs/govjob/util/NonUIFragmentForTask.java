package govjobs.govjob.util;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import govjobs.govjob.task.Task;

/**
 * Created by alamatounkara on 7/27/15.
 */
public class NonUIFragmentForTask extends Fragment {


    public Task mMyTask;
    private Activity mActivity;
    private CallBackInterface callbackInterface = null;


//    public NonUIFragmentForTask() {
//        onAttach(activity);
//    }


    public void startTask(String url) {
        if (mMyTask != null) {// cancel the task, even if its runnig
            mMyTask.cancel(true);
        } else {
            mMyTask = new Task(callbackInterface);
            mMyTask.execute(url);
        }
    }

    public void startTaskOnResume(String url) {
        /*this is used because of shared preferences.
        /*by using startTask() alone, after editing the sharepreferences, the activiy will not get
         * updating. becaus mMytask will just be cancelled.
         *
         * Now with this new method, onResume in activity will used a method that will call this
         * method to cancel the current task and run a new one.
         */

        if (mMyTask != null) {// cancel the task, even if its runnig
            mMyTask.cancel(true);
        }
        mMyTask = new Task(callbackInterface);
        mMyTask.execute(url);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbackInterface = (CallBackInterface) activity;

        if (mMyTask != null) {//check to see if user already started that thread
            mMyTask.onAttach(callbackInterface);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        callbackInterface = null;
        if (mMyTask != null) {//check to see if user already started that thread
            mMyTask.onDetach();
        }
    }


    @Override
    public void onActivityCreated(Bundle onSavedInstanceState) {
        super.onActivityCreated(onSavedInstanceState);
        setRetainInstance(true);//make this fragment undestructable
    }

}




