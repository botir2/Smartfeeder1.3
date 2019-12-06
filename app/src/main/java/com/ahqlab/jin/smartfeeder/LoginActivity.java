package com.ahqlab.jin.smartfeeder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;

    private Button btnLogin;
    private EditText edtId;
    private EditText edtPw;

    private SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
    }

    private void initialize()
    {
        sharedPreference = new SharedPreference(this);
        sharedPreference.setIDString("1");
        sharedPreference.setPWString("1");

        btnLogin = (Button) findViewById(R.id.login_button);
        edtId = (EditText) findViewById(R.id.login_id_edit);
        edtPw = (EditText) findViewById(R.id.login_pw_edit);

        btnLogin.setOnClickListener(onClickListener);
        btnLogin.setOnTouchListener(onTouchListener);

        /*
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if ( !mBluetoothAdapter.isEnabled() )
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        */


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            if ( edtId.getText().toString().equals(sharedPreference.getIDString()) &&
                    edtPw.getText().toString().equals(sharedPreference.getPWString()) )
            {
                /*
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                */
                setContentView(R.layout.activity_main_cam_ctrl_web_view);

                Intent intentCamCtrlWebView = new Intent(LoginActivity.this, CamCtrlWebViewPage.class);
                startActivity(intentCamCtrlWebView);
                finish();
            }
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    btnLogin.setBackgroundResource(R.drawable.login_btn_2_over);
                    break;
                case MotionEvent.ACTION_UP:
                    btnLogin.setBackgroundResource(R.drawable.login_btn_2_release);
                    break;
                default:
                    break;
            }
            return false;
        }
    };

}
