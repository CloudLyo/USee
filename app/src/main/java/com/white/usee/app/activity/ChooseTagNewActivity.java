package com.white.usee.app.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.config.IntentKeyConfig;
import com.white.usee.app.model.ActivityModel;
import com.white.usee.app.model.NewMsgsNumModel;
import com.white.usee.app.model.TopicModel;
import com.white.usee.app.model.TopicsModel;
import com.white.usee.app.model.UserModel;
import com.white.usee.app.util.AnimationUtils;
import com.white.usee.app.util.AssetImageUtils;
import com.white.usee.app.util.DistanceUtils;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.ThemeUtils;
import com.white.usee.app.util.TimerUtils;
import com.white.usee.app.util.refreah.PullToRefreshBase;
import com.white.usee.app.util.refreah.PullToRefreshScrollView;
import com.white.usee.app.view.WrapLineLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 选择话题界面
 */
public class ChooseTagNewActivity extends BaseActivity {
    public static int Request_UPDATE = 5;//跳转界面时候，request码，是否需要更新
    public static int RESULT_NEED = 6;
    public static int RESULT_NO_NEED = 7;
    public static int TAG_DELETE = 3;//删除状态
    public static int TAG_NODELETE = 4;//未删除状态
    public static int MYTAG = 1;//我的话题
    public static int LOCTAG = 2;//周边话题

    WrapLineLayout wrapLy_myTag, wrapLy_tag;

    private int hasNotGetNewTopic = 0;
    private int near_seek_progress = 50;

    private PullToRefreshScrollView pullToRefreshScrollView;
    private ImageButton ib_hide_myTag, ib_ok_delete, ib_create_tag, ib_search, ib_message;
    private TextView tv_delete_num, tv_delete_tag;
    private RelativeLayout rl_delete_tag, toolbar, rl_message;
    private Button tv_location, bt_show_message_num;
    private View view_mesh;
    private ImageView ib_title_head;
    private CarouselView carouselView;

    TopicsModel mytaglist;
    TopicsModel taglist;
    private List<TopicModel> selectedDeleteView = new ArrayList<>(), selectedMyTagDeleteView = new ArrayList<>();


    private int[] resId;
    private String[] name;
    private ArrayList<HashMap<String, Object>> itemList = new ArrayList<>();
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_tag_new);
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));
        findById();
        setOnClick();
        //得到定位信息

        double[] locations = getIntent().getDoubleArrayExtra(IntentKeyConfig.LOCATION);
        setLat(locations[1]);
        setLon(locations[0]);
        if (getLat() == 0) {
            showNoNetWorkDialog();
        } else {
            new GetDataTask().execute(true);
        }
        wrapLy_myTag.setIb_controll(ib_hide_myTag);
    }


    private void findById() {
        near_seek_progress = getNear_seek_progress();


        carouselView = (CarouselView) findViewById(R.id.view_activity);
        pullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.refresh_scrollview);
        toolbar = (RelativeLayout) findViewById(R.id.toolbar);
        wrapLy_myTag = (WrapLineLayout) findViewById(R.id.wrayly_myTag);
        wrapLy_tag = (WrapLineLayout) findViewById(R.id.wrayly_tag);
        ib_hide_myTag = (ImageButton) findViewById(R.id.ib_hide_mytag);
        rl_delete_tag = (RelativeLayout) findViewById(R.id.rl_delete_tag);
        rl_message = (RelativeLayout) findViewById(R.id.rl_message);
        ib_search = (ImageButton) findViewById(R.id.ib_search);
        ib_ok_delete = (ImageButton) findViewById(R.id.ib_ok);
        tv_delete_num = (TextView) findViewById(R.id.tv_delete_num);
        tv_location = (Button) findViewById(R.id.tv_tag_location);
        tv_location.setText((near_seek_progress == 0 ? 0.1f : String.format("%.1f", near_seek_progress * 0.3f)) + "km");
        ib_title_head = (ImageView) findViewById(R.id.title_head);
        ib_message = (ImageButton) findViewById(R.id.ib_message);
        view_mesh = (View) findViewById(R.id.view_mesh);
        ib_create_tag = (ImageButton) findViewById(R.id.ib_create_tag);
        bt_show_message_num = (Button) findViewById(R.id.bt_show_message_num);
        wrapLy_myTag.setIb_controll(ib_hide_myTag);

        wrapLy_myTag.setHide(true);
        if (mytaglist != null)
            initWrapLayout(mytaglist.getTopic(), wrapLy_myTag, MYTAG, false);
        ib_hide_myTag.setImageDrawable(getResources().getDrawable(R.drawable.expand_more));


        tv_delete_tag = (TextView) findViewById(R.id.tv_delete_tag);
        gridView = (GridView) findViewById(R.id.gv_category);

        resId = new int[]{R.drawable.icon_xiaoyuan, R.drawable.icon_meishi, R.drawable.icon_dongmanpng,
                R.drawable.icon_lvyou, R.drawable.icon_yundong, R.drawable.icon_youxi,
                R.drawable.icon_xuexi, R.drawable.icon_yingshi, R.drawable.icon_other};

        name = new String[]{"校园", "美食", "动漫",
                "旅行", "运动", "游戏",
                "学习", "影视", "其他"};

        for (int i = 0; i < resId.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("itemName", name[i]);
            map.put("itemImage", resId[i]);
            itemList.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, itemList, R.layout.item_gridlayout,
                new String[]{"itemName", "itemImage"},
                new int[]{R.id.categroy_text, R.id.category_icon});


        gridView.setAdapter(adapter);
        gridView.setVerticalScrollBarEnabled(false);
        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return motionEvent.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
        gridView.setFocusable(false);
//        setListViewHeightBasedOnChildren(gridView);
    }

    private static void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 3;
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 设置高度
        params.height = totalHeight + 100;
        // 设置参数
        listView.setLayoutParams(params);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onResume() {
        UserModel userModel = BaseApplication.getInstance().getUserModel();
        if (userModel != null) {
            AssetImageUtils.loadUserHead(ChooseTagNewActivity.this, userModel.getUserIcon(), ib_title_head);
            LogUtil.i("用户" + JSONObject.toJSONString(userModel));
        } else {
            ib_title_head.setImageDrawable(getResources().getDrawable(R.drawable.default_head));
            wrapLy_myTag.setVisibility(View.GONE);
            ib_hide_myTag.setVisibility(View.GONE);
        }
        new CheckNewsNumTask().execute();
        super.onResume();
    }

    public int getNear_seek_progress() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        int progress = sharedPreferences.getInt("near_seek_progress", 50);
        return progress;
    }

    //将用户设置的周围话题的距离缓存到本地
    public void setNear_seek_progress(int near_seek_progress) {
        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
        editor.putInt("near_seek_progress", near_seek_progress);
        editor.commit();
    }

    //更新标签界面
    private void refreshTagView() {
        if (!isGpsOpen(this)) {
            showNoGpsWorkDialog();
        } else {
            startGpsByLow(100, true, aMapLocationListener);
        }
    }

    //定位成功后的回调
    private AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    setLat(aMapLocation.getLatitude());//获取纬度
                    setLon(aMapLocation.getLongitude());//获取经度
                    new GetDataTask().execute(true);
                } else if (aMapLocation.getErrorCode() == 12) {
                    if (pullToRefreshScrollView.isRefreshing())
                        pullToRefreshScrollView.onRefreshComplete();
                    showNoNetWorkDialog();
                } else {
                    if (pullToRefreshScrollView.isRefreshing())
                        pullToRefreshScrollView.onRefreshComplete();
                    showToast(getString(R.string.location_fail_no_network));
                }
            }
        }
    };

    //取消删除话题界面
    private void hideDeleteTag() {
        if (rl_delete_tag.getVisibility() == View.VISIBLE) {
            rl_delete_tag.setVisibility(View.GONE);
            //改变所有删除话题时选中的话题背景和状态
            if (!selectedDeleteView.isEmpty() || !selectedMyTagDeleteView.isEmpty()) {
                //遍历周边话题删除列表
//                for (TopicModel topicModel : selectedDeleteView) {
//                    //得到话题在WrapLayout中的位置
//                    View tagView = wrapLy_tag.getChildAt(taglist.getTopic().indexOf(topicModel));
//                    //设置背景
//                    tagView.setBackgroundDrawable(getResources().getDrawable(R.drawable.tag_bg_normal));
//                    //设置背景后都要重新设置padding
//                    reSetTagPadding(tagView);
//                    //改变话题状态TAG_DELETE->TAG_NODELETE
//                    tagView.setTag(TAG_NODELETE);
//                }
                //遍历我的话题删除列表
                for (TopicModel topicModel : selectedMyTagDeleteView) {
                    View tagView = wrapLy_myTag.getChildAt(mytaglist.getTopic().indexOf(topicModel));
                    tagView.setBackgroundDrawable(getResources().getDrawable(R.drawable.mytag_item_bg));
                    reSetTagPadding(tagView);
                    tagView.setTag(TAG_NODELETE);
                }
                //清空列表
                selectedDeleteView.clear();
                selectedMyTagDeleteView.clear();
            }

        }
    }

    //删除话题
    private void delete_tag() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid", BaseApplication.getInstance().getUserId());
        String topics = "";
        if (rl_delete_tag.getVisibility() == View.VISIBLE) {
            if (!selectedMyTagDeleteView.isEmpty() || !selectedDeleteView.isEmpty()) {
                //遍历我的话题删除列表
                for (TopicModel topicModel : selectedDeleteView) {
                    //我的话题列表中删除此话题
                    taglist.getTopic().remove(topicModel);
                    //添加此话题的id
                    if (topics.isEmpty()) topics += topicModel.getId();
                    else topics += ("," + topicModel.getId());
                }
                //遍历周边话题删除列表
                for (TopicModel topicModel : selectedMyTagDeleteView) {
                    //周边话题列表中删除此话题
                    mytaglist.getTopic().remove(topicModel);
                    //添加此话题的id
                    if (topics.isEmpty()) topics += topicModel.getId();
                    else topics += ("," + topicModel.getId());
                }
                //清空我的话题删除列表和周边话题删除列表
                selectedDeleteView.clear();
                selectedMyTagDeleteView.clear();
                //更新话题界面
//                initWrapLayout(taglist.getTopic(), wrapLy_tag, LOCTAG);
                initWrapLayout(mytaglist.getTopic(), wrapLy_myTag, MYTAG);

            }
            //退出删除界面
            rl_delete_tag.setVisibility(View.GONE);
            //请求后台处理删除动作
            jsonObject.put("topics", topics);
            /**
             * 请求：{"userid":"025C6570E9AF4015B2995E0787AE71B3","topics":"3,1"}
             * userid	String	用户ID	eg:025C6570E9AF4015B2995E0787AE71B3
             * topics	String	话题ID	eg:3,1（逗号隔开）
             *
             * 返回：数据库中相应字段变化，getusertopic，getnearbytopic更新
             */
            new HttpManager<Object>().sendQuest(Request.Method.POST, HttpUrlConfig.disliketopic, jsonObject, Object.class, new HttpRequestCallBack<Object>() {
                @Override
                public void onRequestSuccess(Object response, boolean cached) {

                }

                @Override
                public void onRequestFailed(VolleyError error) {

                }
            });
        }
    }

    private void setOnClick() {

//        ImageView iv_activity = (ImageView) findViewById(R.id.iv_activity);
//        iv_activity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ChooseTagNewActivity.this,CategoryTopicsActivity.class);
//                intent.putExtra("category",100);//-1代表活动
//                intent.putExtra("categoryName" , "活动");
//                startActivity(intent);
//            }
//        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChooseTagNewActivity.this, CategoryTopicsActivity.class);
                int category = position + 1;
                if (category == 9) {
                    category = 0;
                }
                intent.putExtra("category", category);
                intent.putExtra("categoryName", name[position]);
                startActivity(intent);
            }
        });
        //删除话题
        tv_delete_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_tag();
            }
        });

        ib_hide_myTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wrapLy_myTag.isHide()) {
                    wrapLy_myTag.setHide(false);
                    if (mytaglist != null)
                        initWrapLayout(mytaglist.getTopic(), wrapLy_myTag, MYTAG, false);
                    ib_hide_myTag.setImageDrawable(getResources().getDrawable(R.drawable.expand_less));
                } else {
                    wrapLy_myTag.setHide(true);
                    if (mytaglist != null)
                        initWrapLayout(mytaglist.getTopic(), wrapLy_myTag, MYTAG, false);
                    ib_hide_myTag.setImageDrawable(getResources().getDrawable(R.drawable.expand_more));
                }
            }
        });
        ib_ok_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDeleteTag();
            }
        });

        tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_mesh.setVisibility(View.VISIBLE);
                showChooseTagLocationPop();

            }
        });

        ib_create_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)) {
                    showNoLogInTipDialog();
                    return;
                }
                startActivityForResult(new Intent(ChooseTagNewActivity.this, CreateTagActivity.class), Request_UPDATE);

            }
        });
        ib_title_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ChooseTagNewActivity.this, UserManagerActivity.class), Request_UPDATE);
            }
        });
        ib_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseTagNewActivity.this, SearchTagActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(IntentKeyConfig.MYTAG, mytaglist);
                bundle.putSerializable(IntentKeyConfig.TAG, taglist);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                TimerUtils.startTime(ChooseTagNewActivity.this, 4, new TimerUtils.OnTimerListenr() {
                    @Override
                    public void TimeOver() {
                        if (pullToRefreshScrollView.isRefreshing()) {
                            showToast(getString(R.string.refresh_no_network));
                            pullToRefreshScrollView.onRefreshComplete();
                        }
                    }

                    @Override
                    public boolean TimeIng(Handler handler, Runnable runnable, int time) {
                        if (!pullToRefreshScrollView.isRefreshing()) {
                            handler.removeCallbacks(runnable);
                            return false;
                        }
                        return true;
                    }
                });
                refreshTagView();
            }
        });
        ib_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)) {
                    showNoLogInTipDialog();
                    return;
                }
                if (bt_show_message_num.getVisibility() == View.VISIBLE) {
                    bt_show_message_num.setVisibility(View.GONE);
                }
                ib_message.setImageDrawable(getResources().getDrawable(R.drawable.message));
                startActivity(new Intent(ChooseTagNewActivity.this, AboutMeMessageActivity.class));
            }
        });

    }

    /**
     * 用于检测用户是否有新消息
     * <p>
     * 示例：{“userID”: “866328023315987”}
     * userID	string	用户ID	eg：866328023315987
     * <p>
     * 返回：该用户的所有消息
     * {
     * "allMsgsNum": 33
     * }
     */
    private class CheckNewsNumTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            if (!BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)) {
                JSONObject getMsgNumParams = new JSONObject();
                getMsgNumParams.put("userID", BaseApplication.getInstance().getUserId());
                getMsgNumParams.put("latestReadTime", BaseApplication.getInstance().getLatestReadTime());
                //发送请求
                new HttpManager<NewMsgsNumModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getNewMsgNum, getMsgNumParams, NewMsgsNumModel.class, new HttpRequestCallBack<NewMsgsNumModel>() {

                    @Override
                    //请求成功
                    public void onRequestSuccess(NewMsgsNumModel response, boolean cached) {

                        if (response.getNewMsgsNum() == 0) {//有新信息
                            bt_show_message_num.setVisibility(View.GONE);
                            ib_message.setImageDrawable(getResources().getDrawable(R.drawable.message));
                        } else {//无新信息
                            bt_show_message_num.setVisibility(View.VISIBLE);
                            bt_show_message_num.setText(String.valueOf(response.getNewMsgsNum()));
                            ib_message.setImageDrawable(getResources().getDrawable(R.drawable.message_no_read));
                        }
                    }

                    @Override
                    public void onRequestFailed(VolleyError error) {

                    }
                });
            }
            return null;
        }
    }

    /**
     * 获取话题
     */
    private class GetDataTask extends AsyncTask<Boolean, Void, List<TopicModel>[]> {

        @Override
        protected List<TopicModel>[] doInBackground(final Boolean... isAnimation) {
            JSONObject jsonObject = new JSONObject();
            UserModel userModel = BaseApplication.getInstance().getUserModel();
            if (userModel != null) {
                jsonObject.put("userid", userModel.getUserID());

                //请求获取我的话题列表
                new HttpManager<TopicsModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getUserTopics, jsonObject, TopicsModel.class, new HttpRequestCallBack<TopicsModel>() {

                    @Override
                    //请求成功
                    public void onRequestSuccess(TopicsModel response, boolean cached) {
                        List<TopicModel> topicModels = new ArrayList<TopicModel>();
                        mytaglist = response;
                        //将得到的话题列表倒置
                        Collections.reverse(response.getTopic());

                        for (TopicModel topicModel : response.getTopic()) {
                            if (!topicModels.contains(topicModel)) {
                                topicModels.add(topicModel);
                            }
                        }
                        //将获取到的话题列表添加至我的话题列表
                        mytaglist.setTopic(topicModels);
                        if (wrapLy_myTag.getVisibility() == View.GONE)
                            wrapLy_myTag.setVisibility(View.VISIBLE);

                        initWrapLayout(mytaglist.getTopic(), wrapLy_myTag, MYTAG, isAnimation[0]);
                        LogUtil.i(JSON.toJSONString(response));
                        //？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
                        if (mytaglist.getTopic().size() > 6 && !wrapLy_myTag.isHide()) {
                            ib_hide_myTag.performClick();
                        }
                    }

                    @Override
                    public void onRequestFailed(VolleyError error) {

                    }
                });
            } else {//？？？？？？？？？？？？？？？？？？？？？？？？
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ib_hide_myTag.setVisibility(View.GONE);
                    }
                });
            }

            JSONObject loctionParams = new JSONObject();
            loctionParams.put("lon", getLon());
            loctionParams.put("lat", getLat());
            loctionParams.put("userid", BaseApplication.getInstance().getUserId());
            loctionParams.put("radius", near_seek_progress * 300);

            //请求获取周围话题列表
            new HttpManager<TopicsModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getNearbyTopics, loctionParams, TopicsModel.class, new HttpRequestCallBack<TopicsModel>() {
                @Override
                //请求成功
                public void onRequestSuccess(TopicsModel response, boolean cached) {
                    List<TopicModel> topicModels = new ArrayList<TopicModel>();
                    taglist = response;

                    for (TopicModel topicModel : response.getTopic()) {
                        double distance = DistanceUtils.GetDistance(topicModel.getLon(), topicModel.getLat(), getLon(), getLat());
                        //获取到距离范囲内的话题列表
                        if (distance > near_seek_progress * 300 || distance > topicModel.getRadius()) {
                            continue;
                        }
                        if (!topicModels.contains(topicModel)) {
                            topicModels.add(topicModel);
                        }
                    }
                    if (taglist != null) {
                        //？？？？？？？？？？？？？？？？？？？？？？？？
                        if (taglist.getTopic().size() == topicModels.size()) {
                            hasNotGetNewTopic++;
                            if (hasNotGetNewTopic == 3) {
                                showToast("您没有获取到新话题，可以考虑扩大范围");
                            }
                        }
                    }
                    taglist.setTopic(topicModels);
//                    initWrapLayout(taglist.getTopic(), wrapLy_tag, LOCTAG, isAnimation[0]);
                    pullToRefreshScrollView.onRefreshComplete();
                }

                @Override
                public void onRequestFailed(VolleyError error) {

                }
            });

            //请求活动列表
            new HttpManager<ActivityModel>().sendQuestArray(Request.Method.POST, HttpUrlConfig.getviewpagers, new JSONObject(), ActivityModel.class, new HttpRequestCallBack<List<ActivityModel>>() {
                @Override
                public void onRequestSuccess(final List<ActivityModel> response, boolean cached) {
                    LogUtil.i("list size:"+response.size());
                    if (response.size() <= 0) return;
                    if (carouselView.getVisibility() != View.VISIBLE) carouselView.setVisibility(View.VISIBLE);
                    carouselView.setImageListener(new ImageListener() {
                        @Override
                        public void setImageForPosition(int position, ImageView imageView) {
                            final ActivityModel model = response.get(position);
                            Glide.with(ChooseTagNewActivity.this).load(model.getImageurl())
                                    .centerCrop()
                                    .crossFade()
                                    .into(imageView);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ChooseTagNewActivity.this, CategoryTopicsActivity.class);
                                    intent.putExtra("category", model.getType());
                                    intent.putExtra("categoryName", model.getTitle());
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                    carouselView.setPageCount(response.size());
                }

                @Override
                public void onRequestFailed(VolleyError error) {

                }
            });
            return null;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Request_UPDATE) {
            if (resultCode == RESULT_NEED) {
                new GetDataTask().execute(false);
                LogUtil.i("更新一次");
            }
        }
    }

    boolean hasChangeLocation = false;

    private void showChooseTagLocationPop() {
        hasChangeLocation = false;
        View mainView = View.inflate(this, R.layout.pop_tag_location, null);
        final PopupWindow tagLocationPop = new PopupWindow(mainView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        tagLocationPop.setOutsideTouchable(true);
        tagLocationPop.setBackgroundDrawable(new ColorDrawable());
        tagLocationPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_mesh.setVisibility(View.GONE);
                if (hasChangeLocation) {
                    pullToRefreshScrollView.setRefreshing(true);
                    setNear_seek_progress(near_seek_progress);
                }
            }
        });
        tagLocationPop.setAnimationStyle(R.style.PoPupWindowAnim);
        final TextView tv_radius_near = (TextView) mainView.findViewById(R.id.tv_near_radius);
        AppCompatSeekBar radius_seekbar = (AppCompatSeekBar) mainView.findViewById(R.id.seekbar_near);
        radius_seekbar.setProgress(near_seek_progress);
        tv_radius_near.setText(tv_location.getText());
        radius_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float km = (float) 3 * i / 10;
                near_seek_progress = i;
                if (i == 0) {
                    km = 0.1f;
                }
                tv_radius_near.setText(km + "km");
                tv_location.setText(km + "km");

                hasChangeLocation = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tagLocationPop.showAsDropDown(toolbar);
    }

    private void initWrapLayout(final List<TopicModel> labelModels, WrapLineLayout wrapLineLayout, final int tagType) {
        initWrapLayout(labelModels, wrapLineLayout, tagType, true);
    }

    //因为layer_list的bug，重新设置background时候标签的背景的padding会消失，因此在每次变化话题背景时候要重新设置padding
    private void reSetTagPadding(View tagView) {
        tagView.setPadding(getResources().getDimensionPixelSize(R.dimen.px_twenty_eight), getResources().getDimensionPixelSize(R.dimen.px_sixteen),
                getResources().getDimensionPixelSize(R.dimen.px_twenty_eight), getResources().getDimensionPixelSize(R.dimen.px_sixteen));
    }

    //初始化标签列表
    private void initWrapLayout(final List<TopicModel> labelModels, WrapLineLayout wrapLineLayout, final int tagType, boolean isAnimation) {
        //移除所有话题
        wrapLineLayout.removeAllViews();
        //设置布局参数
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (final TopicModel topicModel : labelModels) {
            final View tagView;

            if (tagType == LOCTAG) {//周边话题
                if (topicModel.isread()) {//话题已读
                    tagView = View.inflate(ChooseTagNewActivity.this, R.layout.tag_pressed_layout, null);
                } else {//话题未读
                    tagView = View.inflate(ChooseTagNewActivity.this, R.layout.tag_layout, null);
                }
            } else {//我的话题
                tagView = View.inflate(ChooseTagNewActivity.this, R.layout.mytag_layout, null);
            }
            tagView.setTag(TAG_NODELETE);

            //获取tag中的textview
            final TextView tv_tag = (TextView) tagView.findViewById(R.id.bt_tag);
            tv_tag.setText(topicModel.getTitle());

            tagView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rl_delete_tag.getVisibility() == View.GONE) {//正常的话题
                        //设置话题已读
                        //向后台说明此话题已读
                        updateTopic(topicModel.getId());
                        //点击时改变textview背景
                        if (tagType == LOCTAG) {
                            tagView.setBackgroundDrawable(getResources().getDrawable(R.drawable.tag_bg_pressed));
                            reSetTagPadding(tagView);
                        }
                        //存取数据，跳转
                        Intent intent = new Intent(ChooseTagNewActivity.this, Talk2Activity.class);
                        //话题名称
                        intent.putExtra(IntentKeyConfig.Tag_Name, tv_tag.getText());
                        //话题id
                        intent.putExtra(IntentKeyConfig.TOPICID, topicModel.getId());
                        //话题弹幕数量
                        intent.putExtra(IntentKeyConfig.DANMUNUM, topicModel.getDanmuNum());
                        //话题模型对象
                        intent.putExtra(IntentKeyConfig.TOPICMODEL, JSONObject.toJSONString(topicModel));
                        LogUtil.i("弹幕数量：" + topicModel.getDanmuNum());
                        startActivityForResult(intent, Request_UPDATE);
                    } else {
                        if (tagType == LOCTAG) {//长按话题时显示出来的界面，选择要删除的周边话题
                            if ((Integer) tagView.getTag() == TAG_NODELETE) {//话题已标记为删除状态时
                                //标记此话题未删除状态
                                tagView.setTag(TAG_DELETE);
                                //改变背景
                                tagView.setBackgroundDrawable(getResources().getDrawable(R.drawable.tag_bg_delete));
                                reSetTagPadding(tagView);
                                //话题删除列表中添加此话题
                                selectedDeleteView.add(topicModel);
                                int deleteNum = selectedDeleteView.size() + selectedMyTagDeleteView.size();
                                tv_delete_num.setText(deleteNum + "");
                            } else {//话题已被标记为删除状态时
                                //标记此话题为未删除状态
                                tagView.setTag(TAG_NODELETE);
                                //改变背景
                                tagView.setBackgroundDrawable(getResources().getDrawable(R.drawable.tag_bg_normal));
                                //话题删除列表中消除此话题
                                selectedDeleteView.remove(topicModel);
                                reSetTagPadding(tagView);
                                int deleteNum = selectedDeleteView.size() + selectedMyTagDeleteView.size();
                                tv_delete_num.setText(deleteNum + "");
                            }
                        } else {//长按话题时显示出来的界面，选择要删除的我的话题
                            if ((Integer) tagView.getTag() == TAG_NODELETE) {
                                tagView.setTag(TAG_DELETE);
                                tagView.setBackgroundDrawable(getResources().getDrawable(R.drawable.tag_bg_delete));
                                reSetTagPadding(tagView);
                                selectedMyTagDeleteView.add(topicModel);
                                int deleteNum = selectedDeleteView.size() + selectedMyTagDeleteView.size();
                                tv_delete_num.setText(deleteNum + "");
                            } else {
                                tagView.setTag(TAG_NODELETE);
                                selectedMyTagDeleteView.remove(topicModel);
                                tagView.setBackgroundDrawable(getResources().getDrawable(R.drawable.mytag_item_bg));
                                reSetTagPadding(tagView);
                                int deleteNum = selectedDeleteView.size() + selectedMyTagDeleteView.size();
                                tv_delete_num.setText(deleteNum + "");
                            }
                        }
                    }
                }
            });

            //话题长按事件
            tagView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (rl_delete_tag.getVisibility() == View.GONE) {//删除界面未显示的情况下才处理话题长按事件
                        rl_delete_tag.setVisibility(View.VISIBLE);
                        tagView.setTag(TAG_DELETE);
                        tagView.setBackgroundDrawable(getResources().getDrawable(R.drawable.tag_bg_delete));
                        reSetTagPadding(tagView);
                        if (tagType == LOCTAG) {
                            selectedDeleteView.add(topicModel);
                        } else {
                            selectedMyTagDeleteView.add(topicModel);
                        }
                        //删除数目＝　删除我的话题数目＋删除周边话题数目
                        int deleteNum = selectedDeleteView.size() + selectedMyTagDeleteView.size();
                        tv_delete_num.setText(deleteNum + "");
                    }
                    return true;
                }
            });
            wrapLineLayout.addView(tagView, params);

            //加载动画效果
            if (isAnimation) AnimationUtils.translateFromRightToLeft(tagView);
        }
    }


    //话题更新，向后台说明，这个话题已读
    private void updateTopic(String topicid) {
        if (BaseApplication.getInstance().getUserId() == null && BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER))
            return;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid", BaseApplication.getInstance().getUserId());
        jsonObject.put("topicid", topicid);
        new HttpManager<>().sendQuest(Request.Method.POST, HttpUrlConfig.updateusertopic, jsonObject, new Response.Listener() {
            @Override
            public void onResponse(Object response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (rl_delete_tag.getVisibility() == View.VISIBLE) {
            hideDeleteTag();
        } else {
            finish();
        }
    }

    //显示自定义对话框
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


    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isGpsOpen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }


    //显示自定义对话框
    private void showNoGpsWorkDialog() {
        final Dialog tipDialog = new Dialog(this, R.style.MyAlertDialog);
        //加载对话框布局
        View mainView = View.inflate(this, R.layout.dialog_tip_no_network, null);
        Button bt_cancel = (Button) mainView.findViewById(R.id.bt_cancel);
        Button bt_setting_network = (Button) mainView.findViewById(R.id.bt_setting_network);
        TextView tv_text = (TextView) mainView.findViewById(R.id.text);
        tv_text.setText("GPS未打开，请先打开GPS！");
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
                // 转到手机设置界面，用户设置GPS
                Intent intent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                tipDialog.dismiss();
                startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
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
}
