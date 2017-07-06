package com.jia.jsesaypaylibrary.wxpay;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Description: 微信支付类
 * Created by jia on 2016/11/23.
 * 人之所以能，是相信能
 */
public class WXPay {

    private String prepay_id;

    private Context context;

    // 支付请求类
    private PayReq req;
    // IWXAPI对象
    private IWXAPI msgApi;

    public WXPay(@NonNull Context context) {

        this.context = context;

        // 创建PayReq对象
        req = new PayReq();
        // 创建IWXAPI对象
        msgApi = WXAPIFactory.createWXAPI(context, null);
        // 使用app_id注册app
        msgApi.registerApp(WXPayConfig.getInstance().getAPP_ID());
    }

    /**
     * 支付方法
     */
    public void pay(@NonNull String prepay_id) {
        genPayReq(prepay_id);
    }

    /**
     * 获取签名
     */
    private void genPayReq(String prepay_id) {

        req.appId = WXPayConfig.getInstance().getAPP_ID();
        req.partnerId = WXPayConfig.getInstance().getMCH_ID();
        req.prepayId = prepay_id;
        req.packageValue = "Sign=WXPay";
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());

        List<String> list = new ArrayList<>();
        list.add("appid=" + req.appId + "&");
        list.add("noncestr=" + req.nonceStr + "&");
        list.add("package=" + req.packageValue + "&");
        list.add("partnerid=" + req.partnerId + "&");
        list.add("prepayid=" + req.prepayId + "&");
        list.add("timestamp=" + req.timeStamp + "&");

        req.sign = genAppSign(list);

        msgApi.sendReq(req);
    }

    /**
     * 在app端生成签名
     *
     * @return
     */
    private String genAppSign(List<String> list) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            str.append(list.get(i));
        }
        str.append("key=");
        str.append(WXPayConfig.getInstance().getAPI_KEY());

        String appSign = MD5.getMessageDigest(str.toString().getBytes()).toUpperCase();
        return appSign;
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    // 生成当前时间
    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }
}
