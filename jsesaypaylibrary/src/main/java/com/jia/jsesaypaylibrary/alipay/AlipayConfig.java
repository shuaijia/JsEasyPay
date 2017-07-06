package com.jia.jsesaypaylibrary.alipay;

/**
 * Description: 支付宝支付配置类
 * Created by jia on 2016/11/23.
 * 人之所以能，是相信能
 */
public class AlipayConfig {

	// 商户PID
	public String PARTNER;
	// 商户收款账号
	public  String SELLER ;
	// 商户私钥，pkcs8格式
	public  String RSA_PRIVATE;

	private String CALL_SERVER_URL;

	private static AlipayConfig instance;

	private AlipayConfig(){};

	public static AlipayConfig getInstance(){
		if(instance==null){
			instance=new AlipayConfig();
		}
		return instance;
	}

	public String getPARTNER() {
		return PARTNER;
	}

	public void setPARTNER(String PARTNER) {
		this.PARTNER = PARTNER;
	}

	public String getSELLER() {
		return SELLER;
	}

	public void setSELLER(String SELLER) {
		this.SELLER = SELLER;
	}

	public String getRSA_PRIVATE() {
		return RSA_PRIVATE;
	}

	public void setRSA_PRIVATE(String RSA_PRIVATE) {
		this.RSA_PRIVATE = RSA_PRIVATE;
	}

	public String getCALL_SERVER_URL() {
		return CALL_SERVER_URL;
	}

	public void setCALL_SERVER_URL(String CALL_SERVER_URL) {
		this.CALL_SERVER_URL = CALL_SERVER_URL;
	}
}
