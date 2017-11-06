package com.white.usee.app.util;

import com.android.volley.VolleyError;

/**
 * Volley的响应回调
 * Created by white on 2016/4/9 0009.
 */
public interface HttpRequestCallBack<T> {
    public void onRequestSuccess(T response, boolean cached);

    public void onRequestFailed(VolleyError error);
}
