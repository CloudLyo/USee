package com.white.usee.app.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.bean.USeeDanmu;
import com.white.usee.app.config.DanmuConfig;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.config.IntentKeyConfig;
import com.white.usee.app.model.DanmuListModel;
import com.white.usee.app.model.DanmuModel;
import com.white.usee.app.model.DanmuWithCodeModel;
import com.white.usee.app.model.DeleteModel;
import com.white.usee.app.model.GetUserIconModel;
import com.white.usee.app.model.ResultModel;
import com.white.usee.app.model.TopicModel;
import com.white.usee.app.util.*;
import com.white.usee.app.util.emojicon.EmojiconEditText;
import com.white.usee.app.util.emojicon.EmojiconGridFragment;
import com.white.usee.app.util.emojicon.EmojiconHandler;
import com.white.usee.app.util.emojicon.EmojiconsFragment;
import com.white.usee.app.util.emojicon.emoji.Emojicon;
import com.white.usee.app.util.sensitive.KWSeekerManage;
import com.white.usee.app.util.sensitive.SimpleKWSeekerProcessor;
import com.white.usee.app.view.USeeDanmuView;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.io.File;
import java.io.IOException;
import java.util.*;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.Duration;

import static com.white.usee.app.BaseApplication.getInstance;


public class Talk2Activity extends BaseActivity implements View.OnTouchListener, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    public static String ISFIRSTCREATETAG = "isFisrtCreate";//是否是刚刚创建的标签

    private static int Ruqest_to_camera = 5487;
    private static int Ruqest_to_image_danmu = 7546;

    //isFirstOpen用于判断是否是刚打开，刚打开时从delayStart方法中加载弹幕，不是时从startDanmuForUpdate中获取，避免造成冲突
    private boolean isFirstCreate = false, isFirstOpen = true;

    private USeeDanmuView danmakuView;
    private List<DanmuModel> danmuContents = new ArrayList<>(), sumDanmuModel = new ArrayList<>();

    //页数和每页的弹幕数
    private int pageNum = 1, pageSize = 50, lastPageSize = -1, maxPageNum = 1;
    //后台检测更新是否有新弹幕的时间间隔（秒）
    private int UPDATEDURATION = 15;
    private int randomIconId;
    private int danmuHot = 3;//弹幕前三条作为热评弹幕

    private Random random = new Random(100);

    private USeeDanmu currentTouchedDanmu;//记录最近一个被点击的弹幕
    private USeeDanmakuFactory uSeeDanmakuFactory = USeeDanmakuFactory.create();
    private List<Integer> arr;
    private String topicid = "49";
    private String topicName;
    private String userIcon = "0.png";

    private Button bt_sendmsg, bt_emoji;
    private EmojiconEditText et_danmu;
    private View view_mesh;
    private ImageView ib_expand_less, iv_random_head, iv_user_head;
    private ImageView iv_random_be_checked, iv_user_be_checked;
    private ImageView iv_topic_image, iv_preivous_bg, iv_next_bg, iv_bg;
    private ImageButton bt_select_id, bt_more_opera;
    private ImageButton ib_stop_danmu, ib_send_danmuByGallery, ib_send_danmuByCamera, ib_show_DanmuBySpeech, ib_send_danmuBySpeech;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    private RelativeLayout ly_no_login_head, ly_random_head;
    private RelativeLayout share_view;
    private RelativeLayout ly_select_id, ly_more_opera, ly_speech, rl_control_bg;
    private LinearLayout ly_danmu_controller;
    private FrameLayout emoji_fragment;
    private TextView tv_random_name;
    private TextView tv_user_name;
    private TextView tv_hot_word;
    private AppCompatSeekBar seekbar_danmu_speed;
    private TextView tv_speech_clear, tv_speech_send;

    private boolean isReport = false;
    private boolean isanonymous = true;
    private boolean danmuIsStop = false;

    private TopicModel currentTopicModel;

    private long startUpdateTime = System.currentTimeMillis() / 1000;

    SpeechRecognizer mIat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk2);
        //开启低精度定位
        startGpsByLow(1000 * 10, true, aMapLocationListener);
        //沉浸式标题栏
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));

        String topicmodelString = getIntent().getStringExtra(IntentKeyConfig.TOPICMODEL);
        if (topicmodelString != null && !topicmodelString.isEmpty()) {
            currentTopicModel = JSONObject.parseObject(topicmodelString, TopicModel.class);
        }
        topicid = getIntent().getStringExtra(IntentKeyConfig.TOPICID);
        //初始化toolbar
        initToolbar();

        isFirstCreate = getIntent().getBooleanExtra(ISFIRSTCREATETAG, false);
        findById();
        setOnClick();
        getDanmuForLocation();
        getUserRandomIcon(topicid);
        acquireWakeLock();
        arr = new ArrayList();
        for (int i = 0; i < 6; i++) {
            arr.add(i);
        }

    }

    private AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    setLat(aMapLocation.getLatitude());
                    setLon(aMapLocation.getLongitude());
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    LogUtil.e("location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };
    private Handler timerHander;

    //开始更新弹幕的线程
    private Runnable timeRunable = new Runnable() {
        @Override
        public void run() {
            updateDanmu(false);
            timerHander.postDelayed(this, UPDATEDURATION * 1000);
        }
    };

    //获得增量更新
    private void startUpdateTimer() {
        if (timerHander == null) {
            timerHander = new Handler();
            timerHander.post(timeRunable);
        }

    }

    //后台更新弹幕
    private void updateDanmu(final boolean isSendDanmu) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("topicid", topicid);
        jsonObject.put("starttime", startUpdateTime);
        final long nextime = System.currentTimeMillis() / 1000;
        jsonObject.put("T", nextime - startUpdateTime);
        new HttpManager<DanmuListModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getlatestdanmu, jsonObject, DanmuListModel.class, new HttpRequestCallBack<DanmuListModel>() {
            @Override
            public void onRequestSuccess(DanmuListModel response, boolean cached) {
                startUpdateTime = nextime;
                if (response.getDanmu().size() > 0) {
                    if (!isSendDanmu) danmuContents.addAll(0, response.getDanmu());
                    if (sumDanmuModel.size() == 0 && danmakuView.getCurrentVisibleDanmakus().isEmpty()) {
                        addDanmu(danmuContents);
                    }
                    sumDanmuModel.addAll(0, response.getDanmu());
                    if ((lastPageSize + response.getDanmu().size()) < pageSize) {
                        lastPageSize += response.getDanmu().size();
                    } else {
                        pageNum++;
                        lastPageSize = (lastPageSize + response.getDanmu().size()) - pageSize;
                    }
                }

            }

            @Override
            public void onRequestFailed(VolleyError error) {
            }
        });
    }


    //从后台获取弹幕数据
    private void startReciveDanmuUpdate() {

        if (topicid == null && topicid.equals("-1")) {
            showToast(getString(R.string.get_topic_id_fail));
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("topicid", topicid);
        jsonObject.put("pagenum", pageNum);
        jsonObject.put("pagesize", pageSize);
        new HttpManager<DanmuListModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getDanmubyTopic, jsonObject, DanmuListModel.class, new HttpRequestCallBack<DanmuListModel>() {
            @Override
            public void onRequestSuccess(DanmuListModel response, boolean cached) {
                maxPageNum = pageNum;
                if (response.getDanmu().size() > 0) {//获取到至少一条弹幕
                    if (response.getDanmu().size() < pageSize) {//
                        if (lastPageSize < response.getDanmu().size()) {
                            List<DanmuModel> newDanmuList = response.getDanmu().subList(lastPageSize + 1, response.getDanmu().size());
                            danmuContents.addAll(0, newDanmuList);
                            sumDanmuModel.addAll(newDanmuList);
                            lastPageSize = response.getDanmu().size();
                        }
                    } else {
                        sumDanmuModel.addAll(response.getDanmu());
                        danmuContents = new ArrayList<>(sumDanmuModel.subList((pageNum - 1) * pageSize, pageNum * pageSize));
                        pageNum++;
                    }
                    if (!isFirstOpen) {
                        addDanmu(danmuContents);
                    }

                } else {
                    lastPageSize = 0;
                    pageNum = 1;
                    getDanmuForLocation();
                }
                startUpdateTimer();
            }

            @Override
            public void onRequestFailed(VolleyError error) {
                LogUtil.e("获取弹幕列表失败" + error.getMessage() + "\n" + error.getLocalizedMessage());
            }
        });
    }

    //初始化toolbar
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            topicName = getIntent().getStringExtra(IntentKeyConfig.Tag_Name);
            getSupportActionBar().setTitle(topicName);
        }




    }

    private float danmuSpeedFactor = 1f;
    private BackgroudCacheStuffer backgroudCacheStuffer = new BackgroudCacheStuffer();

    private void findById() {
        ly_no_login_head = (RelativeLayout) findViewById(R.id.ly_no_login_head);
        ly_random_head = (RelativeLayout) findViewById(R.id.ly_random_head);
        ly_more_opera = (RelativeLayout) findViewById(R.id.ly_more_opera);
        ly_speech = (RelativeLayout) findViewById(R.id.ly_speech);
        bt_emoji = (Button) findViewById(R.id.bt_emoji);
        bt_more_opera = (ImageButton) findViewById(R.id.bt_more_opera);
        emoji_fragment = (FrameLayout) findViewById(R.id.emojicons);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        danmakuView = (USeeDanmuView) findViewById(R.id.danmakuView);
        bt_sendmsg = (Button) findViewById(R.id.bt_sendComment);
        et_danmu = (EmojiconEditText) findViewById(R.id.et_comment);
        share_view = (RelativeLayout) findViewById(R.id.share_view);
        if (Dp2Px.getWindowWidth(this) <= 1080) {
            danmuSpeedFactor = DanmuConfig.mSpeed;

        } else {
            danmuSpeedFactor = DanmuConfig.mHPISpeed;
        }
        iv_topic_image = (ImageView) findViewById(R.id.iv_topic_image);
        rl_control_bg = (RelativeLayout) findViewById(R.id.rl_control_bg);
        iv_preivous_bg = (ImageView) findViewById(R.id.iv_previous_bg);
        iv_next_bg = (ImageView) findViewById(R.id.iv_next_bg);
        iv_bg = (ImageView) findViewById(R.id.iv_bg);
        float factor = (float) (100 - DanmuConfig.danmuSpeedSeekbarProgress) / 100 * danmuSpeedFactor + danmuSpeedFactor / 2;
        DanmuConfig.setDanmakuViewConfig(danmakuView, factor, backgroudCacheStuffer);
        bt_select_id = (ImageButton) findViewById(R.id.bt_select_id);
        ly_select_id = (RelativeLayout) findViewById(R.id.ly_select_id);
        iv_random_be_checked = (ImageView) findViewById(R.id.iv_random_be_checked);
        iv_user_be_checked = (ImageView) findViewById(R.id.iv_user_be_checked);
        view_mesh = findViewById(R.id.view_mesh);
        ib_expand_less = (ImageView) findViewById(R.id.ib_expand_less);
        ly_danmu_controller = (LinearLayout) findViewById(R.id.ly_danmu_controller);
        ib_stop_danmu = (ImageButton) findViewById(R.id.ib_stop_danmu);
        ib_send_danmuByCamera = (ImageButton) findViewById(R.id.ib_send_danmu_camera);
        ib_send_danmuByGallery = (ImageButton) findViewById(R.id.ib_send_danmu_gallery);
        ib_send_danmuBySpeech = (ImageButton) findViewById(R.id.ib_send_danmu_audio);
        ib_show_DanmuBySpeech = (ImageButton) findViewById(R.id.ib_show_send_danmu_audio);
        seekbar_danmu_speed = (AppCompatSeekBar) findViewById(R.id.seekbar_speed_danmu);
        seekbar_danmu_speed.setProgress(DanmuConfig.danmuSpeedSeekbarProgress);
        iv_random_head = (ImageView) findViewById(R.id.iv_random_head);
        iv_user_head = (ImageView) findViewById(R.id.iv_user_head);
        tv_random_name = (TextView) findViewById(R.id.tv_random_name);
        tv_speech_clear = (TextView) findViewById(R.id.tv_speech_clear);
        tv_speech_send = (TextView) findViewById(R.id.tv_speech_send);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        TextView tv_tag_description = (TextView) findViewById(R.id.tv_tag_description);
        tv_hot_word = (TextView) findViewById(R.id.tv_hot_word);
        tv_tag_description.setText(currentTopicModel == null ? "" : currentTopicModel.getDescription());

        if (currentTopicModel.getImgurls() != null && currentTopicModel.getImgurls().size() > 0) {
            Glide.with(this).load(currentTopicModel.getImgurls().get(0)).centerCrop().placeholder(R.drawable.default_head).crossFade().into(iv_topic_image);
        } else {
            iv_topic_image.setVisibility(View.GONE);
        }

        setEmojiconFragment(false);



    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        int height = toolbar.getHeight();

//        RelativeLayout.LayoutParams lm = new RelativeLayout.LayoutParams(danmakuView.getLayoutParams());
//        lm.setMargins(0,height,0,0);
//        danmakuView.setLayoutParams(lm);

        WindowManager manager = this.getWindowManager();
        int screenHeight = manager.getDefaultDisplay().getHeight();
//        LogUtil.i("sceenHeight:" + screenHeight);
        RelativeLayout rl_bottom = (RelativeLayout) this.findViewById(R.id.rl_bottom);
        int bottomHeight = rl_bottom.getHeight();
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv_bg.getLayoutParams();
        lp.height = screenHeight - height - bottomHeight;
        iv_bg.setLayoutParams(lp);





    }


    //在提示弹幕跑完后换成这个回调接口
    private DrawHandler.Callback startNewDanmuCallback = new DrawHandler.Callback() {
        @Override
        public void prepared() {

        }

        @Override
        public void updateTimer(DanmakuTimer timer) {

        }

        @Override
        public void danmakuShown(BaseDanmaku danmaku) {
            getDanmuForLocation();
        }

        @Override
        public void drawingFinished() {
            getDanmuForLocation();
        }
    };

    /**
     * 隐藏选择匿名窗口，输入法，更多操作，emoji表情
     *
     * @param noHideLayout 表示不隐藏的窗口，0为匿名窗口，1为输入法，2为emoji，3为更多操作,4为语音输入界面
     */
    private void hideLayout(int noHideLayout) {
        switch (noHideLayout) {
            case 0:
                hideInputWindow(et_danmu.getWindowToken());
                if (emoji_fragment.getVisibility() == View.VISIBLE)
                    emoji_fragment.setVisibility(View.GONE);
                if (ly_more_opera.getVisibility() == View.VISIBLE)
                    ly_more_opera.setVisibility(View.GONE);
                if (ly_speech.getVisibility() == View.VISIBLE) ly_speech.setVisibility(View.GONE);
                break;
            case 1:
                if (ly_select_id.getVisibility() == View.VISIBLE)
                    ly_select_id.setVisibility(View.GONE);
                if (emoji_fragment.getVisibility() == View.VISIBLE)
                    emoji_fragment.setVisibility(View.GONE);
                if (ly_more_opera.getVisibility() == View.VISIBLE)
                    ly_more_opera.setVisibility(View.GONE);
                if (ly_speech.getVisibility() == View.VISIBLE) ly_speech.setVisibility(View.GONE);
                break;
            case 2:
                hideInputWindow(et_danmu.getWindowToken());
                if (ly_select_id.getVisibility() == View.VISIBLE)
                    ly_select_id.setVisibility(View.GONE);
                if (ly_more_opera.getVisibility() == View.VISIBLE)
                    ly_more_opera.setVisibility(View.GONE);
                if (ly_speech.getVisibility() == View.VISIBLE) ly_speech.setVisibility(View.GONE);
                break;
            case 3:
                hideInputWindow(et_danmu.getWindowToken());
                if (ly_select_id.getVisibility() == View.VISIBLE)
                    ly_select_id.setVisibility(View.GONE);
                if (emoji_fragment.getVisibility() == View.VISIBLE)
                    emoji_fragment.setVisibility(View.GONE);
                if (ly_speech.getVisibility() == View.VISIBLE) ly_speech.setVisibility(View.GONE);
                break;
            case 4:
                hideInputWindow(et_danmu.getWindowToken());
                if (ly_select_id.getVisibility() == View.VISIBLE)
                    ly_select_id.setVisibility(View.GONE);
                if (emoji_fragment.getVisibility() == View.VISIBLE)
                    emoji_fragment.setVisibility(View.GONE);
                if (ly_more_opera.getVisibility() == View.VISIBLE)
                    ly_more_opera.setVisibility(View.GONE);
                break;
        }
    }

    private void setOnClick() {
        //更多操作点击
        bt_more_opera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏除了更多操作以外的所有窗口
                hideLayout(3);
                if (ly_more_opera.getVisibility() == View.GONE) {
                    ly_more_opera.setVisibility(View.VISIBLE);
                } else {
                    ly_more_opera.setVisibility(View.GONE);
                }
            }
        });

        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                danmakuView.start();
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showWarnDanmu();
                    }
                }, 100);
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        showWarnDanmu();
//                    }
//                });
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {
                danmakuView.setCallback(startNewDanmuCallback);
                getDanmuForLocation();
            }
        });
        View.OnClickListener sendDanmuOnclickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_danmu.getText().toString().trim().isEmpty()) {
                    showToast(getString(R.string.input_no_null));
                    return;
                }
                String danmuMSg = et_danmu.getText().toString().trim();
                sendDanmu(danmuMSg, isanonymous, null);
                et_danmu.setText("");
                hideInputWindow(et_danmu.getWindowToken());
            }
        };
        //发送按钮点击
        bt_sendmsg.setOnClickListener(sendDanmuOnclickListener);

        //语音发送点击
        tv_speech_send.setOnClickListener(sendDanmuOnclickListener);

        tv_speech_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_danmu.setText("");
            }
        });

        bt_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLayout(2);
                if (emoji_fragment.getVisibility() == View.GONE) {
                    emoji_fragment.setVisibility(View.VISIBLE);
                } else {
                    emoji_fragment.setVisibility(View.GONE);
                }
            }
        });
        final ImageButton back = (ImageButton) findViewById(R.id.title_back);
        assert back != null;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_danmu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputWindow(et_danmu);
                hideLayout(1);
            }
        });
        et_danmu.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showInputWindow(et_danmu);
                    hideLayout(1);
                }
            }
        });
        et_danmu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    bt_more_opera.setVisibility(View.VISIBLE);
                    bt_sendmsg.setVisibility(View.INVISIBLE);
                } else {
                    bt_sendmsg.setVisibility(View.VISIBLE);
                    bt_more_opera.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        danmakuView.setOnDanmuListener(new USeeDanmuView.OnDanmuListener() {

            @Override
            public void onDanmuClick(USeeDanmu baseDanmaku) {
                if (baseDanmaku.danmuStyle == -2) return;
                Intent intent = new Intent(Talk2Activity.this, DanmuDetailActivity.class);
                intent.putExtra(DanmuDetailActivity.DANMAKU_CONTENT, baseDanmaku.text + "");
                intent.putExtra(DanmuDetailActivity.DANMAKU_ID, baseDanmaku.id);
                intent.putExtra(IntentKeyConfig.USERID, baseDanmaku.userid);
                intent.putExtra(IntentKeyConfig.Tag_Name, topicName);
                intent.putExtra(IntentKeyConfig.USERICON, userIcon);
                intent.putExtra(IntentKeyConfig.USERNICKNAME, tv_random_name.getText().toString());
                intent.putExtra(IntentKeyConfig.RANDOMICONID, randomIconId);
                startActivity(intent);
            }

            @Override
            public void onDanmuLongClick(USeeDanmu baseDanmaku) {
                if (baseDanmaku.danmuStyle == -2) return;
                showDanmuCopyAndReport(baseDanmaku);
            }


            @Override
            public void onNoDanmusClick() {

                if (danmakuView.isPaused()) {
                    danmakuView.resume();
                } else {
                    danmakuView.pause();
                }
                hideLayout(4);
//                if (ly_danmu_controller.getVisibility() == View.GONE) {
//                    ly_danmu_controller.setVisibility(View.VISIBLE);
//
//                } else {
//                    ly_danmu_controller.setVisibility(View.GONE);
//                }
//
//                hideInputWindow(et_danmu.getWindowToken());
//                danmakuView.setFocusable(true);
//                danmakuView.setFocusableInTouchMode(true);
//                if (ly_select_id.getVisibility() == View.VISIBLE)
//                    ly_select_id.setVisibility(View.GONE);
//                if (emoji_fragment.getVisibility() == View.VISIBLE)
//                    emoji_fragment.setVisibility(View.GONE);

//                if (danmakuView.isPaused() && !danmuIsStop) {
//                    danmakuView.resume();
//                }


            }

            @Override
            public void onActionDown() {
                if (!danmakuView.isPaused()) danmakuView.pause();
            }

            @Override
            public void onActionMove() {
                if (danmakuView.isPaused() && !danmuIsStop && view_mesh.getVisibility() == View.GONE)
                    danmakuView.resume();
            }


        });
//        danmakuView.getView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                danmuBeClickedEvent();
//            }
//        });
//        danmakuView.getView().setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (currentTouchedDanmu != null) {
//                    if (currentTouchedDanmu.danmuStyle == -2) return true;
//                    showDanmuCopyAndReport(currentTouchedDanmu);
//                } else {
//
//                }
//                return true;
//            }
//        });
//        danmakuView.setOnTouchListener(this);
        bt_select_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideLayout(0);
                if (ly_select_id.getVisibility() == View.GONE) {
                    ly_select_id.setVisibility(View.VISIBLE);
                    bt_select_id.setFocusable(true);
                    bt_select_id.setFocusableInTouchMode(true);
                } else {
                    ly_select_id.setVisibility(View.GONE);
                }
            }
        });
        ib_expand_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appBarLayout.setExpanded(false, true);
            }
        });

        //暂停弹幕
        ib_stop_danmu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (danmakuView.isPaused()) {
                    danmakuView.resume();
                    danmuIsStop = false;
                    ib_stop_danmu.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                } else {
                    danmuIsStop = true;
                    danmakuView.pause();
                    ib_stop_danmu.setImageDrawable(getResources().getDrawable(R.drawable.danmu_play));
                }
            }
        });


//        //调节弹幕速度
//        seekbar_danmu_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {//调节速度
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                DanmuConfig.danmuSpeedSeekbarProgress = i;
//                float factor = (float) (100 - i) / 100 * danmuSpeedFactor + danmuSpeedFactor / 2;
//                if (danmakuView.getConfig().scrollSpeedFactor != factor)
//                    uSeeDanmakuFactory.updateDurationFactor(factor);
//
//                danmakuView.getConfig().setScrollSpeedFactor(factor);
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });


        if (getInstance().getUserModel() != null) {
            AssetImageUtils.loadUserHead(this, getInstance().getUserModel().getUserIcon(), iv_user_head);
            tv_user_name.setText(getInstance().getUserModel().getNickname());
            tv_user_name.setTextColor(getResources().getColor(R.color.text_be_clicked));
        }
        ly_no_login_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getInstance().getUserId().equals(BaseApplication.NOUSER)) {
                    startActivityForResult(new Intent(Talk2Activity.this, UserManagerActivity.class), UserManagerActivity.requestCode);
                } else {
                    isanonymous = false;
                    iv_user_be_checked.setVisibility(View.VISIBLE);
                    iv_random_be_checked.setVisibility(View.GONE);
                    AssetImageUtils.loadUserHead(Talk2Activity.this, getInstance().getUserModel().getUserIcon(), bt_select_id);
                }

            }
        });

        ly_random_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isanonymous = true;
                iv_random_be_checked.setVisibility(View.VISIBLE);
                iv_user_be_checked.setVisibility(View.GONE);
                AssetImageUtils.loadUserHead(Talk2Activity.this, userIcon, bt_select_id);
            }
        });
        ib_send_danmuBySpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetAvailable(Talk2Activity.this)){//有网的情况
                     startSpeech();
                }else{//网络不可用的情况
                    showNoNetWorkDialog();
                }

            }
        });
        ib_show_DanmuBySpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLayout(4);
                if (ly_speech.getVisibility() == View.GONE) ly_speech.setVisibility(View.VISIBLE);

//                if(isNetAvailable(Talk2Activity.this)){//有网的情况
////                    ib_send_danmuBySpeech.performClick();
//                }else{//网络不可用的情况
//                    showNoNetWorkDialog();
//                }

            }
        });
        ib_send_danmuByGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtils.toPickPhoto(Talk2Activity.this);
            }
        });
        ib_send_danmuByCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File parent = FileUtil.getCacheDir(Talk2Activity.this);
                imageFile = new File(parent, System.nanoTime() + ".png");
                if (!imageFile.exists()) {
                    try {
                        imageFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(intent, Ruqest_to_camera);
            }
        });
        iv_topic_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_topic_image.setVisibility(View.GONE);
                rl_control_bg.setVisibility(View.VISIBLE);
                Glide.with(Talk2Activity.this).load(currentTopicModel.getImgurls().get(currentBg)).centerCrop().crossFade().into(iv_bg);
                backgroudCacheStuffer.hasBg = true;
            }
        });
        iv_preivous_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentBg == 0) {
                    currentBg = currentTopicModel.getImgurls().size() - 1;
                } else {
                    currentBg--;
                }
                Glide.with(Talk2Activity.this).load(currentTopicModel.getImgurls().get(currentBg)).centerCrop().crossFade().into(iv_bg);
            }

        });
        iv_next_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentBg == currentTopicModel.getImgurls().size() - 1) {
                    currentBg = 0;
                } else {
                    currentBg++;
                }
                Glide.with(Talk2Activity.this).load(currentTopicModel.getImgurls().get(currentBg)).centerCrop().crossFade().into(iv_bg);
            }
        });
    }

    private void showNoNetWorkDialog() {
        final Dialog tipDialog = new Dialog(this, R.style.MyAlertDialog);
        //加载对话框布局
        View mainView = View.inflate(this, R.layout.dialog_tip_no_network, null);
        Button bt_cancel = (Button) mainView.findViewById(R.id.bt_cancel);
        Button bt_setting_network = (Button) mainView.findViewById(R.id.bt_setting_network);
        //对话框中按钮点击事件
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipDialog.dismiss();
            }
        });
        bt_setting_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
                tipDialog.dismiss();
            }
        });
        //对话框设置布局
        tipDialog.setContentView(mainView);
        view_mesh.setVisibility(View.VISIBLE);
        tipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                view_mesh.setVisibility(View.GONE);
            }
        });
        //设置布局参数
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = getResources().getDimensionPixelOffset(R.dimen.px_four_hundred_sixty);
        tipDialog.getWindow().setAttributes(layoutParams);
        tipDialog.show();
    }

    //判断网络是否可用
    private boolean isNetAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isAvailable());
    }

    int currentBg = 0;
    private File imageFile;
    //语音听写的回调
    private RecognizerListener recognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            if (i <= 5) {
                ib_send_danmuBySpeech.setImageDrawable(getResources().getDrawable(R.drawable.audio_0));
            } else if (i > 6 && i <= 11) {
                ib_send_danmuBySpeech.setImageDrawable(getResources().getDrawable(R.drawable.audio_1));
            } else if (i > 11 && i <= 16) {
                ib_send_danmuBySpeech.setImageDrawable(getResources().getDrawable(R.drawable.audio_2));
            } else if (i > 16 && i <= 21) {
                ib_send_danmuBySpeech.setImageDrawable(getResources().getDrawable(R.drawable.audio_3));
            } else if (i > 21 && i <= 25) {
                ib_send_danmuBySpeech.setImageDrawable(getResources().getDrawable(R.drawable.audio_4));
            } else if (i > 25 && i <= 30) {
                ib_send_danmuBySpeech.setImageDrawable(getResources().getDrawable(R.drawable.audio_5));
            }
        }

        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onEndOfSpeech() {
            ib_send_danmuBySpeech.setImageDrawable(getResources().getDrawable(R.drawable.audio));
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            JSONObject recongnizerResultJson = JSONObject.parseObject(recognizerResult.getResultString());
            JSONArray wsJsonArray = recongnizerResultJson.getJSONArray("ws");
            for (int i = 0; i < wsJsonArray.size(); i++) {
                JSONArray cwJsonArray = wsJsonArray.getJSONObject(i).getJSONArray("cw");
                if (cwJsonArray.size() > 0)
                    et_danmu.append(cwJsonArray.getJSONObject(0).getString("w"));
            }
        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    //开始语音听写
    private void startSpeech() {
        if (mIat == null) {
            mIat = SpeechRecognizer.createRecognizer(this, null);
        }
        //2.设置听写参数，详见《MSC Reference Manual》SpeechConstant类
        // mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        // mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
        mIat.startListening(recognizerListener);

    }

    //发送一条弹幕
    private void sendDanmu(String msg, boolean isannoymous, List<String> photoPath) {
        if (BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)) {
            showNoLogInTipDialog();
            return;
        }

        if (getLon() == 0 && getLat() == 0) {
            showToast("定位未成功");
            return;
        }
        KWSeekerManage kwSeekerManage = SimpleKWSeekerProcessor.newInstance(this);
        msg = kwSeekerManage.getKWSeeker("topic-sensitive-word").replaceWords(msg);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isannoymous", isannoymous);
        jsonObject.put("lon", getLon());
        jsonObject.put("lat", getLat());
        jsonObject.put("delete_time", "1577844610");
        jsonObject.put("messages", msg);
        jsonObject.put("devid", getInstance().getDevId());
        jsonObject.put("userid", getInstance().getUserId());
        jsonObject.put("topicid", topicid);
        jsonObject.put("randomUserIcon", userIcon);
        jsonObject.put("randomIconId", randomIconId);
        jsonObject.put("randomUserName", tv_random_name.getText().toString());
        if (photoPath == null) photoPath = new ArrayList<String>();
        jsonObject.put("imgurls", photoPath);
        new HttpManager<DanmuWithCodeModel>().sendQuest(Request.Method.POST, HttpUrlConfig.sendDanmu, jsonObject, DanmuWithCodeModel.class, new HttpRequestCallBack<DanmuWithCodeModel>() {
            @Override
            public void onRequestSuccess(DanmuWithCodeModel response, boolean cached) {

                int code = response.getCode();
                LogUtil.i("返回码：" + code);
                //添加返回码标识
                DanmuModel model = response.getDanmu();
                if(code == 200) {
                    danmuContents.add(0, model);
                }else if(code == 400){
                    showToast("请求失败");
                }else if(code == 401){
                    showToast("未登录");
                }else if(code == 402){
                    showToast("需要充值");
                }else if(code == 403){
                    showToast("你被封号");
                }else if(code == 404){
                    showToast("弹幕不存在");
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
                if (sumDanmuModel.size() == 0 && danmakuView.getCurrentVisibleDanmakus().size() <= 0) {
                    pageNum = 1;

                    addDanmu(danmuContents, 0);
                    isStartover = true;
                }
                if (emoji_fragment.getVisibility() == View.VISIBLE)
                    emoji_fragment.setVisibility(View.GONE);
                if (ly_select_id.getVisibility() == View.VISIBLE)
                    ly_select_id.setVisibility(View.GONE);
                updateDanmu(true);

            }

            @Override
            public void onRequestFailed(VolleyError error) {
                showToast("发送失败");
            }
        });
    }

    boolean isStartover = true;

    private void getDanmuForLocation() {
        isStartover = false;
        if (danmuContents.size() > 0) {//从弹幕队列中获取弹幕
            addDanmu(danmuContents);
        } else if (lastPageSize == -1) {//从后台更新弹幕
            startReciveDanmuUpdate();
        } else if (pageNum <= (sumDanmuModel.size() / pageSize) + 1 && (sumDanmuModel.size() - pageSize * pageNum != 0)) {//从缓存弹幕总列表中最后一页弹幕中获取弹幕
            int nextDanmuIndex, startDanmuIndex;
            startDanmuIndex = (pageNum - 1) * pageSize;
            if (pageNum < (maxPageNum - 1)) {
                nextDanmuIndex = pageNum * pageSize;
                pageNum++;
            } else {
                isStartover = true;
                nextDanmuIndex = sumDanmuModel.size();
                pageNum = 1;
            }
            danmuContents = new ArrayList<>(sumDanmuModel.subList(startDanmuIndex, nextDanmuIndex));
            addDanmu(danmuContents);
        } else {//从缓存弹幕总列表中最后一页弹幕中获取弹幕
            isStartover = true;
            pageNum = 1;
            danmuContents = new ArrayList<>(sumDanmuModel.subList(0, pageSize < sumDanmuModel.size() ? pageSize : sumDanmuModel.size()));
            addDanmu(danmuContents);
        }
        startUpdateTimer();
    }

    //显示复制弹幕和举报弹幕的pop框
    private void showDanmuCopyAndReport(final USeeDanmu danmu) {
        View mainView = View.inflate(this, R.layout.pop_danmu_copy_report, null);
        view_mesh.setVisibility(View.VISIBLE);

        final PopupWindow pop_copy_report = new PopupWindow(mainView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pop_copy_report.setOutsideTouchable(true);
        pop_copy_report.setBackgroundDrawable(new ColorDrawable());
        pop_copy_report.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!isReport) {
                    danmakuView.resume();
                    view_mesh.setVisibility(View.GONE);
                }
            }
        });

        Button bt_report = (Button) mainView.findViewById(R.id.bt_report);
        if (danmu.danmuStyle == -1) bt_report.setText("删除");
        Button bt_copy = (Button) mainView.findViewById(R.id.bt_copy);
        bt_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CopyManager.copy(danmu.text + "", Talk2Activity.this);
                isReport = false;
                pop_copy_report.dismiss();
            }
        });
        bt_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (danmu.danmuStyle == -1) {
                    danmu.setVisibility(false);
//                    danmu.userId = 10;
                    deleteDanmu(danmu.id);
                    sumDanmuModel.remove(danmu);
                    danmakuView.getConfig().setUserIdBlackList(danmu.userId);
                } else {
                    isReport = true;
                    showPopReport(danmu);
                }

                pop_copy_report.dismiss();


            }
        });
        pop_copy_report.showAsDropDown(danmakuView, (int) danmu.getLeft() + danmu.padding, (int) (danmu.getBottom() - danmakuView.getHeight()) - danmu.padding);
    }

    //调用后台接口，从后台删除弹幕
    private void deleteDanmu(String danmuID) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", BaseApplication.getInstance().getUserId());
        jsonObject.put("danmuID", danmuID);
        LogUtil.i("删除弹幕接口" + jsonObject.toJSONString());
        new HttpManager<DeleteModel>().sendQuest(Request.Method.POST, HttpUrlConfig.deleteDanmu, jsonObject, DeleteModel.class, new HttpRequestCallBack<DeleteModel>() {
            @Override
            public void onRequestSuccess(DeleteModel response, boolean cached) {
                LogUtil.i("删除弹幕接口结果" + JSONObject.toJSONString(response));
                if (response.isStatus()) {
                    showToast("删除成功");
                }
            }

            @Override
            public void onRequestFailed(VolleyError error) {

            }
        });
    }

    //举报
    private void report(int reporttyle, USeeDanmu uSeeDanmu) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid", BaseApplication.getInstance().getUserId());
        jsonObject.put("contentuserid", uSeeDanmu.userid);
        jsonObject.put("contentid", uSeeDanmu.id);
        jsonObject.put("contenttype", "d");
        jsonObject.put("reporttype", reporttyle);
        new HttpManager<ResultModel>().sendQuest(Request.Method.POST, HttpUrlConfig.reportcontent, jsonObject, ResultModel.class, new HttpRequestCallBack<ResultModel>() {
            @Override
            public void onRequestSuccess(ResultModel response, boolean cached) {
                if (response.getResult() == 1) {
                    showToast("举报成功");
                } else {
                    showToast("举报失败");
                }
            }

            @Override
            public void onRequestFailed(VolleyError error) {

            }
        });

    }

    //显示举报对话框
    private void showPopReport(final USeeDanmu danmu) {
        if (BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)) {
            showNoLogInTipDialog();
            view_mesh.setVisibility(View.GONE);
            return;
        }
        if (view_mesh.getVisibility() == View.GONE) view_mesh.setVisibility(View.VISIBLE);
        View mainView = View.inflate(this, R.layout.pop_report, null);
        final PopupWindow popupWindow = new PopupWindow(mainView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_mesh.setVisibility(View.GONE);
                isReport = false;
                if (danmakuView.isPaused()) {
                    danmakuView.resume();
                }
            }
        });

        TextView tv_cancel = (TextView) mainView.findViewById(R.id.tv_cancel);
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
                        report(1, danmu);
                        break;
                    case R.id.tv_bilk:
                        report(2, danmu);
                        break;
                    case R.id.tv_pornography:
                        report(3, danmu);
                        break;
                    case R.id.tv_uncivilized:
                        report(4, danmu);
                        break;
                    case R.id.tv_other:
                        report(5, danmu);
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
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

//    private void performClickWithlatest(BaseDanmaku newest) {
//        if (danmakuView.getOnDanmakuClickListener() != null) {
//            danmakuView.getOnDanmakuClickListener().onDanmakuClick(newest);
//        }
//    }
//
//    private void performClick(IDanmakus danmakus) {
//        if (danmakuView.getOnDanmakuClickListener() != null) {
//            danmakuView.getOnDanmakuClickListener().onDanmakuClick(danmakus);
//        }
//    }
//
//    private RectF mDanmakuBounds = new RectF();
//
//    private IDanmakus touchHitDanmaku(float x, float y) {
//        IDanmakus hitDanmakus = new Danmakus();
//        mDanmakuBounds.setEmpty();
//        IDanmakus danmakus = danmakuView.getCurrentVisibleDanmakus();
//        if (null != danmakus && !danmakus.isEmpty()) {
//            IDanmakuIterator iterator = danmakus.iterator();
//            while (iterator.hasNext()) {
//                BaseDanmaku danmaku = iterator.next();
//                if (null != danmaku) {
//                    mDanmakuBounds.set(danmaku.getLeft(), danmaku.getTop(), danmaku.getRight(), danmaku.getBottom());
//                    if (mDanmakuBounds.contains(x, y)) {
//                        hitDanmakus.addItem(danmaku);
//                    }
//                }
//            }
//        }
//
//        return hitDanmakus;
//    }
//
//    private BaseDanmaku fetchLatestOne(IDanmakus danmakus) {
//        if (danmakus.isEmpty()) {
//            return null;
//        }
//        return danmakus.last();
//    }

    //显示分享界面，使用一个布满全屏的View实现
    private void showShareView() {
        share_view.setVisibility(View.VISIBLE);
        share_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_view.setVisibility(View.GONE);
            }
        });
        LinearLayout ly_share_weixin = (LinearLayout) findViewById(R.id.share_weixin);
        LinearLayout ly_share_qq = (LinearLayout) findViewById(R.id.share_qq);
        LinearLayout ly_share_weixin_friends = (LinearLayout) findViewById(R.id.share_weixin_friends);
        LinearLayout ly_share_qq_friends = (LinearLayout) findViewById(R.id.share_qq_friends);
        LinearLayout ly_share_weibo = (LinearLayout) findViewById(R.id.share_weibo);
        View.OnClickListener shareListenr = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.share_qq:
                        UmengUtils.share(currentTopicModel.getId(), currentTopicModel.getTitle(), Talk2Activity.this, SHARE_MEDIA.QQ, umShareListener);
                        break;
                    case R.id.share_qq_friends:
                        UmengUtils.share(currentTopicModel.getId(), currentTopicModel.getTitle(), Talk2Activity.this, SHARE_MEDIA.QZONE, umShareListener);
                        break;
                    case R.id.share_weibo:
                        UmengUtils.share(currentTopicModel.getId(), currentTopicModel.getTitle(), Talk2Activity.this, SHARE_MEDIA.SINA, umShareListener);
                        break;
                    case R.id.share_weixin:
                        UmengUtils.share(currentTopicModel.getId(), currentTopicModel.getTitle(), Talk2Activity.this, SHARE_MEDIA.WEIXIN, umShareListener);
                        break;
                    case R.id.share_weixin_friends:
                        UmengUtils.share(currentTopicModel.getId(), currentTopicModel.getTitle(), Talk2Activity.this, SHARE_MEDIA.WEIXIN_CIRCLE, umShareListener);
                        break;
                }
            }
        };
        ly_share_qq.setOnClickListener(shareListenr);
        ly_share_weixin.setOnClickListener(shareListenr);
        ly_share_weibo.setOnClickListener(shareListenr);
        ly_share_qq_friends.setOnClickListener(shareListenr);
        ly_share_weixin_friends.setOnClickListener(shareListenr);


    }


    //分享结果的回调
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {

        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {

        }
    };
    int i = 0;

    private void addDanmu(List<DanmuModel> danmuContents) {
        addDanmu(danmuContents, isStartover ? 1500 : 500);
    }

    private void addDanmu(final List<DanmuModel> danmuContents, final int time) {
        //弹幕列表为空
        if (danmuContents.size() <= 0) return;

        final DanmuModel danmuModel = danmuContents.get(0);
        //产生0-5随机数
        if (i > 5) {
            i = 0;
            Collections.shuffle(arr);//对数组洗牌，实现出现0—5的不重复的随机数
        }
        //初始化弹幕对象
        final USeeDanmu danmaku = uSeeDanmakuFactory.createDanmaku(danmakuView.getConfig(), arr.get(i++));

        if (danmaku == null || danmakuView == null) {
            return;
        }

        danmaku.id = danmuModel.getId();
        LogUtil.i("danmaid:"   + danmaku.id);
        danmaku.userId = Integer.parseInt(danmaku.id);
        danmaku.userid = danmuModel.getUserId();

        //判断是否为自己发的弹幕
        if (danmuModel.getUserId().equals(getInstance().getUserId())) {//是自己发的弹幕
            if(danmuHot > 0){
                danmaku.danmuHot = danmuHot;
                danmuHot = danmuHot - 1;
            }else{
                danmaku.danmuHot = 0;
            }
            danmaku.danmuStyle = -1;
            danmaku.textColor = getResources().getColor(R.color.warn_title);
            danmaku.textSize = Dp2Px.dip2px(18f);
        } else {//不是自己发的弹幕
            if(danmuHot > 0){
                danmaku.danmuHot = danmuHot;
                danmuHot = danmuHot - 1;
            }else{
                danmaku.danmuHot = 0;
            }
            danmaku.danmuStyle = random.nextInt(4);//danmuStyle为不同的弹幕风格，即背景色不同
            danmaku.textColor = Color.WHITE;
            danmaku.textSize = Dp2Px.dip2px(16f);
        }
        //设置padding
        danmaku.padding = Dp2Px.dip2px(20);
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        final int length = Dp2Px.dip2px(30);

        if (danmuModel.getUserIcon().length() < 15) {
            //添加头像与是否有图片图标
            Spannable msg = createSpannable(danmuModel, AssetImageUtils.getDanmuHead(Talk2Activity.this, danmuModel.getUserIcon(), length), length);

            EmojiconHandler.addEmojis(Talk2Activity.this, msg, EmojiSize, DynamicDrawableSpan.ALIGN_BASELINE, EmojiSize);
            danmaku.text = msg;
//            danmaku.time = danmakuView.getCurrentTime() + time;
            danmaku.setTime(danmakuView.getCurrentTime() + time);
            danmakuView.addDanmaku(danmaku);
            danmuContents.remove(danmuModel);
        } else {
            Glide.with(Talk2Activity.this).load(HttpUrlConfig.iconUrl + danmuModel.getUserIcon()).asBitmap().placeholder(R.drawable.default_head).signature(new StringSignature(String.valueOf(System.currentTimeMillis() / (1000 * 60 * 60 * 24)))).into(new SimpleTarget<Bitmap>(length, length) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    //圆形bitmap
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                    roundedBitmapDrawable.setCircular(true);
                    Spannable msg = createSpannable(danmuModel, roundedBitmapDrawable, length);
                    EmojiconHandler.addEmojis(Talk2Activity.this, msg, EmojiSize, DynamicDrawableSpan.ALIGN_BASELINE, EmojiSize);
                    //设置弹幕文字
                    danmaku.text = msg;
//                    danmaku.time = danmakuView.getCurrentTime() + time;
                    danmaku.setTime(danmakuView.getCurrentTime() + time);
                    danmakuView.addDanmaku(danmaku);
                    danmuContents.remove(danmuModel);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    Drawable failLoadDrawable = getResources().getDrawable(R.drawable.default_head);
                    Spannable msg = createSpannable(danmuModel, failLoadDrawable, length);
                    EmojiconHandler.addEmojis(Talk2Activity.this, msg, EmojiSize, DynamicDrawableSpan.ALIGN_BASELINE, EmojiSize);
                    danmaku.text = msg;
//                    danmaku.time = danmakuView.getCurrentTime() + time;
                    danmaku.setTime(danmakuView.getCurrentTime() + time);
                    danmakuView.addDanmaku(danmaku);
                    danmuContents.remove(danmuModel);
                }
            });
        }

    }

    int EmojiSize = Dp2Px.dip2px(18);

    //添加头像与是否有图片的图标
    private SpannableStringBuilder createSpannable(DanmuModel danmuModel, Drawable headDrawable, int length) {
        String text = "      ";
        Drawable drawable = headDrawable;
        drawable.setBounds(0, 0, length, length);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        CenteredImageSpan imageSpan = new CenteredImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(imageSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append("  ").append(danmuModel.getMessages().replaceAll("\n", " ").replaceAll("\\\\\"", ""));
        if (danmuModel.getUserId().equals(BaseApplication.getInstance().getUserId())) {
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            spannableStringBuilder.setSpan(boldSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        //是否添加是否有图片标识
        if (danmuModel.getImgurls() != null && danmuModel.getImgurls().size() > 0) {

            int spanLen = spannableStringBuilder.length();
            spannableStringBuilder.append("     ");
            Drawable warnDram;

            if(danmuModel.getUserId().equals(BaseApplication.getInstance().getUserId())){
                warnDram = getResources().getDrawable(R.drawable.img_my_danmu);
            }else {
                warnDram = getResources().getDrawable(R.drawable.img_danmu);
            }
            warnDram.setBounds(0, 0, getResources().getDimensionPixelSize(R.dimen.px_thirty_six), getResources().getDimensionPixelSize(R.dimen.px_thirdty_two));
            CenteredImageSpan hasImageSpan = new CenteredImageSpan(warnDram, ImageSpan.ALIGN_BOTTOM);
            spannableStringBuilder.setSpan(hasImageSpan, spanLen + 2, spanLen + 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableStringBuilder;
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i("onPause" + danmakuView.isPrepared());
        hideInputWindow(et_danmu.getWindowToken());
        danmakuView.pause();
        if (timerHander != null) {
            timerHander.removeCallbacks(timeRunable);
            timerHander = null;
        }
    }

    boolean isFromPickPhoto = false;
    ArrayList<String> photosPath = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i("onResume" + danmakuView.isPrepared());
        if (danmuIsStop) {
            danmakuView.pause();
        } else {
            if (danmakuView.isPaused()) danmakuView.resume();
        }
        if (isFromPickPhoto) {
            isFromPickPhoto = false;
            Intent intent = new Intent(Talk2Activity.this, ImageDanmuActivity.class);
            intent.putExtra(IntentKeyConfig.TOPICID, topicid);
            intent.putExtra(IntentKeyConfig.USERICON, userIcon);
            intent.putExtra(IntentKeyConfig.RANDOMICONID, randomIconId);
            intent.putExtra(IntentKeyConfig.RANDOMUSERNAME, tv_random_name.getText().toString());
            intent.putExtra(IntentKeyConfig.IsAnnoymous, isanonymous);
            intent.putStringArrayListExtra("photos", photosPath);
            startActivityForResult(intent, Ruqest_to_image_danmu);
        }
        if (danmakuView != null && danmakuView.getCurrentVisibleDanmakus() != null && danmakuView.getCurrentVisibleDanmakus().isEmpty()) {
            getDanmuForLocation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
        danmakuView.release();
        if (timerHander != null) {
            timerHander.removeCallbacks(timeRunable);
            timerHander = null;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        startUpdateTimer();
        LogUtil.i("danmuView" + danmakuView.isPrepared());
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onBackPressed() {
        if (ly_select_id.getVisibility() == View.VISIBLE) {
            ly_select_id.setVisibility(View.GONE);
        } else if (emoji_fragment.getVisibility() == View.VISIBLE) {
            emoji_fragment.setVisibility(View.GONE);
        } else if (share_view.getVisibility() == View.GONE) {
            finish();
        } else {
            share_view.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        float x1 = 0, x2 = 0, y1 = 0, y2 = 0;
//        if (v.getId() == R.id.danmakuView) {
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                x1 = event.getX();
//                y1 = event.getY();
//                IDanmakus clickDanmakus = touchHitDanmaku(event.getX(), event.getY());
//                BaseDanmaku newestDanmaku = null;
//                if (null != clickDanmakus && !clickDanmakus.isEmpty()) {
//                    performClick(clickDanmakus);
//                    newestDanmaku = fetchLatestOne(clickDanmakus);
//                }
//                if (null != newestDanmaku) {
//                    performClickWithlatest(newestDanmaku);
//                    currentTouchedDanmu = (USeeDanmu) newestDanmaku;
//                    if (!danmakuView.isPaused() && currentTouchedDanmu.danmuStyle != -2)
//                        danmakuView.pause();
//                } else {
//                    currentTouchedDanmu = null;
//                }
//                LogUtil.i("x1:" + x1 + "\ty1" + y1);
//            }else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                x2 = event.getX();
//                y2 = event.getY();
//                if (Math.abs(x1 - x2) > 20 || Math.abs(y1 - y2) > 20) {
//                    if (danmakuView.isPaused() && !danmuIsStop) {
//                        danmakuView.resume();
//                    }
//                    LogUtil.i("danmuView滑动监听");
//                }
//                LogUtil.i("x2:" + x2 + "\ty2" + y2);
//            }
//        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            showShareView();
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_talk2, menu);
        return true;
    }

    //触发一个定时事件
    private void delayStart(int time) {

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                isFirstOpen = false;
                appBarLayout.setExpanded(false);

            }
        };
        Handler pdCanceller = new Handler(getMainLooper());
        pdCanceller.postDelayed(progressRunnable, time);
    }

    //提示弹幕
    private void showWarnDanmu() {
//        USeeDanmu warnDanmu = uSeeDanmakuFactory.createDanmaku(danmakuView.getConfig(), 2);
        USeeDanmu warnDanmu = new USeeDanmu(new Duration(3500), 2);
        warnDanmu.danmuStyle = -2;
        warnDanmu.userId = -1;
        if (currentTopicModel != null) {
            if (currentTopicModel.getDanmuNum() > 999) {

                warnDanmu.text = "本话题已有" + currentTopicModel.getDanmuNum() + "条弹幕！！！高能预警";
            } else if (isFirstCreate) {
                warnDanmu.text = "开始发送你的第一条弹幕吧~~";
            } else if (currentTopicModel.getDanmuNum() > 0) {
                warnDanmu.text = "本话题已有" + currentTopicModel.getDanmuNum() + "条弹幕~，做好准备哦";
            } else {
                warnDanmu.text = "该话题还没有弹幕";
            }
        } else {
            warnDanmu.text = "该话题还没有弹幕";
        }
        warnDanmu.textColor = getResources().getColor(R.color.warn_title);
        warnDanmu.textSize = Dp2Px.dip2px(20f);
//        warnDanmu.time = danmakuView.getCurrentTime();
        warnDanmu.setTime(danmakuView.getCurrentTime());
        danmakuView.addDanmaku(warnDanmu);
        delayStart(2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UserManagerActivity.requestCode) {
            if (!getInstance().getUserId().equals(BaseApplication.NOUSER) && getInstance().getUserModel() != null) {//用户登录后的结果
                AssetImageUtils.loadUserHead(Talk2Activity.this, getInstance().getUserModel().getUserIcon(), iv_user_head);
                tv_user_name.setText(getInstance().getUserModel().getNickname());
                tv_user_name.setTextColor(getResources().getColor(R.color.text_be_clicked));
                Talk2Activity.this.setResult(ChooseTagNewActivity.RESULT_NEED);
            }
            return;
        } else if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {//图片选择后的结果
            // Get Image Path List

            photosPath = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            isFromPickPhoto = true;
//  Intent intent = new Intent(Talk2Activity.this, ImageDanmuActivity.class);
//            intent.putExtra(IntentKeyConfig.TOPICID, topicid);
//            intent.putExtra(IntentKeyConfig.USERICON, userIcon);
//            intent.putExtra(IntentKeyConfig.RANDOMICONID, randomIconId);
//            intent.putExtra(IntentKeyConfig.RANDOMUSERNAME, tv_random_name.getText().toString());
//            intent.putExtra(IntentKeyConfig.IsAnnoymous, isanonymous);
//            intent.putStringArrayListExtra("photos", photosPath);
//            startActivityForResult(intent, Ruqest_to_image_danmu);
        } else if (requestCode == Ruqest_to_camera && resultCode == RESULT_OK) {//照相成功后的结果
            if (imageFile.exists()) {
                photosPath = new ArrayList<>();
                photosPath.add(imageFile.getAbsolutePath());
                isFromPickPhoto = true;
//                Intent intent = new Intent(Talk2Activity.this, ImageDanmuActivity.class);
//                intent.putExtra(IntentKeyConfig.TOPICID, topicid);
//                intent.putExtra(IntentKeyConfig.USERICON, userIcon);
//                intent.putExtra(IntentKeyConfig.RANDOMICONID, randomIconId);
//                intent.putExtra(IntentKeyConfig.RANDOMUSERNAME, tv_random_name.getText().toString());
//                intent.putStringArrayListExtra("photos", photosPath);
//                startActivityForResult(intent, Ruqest_to_image_danmu);
            }
        } else if (requestCode == Ruqest_to_image_danmu && resultCode == RESULT_OK) {
//            if (danmuIsStop) {
//                danmakuView.pause();
//            } else {
//                if (danmakuView.isPaused()) danmakuView.resume();
//            }
//            danmakuView.restart();
            Bundle bundle = data.getBundleExtra("data");
            DanmuModel response = (DanmuModel) bundle.getSerializable("danmuModel");
            LogUtil.i("图片danmuModel" + JSONObject.toJSONString(response));
            danmuContents.add(response);
            if (danmakuView.getCurrentVisibleDanmakus().size() <= 0 && danmuContents.size() <= 1) {
                pageNum = 1;
                addDanmu(danmuContents, 0);
                isStartover = true;
            }
            hideLayout(0);
            updateDanmu(true);
        }
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        LogUtil.i("onActivityResult" + danmakuView.isPrepared() + "\t");
        super.onActivityResult(requestCode, resultCode, data);
    }

    //获取在该话题内该用户的头像昵称
    private void getUserRandomIcon(String topicid) {
        if (getInstance().getUserId() == null && getInstance().getUserId().equals(BaseApplication.NOUSER))
            return;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid", getInstance().getUserId());
        jsonObject.put("topicid", topicid);
        new HttpManager<GetUserIconModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getusericonbytopic, jsonObject, GetUserIconModel.class, new HttpRequestCallBack<GetUserIconModel>() {
            @Override
            public void onRequestSuccess(GetUserIconModel response, boolean cached) {
                userIcon = response.getUsericon();
                randomIconId = response.getRandomIconId();
                iv_random_head.setImageDrawable(AssetImageUtils.getDanmuHead(Talk2Activity.this, userIcon, Dp2Px.dip2px(100)));
                if (response.getIsanonymous() == 1) {
                    isanonymous = false;
                    AssetImageUtils.loadUserHead(Talk2Activity.this, BaseApplication.getInstance().getUserModel().getUserIcon(), bt_select_id);
                    iv_random_be_checked.setVisibility(View.GONE);
                    iv_user_be_checked.setVisibility(View.VISIBLE);
                } else {
                    isanonymous = true;
                    AssetImageUtils.loadUserHead(Talk2Activity.this, userIcon, bt_select_id, Dp2Px.px_32, Dp2Px.px_32);
                    iv_user_be_checked.setVisibility(View.GONE);
                    iv_random_be_checked.setVisibility(View.VISIBLE);
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
        if (et_danmu.length() + emojicon.getEmoji().length() < 30)
            EmojiconsFragment.input(et_danmu, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(et_danmu);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    private PowerManager.WakeLock wakeLock;

    //申请长时间不锁屏
    private void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
            wakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}

