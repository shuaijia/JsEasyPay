package com.jia.jsesaypaylibrary.alipay;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Description: 支付宝支付类
 * Created by jia on 2016/11/23.
 * 人之所以能，是相信能
 */
public class AliPay {

    public static final int SUCCESS = 1;
    public static final int FAILE = 2;
    public static final int PAYING = 3;

    // 订单id(是一随机数且和时间有关，一个订单只许有一个id)
    private String orderId;
    // 订单信息
    private String orderInfo;

    // 上下文
    private Context context;
    // 回调方法
    private AlipayCallBack callback;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:

                    callback.success();

                    break;
                case FAILE:

                    callback.faile();

                    break;
                case PAYING:

                    callback.paying();

                    break;
            }
        }

    };

    /**
     * AliPay构造函数
     *
     * @param goodsName    物品名称
     * @param goodsDetails 物品描述
     * @param price        物品价格
     */
    public AliPay(@NonNull Context context, @NonNull String goodsName, @NonNull String goodsDetails, @NonNull String price) {

        this.context = context;

        // 创建订单id
        orderId = getOutTradeNo();
        // 创建订单信息
        orderInfo = getOrderInfo(goodsName, goodsDetails, price);
        // 签名
        String sign = sign(orderInfo);

        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        orderInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
    }

    /**
     * 对外提供的支付方法
     */
    public void pay(@NonNull AlipayCallBack alipayCallBack) {
        this.callback = alipayCallBack;
        alipay();
    }

    /**
     * 具体的支付宝支付
     */
    private void alipay() {
        // TODO Auto-generated method stub
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message msg = new Message();

                PayTask alipay = new PayTask((Activity) context);
                final String result = alipay.pay(orderInfo);

                PayResult payResult = new PayResult(result);
                // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                String resultInfo = payResult.getResult();

                String resultStatus = payResult.getResultStatus();

                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {

                    msg.what = SUCCESS;
                    handler.sendMessage(msg);
                } else {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    if (TextUtils.equals(resultStatus, "8000")) {
                        msg.what = PAYING;
                        handler.sendMessage(msg);
                    } else {
                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                        msg.what = FAILE;
                        handler.sendMessage(msg);
                    }
                }
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    ;

    /**
     * 获取订单信息
     *
     * @return
     */
    public String getOrderInfo() {
        return orderInfo;
    }

    /**
     * 获取订单id
     *
     * @return
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 支付宝支付回调
     *
     * @author jia
     */
    public interface AlipayCallBack {
        /**
         * 支付成功
         */
        abstract void success();

        /**
         * 支付失败
         */
        abstract void faile();

        /**
         * 支付中
         */
        abstract void paying();

    }


    /**
     * 创建订单信息
     */
    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + AlipayConfig.getInstance().getPARTNER() + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + AlipayConfig.getInstance().getSELLER() + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + orderId + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        // 注意：下面的url为回调接口，请求支付宝后，支付宝会向服务器请求以下的url 所以项目不同，以下的url应不同
        orderInfo += "&notify_url=" + "\"" + AlipayConfig.getInstance().getCALL_SERVER_URL() + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, AlipayConfig.getInstance().getRSA_PRIVATE());
    }

    /**
     * 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

}
