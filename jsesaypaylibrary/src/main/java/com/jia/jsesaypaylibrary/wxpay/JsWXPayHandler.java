package com.jia.jsesaypaylibrary.wxpay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Describtion: 微信支付回调页
 * Created by jia on 2017/7/6.
 * 人之所以能，是相信能
 */
public abstract class JsWXPayHandler extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, WXPayConfig.getInstance().getAPP_ID());
        api.handleIntent(getIntent(), this);

        create(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(final BaseResp resp) {

        // 强制回主线程执行
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

                    if (String.valueOf(resp.errCode).equals("0")) {
                        paySuccess();
                    } else if (String.valueOf(resp.errCode).equals("-1")) {
                        payCancel();
                    } else if (String.valueOf(resp.errCode).equals("-2")) {
                        payFail();
                    }

                }
            }
        });
    }


    public abstract void create(Bundle savedInstanceState);

    /**
     * 支付成功
     */
    public abstract void paySuccess();

    /**
     * 支付取消
     */
    public abstract void payCancel();

    /**
     * 支付失败
     */
    public abstract void payFail();
}
