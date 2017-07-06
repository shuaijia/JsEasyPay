package com.jia.jsesaypaylibrary.uppay;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

/**
 * Describtion: 银联手机控件支付（jar包版）
 * Created by jia on 2017/7/6.
 * 人之所以能，是相信能
 */
public class UPPay {

    private Activity activity;

    private String tn;

    private String serverMode;

    public UPPay(Activity activity, String tn, String serverMode) {
        this.activity = activity;
        this.tn = tn;
        this.serverMode = serverMode;
    }

    public void pay() {
        UPPayAssistEx.startPayByJAR(activity, PayActivity.class, "", "", tn, serverMode);
    }


    public static void getPayResult(Intent data, UPPayCallback callback) {
        if (data == null) {
            return;
        }

        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            callback.onSuccess();
        } else if (str.equalsIgnoreCase("fail")) {
            callback.onFail();
        } else if (str.equalsIgnoreCase("cancel")) {
            callback.onCancel();
        }
    }

    public interface UPPayCallback {
        void onSuccess();

        void onFail();

        void onCancel();
    }
}
