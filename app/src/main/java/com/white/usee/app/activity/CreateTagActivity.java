package com.white.usee.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.IntegerCodec;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.white.usee.app.BaseActivity;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.config.IntentKeyConfig;
import com.white.usee.app.config.RequstConfig;
import com.white.usee.app.model.TopicModel;
import com.white.usee.app.model.TopicsModel;
import com.white.usee.app.util.DateUtil;
import com.white.usee.app.util.DistanceUtils;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.PhotoUtils;
import com.white.usee.app.util.QiniuUtils;
import com.white.usee.app.util.ThemeUtils;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import com.zhy.view.flowlayout.TagView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.Inflater;

public class CreateTagActivity extends BaseActivity {
    private ImageButton ib_back, ib_add_photo;
    private EditText et_tag_name, et_tag_description;
    private AppCompatSeekBar seekBar_tag_location;
    private TextView tv_tag_location, tv_rest, tv_create_tag;
    private float currenTagDistance = 15;
    private LinearLayout ly_photos;
    private ArrayList<String> photosPath = new ArrayList<>();

    private String[] categoryArr;
    private TagFlowLayout tfl_category;
    private HashSet<Integer> selectCategoryItems = new HashSet<>();
    private boolean isDefaultSelected = true;
    //配图是否是第一次点击
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tag);
        ThemeUtils.setColor(this, getResources().getColor(R.color.title));
        findById();
        setOnClick();

        initCateData();

    }

    private void initCateData() {
        categoryArr = getResources().getStringArray(R.array.item_category);
//        LogUtil.i("stringarr:" + categoryArr.toString());
        final LayoutInflater mInflater = LayoutInflater.from(this);
        tfl_category.setAdapter(new TagAdapter<String>(categoryArr) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.layout_tag_textview, null);

                if (position == 0) {
                    tv.setBackgroundResource(R.drawable.checked_bg);
                    tv.setTextColor(getResources().getColor(R.color.title));
                }
                tv.setText(s);
                return tv;
            }
        });
    }

    private void findById() {
        ib_back = (ImageButton) findViewById(R.id.title_back);
        ib_add_photo = (ImageButton) findViewById(R.id.ib_add_phote);
        seekBar_tag_location = (AppCompatSeekBar) findViewById(R.id.seekbar_tag_location);
        tv_tag_location = (TextView) findViewById(R.id.tv_tag_location);
        currenTagDistance = Integer.valueOf(tv_tag_location.getText().toString());
        et_tag_description = (EditText) findViewById(R.id.et_tag_description);
        et_tag_name = (EditText) findViewById(R.id.et_tag_name);
        tv_rest = (TextView) findViewById(R.id.tv_rest);
        tv_create_tag = (TextView) findViewById(R.id.tv_create_tag);
        ly_photos = (LinearLayout) findViewById(R.id.ly_photos);

        tfl_category = (TagFlowLayout) findViewById(R.id.tfl_tag_category);
        selectCategoryItems.add(0);

    }

    private void setOnClick() {

        tfl_category.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                TagView tagView = (TagView) parent.getChildAt(0);
                TextView tv = (TextView) tagView.getTagView();
                if (position == 0) {
                    if (!isDefaultSelected) {
                        tv.setBackgroundResource(R.drawable.normal_bg);
                        tv.setTextColor(getResources().getColor(R.color.default_text_color));
                        isDefaultSelected = false;
//                        Log.d("mytag", "onclicked1: " + selectItems.toString() + isDefault);
                    } else {
                        tv.setBackgroundResource(R.drawable.checked_bg);
                        tv.setTextColor(getResources().getColor(R.color.title));
                        isDefaultSelected = true;
//                        Log.d("mytag", "onclicked2: " + selectItems.toString() + isDefault);
                    }
                }
                return false;
            }
        });

        tfl_category.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                if (selectPosSet.contains(0)) {
                    isDefaultSelected = false;
                } else {
                    isDefaultSelected = true;
                }

                selectCategoryItems = (HashSet<Integer>) selectPosSet;
                if (isDefaultSelected) {
                    selectCategoryItems.add(0);
                } else {
                    selectPosSet.remove(0);
                }
//                Log.d("mytag",selectPosSet.toString());
            }
        });

        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        seekBar_tag_location.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                currenTagDistance = progress;
                if (progress == 0) currenTagDistance = 0.1f;
                tv_tag_location.setText(currenTagDistance + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tv_create_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(et_tag_name.getText().toString().trim())) {
                    showToast("话题名称不能为空，请重新输入");
                    return;
                }
                if (et_tag_name.getText().toString().length() < 2) {
                    showToast("话题名称过短，请重新输入");
                    return;
                }
                String dateString = BaseApplication.getInstance().getCreatTagDate();
                if (dateString != null) {
                    long disTime = DateUtil.RestDateSince(dateString);
                    if ((disTime / (1000 * 60)) < 1) {
                        showToast("话题创建间隔小于1分钟，请稍后创建");
                        return;
                    }
                }


                if (BaseApplication.getInstance().getUserId().equals(BaseApplication.NOUSER)) {
                    showToast("您还未登录，登录后才能执行此操作");
                    return;
                }
                checkIsSameNameTopic(et_tag_name.getText().toString(), et_tag_description.getText().toString(), (int) (currenTagDistance * 1000), BaseApplication.getInstance().getUserId());


            }
        });
        et_tag_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (tv_rest.getVisibility() == View.GONE) tv_rest.setVisibility(View.VISIBLE);
                int rest = 30 - charSequence.length();
                tv_rest.setText(rest + "");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        et_tag_description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    et_tag_description.setHint(R.string.create_tag_description_tip);
                    tv_rest.setVisibility(View.VISIBLE);
                } else {
                    et_tag_description.setHint(null);
                    tv_rest.setVisibility(View.GONE);
                }
            }
        });
        ib_add_photo.setOnClickListener(toPickPhotoListener);
    }


    //TODO:修改点击事件
    private View.OnClickListener toPickPhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //点击后显示选择对话框，选则图片来源
//            select();
            PhotoUtils.toPickPhoto(CreateTagActivity.this, photosPath, 3);

        }
    };

    private void select() {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog_select, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(linearLayout);
        dialog.show();


        TextView tv_dialog_camera = (TextView) linearLayout.findViewById(R.id.dialog_camera);
        TextView tv_dialog_picture = (TextView) linearLayout.findViewById(R.id.dialog_picture);
        TextView tv_dialog_cancel = (TextView) linearLayout.findViewById(R.id.dialog_cancel);

        //设置对话框中每个选项点击事件
        //相机
        tv_dialog_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirst) {
                    isFirst = false;
                    Intent intent = new Intent(CreateTagActivity.this, CameraActivity.class);
                    startActivityForResult(intent, RequstConfig.REQUST_DIALOG_CAMERA);
                }else{
                    Toast.makeText(CreateTagActivity.this, "", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        //图片
        tv_dialog_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirst = false;
                PhotoUtils.toPickPhoto(CreateTagActivity.this, photosPath, 3);
                dialog.dismiss();


            }
        });
        //取消
        tv_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    //TODO:修改返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {//图片选择后的结果

            // Get Image Path List
            photosPath = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            if (photosPath.size() >= 3) ib_add_photo.setVisibility(View.GONE);
            else ib_add_photo.setVisibility(View.VISIBLE);
            ly_photos.removeAllViews();
            for (String path : photosPath) {
                PhotoUtils.addPhotoToLinearLayout(this, ly_photos, path, toPickPhotoListener);
            }
        }
        if (requestCode == RequstConfig.REQUST_DIALOG_CAMERA && resultCode == RESULT_OK && data != null) {
            String path = data.getStringExtra("path");
            if (path != null) {
                Log.d("mytag", "onActivityResult: " + path);
                Bitmap bitmap = centerSquareScaleBitmap(getVideoThumbnail(path), 200);
                ib_add_photo.setImageBitmap(bitmap);
                ib_add_photo.setClickable(false);
            }
        }
    }

    //得到视频缩略图
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    //将bitmap剪切成正方形
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap || edgeLength <= 0) {
            return null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if (widthOrg > edgeLength && heightOrg > edgeLength) {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (int) (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;

            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } catch (Exception e) {
                return null;
            }

            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;

            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            } catch (Exception e) {
                return null;
            }
        }

        return result;
    }


    /**
     * 创建一个话题
     */
    private void createOneTag(final String title, String description, int radius, String userid, List<String> photoPath, String categoryStr) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title);
        jsonObject.put("description", description);
        jsonObject.put("radius", radius);
        jsonObject.put("lon", getLon());
        jsonObject.put("lat", getLat());
//        Log.d("mytag", "createOneTag: " + categoryStr);
        jsonObject.put("type", categoryStr);
        jsonObject.put("userid", userid);
        LogUtil.i(jsonObject.toJSONString());
        if (photoPath == null) photoPath = new ArrayList<>();
//        String categoryString = "";
//        Iterator<Integer> it = selectCategoryItems.iterator();
//        while(it.hasNext()){
//            categoryString += String.valueOf(it.next()) +",";
//        }
//        Log.d("mytag", "onSelected: " + categoryString.substring(0,categoryString.length()-1));


        jsonObject.put("imgurls", photoPath);
        new HttpManager<TopicModel>().sendQuest(Request.Method.POST, HttpUrlConfig.createTopic, jsonObject, TopicModel.class, new HttpRequestCallBack<TopicModel>() {
            @Override
            public void onRequestSuccess(TopicModel topicModel, boolean cached) {
                showToast("创建成功");
                Intent intent = new Intent(CreateTagActivity.this, Talk2Activity.class);
                intent.putExtra(IntentKeyConfig.Tag_Name, topicModel.getTitle());
                intent.putExtra(IntentKeyConfig.TOPICID, topicModel.getId());
                intent.putExtra(IntentKeyConfig.DANMUNUM, topicModel.getDanmuNum());
                intent.putExtra(IntentKeyConfig.TOPICMODEL, JSONObject.toJSONString(topicModel));
                CreateTagActivity.this.setResult(ChooseTagNewActivity.RESULT_NEED);
                startActivityForResult(intent, ChooseTagNewActivity.Request_UPDATE);
                try {
                    Date date = new Date();
                    BaseApplication.getInstance().setCreatTagDate(DateUtil.Date2String(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dismissLoadingDialog();
                finish();
            }

            @Override
            public void onRequestFailed(VolleyError error) {
                showToast("创建失败");
                dismissLoadingDialog();
            }
        });

    }

    /**
     * 检测是否有相同名称的话题
     */
    private void checkIsSameNameTopic(final String title, final String description, final int radius, final String userid) {
        JSONObject loctionParams = new JSONObject();
        loctionParams.put("lon", getLon());
        loctionParams.put("lat", getLat());
        loctionParams.put("userid", BaseApplication.getInstance().getUserId());
        loctionParams.put("radius", 3000);
        new HttpManager<TopicsModel>().sendQuest(Request.Method.POST, HttpUrlConfig.getNearbyTopics, loctionParams, TopicsModel.class, new HttpRequestCallBack<TopicsModel>() {
            @Override
            public void onRequestSuccess(TopicsModel response, boolean cached) {
                boolean hasSame = false;
                List<TopicModel> topicModels = response.getTopic();
                for (TopicModel topicModel : topicModels) {
                    if (topicModel.getTitle().equals(title)) {
                        if (DistanceUtils.GetDistance(topicModel.getLon(), topicModel.getLat(), getLon(), getLat()) < (topicModel.getRadius() + radius)) {
                            hasSame = true;
                        }
                    }
                }
                if (!hasSame) {
                    showLoadingDialog(false);

                    String categoryString = "";
                    Iterator<Integer> it = selectCategoryItems.iterator();
                    while (it.hasNext()) {
                        categoryString += String.valueOf(it.next()) + ",";
                    }
                    final String categoryStr = categoryString.substring(0, categoryString.length() - 1);

                    if (photosPath.size() <= 0) {
                        createOneTag(title, description, radius, userid, null, categoryStr);
//                        jsonObject.put("type",categoryString);
                    } else {
                        QiniuUtils.uploadMutliFiles(photosPath, new QiniuUtils.UploadMutliListener() {
                            @Override
                            public void onUploadMutliSuccess(ArrayList<String> fileUrls) {
                                createOneTag(title, description, radius, userid, fileUrls, categoryStr);
                                LogUtil.i("图片上传成功" + fileUrls.toString());
                            }

                            @Override
                            public void onUploadMutliFail(Error error) {
                                LogUtil.e("图片上传失败" + error.getMessage());
                                dismissLoadingDialog();
                            }
                        });
                    }

                } else {
                    showToast("附近有重复话题，请更换话题名称");
                }
            }

            @Override
            public void onRequestFailed(VolleyError error) {

            }
        });
    }

}
