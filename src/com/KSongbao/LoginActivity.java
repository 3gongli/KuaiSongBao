package com.KSongbao;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.KSongbao.bean.Obj;
import com.KSongbao.utils.APNUtil;
import com.KSongbao.utils.GsonUtil;
import com.KSongbao.utils.HttpPathUtils;
import com.KSongbao.utils.Httptool;
import com.KSongbao.utils.SharePrefUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends BaseActivity implements OnClickListener {
	public static final String TAG = "---------->TAG";
	private EditText et_admin;
	private EditText et_password;
	private TextView textView_version;
	private Button bt_login;
	private SharePrefUtil shareUtils;
	private String titleid = "id";

	public static final String INTENT_ACTION = "com.KSongbao.LocationService";
	public static final int UPDATA_CLIENT = 1;
	public static final int GET_UNDATAINFO_ERROR = 2;
	public static final int DOWN_ERROR = 3;
	private double localVersion;
	private double serverVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		TestinAgent.init(this);
		super.onCreate(savedInstanceState);
		String info = getDeviceInfo(getApplicationContext());
		Log.i("--->ksb", info);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		TestinAgent.setLocalDebug(true);
		setContentView(R.layout.login_activity);
		// shareUtils.saveString(getApplicationContext(), "version_code",
		// "3.0");
		try {
			new Thread(runn).start();
			checkNetwork();
			initUI();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MobclickAgent.reportError(getApplicationContext(), e);

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ExitApp();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void initUI() {

		et_admin = (EditText) findViewById(R.id.et_admin);
		et_password = (EditText) findViewById(R.id.et_password);
		textView_version = (TextView) findViewById(R.id.textView_version);
		bt_login = (Button) findViewById(R.id.bt_login);
		initData();
		// 设置Button按钮的点击事件
		bt_login.setOnClickListener(this);
	}

	private void initData() {
		try {
			textView_version
					.setText("版本号:V"
							+ getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String admin = SharePrefUtil.getString(this, "admin", "");
		String password = SharePrefUtil.getString(this, "password", "");
		// System.out.println(admin + "***********" + password);
		if (!TextUtils.isEmpty(admin)) {
			et_admin.setText(admin);
		}
		if (!TextUtils.isEmpty(password)) {
			et_password.setText(password);
		}
	}

	@Override
	public void onClick(View view) {
		// 得到输入框中的内容，并保存
		String name = et_admin.getText().toString().trim();
		String password = et_password.getText().toString().trim();
		SharePrefUtil.saveString(this, "admin", name);
		SharePrefUtil.saveString(this, "password", password);
		// System.out.println(name + "***********" + password);
		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
			Toast.makeText(this, "用户名和密码都不能为空", 0).show();
			return;
		} else {
			String path = HttpPathUtils.HOST + "/mobile/sp_login";
			// 创建浏览器
			AsyncHttpClient client = new AsyncHttpClient();
			// 提交的参数对象
			RequestParams params = new RequestParams();
			params.put("un", name);
			params.put("pwd", password);
			client.setTimeout(5000);
			client.get(path, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					String result = new String(responseBody);
					System.out.println(result);
					// 解析json
					processData(result);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					String result = new String(responseBody);
					System.out.println(result);
					Toast.makeText(LoginActivity.this, "网络连接失败", 0).show();

				}
			});

		}
	}

	/**
	 * 解析json
	 * 
	 * @param result
	 *            传入要解析的json字符串
	 */
	private void processData(String result) {
		try {
			// 手动解析json
			JSONObject userBean = new JSONObject(result);
			boolean isSuccess = userBean.getBoolean("success");
			if (isSuccess) {
				Toast.makeText(LoginActivity.this, "登录成功", 0).show();
				// JSONObject jsonObject = userBean.getJSONObject("obj");
				// 得到obj对应的字符串
				String strObj = userBean.getString("obj");
				Log.i("-------->", strObj + "");
				Obj obj = GsonUtil.json2Bean(strObj, Obj.class);
				shareUtils.saveString(getApplicationContext(), "sp_id",
						obj.sp_id);
				shareUtils.saveString(getApplicationContext(), "uid", obj.uid);
				shareUtils.saveString(getApplicationContext(), "address",
						obj.address);
				shareUtils.saveString(getApplicationContext(), "city_name",
						obj.city_name);
				shareUtils.saveString(getApplicationContext(), "city_code",
						obj.city_code);
				shareUtils.saveString(getApplicationContext(), "real_name",
						obj.real_name);
				shareUtils.saveString(getApplicationContext(), "sp_name",
						obj.sp_name);
				shareUtils.saveString(getApplicationContext(),
						"write_door_num", obj.address_detail + "");// 街道信息
				// Log.i("------>", obj.city_code + "..");
				shareUtils.saveString(getApplicationContext(), "x",
						obj.address_x);
				// Log.i("------>", obj.address_x + "..");
				shareUtils.saveString(getApplicationContext(), "y",
						obj.address_y);
				// Log.i("------>", obj.address_y + "..");

				if ("1".equals(obj.new_user)) {
					Intent intent = new Intent(LoginActivity.this,
							Song_MyOrderActivity.class);
					// 目前先让它跳转到我要发单的页面
					// Intent intent = new Intent(LoginActivity.this,
					// NewOrderActivity.class);
					startActivity(intent);
					finish();
				} else if ("0".equals(obj.new_user)) {
					if (LocationService.GPSIsOPen(LoginActivity.this)) {
						Intent intent = new Intent(LoginActivity.this,
								Setting.class);
						startActivity(intent);
						finish();
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								startService(new Intent(
										LoginActivity.INTENT_ACTION));
							}
						}).start();
					} else {
						AlertDialog.Builder ab = new AlertDialog.Builder(this);
						ab.setMessage("GPS未打开,请设置");
						ab.setPositiveButton("设置",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent();
										intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
										intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										try {
											startActivity(intent);
										} catch (ActivityNotFoundException ex) {
											intent.setAction("android.settings.SETTINGS");
											try {
												startActivity(intent);
											} catch (Exception e) {
											}
										}
									}
								});
						ab.setNegativeButton("退出",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										System.exit(0);
									}
								});
						ab.show();
					}

				}
			} else {
				Toast.makeText(LoginActivity.this, userBean.getString("obj"), 0)
						.show();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * 进行网络检查
	 */
	public boolean checkNetwork() {
		// !!!在调用SDK初始化前进行网络检查
		// 当前没有拥有网络
		if (false == APNUtil.isNetworkAvailable(this)) {
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setMessage("网络未连接,请设置网络");
			ab.setPositiveButton("设置", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent("android.settings.SETTINGS");
					startActivityForResult(intent, 0);
				}
			});
			ab.setNegativeButton("退出", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					System.exit(0);
				}
			});
			ab.show();
			return false;
		} else {
			return true;
		}
	}

	/*
	 * 从服务器获取解析并进行比对版本号
	 */
	Runnable runn = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			try {

				// String str = shareUtils.getString(getApplicationContext(),
				// "version_code", null);
				// localVersion = Double.parseDouble(str);
				localVersion = getPackageManager().getPackageInfo(
						getPackageName(), 0).versionCode;
				Httptool http = new Httptool();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				JSONObject obj = new JSONObject(http.httppost(
						HttpPathUtils.shipper_checkversion, params));
				if (obj.getString("success").equals("true")) {
					JSONObject jsonObject = obj.getJSONObject("obj");
					serverVersion = jsonObject.getDouble("version_num");
					// Log.i("---------->serverVersion", serverVersion + "..");
					// Log.i("---------->localVersion", localVersion + "..");
					String version_code = jsonObject.getString("version_code");
					// Log.i("---------->version_code", version_code + "..");
					shareUtils.saveString(getApplicationContext(),
							"version_code", version_code);

				} else {

				}
				if (localVersion == serverVersion) {
					Log.i("--------->", "版本号相同无需升级");
					// LoginMain();
				} else {
					// Log.i(TAG,"版本号不同 ,提示用户升级 ");
					Message msg = new Message();
					msg.what = UPDATA_CLIENT;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				// 待处理
				Message msg = new Message();
				msg.what = GET_UNDATAINFO_ERROR;
				handler.sendMessage(msg);
				e.printStackTrace();
			}

		}
	};
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_CLIENT:
				// 对话框通知用户升级程序
				showUpdataDialog();
				break;
			case GET_UNDATAINFO_ERROR:
				// 服务器超时
				Toast.makeText(getApplicationContext(), "获取服务器更新信息失败", 1)
						.show();
				// LoginMain();
				break;
			case DOWN_ERROR:
				// 下载apk失败
				Toast.makeText(getApplicationContext(), "下载新版本失败", 1).show();
				// LoginMain();
				break;
			}
		}
	};

	protected void showUpdataDialog() {
		AlertDialog.Builder builer = new Builder(this);
		builer.setTitle("版本升级");
		builer.setMessage("快送宝"
				+ shareUtils.getString(getApplicationContext(), "version_code",
						null));
		// 当点确定按钮时从服务器上下载 新的apk 然后安装
		builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Log.i(TAG,"下载apk,更新");
				downLoadApk();
			}
		});
		// 当点取消按钮时进行登录
		builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// LoginMain();
				System.exit(0);
			}
		});
		AlertDialog dialog = builer.create();
		dialog.show();
	}

	/*
	 * 从服务器中下载APK
	 */
	protected void downLoadApk() {
		final ProgressDialog pd; // 进度条对话框
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载更新");
		pd.show();
		new Thread() {
			@Override
			public void run() {
				try {
					File file = getFileFromServer(
							HttpPathUtils.shipper_download_newVersion
									+ shareUtils.getString(
											getApplicationContext(),
											"version_code", null), pd);
					sleep(3000);
					installApk(file);
					pd.dismiss(); // 结束掉进度条对话框
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = DOWN_ERROR;
					handler.sendMessage(msg);
					e.printStackTrace();
				}
			}
		}.start();
	}

	// 安装apk
	protected void installApk(File file) {
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	public static File getFileFromServer(String path, ProgressDialog pd)
			throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			// 获取到文件的大小
			pd.setMax(conn.getContentLength());
			Log.i(TAG, conn.getContentLength() + "");
			InputStream is = conn.getInputStream();
			File file = new File(Environment.getExternalStorageDirectory(),
					"ksb.apk");
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				// 获取当前下载量
				Log.i(TAG, "total" + total + "");
				pd.setProgress(total);
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		} else {
			return null;
		}
	}

	// 友盟
	public static String getDeviceInfo(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			String device_id = tm.getDeviceId();

			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);

			String mac = wifi.getConnectionInfo().getMacAddress();
			json.put("mac", mac);

			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}

			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}

			json.put("device_id", device_id);

			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
