package com.white.usee.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.model.NewMsgModel;

import java.util.List;

/**
 * 历史记录的管理器，缓存到本地
 * Created by 10037 on 2016/8/4 0004.
 */

public class HistoryMsgUtils {
    private static String tag = "historymsg";
    private static String noMsg = "nomsg";
    List<NewMsgModel> newMsgModels;

    public static List<NewMsgModel> getHistoryMsg(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(tag,Context.MODE_PRIVATE);
        String msgJson = sharedPreferences.getString(BaseApplication.getInstance().getUserId(),noMsg);
        if (!msgJson.equals(noMsg)){
            List<NewMsgModel> newMsgModels = JSONArray.parseArray(msgJson,NewMsgModel.class);
            return newMsgModels;
        }else {
            return null;
        }
    }

    public static void saveHistoryMsg(Context context,List<NewMsgModel> newMsgModels){
        if (newMsgModels.isEmpty()&&newMsgModels.size()<=0) return;
        List<NewMsgModel> oldMsg = getHistoryMsg(context);
        if (oldMsg!=null){
            oldMsg.addAll(0,newMsgModels);
        }else {
            oldMsg = newMsgModels;
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(tag,Context.MODE_PRIVATE).edit();
        editor.putString(BaseApplication.getInstance().getUserId(),JSONArray.toJSONString(oldMsg));
        editor.commit();
    }
    //清空本地数据库
    public static void deleteHistoryMsg(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(tag,Context.MODE_PRIVATE).edit();
        editor.putString(BaseApplication.getInstance().getUserId(),"");
        editor.commit();
    }

}
