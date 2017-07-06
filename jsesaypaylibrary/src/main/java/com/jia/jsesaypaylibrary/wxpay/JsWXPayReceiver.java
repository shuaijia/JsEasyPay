package com.jia.jsesaypaylibrary.wxpay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Describtion: 微信支付广播接收器
 * Created by jia on 2017/7/6.
 * 人之所以能，是相信能
 */
public class JsWXPayReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final IWXAPI api = WXAPIFactory.createWXAPI(context, null);
        api.registerApp(WXPayConfig.getInstance().getAPP_ID());
    }
}
