package com.white.usee.app.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.adapter.AboutMeMessageAdapter;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.config.IntentKeyConfig;
import com.white.usee.app.model.AllMsgsModel;
import com.white.usee.app.model.AllMsgsNumModel;
import com.white.usee.app.model.CommentDanmuModel;
import com.white.usee.app.model.NewMsgModel;
import com.white.usee.app.model.NewMsgsModel;
import com.white.usee.app.util.AssetImageUtils;
import com.white.usee.app.util.HistoryMsgUtils;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.ThemeUtils;
import com.white.usee.app.util.emojicon.EmojiconEditText;
import com.white.usee.app.util.emojicon.EmojiconGridFragment;
import com.white.usee.app.util.emojicon.EmojiconsFragment;
import com.white.usee.app.util.emojicon.emoji.Emojicon;

import java.util.Date;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class AboutMeMessageActivity extends BaseActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    private ListView aboutMeList;
    private List<NewMsgModel> newMsgModels;
    private AboutMeMessageAdapter aboutMeMessageAdapter;
    private LinearLayout ly_messages_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me_message);
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));
        findById();
        setOnClick();
        JPushInterface.clearAllNotifications(this);
    }

    @Override
    protected void onResume() {
        new InitMsgTask().execute();
        super.onResume();
    }

    private void findById() {
        aboutMeList = (ListView) findViewById(R.id.list_about_me);
        ly_messages_history = (LinearLayout) findViewById(R.id.ly_message_history);
    }

    private void setOnClick() {

        (findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ly_messages_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<NewMsgModel> historyList = HistoryMsgUtils.getHistoryMsg(AboutMeMessageActivity.this);
                if (historyList != null && historyList.size() > 0) {
                    newMsgModels = historyList;
                    if (aboutMeMessageAdapter == null) {
                        aboutMeMessageAdapter = new AboutMeMessageAdapter(newMsgModels, AboutMeMessageActivity.this);
                        aboutMeList.setAdapter(aboutMeMessageAdapter);
                    } else {
                        aboutMeMessageAdapter.setNewMsgModels(newMsgModels);
                        aboutMeMessageAdapter.notifyDataSetChanged();
                    }
                    ly_messages_history.setVisibility(View.GONE);
                } else {

                    showToast("无历史记录");
                }
            }
        });
        aboutMeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewMsgModel newMsgModel = aboutMeMessageAdapter.getItem(i);

                Intent intent = new Intent(AboutMeMessageActivity.this, DanmuDetailActivity.class);
                intent.putExtra(DanmuDetailActivity.DANMAKU_CONTENT, newMsgModel.getContent());
                intent.putExtra(DanmuDetailActivity.DANMAKU_ID, newMsgModel.getDanmuId() + "");
                intent.putExtra(IntentKeyConfig.USERID, newMsgModel.getDanmuUserID() + "");
                intent.putExtra(IntentKeyConfig.Tag_Name, newMsgModel.getTopicTitle());
                intent.putExtra(IntentKeyConfig.LOCATION, new double[]{getLon(), getLat()});
                intent.putExtra(IntentKeyConfig.COMMENTID, newMsgModel.getCommentId());
                intent.putExtra(IntentKeyConfig.FromeActivity, DanmuDetailActivity.FROMABOUTME);
                intent.putExtra(IntentKeyConfig.USERICON, "0.png");
                startActivity(intent);

            }
        });
    }

    boolean hasPrivate = false, isanonymous = true;
    EmojiconEditText et_comment;

    private void showDirectionReply(final View selectedView, final NewMsgModel newMsgModel) {

        View mainView = View.inflate(this, R.layout.pop_reply, null);
        selectedView.setBackgroundColor(getResources().getColor(R.color.view_mesh));
        PopupWindow popupWindow = new PopupWindow(mainView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                selectedView.setBackgroundDrawable(getResources().getDrawable(R.drawable.item_pressed));
            }
        });
        final FrameLayout frameLayout = (FrameLayout) mainView.findViewById(R.id.emojicons);
        EmojiconsFragment fragment = EmojiconsFragment.newInstance(false);
        getSupportFragmentManager().beginTransaction().replace(frameLayout.getId(), fragment).commit();
        Button bt_comment = (Button) mainView.findViewById(R.id.bt_sendComment);
        final Button bt_private = (Button) mainView.findViewById(R.id.bt_private);
        if (hasPrivate)
            bt_private.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_private_pressed));
        else bt_private.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_private));
        Button bt_emoji = (Button) mainView.findViewById(R.id.bt_emoji);
        bt_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPrivate) {
                    bt_private.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_private));
                    hasPrivate = false;
                } else {
                    hasPrivate = true;
                    bt_private.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_private_pressed));
                }
            }
        });
        et_comment = (EmojiconEditText) mainView.findViewById(R.id.et_comment);
        final RelativeLayout rl_select_id = (RelativeLayout) mainView.findViewById(R.id.ly_select_id);
        ImageButton bt_select_id = (ImageButton) mainView.findViewById(R.id.bt_select_id);
        bt_select_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rl_select_id.getVisibility() == View.VISIBLE)
                    rl_select_id.setVisibility(View.GONE);
                else rl_select_id.setVisibility(View.VISIBLE);
            }
        });

        et_comment.setHint("回复 " + newMsgModel.getNickname());
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(et_comment.getText())) {
                    showToast("回复内容不能为空");
                } else {
                    sendComment(et_comment.getText().toString(), newMsgModel.getDanmuId(), newMsgModel.getSender(), hasPrivate ? 2 : 1, isanonymous, newMsgModel.getCommentId(), et_comment);
                }
            }
        });
        bt_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rl_select_id.getVisibility() == View.VISIBLE)
                    rl_select_id.setVisibility(View.GONE);
                if (frameLayout.getVisibility() == View.VISIBLE) {
                    frameLayout.setVisibility(View.GONE);
                } else {
                    frameLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        initLySelectId(mainView, bt_select_id);

        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

    }

    private void initLySelectId(View view, final ImageButton bt_select_id) {
        RelativeLayout ly_no_login_head, ly_random_head;
        ly_no_login_head = (RelativeLayout) view.findViewById(R.id.ly_no_login_head);
        ly_random_head = (RelativeLayout) view.findViewById(R.id.ly_random_head);
        final ImageView iv_user_head = (ImageView) view.findViewById(R.id.iv_user_head);
        final ImageView iv_random_head = (ImageView) view.findViewById(R.id.iv_random_head);
        ly_no_login_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isanonymous = false;
                iv_user_head.setBackgroundColor(getResources().getColor(R.color.id_pressed));
                iv_random_head.setBackgroundColor(getResources().getColor(R.color.transparent));
                if (!BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)) {
                    AssetImageUtils.loadUserHead(AboutMeMessageActivity.this, HttpUrlConfig.iconUrl + BaseApplication.getInstance().getUserModel().getUserIcon(), bt_select_id);
                }
            }
        });
        ly_random_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isanonymous = true;
                iv_random_head.setBackgroundColor(getResources().getColor(R.color.id_pressed));
                iv_user_head.setBackgroundColor(getResources().getColor(R.color.transparent));
                bt_select_id.setImageDrawable(AssetImageUtils.getHeadImageFromAssetsFile(AboutMeMessageActivity.this, "0.png"));
            }
        });
    }

    private void sendComment(String msg, int danmuID, String receiver, int type, boolean isannoymous, int reply_commentid, final EditText editText) {
        JSONObject jsonObject = new JSONObject();
        if (BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)) {
            showToast("您还未登入，无法发送评论,请登入后重试");
            return;
        }
        jsonObject.put("userid", BaseApplication.getInstance().getUserId());
        jsonObject.put("danmuid", danmuID);
        jsonObject.put("receiver", receiver);
        jsonObject.put("content", msg);
        jsonObject.put("type", type);
        jsonObject.put("isannoymous", isannoymous);
        jsonObject.put("reply_commentid", reply_commentid);
        new HttpManager<CommentDanmuModel>().sendQuest(Request.Method.POST, HttpUrlConfig.commentdanmu, jsonObject, CommentDanmuModel.class, new HttpRequestCallBack<CommentDanmuModel>() {
            @Override
            public void onRequestSuccess(CommentDanmuModel response, boolean cached) {
                showToast("评论成功");
                editText.setText("");
            }

            @Override
            public void onRequestFailed(VolleyError error) {

            }
        });

    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        if (et_comment != null)
            EmojiconsFragment.input(et_comment, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        if (et_comment != null)
            EmojiconsFragment.backspace(et_comment);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }


    //初始化我的消息
    private class InitMsgTask extends AsyncTask {
        private void updateLocationMsg() {
            HistoryMsgUtils.deleteHistoryMsg(AboutMeMessageActivity.this);
            JSONObject params = new JSONObject();
            params.put("userID", BaseApplication.getInstance().getUserId());
            LogUtil.i("用户消息" + params.toJSONString());
            new HttpManager<AllMsgsModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getAllMsgs, params, AllMsgsModel.class, new HttpRequestCallBack<AllMsgsModel>() {
                @Override
                public void onRequestSuccess(AllMsgsModel response, boolean cached) {
                    LogUtil.i("历史消息" + JSONObject.toJSONString(response));
                    if (response.getAllMsgs().size() > 0) {
                        if (ly_messages_history.getVisibility() == View.GONE)
                            ly_messages_history.setVisibility(View.VISIBLE);
                        HistoryMsgUtils.saveHistoryMsg(AboutMeMessageActivity.this, response.getAllMsgs());
                    } else {
                        if (ly_messages_history.getVisibility() == View.VISIBLE)
                            ly_messages_history.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onRequestFailed(VolleyError error) {
                    ly_messages_history.setVisibility(View.GONE);
                }
            });
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userID", BaseApplication.getInstance().getUserId());
            jsonObject.put("latestReadTime", BaseApplication.getInstance().getLatestReadTime());
            LogUtil.i("获取消息请求参数" + jsonObject.toJSONString());
            new HttpManager<NewMsgsModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getNewMsgs, jsonObject, NewMsgsModel.class, new HttpRequestCallBack<NewMsgsModel>() {
                @Override
                public void onRequestSuccess(NewMsgsModel response, boolean cached) {
                    LogUtil.i("我的消息" + JSONObject.toJSONString(response));
                    BaseApplication.getInstance().setLatestReadTime(new Date().getTime());
                    if (response.getNewMsgs().size() > 0) {
                        if (newMsgModels == null) newMsgModels = response.getNewMsgs();
                        else newMsgModels.addAll(0, response.getNewMsgs());
                        if (aboutMeMessageAdapter == null) {
                            aboutMeMessageAdapter = new AboutMeMessageAdapter(newMsgModels, AboutMeMessageActivity.this);
                            aboutMeList.setAdapter(aboutMeMessageAdapter);
                        } else {
                            aboutMeMessageAdapter.setNewMsgModels(newMsgModels);
                            aboutMeMessageAdapter.notifyDataSetChanged();
                        }
                    }


                }

                @Override
                public void onRequestFailed(VolleyError error) {

                }
            });

            final List<NewMsgModel> historyList = HistoryMsgUtils.getHistoryMsg(AboutMeMessageActivity.this);
            if (historyList != null && historyList.size() > 0) {
                JSONObject msgNumJson = new JSONObject();
                msgNumJson.put("userID", BaseApplication.getInstance().getUserId());
                LogUtil.i("获取消息数目" + msgNumJson.toJSONString());
                new HttpManager<AllMsgsNumModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getallMsgsNum, msgNumJson, AllMsgsNumModel.class, new HttpRequestCallBack<AllMsgsNumModel>() {
                    @Override
                    public void onRequestSuccess(AllMsgsNumModel response, boolean cached) {
                        if (response.getAllMsgsNum() != historyList.size()) {
                            updateLocationMsg();
                        }
                    }

                    @Override
                    public void onRequestFailed(VolleyError error) {

                    }
                });
            } else {
                updateLocationMsg();
            }
            return null;
        }
    }

}
