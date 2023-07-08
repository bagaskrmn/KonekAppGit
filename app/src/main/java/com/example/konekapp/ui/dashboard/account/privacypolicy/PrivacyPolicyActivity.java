package com.example.konekapp.ui.dashboard.account.privacypolicy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.example.konekapp.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private ImageView PrivacyPolicyBackAction;
    private WebView WvPrivacyPolicy;
    private View decorView;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        PrivacyPolicyBackAction = findViewById(R.id.privacyPolicyBackAction);
        WvPrivacyPolicy = findViewById(R.id.wvPrivacyPolicy);

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat");
        pd.show();

        PrivacyPolicyBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrivacyPolicyActivity.super.onBackPressed();
            }
        });

        loadWebView();


    }

    private void loadWebView() {
        pd.dismiss();
        WvPrivacyPolicy.setWebViewClient(new WebViewClient());
        WvPrivacyPolicy.loadUrl("https://www.privacypolicies.com/live/569fdf74-19a6-4ca5-90bd-9a63f5d42193");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }
    private int hideSystemBars() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}