package com.white.usee.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.R;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.config.IntentKeyConfig;
import com.white.usee.app.model.TopicModel;
import com.white.usee.app.model.UserInfoModel;
import com.white.usee.app.util.AssetImageUtils;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.ThemeUtils;
import com.white.usee.app.view.WrapLineLayout;

import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends BaseActivity {

    private static int MYTAG = 1;


    private String userId;//要查询的用户名Id
    private String head_image_url;//头像URL
    private String nick_name;//昵称
    private int gender;//性别
    private List<TopicModel> topics;//参与过的话题

    private ImageView iv_head_icon;//显示头像
    private ImageButton ib_title_back;//显示toolbar返回
    private TextView tv_nick_name;//显示昵称
    private TextView tv_gender;//显示性别
    private TextView tv_user_like_topic;//显示参与过的话题
    private WrapLineLayout wrapLY_like_topics;//显示话题


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        //沉浸式标题栏
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));

        //从弹幕详情界面过去到弹幕作者的id
        userId = getIntent().getStringExtra("userId");
        //初始化话题列表
        topics = new ArrayList<>();

        findById();
        setOnClick();
        getData();
        init();

    }


    private void findById() {
        ib_title_back = (ImageButton) findViewById(R.id.title_back);
        iv_head_icon = (ImageView) findViewById(R.id.iv_head_icon);
        tv_nick_name = (TextView) findViewById(R.id.tv_nick_name);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_user_like_topic = (TextView) findViewById(R.id.tv_user_like_topic);
        wrapLY_like_topics = (WrapLineLayout) findViewById(R.id.wrapLY_user_like_topic);
    }

    private void setOnClick() {
        ib_title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", userId);

        //根据userID请求获得该用户参与过的话题
        new HttpManager<UserInfoModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getRealNameInfo,
                jsonObject, UserInfoModel.class, new HttpRequestCallBack<UserInfoModel>() {
                    @Override
                    public void onRequestSuccess(UserInfoModel response, boolean cached) {

                        head_image_url = response.getUserIcon();//得到头像url
                        nick_name = response.getNickName();//得到昵称
                        gender = response.getGender();//得到性别
                        topics = response.getTopic();//得到话题列表

                        LogUtil.i("头像：" + head_image_url + " 昵称：" + nick_name + " 性别：" + gender + " 话题：" + topics.toString());
                        //加载头像
                        AssetImageUtils.loadUserHead(UserInfoActivity.this, head_image_url, iv_head_icon);
                        //设置昵称
                        tv_nick_name.setText(nick_name);
                        //设置性别
                        switch (gender) {
                            case 0:
                                tv_gender.setText(R.string.male);
                                tv_user_like_topic.setText(R.string.participated_topics);
                                break;
                            case 1:
                                tv_gender.setText(R.string.female);
                                tv_user_like_topic.setText(R.string.participated_topics);
                                break;
                            case 2:
                                tv_gender.setText(R.string.not_tell);
                                tv_user_like_topic.setText(R.string.participated_topics);
                                break;
                        }
                        //显示话题列表
                        initWrapLayout(topics, wrapLY_like_topics);
//                        LogUtil.i("头像：" + head_image_url+ " 昵称：" + nick_name + " 性别：" + gender + " 话题：" + topics.toString());
                    }

                    @Override
                    public void onRequestFailed(VolleyError error) {

                    }
                });

    }

    private void init() {


//        initWrapLayout(topics,wrapLY_like_topics);
    }


    private void initWrapLayout(final List<TopicModel> labelModels, WrapLineLayout wrapLineLayout) {
        //移除所有话题
        wrapLineLayout.removeAllViews();
        //设置布局参数
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (final TopicModel topicModel : labelModels) {
            final View tagView;

            tagView = View.inflate(UserInfoActivity.this, R.layout.mytag_layout, null);

            //获取tag中的textview
            final TextView tv_tag = (TextView) tagView.findViewById(R.id.bt_tag);
            //将话题显示出来
            tv_tag.setText(topicModel.getTitle());

            tagView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(UserInfoActivity.this, Talk2Activity.class);
                    //话题名称
                    intent.putExtra(IntentKeyConfig.Tag_Name, tv_tag.getText());
                    //话题id
                    intent.putExtra(IntentKeyConfig.TOPICID, topicModel.getId());
                    //话题弹幕数量
                    intent.putExtra(IntentKeyConfig.DANMUNUM, topicModel.getDanmuNum());
                    //话题模型对象
                    intent.putExtra(IntentKeyConfig.TOPICMODEL, JSONObject.toJSONString(topicModel));
                    startActivity(intent);

                }
            });

            wrapLineLayout.addView(tagView, params);

        }
    }
}










