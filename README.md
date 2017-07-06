# JsEsayPay
对安卓端微信、支付宝、银联支付作封装，便于快速集成支付
## 使用方法
### 在build.gradle中
```java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
	compile 'com.github.shuaijia:JsEsayPay:V1.0'
}
```
### 注：每次返回支付成功后，都建议再次请求服务器是否成功，双方都返回成功，则此次交易成功，否则失败。
### 微信支付
#### step 1：在清单文件中添加(直接复制即可)
```java
<!-- 微信支付权限-->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

<!--依赖库中的微信支付广播-->
<receiver android:name="com.jia.jsesaypaylibrary.wxpay.JsWXPayReceiver">
	<intent-filter>
		<action android:name="com.tencent.mm.plugin.openapi.Intent.ACTION_REFRESH_WXAPP" />
	</intent-filter>
</receiver>
```
#### step 2:在根包名下建wxapi包，在该包下新建WXPayEntryActivity（支付完成回调页），继承JsWXPayHandler，实现其抽象方法(已回到主线程执行)
```java
@Override
public void create(Bundle savedInstanceState) {

}

@Override
public void paySuccess() {

}

@Override
public void payCancel() {

}

@Override
public void payFail() {

}
```
将该Activity在清单文件中注册
#### step 3:将订单信息提交服务器，获取prepay_id， 开始支付
```java
String prepay_id = "GET PREPAY_ID FROM SERVER";// 下单后将订单信息传给服务器，以获取prepay_id

WXPayConfig.getInstance().setAPI_KEY("YOUR API_KEY");
WXPayConfig.getInstance().setAPP_ID("YOUR APP_ID");
WXPayConfig.getInstance().setMCH_ID("YOUR MCH_ID");

WXPay wxPay = new WXPay(MainActivity.this);
wxPay.pay(prepay_id);
```
#### step 4:微信支付集成到此完成

### 支付宝支付
#### step 1:在清单文件中（直接复制即可）
```java
<!-- 支付宝支付权限-->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
#### step 2: 配置账号信息，并创建支付对象
```java
AlipayConfig.getInstance().setSELLER("YOUR SELLER"); // 商户收款账号
AlipayConfig.getInstance().setPARTNER("YOUR PARTNER"); // 商户PID
AlipayConfig.getInstance().setRSA_PRIVATE(""); // 商户私钥，pkcs8格式
AlipayConfig.getInstance().setCALL_SERVER_URL("YOUR CALLBACK URL"); // 支付宝请求服务器回调页

AliPay aliPay = new AliPay(MainActivity.this, "GOODS NAME", "GOODS DETAIL", "PRICE");
```
#### step 3:将订单ID和订单信息传给服务器，返回是否可以支付，可以则开始支付（项目不同，可自行处理此逻辑）
```java
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
```
#### step 4:支付宝集成完成
### 银联支付
#### step 1:在清单文件中（直接复制即可）
```java
<!--银联支付权限-->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.NFC" />
<uses-feature android:name="android.hardware.nfc.hce" />

<!--银联支付页-->
<activity
	android:name="com.unionpay.uppay.PayActivity"
	android:configChanges="orientation|keyboardHidden"
	android:excludeFromRecents="true"
	android:label="@string/app_name"
	android:screenOrientation="portrait"
	android:windowSoftInputMode="adjustResize" />
```
#### step 2:将订单信息提交服务器，获取订单流水号tn
```java
String tn=getTNfromServer(); // 从服务器获取订单流水号
```
#### step 3:开始支付
```java
UPPay upPay=new UPPay(MainActivity.this,tn,"01"); //00表示正式环境，01表示测试环境
upPay.pay();
```
#### step 4:在onActivityResult中
```java
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
```
#### step 5:银联集成完了，有没有很简单！
### 最后，由于时间匆忙，难免有错或需要改进的地方 有好的建议记得联系我哦！
### 电话：13693510929
![image](https://raw.githubusercontent.com/shuaijia/JsEsayPay/master/pics/weixin.png)
