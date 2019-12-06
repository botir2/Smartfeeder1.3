package com.ahqlab.jin.smartfeeder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class IntroActivity extends Activity {

    Handler mHandler = new Handler();
    ImageSwitcher mSwitcherLabel, mSwitcherText;
    TextView mTextViewIntro;
    boolean mRunning;
    Thread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        // intro label
        mSwitcherLabel = (ImageSwitcher) findViewById(R.id.img_switcher_intro_label);

        mSwitcherLabel.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView view = new ImageView(getApplicationContext());
                //view.setBackgroundColor(Color.WHITE);
                view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                view.setLayoutParams(
                        new ImageSwitcher.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                return view;
            }
        });

        mSwitcherLabel.setInAnimation(this, android.R.anim.fade_in);
        mSwitcherLabel.setOutAnimation(this, android.R.anim.fade_out);

        // intro text
        mTextViewIntro = (TextView) findViewById(R.id.intro_text_view);

        mSwitcherText = (ImageSwitcher) findViewById(R.id.img_switcher_intro_text);
        mSwitcherText.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView view = new ImageView(getApplicationContext());
                //view.setBackgroundColor(Color.WHITE);
                view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                view.setLayoutParams(
                        new ImageSwitcher.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                return view;
            }
        });

        startAnimation();
    }

    private void startLoginActivity() {
        Intent intentLogin = new Intent(IntroActivity.this, LoginActivity.class);
        startActivity(intentLogin);

        // close activity
        finish();

        stopAnimation();
    }

    private void startAnimation() {
        mSwitcherLabel.setVisibility(View.VISIBLE);
        mSwitcherText.setVisibility(View.VISIBLE);

        mThread = new ImageThread();
        mThread.start();
    }

    private void stopAnimation() {
        mRunning = false;

        try {
            mThread.join();
        } catch (InterruptedException ex) {

        }

        mSwitcherLabel.setVisibility(View.INVISIBLE);
        mSwitcherText.setVisibility(View.INVISIBLE);
    }

    class ImageThread extends Thread {
        int duration = 300;
        final int imagesLabel[] = {R.drawable.hodoo_brand_intro_label, };
        final int imagesText[] = {R.drawable.intro_text, };
        final String strIntroText = "고마워, 거기에 있어줘서.";

        int curLabel = 0, curText = 0;

        public void run () {
            mRunning = true;

            while(mRunning) {
                synchronized (this) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwitcherLabel.setImageResource(imagesLabel[curLabel]);
                            //mSwitcherText.setImageResource(imagesText[curText]);
                            mTextViewIntro.setText(strIntroText.substring(0, curText));
                        }
                    });
                }

                curLabel++;
                curText++;

                if(curLabel == imagesText.length) {
                    curLabel = 0;
                }

                //if(curText == imagesText.length) {
                if(curText == strIntroText.length()) {
                    curText = 0;
                    startLoginActivity();
                }

                try {
                    Thread.sleep(duration);
                }catch (InterruptedException e) {

                }
            }
        }
    }
}
