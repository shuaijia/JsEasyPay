package com.jia.jsesaypay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jia.jsesaypaylibrary.alipay.AliPay;
import com.jia.jsesaypaylibrary.alipay.AlipayConfig;
import com.jia.jsesaypaylibrary.uppay.UPPay;
import com.jia.jsesaypaylibrary.wxpay.WXPay;
import com.jia.jsesaypaylibrary.wxpay.WXPayConfig;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /**
         * ====================微信支付===========================
         */
        String prepay_id = "GET PREPAY_ID FROM SERVER";// 下单后将订单信息传给服务器，以获取prepay_id

        WXPayConfig.getInstance().setAPI_KEY("YOUR API_KEY");
        WXPayConfig.getInstance().setAPP_ID("YOUR APP_ID");
        WXPayConfig.getInstance().setMCH_ID("YOUR MCH_ID");

        WXPay wxPay = new WXPay(MainActivity.this);
        wxPay.pay(prepay_id);


        /**
         * ====================支付宝支付===========================
         */
        AlipayConfig.getInstance().setSELLER("YOUR SELLER"); // 商户收款账号
        AlipayConfig.getInstance().setPARTNER("YOUR PARTNER"); // 商户PID
        AlipayConfig.getInstance().setRSA_PRIVATE(""); // 商户私钥，pkcs8格式
        AlipayConfig.getInstance().setCALL_SERVER_URL("YOUR CALLBACK URL"); // 支付宝请求服务器回调页

        AliPay aliPay = new AliPay(MainActivity.this, "GOODS NAME", "GOODS DETAIL", "PRICE");
        // 将订单id和info传给服务器，可以支付，则开始支付
        if (checkOrderFromServer(aliPay.getOrderId(), aliPay.getOrderInfo())) {
            aliPay.pay(new AliPay.AlipayCallBack() {
                @Override
                public void success() {
                    Log.e(TAG, "支付宝 success: ");
                }

                @Override
                public void faile() {
                    Log.e(TAG, "支付宝 faile: ");
                }

                @Override
                public void paying() {
                    Log.e(TAG, "支付宝 paying: ");
                }
            });
        }

        /**
         * ====================银联支付===========================
         */
        String tn=getTNfromServer(); // 从服务器获取订单流水号
        UPPay upPay=new UPPay(MainActivity.this,tn,"01"); //00表示正式环境，01表示测试环境
        upPay.pay();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        UPPay.getPayResult(data, new UPPay.UPPayCallback() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "银联 success: ");
            }

            @Override
            public void onFail() {
                Log.e(TAG, "银联 onFail: ");
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "银联 onCancel: ");
            }
        });
    }

    /**
     * 将订单id和info传给服务器
     *
     * @param orderId
     * @param orderInfo
     * @return
     */
    private boolean checkOrderFromServer(String orderId, String orderInfo) {
        return false;
    }

    /**
     * 从服务器获取TN流水号
     * @return
     */
    private String getTNfromServer(){
        return "";
    }
}
