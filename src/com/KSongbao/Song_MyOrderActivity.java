package com.KSongbao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.KSongbao.utils.HttpPathUtils;
import com.KSongbao.utils.Httptool;
import com.KSongbao.utils.Jsontool;
import com.KSongbao.utils.SharePrefUtil;
import com.umeng.analytics.MobclickAgent;

public class Song_MyOrderActivity extends TabActivity implements
		OnCheckedChangeListener {
	// private BaseActivity base_exit = new BaseActivity();

	private View layoutTitle;
	private TabHost mHost;
	private RadioButton radioButton0, radioButton1, radioButton2, radioButton3;
	private RadioGroup radioderGroup;
	private Button button_sendorder, button_refresh;
	private ImageButton btn_scan;
	private ImageView imageView_total;

	private SharePrefUtil sharePrefUtil = new SharePrefUtil();
	private SharedPreferences sp;
	public static String waitAcceptNum = "0";
	public static String ncTotalNum = "0";
	public static String deliveryTotalNum = "0";
	public static String completeTotalNum = "0";
	public static String unusualTotalNum = "0";

	public static final int MSG_GET_TAKE_LIST_OK = 236;
	public static final int MSG_GET_TAKE_LIST_FAILED = 237;
	public static final int MSG_GET_DELIVERY_LIST_OK = 234;
	public static final int MSG_GET_DELIVERY_LIST_FAILED = 235;
	public static final int MSG_GET_COMPLETE_LIST_OK = 238;
	public static final int MSG_GET_COMPLETE_LIST_FAILED = 239;
	public static final int MSG_GET_WAITACCEPT_LIST_OK = 240;
	public static final int MSG_GET_WAITACCEPT_LIST_FAILED = 241;

	public static final String ACTION_REFRESH_REDIOBUTTON_2 = "action_refresh_rediobutton_2";
	public static final int MSG_REFRESH_REDIOBUTTON = 204;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.toptabs);
		TextView head_title = (TextView) findViewById(R.id.textTile);
		layoutTitle = (View) findViewById(R.id.line_title);
		head_title.setText("我的订单");
		// 实例化TabHost
		mHost = this.getTabHost();
		// 添加选项卡
		mHost.addTab(mHost
				.newTabSpec("ONE")
				.setIndicator("ONE")
				.setContent(
						new Intent(this, Song_MyOrder_WaitAcceptactivity.class)));
		mHost.addTab(mHost.newTabSpec("TWO").setIndicator("TWO")
				.setContent(new Intent(this, Song_MyOrder_NCactivity.class)));
		mHost.addTab(mHost
				.newTabSpec("THREE")
				.setIndicator("THREE")
				.setContent(
						new Intent(this, Song_MyOrder_Deliveryactivity.class)));
		mHost.addTab(mHost
				.newTabSpec("FOUR")
				.setIndicator("FOUR")
				.setContent(
						new Intent(this, Song_MyOrder_Completeactivity.class)));
		mHost.setCurrentTab(0);
		registerBoradcastReceiver();
		updateOrderNum();
		sp = Song_MyOrderActivity.this.getSharedPreferences("registered",
				MODE_PRIVATE);
		radioderGroup = (RadioGroup) findViewById(R.id.top_radio);
		button_sendorder = (Button) findViewById(R.id.button_sendorder);
		button_refresh = (Button) findViewById(R.id.button_refresh);
		radioderGroup.setOnCheckedChangeListener(this);

		radioButton0 = (RadioButton) findViewById(R.id.radio_button0);
		radioButton1 = (RadioButton) findViewById(R.id.radio_button1);
		radioButton2 = (RadioButton) findViewById(R.id.radio_button2);
		radioButton3 = (RadioButton) findViewById(R.id.radio_button3);
		radioButton0.setChecked(true);

		btn_scan = (ImageButton) findViewById(R.id.top_btn);
		btn_scan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						MyPageActivity.class);
				finish();
				startActivity(intent);
			}
		});
		mHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if (tabId.equals("ONE")) { // 第一个标签

				}
				if (tabId.equals("TWO")) { // 第二个标签

				}
				if (tabId.equals("THREE")) { // 第三个标签

				}
				if (tabId.equals("FOUR")) { // 第三个标签

				}
				updateOrderNum();
			}

		});
		// updateopsp_list();
	}

	public void ck(View v) {
		switch (v.getId()) {
		case R.id.button_sendorder:
			Intent intent = new Intent(this, NewOrderActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.button_refresh:
			if (mHost.getCurrentTabTag().equals("ONE")) {
				Intent refresh_nc = new Intent(
						Song_MyOrder_WaitAcceptactivity.ACTION_REFRESH_WAITACCEPT_LISTVIEW);
				sendBroadcast(refresh_nc);
			} else if (mHost.getCurrentTabTag().equals("TWO")) {
				Intent refresh_delivery = new Intent(
						Song_MyOrder_NCactivity.ACTION_REFRESH_LISTVIEW);
				sendBroadcast(refresh_delivery);
			} else if (mHost.getCurrentTabTag().equals("FOUR")) {
				Intent refresh_waitaccept = new Intent(
						Song_MyOrder_Completeactivity.ACTION_REFRESH_COMPLETE_LISTVIEW);
				sendBroadcast(refresh_waitaccept);
			} else if (mHost.getCurrentTabTag().equals("THREE")) {
				Intent refresh_complete = new Intent(
						Song_MyOrder_Deliveryactivity.ACTION_REFRESH_DELIVERY_LISTVIEW);
				sendBroadcast(refresh_complete);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
		case R.id.radio_button0:
			mHost.setCurrentTabByTag("ONE");
			break;
		case R.id.radio_button1:
			mHost.setCurrentTabByTag("TWO");
			break;
		case R.id.radio_button2:
			mHost.setCurrentTabByTag("THREE");
			break;
		case R.id.radio_button3:
			mHost.setCurrentTabByTag("FOUR");
			break;
		}
	}

	public void updateOrderNum() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Httptool http = new Httptool();
					String sp_id = sharePrefUtil.getString(
							getApplicationContext(), "sp_id", null);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("sp_id", sp_id));
					JSONObject obj = new JSONObject(http.httppost(
							HttpPathUtils.sp_today_count, params));
					Log.i("---------->运单状态", ".." + String.valueOf(obj));
					if (obj.getString("success").equals("true")
							&& obj.getString("errors").equals("OK")) {
						JSONArray order_array = obj.getJSONArray("obj");
						for (int i = 0; i < order_array.length(); i++) {
							JSONObject data = order_array.getJSONObject(i);
							switch (Integer.valueOf(data.getString("status"))) {
							case -1:// 异常
								unusualTotalNum = (Jsontool.isnull(data
										.getString("num")));
								break;
							case 0:
								ncTotalNum = (Jsontool.isnull(data
										.getString("num")));
								break;
							case 1:
								deliveryTotalNum = (Jsontool.isnull(data
										.getString("num")));
								break;
							case 2:

								waitAcceptNum = (Jsontool.isnull(data
										.getString("num")));

								break;
							case 5:
								completeTotalNum = (Jsontool.isnull(data
										.getString("num")));
								break;
							default:
								break;
							}
						}
						Message msg = new Message();
						msg.what = MSG_REFRESH_REDIOBUTTON;
						handler.sendMessage(msg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REFRESH_REDIOBUTTON:
				radioButton0.setText("待接单（" + waitAcceptNum + "）");
				radioButton1.setText("待取（" + ncTotalNum + "）");
				radioButton2.setText("配送（" + deliveryTotalNum + "）");
				radioButton3.setText("完成（" + completeTotalNum + "）");
				break;

			default:
				break;
			}
		}
	};
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();

			// Log.i("---------->", action + "");
			if (action.equals(ACTION_REFRESH_REDIOBUTTON_2)) {
				// Log.i("-------->执行了", "111111111111111");
				updateOrderNum();
			}
		}

	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_REFRESH_REDIOBUTTON_2);
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private long time = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - time) > 2000) {
				Toast.makeText(Song_MyOrderActivity.this, "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				time = System.currentTimeMillis();
			} else {
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unregisterReceiver(mBroadcastReceiver);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		registerBoradcastReceiver();
	}
}
