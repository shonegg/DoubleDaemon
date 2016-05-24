package com.miraclesnow.app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;


public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView image = new ImageView(this);
        image.setBackgroundColor(Color.parseColor("#ccd000"));
        setContentView(image);
        DaemonHelper.initDaemon(getApplicationContext());
    }
}
