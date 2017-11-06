package com.white.usee.app.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.R;
import com.white.usee.app.activity.DanmuDetailActivity;
import com.white.usee.app.config.HttpUrlConfig;
import com.white.usee.app.model.DeleteModel;
import com.white.usee.app.model.UserCommentModel;
import com.white.usee.app.util.AssetImageUtils;
import com.white.usee.app.util.CopyManager;
import com.white.usee.app.util.DateUtil;
import com.white.usee.app.util.Dp2Px;
import com.white.usee.app.util.HttpManager;
import com.white.usee.app.util.HttpRequestCallBack;
import com.white.usee.app.util.LogUtil;
import com.white.usee.app.util.emojicon.EmojiconTextView;

import java.util.List;

/**
 * Created by 10037 on 2016/7/7 0007.
 */

public class CommentListViewAdapter extends BaseAdapter {
    private List<UserCommentModel> commentModels;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public CommentListViewAdapter(Context mContext, List<UserCommentModel> commentModels) {
        this.mContext = mContext;
        this.commentModels = commentModels;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return commentModels.size();
    }

    @Override
    public UserCommentModel getItem(int position) {
        return commentModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_comment, null);
            initView(view, holder);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        initData(view, holder, position);
        return view;
    }

    private void initView(View view, final ViewHolder holder) {
        holder.iv_head = (ImageView) view.findViewById(R.id.iv_head);
        holder.tv_comment_content = (EmojiconTextView) view.findViewById(R.id.tv_comment_content);
        holder.tv_comment_name = (TextView) view.findViewById(R.id.tv_comment_name);
        holder.tv_reply_name = (TextView) view.findViewById(R.id.tv_reply_name);
        holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
        holder.iv_sex = (ImageView) view.findViewById(R.id.iv_sex);
        holder.tv_comment_style = (TextView) view.findViewById(R.id.tv_comment_style);
    }

    public List<UserCommentModel> getCommentModels() {
        return commentModels;
    }

    public void setCommentModels(List<UserCommentModel> commentModels) {
        this.commentModels = commentModels;
    }

    private void initData(View view, final ViewHolder holder, final int position) {
        final UserCommentModel userCommentModel = commentModels.get(position);
        if (userCommentModel.getUser().getUserID().equals(BaseApplication.getInstance().getUserId())) {
            holder.tv_comment_name.setText("我");
        } else {
            holder.tv_comment_name.setText(userCommentModel.getUser().getNickname());
        }
        holder.iv_sex.setVisibility(View.VISIBLE);
        if (userCommentModel.getUser().getGender() == 0)
            holder.iv_sex.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sex_man));
        else if (userCommentModel.getUser().getGender() == 1)
            holder.iv_sex.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sex_woman));
        else if (userCommentModel.getUser().getGender() == 2)
            holder.iv_sex.setVisibility(View.GONE);
        Glide.clear(holder.iv_head);
        AssetImageUtils.loadUserHead(mContext, userCommentModel.getUser().getUserIcon(), holder.iv_head, Dp2Px.px_24, Dp2Px.px_24);
        holder.tv_comment_content.setText(userCommentModel.getComment().getContent());
        holder.tv_time.setText(DateUtil.showDanmuOrCommentDay(userCommentModel.getComment().getCreate_time()));
        if (userCommentModel.getComment().getType() == 1) {
            //评论
            holder.tv_comment_style.setText(R.string.comment);
            holder.tv_reply_name.setText("");
        } else if (userCommentModel.getComment().getType() == 2) {
            //回复
            holder.tv_comment_style.setText(R.string.comment);
            if (userCommentModel.getComment().getReceiver().equals(BaseApplication.getInstance().getUserId())) {
                holder.tv_reply_name.setText("我");
            } else
                holder.tv_reply_name.setText(userCommentModel.getReplycomment_name());
        } else if (userCommentModel.getComment().getType() == 3) {
            //悄悄回复
            holder.tv_comment_style.setText(R.string.private_reply);
            if (userCommentModel.getComment().getReceiver().equals(BaseApplication.getInstance().getUserId())) {
                holder.tv_reply_name.setText("我");
            } else
                holder.tv_reply_name.setText(userCommentModel.getReplycomment_name());
        } else {
            //悄悄评论
            holder.tv_comment_style.setText(R.string.private_comment);
            holder.tv_reply_name.setText("");
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null)
                    onItemClickListener.onClick(commentModels.get(position));
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopCopyAndReport(view, holder, userCommentModel);
                return true;
            }
        });
    }

    private void showPopCopyAndReport(final View anchorView, final ViewHolder holder, final UserCommentModel userCommentModel) {
        final View mainView = View.inflate(mContext, R.layout.pop_danmu_copy_report, null);
        anchorView.setBackgroundColor(mContext.getResources().getColor(R.color.view_mesh));

        final PopupWindow pop_copy_report = new PopupWindow(mainView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pop_copy_report.setOutsideTouchable(true);
        pop_copy_report.setBackgroundDrawable(new ColorDrawable());
        pop_copy_report.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                anchorView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.item_pressed));
            }
        });

        Button bt_report = (Button) mainView.findViewById(R.id.bt_report);
        Button bt_copy = (Button) mainView.findViewById(R.id.bt_copy);
        if (userCommentModel.getUser().getUserID().equals(BaseApplication.getInstance().getUserId())) {
            bt_report.setText("删除");
        }
        bt_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CopyManager.copy(holder.tv_comment_content.getText() + "", mContext);
                pop_copy_report.dismiss();
            }
        });
        bt_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userCommentModel.getUser().getUserID().equals(BaseApplication.getInstance().getUserId())) {
                    deleteComment(userCommentModel.getComment().getId(), userCommentModel);
                } else {
                    ((DanmuDetailActivity) mContext).showPopReport(userCommentModel);
                }
                pop_copy_report.dismiss();
            }
        });

        pop_copy_report.showAsDropDown(anchorView, anchorView.getWidth() / 2 - Dp2Px.dip2px(66), -anchorView.getHeight() / 2 - Dp2Px.dip2px(16));

    }

    //从后台删除评论
    private void deleteComment(int commentId, final UserCommentModel usercom) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", BaseApplication.getInstance().getUserId());
        jsonObject.put("commentID", commentId);
        new HttpManager<DeleteModel>().sendQuest(Request.Method.POST, HttpUrlConfig.deleteComment, jsonObject, DeleteModel.class, new HttpRequestCallBack<DeleteModel>() {
            @Override
            public void onRequestSuccess(DeleteModel response, boolean cached) {
                if (response.isStatus()) {
                    commentModels.remove(usercom);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onRequestFailed(VolleyError error) {

            }
        });
    }

    private class ViewHolder {
        ImageView iv_head;
        EmojiconTextView tv_comment_content;
        TextView tv_comment_name;//评论人的名字
        TextView tv_reply_name;//被回复人的名字
        TextView tv_time;
        ImageView iv_sex;
        TextView tv_comment_style;
    }

    public interface OnItemClickListener {
        void onClick(UserCommentModel userCommentModel);
    }
}
