package com.white.usee.app.util;

import android.content.Context;

import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.config.HttpUrlConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 敏感词过滤
 * Created by white on 16/6/2.
 */

public class BadKeyWordFilter {
    /**
     * 直接禁止的
     */
    private HashMap keysMap = new HashMap();
    private int matchType = 1; // 1:最小长度匹配 2：最大长度匹配

    public BadKeyWordFilter(Context context) {
        new HttpManager<>().sendQuest(Request.Method.GET, HttpUrlConfig.getSenSiteiveWord, new JSONObject(), new Response.Listener() {
            @Override
            public void onResponse(Object o) {
                try {
                    List<String> keywords = JSON.parseArray(o.toString(),String.class);
                    addKeywords(keywords);
                }catch (Exception e){
                    LogUtil.i("敏感词获取失败失败");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.i("敏感词获取失败失败"+volleyError.getMessage());
            }
        });

    }

    public void addKeywords(List<String> keywords) {
        for (String key : keywords) {
            HashMap nowhash = null;
            nowhash = keysMap;
            for (int j = 0; j < key.length(); j++) {
                char word = key.charAt(j);
                Object wordMap = nowhash.get(word);
                if (wordMap != null) {
                    nowhash = (HashMap) wordMap;
                } else {
                    HashMap<String, String> newWordHash = new HashMap<String, String>();
                    newWordHash.put("isEnd", "0");
                    nowhash.put(word, newWordHash);
                    nowhash = newWordHash;
                }
                if (j == key.length() - 1) {
                    nowhash.put("isEnd", "1");
                }
            }

        }
    }

    /**
     * 返回过滤违禁词后的列表，并且被替换为“*”
     */
    public String getFilerWords(String txt) {
        int l = txt.length();
        String filterWords = txt;
        for (int i = 0; i < l; ) {
            int len = checkKeyWords(txt, i, matchType);
            if (len > 0) {
                filterWords = filterWords.replaceAll(txt.substring(i, i + len), "*");
                i += len;
            } else {
                i++;
            }
        }
        return filterWords;
    }

    /**
     * 重置关键词
     */
    public void clearKeywords() {
        keysMap.clear();
    }

    /**
     * 检查一个字符串从begin位置起开始是否有keyword符合， 如果有符合的keyword值，返回值为匹配keyword的长度，否则返回零
     * flag 1:最小长度匹配 2：最大长度匹配
     */
    private int checkKeyWords(String txt, int begin, int flag) {
        HashMap nowhash = null;
        nowhash = keysMap;
        int maxMatchRes = 0;
        int res = 0;
        int l = txt.length();
        char word = 0;
        for (int i = begin; i < l; i++) {
            word = txt.charAt(i);
            Object wordMap = nowhash.get(word);
            if (wordMap != null) {
                res++;
                nowhash = (HashMap) wordMap;
                if (((String) nowhash.get("isEnd")).equals("1")) {
                    if (flag == 1) {
                        wordMap = null;
                        nowhash = null;
                        txt = null;
                        return res;
                    } else {
                        maxMatchRes = res;
                    }
                }
            } else {
                txt = null;
                nowhash = null;
                return maxMatchRes;
            }
        }
        txt = null;
        nowhash = null;
        return maxMatchRes;
    }

    /**
     * 返回txt中关键字的列表
     */
    public Set<String> getTxtKeyWords(String txt) {
        Set set = new HashSet();
        int l = txt.length();
        for (int i = 0; i < l; ) {
            int len = checkKeyWords(txt, i, matchType);
            if (len > 0) {
                set.add(txt.substring(i, i + len));
                i += len;
            } else {
                i++;
            }
        }
        txt = null;
        return set;
    }

    /**
     * 仅判断txt中是否有关键字
     */
    public boolean isContentKeyWords(String txt) {
        for (int i = 0; i < txt.length(); i++) {
            int len = checkKeyWords(txt, i, 1);
            if (len > 0) {
                return true;
            }
        }
        txt = null;
        return false;
    }

    public int getMatchType() {
        return matchType;
    }

    public void setMatchType(int matchType) {
        this.matchType = matchType;
    }
}