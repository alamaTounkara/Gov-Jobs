package govjobs.govjob.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import govjobs.govjob.R;
import govjobs.govjob.util.CallBackInterface;

public class ApplyDialogBox extends DialogFragment {
    /* DialogFragment: A fragment that displays a dialog window, floating on top of its activity's
       * window. This fragment contains a Dialog object, which it displays as appropriate based on the
       * fragment's state. Control of the dialog (deciding when to show, hide, dismiss it) should be
       * done through the API here, not with direct calls on the dialog.
       * Implementations should override this class and implement
       * onCreateView(LayoutInflater, ViewGroup, Bundle) to supply the content of the dialog.
       * Alternatively, they can override onCreateDialog(Bundle) to create an entirely custom dialog,
        * such as an AlertDialog, with its own content.
       */
    CallBackInterface comm;
    String mydata ="";
    private WebView mWebView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("ApplyDialogBox", "onAttach(Activity activity) called");

        //comm = (CallBackInterface) activity;
        Log.d("ApplyDialogBox", "onAttach(Activity activity) finish");

    }

    /* Instead of (or in addition to) implementing onCreateView(LayoutInflater, ViewGroup, Bundle)
     * to generate the view hierarchy inside of a dialog, you may implement onCreateDialog(Bundle)
     * to create your own custom Dialog object.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /*onCreateDialog() gets call after onCreate() in fragment and onCreateView()
        * That is wherer you define your dialog box element, in the older way
        */
        Log.d("ApplyDialogBox", "onCreateDialog(Bundle savedInstanceState) called");


        //create a dialog box
        AlertDialog.Builder builder = new  AlertDialog.Builder(getActivity());//@param is context
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.applydialogbox_fragment,null);
        builder.setView(view);
        mWebView = (WebView) view.findViewById(R.id.applyWebView);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.loadUrl("javascript:(function() { " +
                        "document.getElementsById('infosidebarinner').style.display=\"none\"; " +
                        "})()");
            }
        });
        mWebView.loadUrl("https://www.usajobs.gov/GetJob/ViewDetails/411047700?PostingChannelID=RESTAPI");
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.setWebViewClient(new WebViewClient(){
//            public void onPageFinished(WebView view, String url)
//            {
//                mWebView.loadUrl("javascript:document.getElementById('header').style.display = \"none\";");
//            }//javascript:document.getElementById(id).style.display = 'none';
//        });
//        mWebView.loadUrl("https://www.usajobs.gov/GetJob/ViewDetails/411047700?PostingChannelID=RESTAPI");
//
////    });
////
//        mWebView.loadUrl("http://www.google.com");
        //now let create our dialog box characteristics
        builder.setTitle("Apply...");//TITLE
        /*******for jsut asking a simple question content to the user**********/
        /* builder.setMessage("Do you want to see my dialog box msg?"); */

        /*******For a list of item as content used as dialog for user to take one of them**********/
        /* final String[] data = getResources().getStringArray(R.array.days);
         builder.setItems(R.array.days, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 comm.communicate(data[which]);
             }
         });
       */
        Log.d("ApplyDialogBox", "onCreateDialog(Bundle savedInstanceState) middle 1");

        /*******For a list of item as content used as dialog for user to take one of them**********/
       final int[] data = {1,2,3,4,5,4,5,5,6};

        //'null' is an array that allows us to specify which items are pre-selected. but here we dont need it
//        builder.setMultiChoiceItems(R.array.days,null, new DialogInterface.OnMultiChoiceClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//
//                mydata += "; "+data[which];
//                comm.passFilterData(mydata);
//
//            }
//        });

        Log.d("ApplyDialogBox", "onCreateDialog(Bundle savedInstanceState) middle 2");

        builder.setNegativeButton(R.string.no_dialogbtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //comm.passFilterData("NO BUTTON WAS CLICKED!");
            }
        });

        builder.setPositiveButton(R.string.yes_dialogbtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // comm.passFilterData("YES BUTTON WAS CLICKED!");
            }
        });
        Log.d("ApplyDialogBox", "onCreateDialog(Bundle savedInstanceState) finished");

        Dialog dialog = builder.create(); //NOW OUR DIALOG BOX IS SET
        return dialog;
    }


}
