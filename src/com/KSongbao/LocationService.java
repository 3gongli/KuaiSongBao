package com.KSongbao;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.KSongbao.utils.SharePrefUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.testin.agent.TestinAgent;

public class LocationService extends Service {
	private static final String TAG = "LocalTestService--->";
	private LocationClient mLocationClient;
	private static final int UPDATE_LOCATION_INFO = 303;
	private static final int CANNOT_GET_LOCATION = 305;
	private int time = 2;
	private int i = 0;
	private SharePrefUtil sp = new SharePrefUtil();
	public static String addressdetail = "";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG, "LocalTestService--------onBind");
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i(TAG, "LocalTestService--------onCreate");
		TestinAgent.setLocalDebug(true);
		super.onCreate();
		initLocation();
		localThread.start();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		Log.i(TAG, "LocalTestService--------onStart");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "LocalTestService--------onDestroy");
		super.onDestroy();
		mLocationClient.stop();
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "LocalTestService--------onRebind");
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "LocalTestService--------onUnbind");
		return super.onUnbind(intent);
	}

	// 初始化 location
	private void initLocation() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(new MyLocationListener());// 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开GPS
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		// option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		// BDLocation类，封装了定位SDK的定位结果，通过该类用户可以获取error code，位置的坐标，精度半径等信息
		public void onReceiveLocation(BDLocation location) {

			if (location == null) {
				Log.i("LocationService", "定位失败");
				if (time > 0) {
					mLocationClient.start();
					time--;
				} else {

					Message msg = new Message();
					msg.what = CANNOT_GET_LOCATION;
					handler.sendMessage(msg);

				}
				return;
			} else {
				Log.i("LocationService",
						"获取到定位信息纬度：" + String.valueOf(location.getLatitude()));
				sp.saveString(getApplicationContext(), "y",
						String.valueOf(location.getLatitude()));
				Log.i("LocationService",
						"获取到定位信息经度：" + String.valueOf(location.getLongitude()));
				sp.saveString(getApplicationContext(), "x",
						String.valueOf(location.getLongitude()));
				Log.i("LocationService",
						"获取到定位信息地址：" + String.valueOf(location.getAddrStr()));
				Log.i("LocationService",
						"获取到定位信息省份：" + String.valueOf(location.getProvince()));
				Log.i("LocationService",
						"获取到定位信息城市：" + String.valueOf(location.getCity()));
				Log.i("LocationService",
						"获取到定位信息区/县：" + String.valueOf(location.getDistrict()));
				Log.i("LocationService",
						"获取到定位信息街道：" + String.valueOf(location.getStreet()));
				Log.i("LocationService",
						"获取到定位信息街道号码："
								+ String.valueOf(location.getStreetNumber()));
				Log.i("LocationService",
						"获取到定位信息楼层信息：" + String.valueOf(location.getFloor()));
				Setting.select_province.setText(location.getProvince());
				Setting.select_province.setKeyListener(null);
				Setting.select_province.setOnClickListener(null);
				Setting.select_city.setText(location.getCity());
				Setting.select_city.setKeyListener(null);
				Setting.select_city.setOnClickListener(null);
				Setting.select_street.setText(location.getDistrict() + ""
						+ location.getStreet() + ""
						+ location.getStreetNumber());
				addressdetail = location.getDistrict() + ""
						+ location.getStreet() + ""
						+ location.getStreetNumber();
				// Setting.select_street.setKeyListener(null);
				Setting.select_street.setFocusable(false);
				Setting.select_street.setFocusableInTouchMode(false);
				Toast.makeText(getApplicationContext(), "定位成功", 0).show();
				// Setting.write_door_num.setOnClickListener(null);
				stopService(new Intent(LoginActivity.INTENT_ACTION));
				// DataManger.instance.setLatitude((double) (location
				// .getLatitude()));// 纬度
				// DataManger.instance.setLongitude((double) (location
				// .getLongitude()));// 经度
				// if (System.currentTimeMillis() - lastTime > 5000) {
				// Message msg = new Message();
				// msg.what = UPDATE_LOCATION_INFO;
				// handler.sendMessage(msg);
				// }
				// lastTime = System.currentTimeMillis();
			}

		}

	}

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CANNOT_GET_LOCATION:
				Toast.makeText(LocationService.this, "获取定位信息失败！", 0).show();
				stopService(new Intent(LoginActivity.INTENT_ACTION));
				break;
			}
		}
	};

	public static final boolean GPSIsOPen(final Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		boolean network = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps && network) {
			return true;
		}
		return false;
	}

	private Thread localThread = new Thread(new Runnable() {

		public void run() {
			// handler.postDelayed(this, 5 * 60 * 1000);// 十分钟一次
			mLocationClient.start();// 开始定位
			time = 2;
			Log.i(TAG, "LocalTestService" + i++);
		}
	});
}
