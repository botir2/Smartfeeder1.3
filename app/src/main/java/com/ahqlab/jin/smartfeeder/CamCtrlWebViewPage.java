package com.ahqlab.jin.smartfeeder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class CamCtrlWebViewPage extends Activity implements View.OnClickListener {
    String TAG = "CamCtrlWebViewPage";
    private WebView mCamWebView, mCtrlWebView;
    static String MQTTHOST  = "tcp://192.168.0.5:1883";
    String chanel_1 = "$feeder/feed";
    private MqttAndroidClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createCamWebView(savedInstanceState);
     //   createCtrlWebView(savedInstanceState);
        Button feedingBtn = (Button)findViewById( R.id.RemoteControl);
        feedingBtn.setOnClickListener( this );
        mqttconncetion();
    }


    // connection to MQTT sercver
    public void mqttconncetion(){
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient( this.getApplicationContext(), MQTTHOST, clientId );
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(CamCtrlWebViewPage.this,"Conneted", Toast.LENGTH_LONG).show();
                    Log.d( TAG,"Conneted" );
                    //sendMessagestatus();
                   // setSubscrition();
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(CamCtrlWebViewPage.this,"Disconnect", Toast.LENGTH_LONG).show();
                    Log.d( TAG,"Disconnect" );
                }
            });
        }catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        String topic = chanel_1;
        String message = "hi";
        try {
            client.publish(topic, message.getBytes(),2,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void createCamWebView(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cam_ctrl_web_view);

        mCamWebView = (WebView) findViewById(R.id.cameraWebView);

        WebSettings webSettings = mCamWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mCamWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mCamWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Alert")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message,
                                       final JsResult result) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Confirm")
                        .setMessage(message)
                        .setPositiveButton("Yes",
                                new AlertDialog.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton("No",
                                new AlertDialog.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.cancel();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }
        });

       // mCamWebView.loadUrl("http://192.168.5.102:8081");
        mCamWebView.loadUrl("http://192.168.0.5:8000");

        mCamWebView.getSettings().setBuiltInZoomControls(true);
        mCamWebView.getSettings().setSupportZoom(true);
        mCamWebView.getSettings().setUseWideViewPort(true);//must be true
        mCamWebView.setInitialScale(1);
    }




    private void createCtrlWebView(Bundle savedInstanceState)
    {
       // mCtrlWebView = (WebView) findViewById(R.id.ctrlWebView);

        WebSettings webSettings = mCtrlWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mCtrlWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mCtrlWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Alert")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message,
                                       final JsResult result) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Confirm")
                        .setMessage(message)
                        .setPositiveButton("Yes",
                                new AlertDialog.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton("No",
                                new AlertDialog.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.cancel();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }
        });


        //mCtrlWebView.loadUrl("http://zeany.tistory.com");
        //mCtrlWebView.loadUrl("http://192.168.10.100:8082");
       // mCtrlWebView.loadUrl("http://192.168.10.102:8083/app/gpio-list");
        mCtrlWebView.loadUrl("http://192.168.0.5:8000");

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCtrlWebView.canGoBack()) {
                mCtrlWebView.goBack();
                finish();
                return false;
            }
        } else {
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

}
