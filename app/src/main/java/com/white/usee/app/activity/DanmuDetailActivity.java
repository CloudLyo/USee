package com.white.usee.app.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.adapter.CommentListViewAdapter;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.config.IntentKeyConfig;
import com.white.usee.app.model.CommentDanmuWithCodeModel;
import com.white.usee.app.model.FavModel;
import com.white.usee.app.model.GetUserIconModel;
import com.white.usee.app.model.ResultModel;
import com.white.usee.app.model.SingleTopicModel;
import com.white.usee.app.model.TopicModel;
import com.white.usee.app.model.UpdateUserAction;
import com.white.usee.app.model.UserCommentModel;
import com.white.usee.app.model.DanmuDetailsModel;
import com.white.usee.app.util.AssetImageUtils;
import com.white.usee.app.util.DateUtil;
import com.white.usee.app.util.DistanceUtils;
import com.white.usee.app.util.Dp2Px;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.ThemeUtils;
import com.white.usee.app.util.emojicon.EmojiconEditText;
import com.white.usee.app.util.emojicon.EmojiconGridFragment;
import com.white.usee.app.util.emojicon.EmojiconTextView;
import com.white.usee.app.util.emojicon.EmojiconsFragment;
import com.white.usee.app.util.emojicon.emoji.Emojicon;
import com.white.usee.app.util.sensitive.KWSeekerManage;
import com.white.usee.app.util.sensitive.SimpleKWSeekerProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.white.usee.app.BaseApplication.getInstance;

public class DanmuDetailActivity extends BaseActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    public static String DANMAKU_CONTENT = "content";
    public static String DANMAKU_ID = "danmuid";
    public static int FROMABOUTME = 1;//从消息界面跳转过来
    public static int FROMETALK = 2;//从聊天界面跳转过来
    private TextView tv_user_name;
    private TextView tv_random_name;
    private ImageButton ib_like, ib_unlike, ib_collect;
    private Button bt_private, bt_sendComment, bt_emoji;
    private TextView tv_like_num;
    private TextView tv_unlike_num;
    private TextView tv_name;
    private TextView tv_time;
    private TextView tv_danmu_location, tv_topicName;
    private TextView tv_commentNum;
    private ListView list_comment;
    private FrameLayout emojiFrameLayout;
    private CommentListViewAdapter commentListViewAdapter;
    private ImageView iv_sex, iv_random_head, iv_user_head, iv_head;
    private RelativeLayout rl_select_id;
    private EmojiconEditText et_comment;
    private String danmuID;
    private RelativeLayout ly_no_login_head, ly_random_head;
    private boolean isanonymous = true;
    private ImageButton bt_select_id;
    private String danmuUserId;
    private RelativeLayout rl_danmuDetail;
    private String reveciverId;
    private int reply_commentId;
    private boolean isPrivateReply;//是否是私人回复
    private String userIcon = "0.png";
    private int randomIconId;
    private List<UserCommentModel> userCommentModels = new ArrayList<>();
    private EmojiconTextView tv_danmuDetail;
    private boolean hasPraise = false, hasDown = false, hasCollect = false;
    private boolean isAnonymous = false;//该弹幕详情是否为匿名弹幕
    private ImageView iv_random_be_checked, iv_user_be_checked;
    private LinearLayout ly_photos;
    private int type = 1;//1是直接评论，2是直接回复，3是悄悄回复，4是悄悄评论
    private NineGridView nineGridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danmu_detail);
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));
        findById();
        setOnClick();
        initDanmuDetail(true);
        getUserRandomIcon(danmuID);
    }

    //如果从消息界面传递过来，设置一下回复为消息的发送者,
    private void setReveciverFromAboutUs(CommentListViewAdapter commentListViewAdapter, ListView list_comment, final String topicId) {
        if (getIntent().getIntExtra(IntentKeyConfig.FromeActivity, FROMETALK) == FROMABOUTME) {
            int commentId = getIntent().getIntExtra(IntentKeyConfig.COMMENTID, 0);
            tv_topicName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toTalkActivity(topicId);
                }
            });
            for (int i = 0; i < commentListViewAdapter.getCommentModels().size(); i++) {
                UserCommentModel userCommentModel = commentListViewAdapter.getItem(i);
                if (userCommentModel.getComment().getId() == commentId) {
                    commentBeClickedEvent(userCommentModel);
                    list_comment.setSelection(i);
                }
            }

        } else {
            tv_topicName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }


    /**
     * 如果是从消息界面跳转过来的，调用获取详情接口，跳转到弹幕播放界面
     */
    private void toTalkActivity(final String topicId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("topicID", topicId);
        new HttpManager<SingleTopicModel>().sendQuest(Request.Method.POST, HttpUrlConfig.gettopicinfo, jsonObject, SingleTopicModel.class, new HttpRequestCallBack<SingleTopicModel>() {
            @Override
            public void onRequestSuccess(SingleTopicModel response, boolean cached) {
                LogUtil.i("弹幕详情"+JSONObject.toJSONString(response));

                TopicModel topic = response.getTopic();

                Intent intent = new Intent(DanmuDetailActivity.this, Talk2Activity.class);
                intent.putExtra(IntentKeyConfig.Tag_Name, tv_topicName.getText());
                intent.putExtra(IntentKeyConfig.TOPICID, topicId);
                //-----------------
                intent.putExtra(IntentKeyConfig.DANMUNUM, topic.getDanmuNum());
                //-----------------
                intent.putExtra(IntentKeyConfig.TOPICMODEL, JSONObject.toJSONString(topic));

                startActivity(intent);
            }

            @Override
            public void onRequestFailed(VolleyError error) {

            }
        });
    }

    private void findById() {
        view_mesh = findViewById(R.id.view_mesh);
        bt_emoji = (Button) findViewById(R.id.bt_emoji);
        emojiFrameLayout = (FrameLayout) findViewById(R.id.emojicons);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        iv_sex = (ImageView) findViewById(R.id.iv_sex);
        ly_no_login_head = (RelativeLayout) findViewById(R.id.ly_no_login_head);
        ly_random_head = (RelativeLayout) findViewById(R.id.ly_random_head);
        rl_danmuDetail = (RelativeLayout) findViewById(R.id.rl_danmuDetail);
        tv_danmuDetail = (EmojiconTextView) findViewById(R.id.tv_danmu_content);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_topicName = (TextView) findViewById(R.id.tv_topicName);
        tv_topicName.setText(getIntent().getStringExtra(IntentKeyConfig.Tag_Name));
        tv_danmu_location = (TextView) findViewById(R.id.tv_danmu_location);
        tv_commentNum = (TextView) findViewById(R.id.tv_commentNum);
        String content = getIntent().getStringExtra(DANMAKU_CONTENT);
        danmuID = getIntent().getStringExtra(DANMAKU_ID);
        userIcon = getIntent().getStringExtra(IntentKeyConfig.USERICON);
        danmuUserId = getIntent().getStringExtra(IntentKeyConfig.USERID);
        randomIconId = getIntent().getIntExtra(IntentKeyConfig.RANDOMICONID, 0);
        reveciverId = danmuUserId;
        nineGridView = (NineGridView)findViewById(R.id.grid_view);
        if (content != null) {
            tv_danmuDetail.setText(content.trim());
        }
        iv_random_be_checked = (ImageView) findViewById(R.id.iv_random_be_checked);
        iv_user_be_checked = (ImageView) findViewById(R.id.iv_user_be_checked);
        list_comment = (ListView) findViewById(R.id.list_comment);
        ib_like = (ImageButton) findViewById(R.id.ib_like);
        ib_unlike = (ImageButton) findViewById(R.id.ib_unlike);
        ib_collect = (ImageButton) findViewById(R.id.ib_collect);
        tv_like_num = (TextView) findViewById(R.id.tv_like_num);
        tv_unlike_num = (TextView) findViewById(R.id.tv_unlike_num);
        bt_select_id = (ImageButton) findViewById(R.id.bt_select_id);
        rl_select_id = (RelativeLayout) findViewById(R.id.ly_select_id);
        bt_private = (Button) findViewById(R.id.bt_private);
        et_comment = (EmojiconEditText) findViewById(R.id.et_comment);
        bt_sendComment = (Button) findViewById(R.id.bt_sendComment);
        iv_random_head = (ImageView) findViewById(R.id.iv_random_head);
        iv_user_head = (ImageView) findViewById(R.id.iv_user_head);
        tv_random_name = (TextView) findViewById(R.id.tv_random_name);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        ly_photos = (LinearLayout) findViewById(R.id.ly_photos);
        AssetImageUtils.loadUserHead(this, userIcon, iv_random_head);
        setEmojiconFragment(false);
    }

    private void setOnClick() {
        ImageButton bt_back = (ImageButton) findViewById(R.id.title_back);
        assert bt_back != null;
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                hideInputWindow(et_comment.getWindowToken());
            }
        });

        ib_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPraise) {
                    updateUserAction(0, new HttpRequestCallBack<UpdateUserAction>() {
                        @Override
                        public void onRequestSuccess(UpdateUserAction response, boolean cached) {
                            if (response.getResult() == 1) {
                                ib_like.setImageDrawable(getResources().getDrawable(R.drawable.bt_like_normal));
                                int likeNum = Integer.valueOf(tv_like_num.getText().toString());
                                tv_like_num.setText((likeNum - 1) + "");
                                hasPraise = false;
                                showToast(getString(R.string.no_unlike));
                            }
                        }

                        @Override
                        public void onRequestFailed(VolleyError error) {
                            showToast(getString(R.string.action_failed));
                        }
                    });
                } else {
                    if (hasDown) {
                        showToast("您已踩过该弹幕，请先取消");
                        return;
                    }

                    updateUserAction(1, new HttpRequestCallBack<UpdateUserAction>() {
                        @Override
                        public void onRequestSuccess(UpdateUserAction response, boolean cached) {
                            if (response.getResult() == 1) {
                                ib_like.setImageDrawable(getResources().getDrawable(R.drawable.bt_like_pressed));
                                int likeNum = Integer.valueOf(tv_like_num.getText().toString());
                                tv_like_num.setText((likeNum + 1) + "");
                                hasPraise = true;
                                showToast(getString(R.string.like_success));
                            }
                        }

                        @Override
                        public void onRequestFailed(VolleyError error) {
                            showToast(getString(R.string.action_failed));
                        }
                    });
                }

            }
        });
        bt_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideInputWindow(et_comment.getWindowToken());
                if (rl_select_id.getVisibility() == View.VISIBLE)
                    rl_select_id.setVisibility(View.GONE);
                if (emojiFrameLayout.getVisibility() == View.VISIBLE) {
                    emojiFrameLayout.setVisibility(View.GONE);
                } else {
                    emojiFrameLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        tv_danmuDetail.setMovementMethod(LinkMovementMethod.getInstance());
        ib_unlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasDown) {

                    updateUserAction(0, new HttpRequestCallBack<UpdateUserAction>() {
                        @Override
                        public void onRequestSuccess(UpdateUserAction response, boolean cached) {
                            if (response.getResult() == 1) {
                                ib_unlike.setImageDrawable(getResources().getDrawable(R.drawable.bt_unlike_normal));
                                int unlikeNum = Integer.valueOf(tv_unlike_num.getText().toString());
                                tv_unlike_num.setText((unlikeNum - 1) + "");
                                hasDown = false;
                                showToast(getString(R.string.cancel_unlike_success));
                            } else {
                                showToast(getString(R.string.action_failed));
                            }
                        }

                        @Override
                        public void onRequestFailed(VolleyError error) {
                            showToast(getString(R.string.action_failed));
                        }
                    });
                } else {
                    if (hasPraise) {
                        showToast("您已赞过该弹幕，请先取消");
                        return;
                    }
                    updateUserAction(2, new HttpRequestCallBack<UpdateUserAction>() {
                        @Override
                        public void onRequestSuccess(UpdateUserAction response, boolean cached) {
                            if (response.getResult() == 1) {
                                ib_unlike.setImageDrawable(getResources().getDrawable(R.drawable.bt_unlike_pressed));
                                int unlikeNum = Integer.valueOf(tv_unlike_num.getText().toString());
                                tv_unlike_num.setText((unlikeNum + 1) + "");
                                hasDown = true;
                                showToast(getString(R.string.unlike_success));
                            } else {
                                showToast(getString(R.string.action_failed));
                            }

                        }

                        @Override
                        public void onRequestFailed(VolleyError error) {
                            showToast(getString(R.string.action_failed));
                        }
                    });

                }
            }
        });
        et_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputWindow(et_comment);
                if (rl_select_id.getVisibility() == View.VISIBLE)
                    rl_select_id.setVisibility(View.GONE);
                if (emojiFrameLayout.getVisibility() == View.VISIBLE)
                    emojiFrameLayout.setVisibility(View.GONE);
            }
        });
        et_comment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showInputWindow(et_comment);
                    if (rl_select_id.getVisibility() == View.VISIBLE)
                        rl_select_id.setVisibility(View.GONE);
                    if (emojiFrameLayout.getVisibility() == View.VISIBLE)
                        emojiFrameLayout.setVisibility(View.GONE);
                }
            }
        });
        ib_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favDanmu(danmuID, hasCollect);
            }
        });
        bt_select_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideInputWindow(et_comment.getWindowToken());
                if (emojiFrameLayout.getVisibility() == View.VISIBLE)
                    emojiFrameLayout.setVisibility(View.GONE);
                if (rl_select_id.getVisibility() == View.VISIBLE) {
                    rl_select_id.setVisibility(View.GONE);
                } else {
                    rl_select_id.setVisibility(View.VISIBLE);
                }
            }
        });
        bt_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPrivateReply) {
                    if (type == 3) type = 2;
                    else if (type == 4) type = 1;
                    isPrivateReply = false;
                    bt_private.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_private));
                    et_comment.setHint(et_comment.getHint().toString().replaceFirst(getString(R.string.private_string), ""));

                } else {
                    if (type == 1) type = 4;
                    else if (type == 2) type = 3;
                    isPrivateReply = true;
                    bt_private.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_private_pressed));
                    et_comment.setHint(getString(R.string.private_string) + et_comment.getHint());
                }

            }
        });
        //发送评论
        bt_sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(et_comment.getText())) {
                    return;
                } else {
                    showLoadingDialog();
                    sendComment(et_comment.getText().toString().trim(), reveciverId, type, isanonymous, reply_commentId);
                    if (rl_select_id.getVisibility() == View.VISIBLE)
                        rl_select_id.setVisibility(View.GONE);
                    hideInputWindow(et_comment.getWindowToken());
                }

            }
        });
        et_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    bt_sendComment.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_sendmsg_bg_normal));
                } else {
                    bt_sendComment.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_sendmsg_bg_pressed));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        rl_danmuDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reveciverId = danmuUserId;
                reply_commentId = 0;
                if (isPrivateReply) {
                    type = 4;
                    //"悄悄评论.."
                    et_comment.setHint(getString(R.string.private_comment)+"..");
                } else {
                    type = 1;
                    //"评论.."
                    et_comment.setHint(getString(R.string.comment) + "..");
                }

            }
        });
        if (BaseApplication.getInstance().getUserModel() != null && !BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)) {
            if (BaseApplication.getInstance().getUserModel().getUserIcon().length() > 8) {
                AssetImageUtils.loadUserHead(DanmuDetailActivity.this, BaseApplication.getInstance().getUserModel().getUserIcon(), iv_user_head);
                tv_user_name.setText(BaseApplication.getInstance().getUserModel().getNickname());
            }
            ly_no_login_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isanonymous = false;
                    iv_user_be_checked.setVisibility(View.VISIBLE);
                    iv_random_be_checked.setVisibility(View.GONE);
                    AssetImageUtils.loadUserHead(DanmuDetailActivity.this, BaseApplication.getInstance().getUserModel().getUserIcon(), bt_select_id);
                }
            });
        } else {
            ly_no_login_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DanmuDetailActivity.this, UserManagerActivity.class));
                }
            });
        }
        ly_random_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isanonymous = true;
                iv_random_be_checked.setVisibility(View.VISIBLE);
                iv_user_be_checked.setVisibility(View.GONE);
                AssetImageUtils.loadUserHead(DanmuDetailActivity.this, userIcon, bt_select_id);
            }
        });

        //头像点击跳转
        iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAnonymous){
                    Intent intent = new Intent(DanmuDetailActivity.this, UserInfoActivity.class);
                    intent.putExtra("userId", danmuUserId);
                    startActivity(intent);
                }else{
                    showToast("匿名弹幕无法查看信息！");
                }
            }
        });

        //用户名点击跳转
        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAnonymous){
                    Intent intent = new Intent(DanmuDetailActivity.this, UserInfoActivity.class);
                    intent.putExtra("userId", danmuUserId);
                    startActivity(intent);
                }else{
                    showToast("匿名弹幕无法查看信息！");
                }
            }
        });

    }

    //用于处理点赞和踩
    private void updateUserAction(final int action, HttpRequestCallBack<UpdateUserAction> httpRequestCallBack) {
        if (BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)) {
            showNoLogInTipDialog();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid", BaseApplication.getInstance().getUserId());
        jsonObject.put("action", action);
        jsonObject.put("danmuid", danmuID);
        new HttpManager<UpdateUserAction>().sendQuest(Request.Method.POST, HttpUrlConfig.updateUserAction, jsonObject, UpdateUserAction.class, httpRequestCallBack);
    }

    //发送一条评论
    private void sendComment(String msg, String receiver, int type, boolean isannoymous, int reply_commentid) {
        JSONObject jsonObject = new JSONObject();
        if (BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)) {
            showNoLogInTipDialog();
            return;
        }
        KWSeekerManage kwSeekerManage = SimpleKWSeekerProcessor.newInstance(this);
        msg = kwSeekerManage.getKWSeeker("topic-sensitive-word").replaceWords(msg);
        jsonObject.put("userid", BaseApplication.getInstance().getUserId());
        jsonObject.put("danmuid", danmuID);
        jsonObject.put("receiver", receiver);
        jsonObject.put("content", msg);
        jsonObject.put("type", type);
        jsonObject.put("isannoymous", isannoymous);
        jsonObject.put("reply_commentid", reply_commentid == 0 ? null : reply_commentid);
        jsonObject.put("randomUserIcon", userIcon);
        jsonObject.put("randomIconId", randomIconId);
        jsonObject.put("randomUserName", tv_random_name.getText().toString());
        new HttpManager<CommentDanmuWithCodeModel>().sendQuest(Request.Method.POST, HttpUrlConfig.commentdanmu, jsonObject, CommentDanmuWithCodeModel.class, new HttpRequestCallBack<CommentDanmuWithCodeModel>() {
            @Override
            public void onRequestSuccess(CommentDanmuWithCodeModel response, boolean cached) {
                int code = response.getCode();

                //可添加code判断逻辑
                if(code == 200) {
                    showToast("评论成功");
                }else if(code == 400){
                    showToast("请求失败");
                }else if(code == 401){
                    showToast("未登录");
                }else if(code == 402){
                    showToast("需要充值");
                }else if(code == 403){
                    showToast("你被封号");
                }else if(code == 404){
                    showToast("评论不存在");
                }else if(code == 405){
                    showToast("请勿发送相同弹幕");
                }else if(code == 406){
                    showToast("不接受请求");
                }else if(code == 407){
                    showToast("需要代理认证");
                }else if(code == 408){
                    showToast("服务器超时");
                }else if(code == 409) {
                    showToast("请求格式错误");
                }else if(code == 410){
                    showToast("客户端版本过低");
                }else if(code == 500){
                    showToast("服务器内部错误");
                }

                et_comment.setText("");
                if (emojiFrameLayout.getVisibility() == View.VISIBLE)
                    emojiFrameLayout.setVisibility(View.GONE);
                if (rl_select_id.getVisibility() == View.VISIBLE)
                    rl_select_id.setVisibility(View.GONE);
                initDanmuDetail(false);
                dismissLoadingDialog();
            }

            @Override
            public void onRequestFailed(VolleyError error) {
                showToast("评论失败");
                dismissLoadingDialog();
            }
        });

    }

    private void initDanmuDetail(final boolean isOpenActivity) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("danmuid", danmuID);
        jsonObject.put("userid", BaseApplication.getInstance().getUserId());
        LogUtil.i("弹幕详情参数"+jsonObject.toJSONString());
        new HttpManager<DanmuDetailsModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getdmdetails, jsonObject, DanmuDetailsModel.class, new HttpRequestCallBack<DanmuDetailsModel>() {
            @Override
            public void onRequestSuccess(final DanmuDetailsModel response, boolean cached) {
                LogUtil.i("弹幕详情"+JSONObject.toJSONString(response));
                LogUtil.i("id:"+response.getUserId());
                //判断是否为匿名弹幕
                if(response.getUserIcon().endsWith(".jpg") || response.getUserIcon().endsWith(".png")){
                    isAnonymous = false;
                }else{
                    isAnonymous = true;
                }
                if (response.getAction() == 0) {
                    hasPraise = false;
                    hasDown = false;
                } else if (response.getAction() == 1) {
                    hasPraise = true;
                    hasDown = false;
                } else if (response.getAction() == 2) {
                    hasDown = true;
                    hasPraise = false;
                }
                if (response.isfav()) {
                    hasCollect = true;
                    ib_collect.setImageDrawable(getResources().getDrawable(R.drawable.bt_has_collect));
                } else {
                    hasCollect = false;
                    ib_collect.setImageDrawable(getResources().getDrawable(R.drawable.bt_collect));
                }
                if (response.getGender() == 0) {
                    iv_sex.setImageDrawable(getResources().getDrawable(R.drawable.sex_man));
                } else if (response.getGender() == 1) {
                    iv_sex.setImageDrawable(getResources().getDrawable(R.drawable.sex_woman));
                } else {
                    iv_sex.setVisibility(View.GONE);
                }
                if (hasPraise) {
                    ib_like.setImageDrawable(getResources().getDrawable(R.drawable.bt_like_pressed));
                } else
                    ib_like.setImageDrawable(getResources().getDrawable(R.drawable.bt_like_normal));

                if (hasDown) {
                    ib_unlike.setImageDrawable(getResources().getDrawable(R.drawable.bt_unlike_pressed));
                } else
                    ib_unlike.setImageDrawable(getResources().getDrawable(R.drawable.bt_unlike_normal));

                ArrayList<ImageInfo> imageInfo = new ArrayList<>();
                if (response.getImgurls()!=null&&response.getImgurls().size()>0)
                    nineGridView.setVisibility(View.VISIBLE);
                for (String path : response.getImgurls()) {
                    ImageInfo info = new ImageInfo();
                    info.setThumbnailUrl(path);
                    info.setBigImageUrl(path);
                    imageInfo.add(info);
                }
                nineGridView.setAdapter(new NineGridViewClickAdapter(DanmuDetailActivity.this, imageInfo));
                tv_like_num.setText(response.getPraisenum() + "");
                tv_danmuDetail.setText(response.getMessages());
                tv_unlike_num.setText(response.getDownnum() + "");
                AssetImageUtils.loadUserHead(DanmuDetailActivity.this, response.getUserIcon(), iv_head);
                tv_name.setText(response.getNickname());
                tv_time.setText(DateUtil.showDanmuOrCommentDay(response.getCreate_time()));
                double distance = DistanceUtils.GetDistance(getLon(), getLat(), response.getLon(), response.getLat());
                if (distance < 1000) {
                    //发布于20m以内
                    tv_danmu_location.setText(getString(R.string.within_start) + (int) Math.ceil(distance / 100) * 100 + getString(R.string.within_m_end));
                } else {
                    //发布于20km以内
                    tv_danmu_location.setText(getString(R.string.within_start) + String.format("%.1f", distance / 1000) + getString(R.string.within_km_end));
                }
                if (response.getUsercomments() != null && response.getUsercomments().size() > 0) {
                    tv_commentNum.setText("全部评论 " + response.getUsercomments().size() + "");
                    final List<UserCommentModel> resultcomment = new ArrayList<UserCommentModel>();
                    for (UserCommentModel userCommentModel : response.getUsercomments()) {
                        if ((userCommentModel.getComment().getType() == 3 || userCommentModel.getComment().getType() == 4)
                                && !userCommentModel.getComment().getReceiver().equals(BaseApplication.getInstance().getUserId())
                                && !userCommentModel.getComment().getSender().equals(BaseApplication.getInstance().getUserId())) {
                            continue;
                        }
                        resultcomment.add(userCommentModel);
                    }
                    userCommentModels = resultcomment;
                    Collections.reverse(userCommentModels);
                    if (commentListViewAdapter == null) {
                        commentListViewAdapter = new CommentListViewAdapter(DanmuDetailActivity.this, userCommentModels);
                        list_comment.setAdapter(commentListViewAdapter);
                        commentListViewAdapter.setOnItemClickListener(new CommentListViewAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(UserCommentModel i) {
                                UserCommentModel userCommentModel = i;
                                commentBeClickedEvent(userCommentModel);
                            }
                        });
                    } else {
                        commentListViewAdapter.setCommentModels(userCommentModels);
                        commentListViewAdapter.notifyDataSetChanged();
                    }
                    if (isOpenActivity) {
                        setReveciverFromAboutUs(commentListViewAdapter, list_comment, response.getTopicId());
                    }
                }


            }

            @Override
            public void onRequestFailed(VolleyError error) {
                LogUtil.e(error.getMessage() + "弹幕详情");
            }
        });
    }

    //评论被点击后的事件
    private void commentBeClickedEvent(UserCommentModel userCommentModel) {
        if ((userCommentModel.getComment().getType() == 3 || userCommentModel.getComment().getType() == 4)) {
            isPrivateReply = true;
            bt_private.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_private_pressed));
        } else {
            isPrivateReply = false;
            bt_private.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_private));
        }
        if (isPrivateReply) {
            type = 3;
            if (userCommentModel.getUser().getUserID().equals(BaseApplication.getInstance().getUserId())) {
                //"悄悄回复我"
                et_comment.setHint(getString(R.string.private_reply) + "我");
            } else {
                //"悄悄回复"+...
                et_comment.setHint(getString(R.string.private_reply) + userCommentModel.getUser().getNickname());
            }
        } else {
            type = 2;
            if (userCommentModel.getUser().getUserID().equals(BaseApplication.getInstance().getUserId())) {
                //回复我
                et_comment.setHint(getString(R.string.reply) + "我");
            } else {
                //"回复"+...
                et_comment.setHint(getString(R.string.reply) + userCommentModel.getUser().getNickname());
            }

        }

        reveciverId = userCommentModel.getUser().getUserID();
        reply_commentId = userCommentModel.getComment().getId();
    }

    //调用举报接口
    private void report(int reporttyle, UserCommentModel userCommentModel) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid", BaseApplication.getInstance().getUserId());
        jsonObject.put("contentuserid", userCommentModel.getUser().getUserID());
        jsonObject.put("contentid", userCommentModel.getComment().getId());
        jsonObject.put("contenttype", "c");
        jsonObject.put("reporttype", reporttyle);
        LogUtil.e("举报评论"+jsonObject.toJSONString());
        new HttpManager<ResultModel>().sendQuest(Request.Method.POST, HttpUrlConfig.reportcontent, jsonObject, ResultModel.class, new HttpRequestCallBack<ResultModel>() {
            @Override
            public void onRequestSuccess(ResultModel response, boolean cached) {
                if (response.getResult() == 1) {
                    showToast("举报成功");
                }
            }

            @Override
            public void onRequestFailed(VolleyError error) {

            }
        });

    }

    View view_mesh;

    public void showPopReport(final UserCommentModel userCommentModel) {
        view_mesh.setVisibility(View.VISIBLE);
        View mainView = View.inflate(this, R.layout.pop_report, null);
        final PopupWindow popupWindow = new PopupWindow(mainView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_mesh.setVisibility(View.GONE);
            }
        });
        TextView tv_ad = (TextView) mainView.findViewById(R.id.tv_ad);
        TextView tv_bilk = (TextView) mainView.findViewById(R.id.tv_bilk);
        TextView tv_pornography = (TextView) mainView.findViewById(R.id.tv_pornography);
        TextView tv_uncivilized = (TextView) mainView.findViewById(R.id.tv_uncivilized);
        TextView tv_other = (TextView) mainView.findViewById(R.id.tv_other);
        View.OnClickListener reportListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_ad:
                        report(1, userCommentModel);
                        break;
                    case R.id.tv_bilk:
                        report(2, userCommentModel);
                        break;
                    case R.id.tv_pornography:
                        report(3, userCommentModel);
                        break;
                    case R.id.tv_uncivilized:
                        report(4, userCommentModel);
                        break;
                    case R.id.tv_other:
                        report(5, userCommentModel);
                        break;
                }
                popupWindow.dismiss();
            }
        };
        tv_ad.setOnClickListener(reportListener);
        tv_bilk.setOnClickListener(reportListener);
        tv_pornography.setOnClickListener(reportListener);
        tv_uncivilized.setOnClickListener(reportListener);
        tv_other.setOnClickListener(reportListener);
        TextView tv_cancel = (TextView) mainView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    //收藏弹幕
    private void favDanmu(String danmuID, final boolean isFav) {
        if (BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)) {
            showNoLogInTipDialog();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid", BaseApplication.getInstance().getUserId());
        jsonObject.put("danmuid", danmuID);
        jsonObject.put("isfav", isFav);
        new HttpManager<FavModel>().sendQuest(Request.Method.POST, HttpUrlConfig.favdanmu, jsonObject, FavModel.class, new HttpRequestCallBack<FavModel>() {
            @Override
            public void onRequestSuccess(FavModel response, boolean cached) {
                if (response.isStatus()) {
                    if (!isFav) {
                        showToast("收藏成功");
                        hasCollect = true;
                        ib_collect.setImageDrawable(getResources().getDrawable(R.drawable.bt_has_collect));
                    } else {
                        showToast("取消收藏");
                        hasCollect = false;
                        ib_collect.setImageDrawable(getResources().getDrawable(R.drawable.bt_collect));
                    }

                } else {
                    showToast("收藏操作失败");
                }
            }

            @Override
            public void onRequestFailed(VolleyError error) {
                showToast("操作失败，请检查网络");
            }
        });
    }

    //获取在该话题内该用户的头像昵称
    private void getUserRandomIcon(String danmuID) {
        if (getInstance().getUserId() == null && getInstance().getUserId().equals(BaseApplication.NOUSER))
            return;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid", getInstance().getUserId());
        jsonObject.put("danmuid", danmuID);
        new HttpManager<GetUserIconModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getusericonbycomment, jsonObject, GetUserIconModel.class, new HttpRequestCallBack<GetUserIconModel>() {
            @Override
            public void onRequestSuccess(GetUserIconModel response, boolean cached) {
                userIcon = response.getUsericon();
                iv_random_head.setImageDrawable(AssetImageUtils.getDanmuHead(DanmuDetailActivity.this, userIcon, Dp2Px.dip2px(100)));
                randomIconId = response.getRandomIconId();
                if (response.getIsanonymous() == 1) {
                    isanonymous = false;
                    AssetImageUtils.loadUserHead(DanmuDetailActivity.this, BaseApplication.getInstance().getUserModel().getUserIcon(), bt_select_id);
                    iv_user_be_checked.setVisibility(View.VISIBLE);
                    iv_random_be_checked.setVisibility(View.GONE);
                } else {
                    isanonymous = true;
                    AssetImageUtils.loadUserHead(DanmuDetailActivity.this, userIcon, bt_select_id, Dp2Px.px_32, Dp2Px.px_32);
                    iv_random_be_checked.setVisibility(View.VISIBLE);
                    iv_user_be_checked.setVisibility(View.GONE);
                }
                tv_random_name.setText(response.getUsername());
            }

            @Override
            public void onRequestFailed(VolleyError error) {

            }
        });

    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(et_comment, emojicon);
    }

    @Override
    public void onBackPressed() {
        if (rl_select_id.getVisibility() == View.VISIBLE) {
            rl_select_id.setVisibility(View.GONE);
        } else if (emojiFrameLayout.getVisibility() == View.VISIBLE) {
            emojiFrameLayout.setVisibility(View.GONE);
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        hideInputWindow(et_comment.getWindowToken());
        super.onPause();
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(et_comment);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }
}
