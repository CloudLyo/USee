package com.white.usee.app.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.SymbolTable;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.white.usee.app.BaseApplication;
import com.white.usee.app.util.volley.MultipartRequest;
import com.white.usee.app.util.volley.MultipartRequestParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Volley的封装类
 * Created by white on 15-10-29.
 */
public class HttpManager<T> {
    public void sendQuest(int questMethod, String url, JSONObject object, final Class<T> clz, final HttpRequestCallBack<T> httpRequestCallBack) {
        {

            sendQuest(questMethod, url, object, new Response.Listener() {
                @Override
                public void onResponse(Object o) {
                    try {
                        String result = o.toString();
                        T resultObject = JSON.parseObject(result, clz);
                        if (httpRequestCallBack != null)
                            httpRequestCallBack.onRequestSuccess(resultObject, false);
                    } catch (Exception e) {
                        if (httpRequestCallBack != null)
                            httpRequestCallBack.onRequestFailed(new VolleyError(e.getMessage()));
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (httpRequestCallBack != null)
                        httpRequestCallBack.onRequestFailed(volleyError);

                }
            });
        }
    }

    public void sendQuestArray(int questMethod, String url, JSONObject object, final Class<T> clz, final HttpRequestCallBack<List<T>> httpRequestCallBack){
        sendQuest(questMethod, url, object, new Response.Listener() {
            @Override
            public void onResponse(Object o) {
                try {
                    String result = o.toString();
                    List<T> resultObject = JSON.parseArray(result, clz);
                    if (httpRequestCallBack != null)
                        httpRequestCallBack.onRequestSuccess(resultObject, false);
                } catch (Exception e) {
                    if (httpRequestCallBack != null)
                        httpRequestCallBack.onRequestFailed(new VolleyError(e.getMessage()));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (httpRequestCallBack != null)
                    httpRequestCallBack.onRequestFailed(volleyError);

            }
        });

    }

    public void uploadImage(String url, MultipartRequestParams params, final Class<T> clz, final HttpRequestCallBack<T> httpRequestCallBack) {
        final RequestQueue requestQueue = BaseApplication.getInstance().getRequestQueue();
        requestQueue.add(new MultipartRequest(Request.Method.POST, params, url, new Response.Listener() {
            @Override
            public void onResponse(Object o) {
                try {
                    String result = o.toString();
                    T resultObject = JSON.parseObject(result, clz);
                    if (httpRequestCallBack != null)
                        httpRequestCallBack.onRequestSuccess(resultObject, false);
                } catch (Exception e) {
                    if (httpRequestCallBack != null)
                        httpRequestCallBack.onRequestFailed(new VolleyError(e.getMessage()));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (httpRequestCallBack != null)
                    httpRequestCallBack.onRequestFailed(error);
            }
        }));
    }

    /**
     * 通用的请求方法,会根据请求get或者post自动发送不同格式请求
     *
     * @param questMethod   请求类型,在Request.Method中
     * @param url           请求的url网址,在HttpUrlConfig中
     * @param params        请求参数,要求封装成fastJson的JSONObject对象
     * @param listener      请求成功回调
     * @param errorListener 请求失败回调
     */
    public void sendQuest(int questMethod, String url, final JSONObject params, Response.Listener listener, Response.ErrorListener errorListener) {
        try {
            final RequestQueue requestQueue = BaseApplication.getInstance().getRequestQueue();
            int i = 0;
            if (questMethod == Request.Method.GET) {
                url = url + "?";
                for (String key : params.keySet()) {
                    i++;
                    if (i == params.keySet().size()) {
                        url += key + "=" + params.getString(key);
                    } else {
                        url += key + "=" + params.getString(key) + "&";
                    }
                }
            }
            StringRequest stringRequest = new StringRequest(questMethod, url, listener, errorListener) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> requestparms = new HashMap<String, String>();
                    for (String key : params.keySet()) {
                        requestparms.put(key, params.getString(key));
                    }
                    return requestparms;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return  params.toJSONString().getBytes();
                }

                @Override
                public byte[] getPostBody() throws AuthFailureError {
                    return getBody();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json;charset=" + getParamsEncoding();
                }

            };
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            LogUtil.i(e.getLocalizedMessage() + "\t" + e.getMessage());
        }
    }


}
