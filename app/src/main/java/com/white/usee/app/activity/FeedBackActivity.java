package com.white.usee.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.R;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.ThemeUtils;

public class FeedBackActivity extends BaseActivity {
    private EditText et_feedBack_content;
    private Button bt_send_feedback;
    private OnFeedCallBack onFeedCallBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));
        findById();
        setOnClick();
    }

    private void findById(){
        et_feedBack_content = (EditText)findViewById(R.id.et_feedback_content);
        bt_send_feedback = (Button)findViewById(R.id.bt_send_feedback);
    }

    private void setOnClick(){

        (findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bt_send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_feedBack_content.getText().length()<10){
                    showToast("请输入不少于10个字的建议");
                    return;
                }
                setFeedBack(et_feedBack_content.getText().toString().trim());
            }
        });
        this.onFeedCallBack = new OnFeedCallBack() {
            @Override
            public void onSucceed() {
                showToast("意见反馈成功，感谢您的反馈");
                finish();
            }

            @Override
            public void onFailed() {
                showToast("反馈失败，请检测网络");
                et_feedBack_content.setText("");
            }
        };
    }

    private void setFeedBack(String msg){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messages",msg);
        new HttpManager<String>().sendQuest(Request.Method.POST, HttpUrlConfig.feedback, jsonObject, String.class, new HttpRequestCallBack<String>() {
            @Override
            public void onRequestSuccess(String response, boolean cached) {
                if (onFeedCallBack!=null) onFeedCallBack.onSucceed();
            }

            @Override
            public void onRequestFailed(VolleyError error) {
                if (onFeedCallBack!=null) onFeedCallBack.onFailed();
            }
        });
    }
//反馈后的回调
     interface OnFeedCallBack{
        void onSucceed();
         void onFailed();
    }
}
