package com.wanghaisheng.largeimageview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wanghaisheng.view.largeimageview.LargeImageView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    LargeImageView mLargeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLargeImageView = (LargeImageView) findViewById(R.id.largeimageview);

        try {
            InputStream in = getAssets().open("qm.jpg");
            mLargeImageView.setImageInputStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
