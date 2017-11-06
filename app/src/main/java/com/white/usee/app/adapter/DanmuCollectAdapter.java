package com.white.usee.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.white.usee.app.R;
import com.white.usee.app.model.FavDanmuModel;
import com.white.usee.app.util.AssetImageUtils;
import com.white.usee.app.util.DateUtil;
import com.white.usee.app.util.Dp2Px;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.emojicon.EmojiconTextView;

import java.util.List;

/**
 * Created by 10037 on 2016/7/14 0014.
 */

public class DanmuCollectAdapter extends BaseAdapter {
    private List<FavDanmuModel> messageList;
    private Context mContext;

    public DanmuCollectAdapter(List messageList,Context mContext){
        this.messageList = messageList;
        this.mContext =mContext;
    }
    @Override
    public int getCount() {
        return messageList == null?0:messageList.size();
    }

    @Override
    public FavDanmuModel getItem(int i) {
        return i<getCount()?messageList.get(i):null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.list_danmu_collect_item, null);
            initView(view, holder);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        initData(holder, i);
        return view;
    }


    public void setMessageList(List<FavDanmuModel> messageList) {
        this.messageList = messageList;
    }

    private void initView(View view, ViewHolder holder) {
        holder.iv_head = (ImageView)view.findViewById(R.id.iv_head);
        holder.iv_sex  = (ImageView)view.findViewById(R.id.iv_sex);
        holder.iv_warn = (ImageView)view.findViewById(R.id.iv_warn);
        holder.tv_time = (TextView)view.findViewById(R.id.tv_time);
        holder.tv_comment_content = (EmojiconTextView) view.findViewById(R.id.tv_comment_content);
        holder.tv_comment_name = (TextView)view.findViewById(R.id.tv_comment_name);
        holder.tv_topicName = (TextView)view.findViewById(R.id.tv_topicName);

    }
    private void initData(ViewHolder holder, int i) {
        FavDanmuModel favDanmuModel = messageList.get(i);
        Glide.clear(holder.iv_head);//Glide在listview中异步加载时候可能会加载错位，所以每次加载时先clear掉原本的view
        AssetImageUtils.loadUserHead(mContext,favDanmuModel.getUserIcon(),holder.iv_head, Dp2Px.px_24,Dp2Px.px_24);
        holder.iv_sex.setVisibility(View.VISIBLE);
        if (favDanmuModel.getGender()==0){
            holder.iv_sex.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sex_man));
        }else if (favDanmuModel.getGender()==1){
            holder.iv_sex.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sex_woman));
        }else {
            holder.iv_sex.setVisibility(View.GONE);
        }
        if (favDanmuModel.isHavecomment()) holder.iv_warn.setVisibility(View.VISIBLE);
        else holder.iv_warn.setVisibility(View.GONE);
        holder.tv_time.setText(DateUtil.showDanmuOrCommentDay(favDanmuModel.getFav_time()));
        holder.tv_topicName.setText(favDanmuModel.getTopicTitle());
        holder.tv_comment_name.setText(favDanmuModel.getNickname());
        holder.tv_comment_content.setText(favDanmuModel.getMessages());
    }


    class ViewHolder{
        ImageView iv_head;
        ImageView iv_sex;
        ImageView iv_warn;
        TextView tv_comment_name;
        TextView tv_topicName;
        TextView tv_time;
        EmojiconTextView tv_comment_content;
    }
}
