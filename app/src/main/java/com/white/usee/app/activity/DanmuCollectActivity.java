package com.white.usee.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.adapter.DanmuCollectAdapter;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.config.IntentKeyConfig;
import com.white.usee.app.model.FavDanmuModel;
import com.white.usee.app.model.FavDanmusModel;
import com.white.usee.app.model.FavModel;
import com.white.usee.app.util.Dp2Px;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.ThemeUtils;

import java.util.List;

public class DanmuCollectActivity extends BaseActivity {
    private ListView danmuCollectListView;
    private List<FavDanmuModel> messages;
    private DanmuCollectAdapter danmuCollectAdapter;
    private TextView tv_no_collect;
    private long lasteIntoTime;//最后一次进入该界面的时间


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danmu_collect);
        ThemeUtils.setColor(this,getResources().getColor(R.color.title));
        findById();
        setOnClick();

    }



    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    public long getLasteIntoTime() {
        SharedPreferences sharedPreferences = getSharedPreferences("favDanmu",MODE_PRIVATE);
        lasteIntoTime = sharedPreferences.getLong("favDanmu",0);
        return lasteIntoTime;
    }

    public void setLasteIntoTime(long lasteIntoTime) {
        SharedPreferences.Editor editor = getSharedPreferences("favDanmu",MODE_PRIVATE).edit();
        editor.putLong("favDanmu",lasteIntoTime);
        editor.commit();
    }

    private void findById(){
        danmuCollectListView = (ListView)findViewById(R.id.list_danmu_collect);
        tv_no_collect = (TextView)findViewById(R.id.tv_no_collect);
    }

    private void initData(){
        if (BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)){
            showNoLogInTipDialog();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid",BaseApplication.getInstance().getUserId());
        jsonObject.put("time",getLasteIntoTime());
        new HttpManager<FavDanmusModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getFavdanmuList, jsonObject, FavDanmusModel.class, new HttpRequestCallBack<FavDanmusModel>() {
            @Override
            public void onRequestSuccess(FavDanmusModel response, boolean cached) {
                setLasteIntoTime(System.currentTimeMillis());
                LogUtil.i("用户收藏的弹幕"+JSONObject.toJSONString(response));
                messages = response.getFavdanmu();
                if (messages!=null){
                    if (danmuCollectAdapter ==null) {
                        danmuCollectAdapter = new DanmuCollectAdapter(messages,DanmuCollectActivity.this);
                        danmuCollectListView.setAdapter(danmuCollectAdapter);
                    }
                    else{
                        danmuCollectAdapter.setMessageList(messages);
                        danmuCollectAdapter.notifyDataSetChanged();
                    }
                   if (messages.size()==0) tv_no_collect.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onRequestFailed(VolleyError error) {
                    showToast(getString(R.string.action_failed));
            }
        });
    }

    private void setOnClick(){
        (findViewById(R.id.title_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        danmuCollectListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showPopTagNoRead(view,danmuCollectAdapter.getItem(i));
                return true;
            }
        });
        danmuCollectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FavDanmuModel favDanmuModel = messages.get(i);
                Intent intent = new Intent(DanmuCollectActivity.this, DanmuDetailActivity.class);
                intent.putExtra(DanmuDetailActivity.DANMAKU_CONTENT, favDanmuModel.getMessages()+ "");
                intent.putExtra(DanmuDetailActivity.DANMAKU_ID, String.valueOf(favDanmuModel.getDanmuID()));
                intent.putExtra(IntentKeyConfig.USERID, favDanmuModel.getUserID());
                intent.putExtra(IntentKeyConfig.Tag_Name,favDanmuModel.getTopicTitle());
                intent.putExtra(IntentKeyConfig.LOCATION, new double[]{getLon(), getLat()});
                intent.putExtra(IntentKeyConfig.USERICON,"0.png");
                startActivity(intent);
            }
        });
    }

    //显示标记未读的消息框
    private void showPopTagNoRead(View anchorView, final FavDanmuModel favDanmuModel){
        View mainView = View.inflate(this,R.layout.pop_tag_no_read,null);
        Button bt_cancel_collect = (Button)mainView.findViewById(R.id.bt_cancel_collect);

        final View view_mesh = (View)findViewById(R.id.view_mesh);
        view_mesh.setVisibility(View.VISIBLE);
        final PopupWindow popupWindow = new PopupWindow(mainView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_mesh.setVisibility(View.GONE);
            }
        });
        bt_cancel_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelfavDanmu(favDanmuModel,popupWindow);
//                initData();
            }
        });
        popupWindow.showAsDropDown(anchorView, Dp2Px.dip2px(64),-(anchorView.getHeight()+Dp2Px.dip2px(98))/2);
    }

    private void cancelfavDanmu(final FavDanmuModel favDanmuModel, final PopupWindow popupWindow){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid",BaseApplication.getInstance().getUserId());
        jsonObject.put("danmuid",favDanmuModel.getDanmuID());
        jsonObject.put("isfav",true);
        LogUtil.i("取消收藏接口"+jsonObject.toJSONString());
        new HttpManager<FavModel>().sendQuest(Request.Method.POST, HttpUrlConfig.favdanmu, jsonObject, FavModel.class, new HttpRequestCallBack<FavModel>() {
            @Override
            public void onRequestSuccess(FavModel response, boolean cached) {
                LogUtil.i("收藏接口"+JSONObject.toJSONString(response));
                if (response.isStatus()){
                    if (false){
                        showToast("收藏成功");
                    }else {
                        initData();
//                        messages.remove(favDanmuModel);
//                        danmuCollectAdapter.notifyDataSetChanged();
                        showToast("取消收藏");
                    }

                }else {
                    showToast("收藏操作失败");
                }
                popupWindow.dismiss();
            }

            @Override
            public void onRequestFailed(VolleyError error) {
                showToast("操作失败，请检查网络");
            }
        });
    }

}
