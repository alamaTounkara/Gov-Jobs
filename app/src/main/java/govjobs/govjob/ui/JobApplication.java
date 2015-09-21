package govjobs.govjob.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import govjobs.govjob.util.Constants;
import govjobs.govjob.R;

public class JobApplication extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobapplication);
        Intent intent = getIntent();
        String url ="https://www.usajobs.gov/Applicant/Application/ApplyStart/"+intent.getStringExtra(Constants.JOB_ID);
        Log.d("URL", "url: "+url);


        mWebView = (WebView)findViewById(R.id.applyWebView);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String javascript = "javascript: document.getElementsByClassName('text-center')[0].style.display = 'none';void(0)";
                String javascript2 = "javascript: document.getElementsByClassName('text-center')[1].style.display = 'none';void(0)";
                view.loadUrl(javascript);
                view.loadUrl(javascript2);
            }
        });
        mWebView.loadUrl(url);

    }

}

