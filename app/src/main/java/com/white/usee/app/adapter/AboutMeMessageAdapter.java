package com.white.usee.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.model.NewMsgModel;
import com.white.usee.app.util.AssetImageUtils;
import com.white.usee.app.util.DateUtil;
import com.white.usee.app.util.Dp2Px;
import com.white.usee.app.util.emojicon.EmojiconTextView;

import java.util.List;

/**
 * Created by 10037 on 2016/7/14 0014.
 */

public class AboutMeMessageAdapter extends BaseAdapter {
    private List<NewMsgModel> newMsgModels;
    private Context mContext;

    public void setNewMsgModels(List<NewMsgModel> newMsgModels) {
        this.newMsgModels = newMsgModels;
    }

    public AboutMeMessageAdapter(List newMsgModels, Context mContext) {
        this.newMsgModels = newMsgModels;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return newMsgModels.size();
    }

    @Override
    public NewMsgModel getItem(int i) {
        return newMsgModels.get(i);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.list_message_item, null);
            initView(view, holder);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        initData(holder, i);
        return view;
    }


    private void initView(View view, ViewHolder holder) {
        holder.iv_head = (ImageView) view.findViewById(R.id.iv_head);
        holder.iv_sex = (ImageView) view.findViewById(R.id.iv_sex);
        holder.tv_name = (TextView) view.findViewById(R.id.tv_comment_name);
        holder.reply_type = (TextView) view.findViewById(R.id.tv_reply_type);
        holder.create_time = (TextView) view.findViewById(R.id.tv_time);
        holder.message = (EmojiconTextView) view.findViewById(R.id.tv_comment_content);
    }

    private void initData(ViewHolder holder, int i) {
        NewMsgModel newMsgModel = newMsgModels.get(i);
        Glide.clear(holder.iv_head);

        //加载头像
        AssetImageUtils.loadUserHead(mContext, newMsgModel.getUserIcon(), holder.iv_head, Dp2Px.px_24, Dp2Px.px_24);
        holder.iv_sex.setVisibility(View.VISIBLE);
        //加载性别
        if (newMsgModel.getGender() == 0) {
            holder.iv_sex.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sex_man));
        } else if (newMsgModel.getGender() == 1) {
            holder.iv_sex.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sex_woman));
        } else {
            holder.iv_sex.setVisibility(View.GONE);
        }

        if (newMsgModel.getSender().equals(BaseApplication.getInstance().getUserId())) {
            holder.tv_name.setText("我");
        } else {
            holder.tv_name.setText(newMsgModel.getNickname());
        }

        //显示回复类型
        if (newMsgModel.getType() == 1) {
            //评论
            holder.reply_type.setText(R.string.comment);
        } else if (newMsgModel.getType() == 2) {
            //回复
            holder.reply_type.setText(R.string.reply);
        } else if (newMsgModel.getType() == 3) {
            //悄悄回复
            holder.reply_type.setText(R.string.private_reply);
        } else {
            //悄悄评论
            holder.reply_type.setText(R.string.private_comment);
        }
        //显示时间

        holder.create_time.setText(DateUtil.showDanmuOrCommentDay(newMsgModel.getCreate_time()));


        //显示消息内容
        holder.message.setText(newMsgModel.getContent());
    }


    class ViewHolder {
        ImageView iv_sex;
        ImageView iv_head;
        TextView tv_name;
        TextView reply_type;
        TextView create_time;
        EmojiconTextView message;

    }

}
