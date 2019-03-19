package com.hikvision.dashcampredemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hikvision.dashcampredemo.R;

public class PhotoActivity extends AppCompatActivity {
    //显示图片的控件
    private ImageView ivPhoto;
    //返回上层界面的控件
    private ImageButton ibFinish;
    //缩略图的路径
    private String url;
    //获取数据
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ibFinish = (ImageButton)findViewById(R.id.ib_finish);
        ibFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivPhoto = (ImageView)findViewById(R.id.iv_photo);
        intent = getIntent();
        url = intent.getStringExtra("url");
        //图片加载
        Glide.with(PhotoActivity.this).load(url).into(ivPhoto);
    }
}
