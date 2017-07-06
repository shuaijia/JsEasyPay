package com.jia.jsesaypaylibrary.wxpay;

/**
 * Describtion: 微信支付配置类
 * Created by jia on 2017/7/6.
 * 人之所以能，是相信能
 */
public class WXPayConfig {

    private String APP_ID;

    private String API_KEY;

    private String MCH_ID;

    private static WXPayConfig instance;

    private WXPayConfig(){};

    public static WXPayConfig getInstance(){
        if(instance==null){
            instance=new WXPayConfig();
        }
        return instance;
    }


    public String getAPP_ID() {
        return APP_ID;
    }

    public void setAPP_ID(String APP_ID) {
        this.APP_ID = APP_ID;
    }

    public String getAPI_KEY() {
        return API_KEY;
    }

    public void setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public String getMCH_ID() {
        return MCH_ID;
    }

    public void setMCH_ID(String MCH_ID) {
        this.MCH_ID = MCH_ID;
    }
}
