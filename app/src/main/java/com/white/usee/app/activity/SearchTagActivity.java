package com.white.usee.app.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.config.IntentKeyConfig;
import com.white.usee.app.model.HotWordModel;
import com.white.usee.app.model.HotWordsModel;
import com.white.usee.app.model.TopicModel;
import com.white.usee.app.model.TopicsModel;
import com.white.usee.app.util.AnimationUtils;
import com.white.usee.app.util.DistanceUtils;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.ThemeUtils;
import com.white.usee.app.view.WrapLineLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchTagActivity extends BaseActivity {

    private ListView list_hot_search;
    private AutoCompleteTextView ed_search;
    private ImageButton ib_clean_text;
    private WrapLineLayout ly_search_result;
    private TextView tv_hot_search;
    private ArrayAdapter<String> hotArrayAdater;
    private List<TopicModel> sumTag = new ArrayList<>();
    TopicsModel mytaglist;
    TopicsModel taglist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tag);
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));
        findById();
        setOnClick();
        getHotTopicForUpdate(list_hot_search);
    }
    //因为layer_list的bug，重新设置background时候标签的背景的padding会消失，因此在每次变化话题背景时候要重新设置padding
    private void reSetTagPadding(View tagView) {
        tagView.setPadding(getResources().getDimensionPixelSize(R.dimen.px_twenty_eight), getResources().getDimensionPixelSize(R.dimen.px_sixteen),
                getResources().getDimensionPixelSize(R.dimen.px_twenty_eight), getResources().getDimensionPixelSize(R.dimen.px_sixteen));
    }
    private void findById() {
        list_hot_search = (ListView) findViewById(R.id.list_hot_search);
        ed_search = (AutoCompleteTextView) findViewById(R.id.et_search_tag);
        ed_search.setDropDownAnchor(R.id.line_search_edit);
        tv_hot_search = (TextView) findViewById(R.id.tv_hot_search);
        mytaglist = (TopicsModel) getIntent().getSerializableExtra(IntentKeyConfig.MYTAG);
        taglist = (TopicsModel) getIntent().getSerializableExtra(IntentKeyConfig.TAG);
        sumTag = new ArrayList<>();
        if (mytaglist != null && mytaglist.getTopic().size() > 0) {
            for (TopicModel topicModel : mytaglist.getTopic()) {
                if (!sumTag.contains(topicModel)) sumTag.add(topicModel);
            }
        }
        if (taglist != null && taglist.getTopic().size() > 0) {
            for (TopicModel topicModel : taglist.getTopic()) {
                if (!sumTag.contains(topicModel)) sumTag.add(topicModel);
            }
        }
        ib_clean_text = (ImageButton) findViewById(R.id.ib_clean_text);
        ly_search_result = (WrapLineLayout) findViewById(R.id.ly_search_result);
    }

    //获取到热门话题
    private void getHotTopicForUpdate(final ListView list_hot_search) {
        new HttpManager<HotWordsModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getHotTopic, new JSONObject(), HotWordsModel.class, new HttpRequestCallBack<HotWordsModel>() {
            @Override
            public void onRequestSuccess(HotWordsModel response, boolean cached) {
                List<HotWordModel> hotWordModels = response.getHotwords();
                Collections.sort(hotWordModels, new Comparator<HotWordModel>() {
                    @Override
                    public int compare(HotWordModel hotWordModel, HotWordModel t1) {
                        if (hotWordModel.getWeight() < t1.getWeight())
                            return 1;
                        else return -1;
                    }
                });
                List<String> data = new ArrayList<String>();
                for (HotWordModel hotWordModel : hotWordModels) {
                    data.add(hotWordModel.getWord());
                }
                hotArrayAdater = new ArrayAdapter<String>(SearchTagActivity.this, R.layout.hot_search_list_item, R.id.tv_hot_search_item, data);
                list_hot_search.setAdapter(hotArrayAdater);
            }

            @Override
            public void onRequestFailed(VolleyError error) {

            }
        });
    }


    private void showNoResultPop(View anchorView) {
        View mainView = View.inflate(this, R.layout.autocomplete, null);
        PopupWindow popupWindow = new PopupWindow(mainView, anchorView.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setAnimationStyle(R.style.PoPupWindowAnim);
        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchTagActivity.this, CreateTagActivity.class));
            }
        });
        popupWindow.showAsDropDown(anchorView);
        anchorView.setFocusable(true);
        anchorView.setFocusableInTouchMode(true);
    }


    private void setOnClick() {
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ed_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    String context = ed_search.getText().toString();
                    tv_hot_search.setText("相关结果");
                    if (ly_search_result.getVisibility() == View.GONE) {
                        ly_search_result.setVisibility(View.VISIBLE);
                        list_hot_search.setVisibility(View.GONE);
                    }
                    ly_search_result.removeAllViews();
                    searchTopicForUpdate(context, view);
//                    filterData(context, ly_search_result, sumTag);

                    return true;
                }
                return false;
            }
        });
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String context = charSequence + "";
                if (context.isEmpty()) {
                    list_hot_search.setVisibility(View.VISIBLE);
                    ib_clean_text.setVisibility(View.GONE);
                    tv_hot_search.setText("热门搜索");
                    ly_search_result.setVisibility(View.GONE);
                } else {
                    ib_clean_text.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ib_clean_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ed_search.setText("");
            }
        });
        list_hot_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ed_search.setText(hotArrayAdater.getItem(position));
                String context = ed_search.getText().toString();
                tv_hot_search.setText("相关结果");
                ly_search_result.setVisibility(View.VISIBLE);
                list_hot_search.setVisibility(View.GONE);
                ly_search_result.removeAllViews();
                searchTopicForUpdate(context, ed_search);
            }
        });
    }


    private void searchTopicForUpdate(String keyWord, final View view) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("keyword", keyWord);
        new HttpManager<TopicsModel>().sendQuest(Request.Method.POST, HttpUrlConfig.searchtopic, jsonObject, TopicsModel.class, new HttpRequestCallBack<TopicsModel>() {
            @Override
            public void onRequestSuccess(TopicsModel response, boolean cached) {
                List<TopicModel> result = new ArrayList<TopicModel>();
                for (TopicModel topicModel : response.getTopic()) {
                    double distance = DistanceUtils.GetDistance(topicModel.getLon(), topicModel.getLat(), getLon(), getLat());
                    if (mytaglist != null && mytaglist.getTopic().contains(topicModel)) {
                        result.add(topicModel);
                    } else if (distance > 35000 || distance > topicModel.getRadius()) {
                        continue;
                    } else {
                        result.add(topicModel);
                    }
                }
                LogUtil.i(JSONArray.toJSONString(result));
                if (result.size() <= 0) {
                    showNoResultPop(view);
                } else {
                    initWrapLayout(result, ly_search_result);
                }

            }

            @Override
            public void onRequestFailed(VolleyError error) {

            }
        });
    }

    private void initWrapLayout(final List<TopicModel> labelModels, WrapLineLayout wrapLineLayout) {
        wrapLineLayout.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (final TopicModel topicModel : labelModels) {


            final int tagType;
            if (mytaglist != null && mytaglist.getTopic().contains(topicModel)) {
                tagType = 1;
            } else {
                tagType = 0;
            }
            final View tagView;
            if (tagType == 0) {
                tagView = View.inflate(SearchTagActivity.this, R.layout.tag_layout, null);

            } else {
                tagView = View.inflate(SearchTagActivity.this, R.layout.mytag_layout, null);

            }
            final TextView tv_tag = (TextView) tagView.findViewById(R.id.bt_tag);
            tv_tag.setText(topicModel.getTitle());
            tagView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tagType == 0){
                        tagView.setBackgroundDrawable(getResources().getDrawable(R.drawable.tag_bg_pressed));
                        reSetTagPadding(tagView);
                    }
                    updateTopic(topicModel.getId());
                    Intent intent = new Intent(SearchTagActivity.this, Talk2Activity.class);
                    intent.putExtra(IntentKeyConfig.Tag_Name, tv_tag.getText());
                    intent.putExtra(IntentKeyConfig.TOPICID, topicModel.getId());
                    intent.putExtra(IntentKeyConfig.DANMUNUM, topicModel.getDanmuNum());
                    intent.putExtra(IntentKeyConfig.TOPICMODEL, JSONObject.toJSONString(topicModel));
                    startActivity(intent);

                }
            });
            wrapLineLayout.addView(tagView, params);
            AnimationUtils.translateFromRightToLeft(tagView);
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
}
