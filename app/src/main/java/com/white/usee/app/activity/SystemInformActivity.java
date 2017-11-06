package com.white.usee.app.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.white.usee.app.R;
import com.white.usee.app.util.emojicon.EmojiconTextView;

public class SystemInformActivity extends AppCompatActivity {

    private ImageView iv_head;
    private EmojiconTextView tv_notification;
    private TextView tv_title;
    private ImageButton ib_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_inform);
        findById();
        initData();
        setOnClick();
    }

    private void setOnClick() {
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void findById() {
        iv_head = (ImageView) findViewById(R.id.iv_head);
        tv_notification = (EmojiconTextView) findViewById(R.id.tv_notification);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ib_back = (ImageButton) findViewById(R.id.title_back);
    }

    private void initData() {
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        
        tv_title.setText("系统消息");
        tv_notification.setText(message);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);

        setIntent(intent);
    }

}
