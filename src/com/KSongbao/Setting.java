package com.KSongbao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.KSongbao.bean.Obj;
import com.KSongbao.utils.HttpPathUtils;
import com.KSongbao.utils.Httptool;
import com.KSongbao.utils.SharePrefUtil;
import com.baidu.location.LocationClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

public class Setting extends Activity implements OnClickListener {
	private Button bt_determine;
	public static EditText select_province;
	public static EditText select_city;
	public static EditText sp_name;
	public static EditText select_street;
	public static EditText write_door_num;
	private ImageView iv__street;
	private Button bt_modify;

	private List<String> shengList;
	private List<String> shiList;
	private Map<String, String> citycode_Map = new HashMap<String, String>();
	PopupWindow popupWindow;

	private Map<String, List<Map<String, String>>> myMap = new HashMap<String, List<Map<String, String>>>();
	private List<Map<String, String>> myMapValue = new ArrayList<Map<String, String>>();

	private SharePrefUtil myShare = new SharePrefUtil();

	private LocationClient locationClient = null;
	private static final int UPDATE_TIME = 5000;
	private static int LOCATION_COUTNS = 0;
	private Httptool ptool = new Httptool();

	static final int SUBMIT_SUCCESS = 0;
	static final int SUBMIT_FAIL = 1;

	private String fail_message = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_activity);

		shengList = new ArrayList<String>();
		shiList = new ArrayList<String>();

		initData();
		init();

	}

	private void initData() {
		String path = HttpPathUtils.HOST + "/mobile/city_list";
		AsyncHttpClient client = new AsyncHttpClient();
		// 提交的参数对象
		// RequestParams params = new RequestParams();
		// params.put("un", name);
		// params.put("pwd", password);
		client.get(path, null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				String result = new String(responseBody);
				// System.out.println(result);
				// 解析json
				processData(result);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				Toast.makeText(Setting.this, "网络连接失败", 0).show();

			}
		});

	}

	/**
	 * json解析
	 * 
	 * @param result
	 */
	private void processData(String result) {
		// TODO Auto-generated method stub
		// System.out.println(result);
		try {
			JSONObject jsonObj = new JSONObject(result);
			boolean isSuccess = jsonObj.getBoolean("success");
			// 当解析json的success为true时
			if (isSuccess) {
				JSONArray obJsonArray = jsonObj.getJSONArray("obj");
				int len = obJsonArray.length();

				for (int k = 0; k < obJsonArray.length(); k++) {
					JSONObject cityobj = obJsonArray.getJSONObject(k);
					JSONArray citynameArray = cityobj.getJSONArray("citys");
					String proString = cityobj.getString("province");
					shengList.add(proString);
					List<Map<String, String>> citycodeList = new ArrayList<Map<String, String>>();
					for (int n = 0; n < citynameArray.length(); n++) {
						JSONObject citycode = citynameArray.getJSONObject(n);
						Map<String, String> citycodeMap = new HashMap<String, String>();

						String city_name = citycode.getString("city_name");
						String city_code = citycode.getString("city_code");
						citycode_Map.put(city_name, city_code);

						citycodeMap.put(city_name, city_code);
						citycodeList.add(citycodeMap);
					}
					myMap.put(proString, citycodeList);
				}
				Log.i("-----解析结果", myMap.toString());
				Log.i("-----解析结果", citycode_Map.toString());

			} else {
				String city = jsonObj.getString("obj");
			}

		} catch (Exception e) {

		}

	}

	private void init() {
		select_street = (EditText) findViewById(R.id.select_street);
		iv__street = (ImageView) findViewById(R.id.iv__street);

		TextView txt_title = (TextView) findViewById(R.id.txt_title);

		select_province = (EditText) findViewById(R.id.select_province);
		select_city = (EditText) findViewById(R.id.select_city);
		sp_name = (EditText) findViewById(R.id.sp_name);
		write_door_num = (EditText) findViewById(R.id.write_door_num);
		sp_name.setText(myShare.getString(getApplicationContext(), "sp_name",
				null));
		// 确定按钮
		bt_determine = (Button) findViewById(R.id.bt_determine);
		bt_modify = (Button) findViewById(R.id.bt_modify);
		txt_title.setText("地址设置");

		select_province.setOnClickListener(this);
		select_city.setOnClickListener(this);
		iv__street.setOnClickListener(this);
		bt_determine.setOnClickListener(this);
		bt_modify.setOnClickListener(this);

		select_province.setInputType(InputType.TYPE_NULL);
		select_city.setInputType(InputType.TYPE_NULL);

	}

	@Override
	public void onClick(View v) {
		View convertView = LayoutInflater.from(this).inflate(
				R.layout.pop_window, null);

		GridView gridView = (GridView) convertView.findViewById(R.id.gridView);
		TextView tv_select = (TextView) convertView
				.findViewById(R.id.tv_select);
		switch (v.getId()) {
		case R.id.select_province:
			shiList.clear();
			tv_select.setText("选择省份");
			popupWindow = new PopupWindow(this);
			popupWindow.setWidth(select_province.getWidth());
			popupWindow.setHeight(480);
			MyAdapter adapter = new MyAdapter(shengList);
			gridView.setAdapter(adapter);
			// 设置条目点击侦听，点击某个条目把该条目内容显示到输入框中
			gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					select_province.setText(shengList.get(position));

					myMapValue = myMap.get(shengList.get(position));
					// Map<String ,String> citynameMap = new HashMap<String
					// ,String>();
					for (Map<String, String> citynameMap : myMapValue) {
						Set<String> keySet = citynameMap.keySet();
						for (String key : keySet) {
							shiList.add(key);
						}
					}

					if (popupWindow != null && popupWindow.isShowing()) {
						popupWindow.dismiss();
					}
				}
			});
			popupWindow.setContentView(convertView);

			popupWindow.setOutsideTouchable(true);
			// 设置popupWindow的焦点
			popupWindow.setFocusable(true);
			popupWindow.showAsDropDown(select_province, 0, 0);

			break;
		case R.id.select_city:
			tv_select.setText("选择市区");
			popupWindow = new PopupWindow(this);
			popupWindow.setWidth(select_city.getWidth());
			popupWindow.setHeight(480);

			MyAdapter shiAdapter = new MyAdapter(shiList);
			gridView.setAdapter(shiAdapter);
			convertView.setBackgroundResource(R.drawable.listview_background);
			// 设置条目点击侦听，点击某个条目把该条目内容显示到输入框中
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					select_city.setText(shiList.get(position));
					if (popupWindow != null && popupWindow.isShowing()) {
						popupWindow.dismiss();
					}
				}
			});
			popupWindow.setContentView(convertView);
			// }
			popupWindow.setOutsideTouchable(true);
			// 设置popupWindow的焦点
			popupWindow.setFocusable(true);
			popupWindow.showAsDropDown(select_city, 0, 0);
			break;
		case R.id.iv__street:

			if (select_province.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(), "正在尝试重新定位...", 0)
						.show();
				startService(new Intent(LoginActivity.INTENT_ACTION));
			} else {
				Toast.makeText(getApplicationContext(), "定位成功,如果没有定位到的请手动填写", 1)
						.show();
				select_province.setKeyListener(null);
				select_province.setOnClickListener(null);
				select_city.setKeyListener(null);
				select_city.setOnClickListener(null);
				select_street.setFocusable(false);
				select_street.setFocusableInTouchMode(false);
			}
			break;
		case R.id.bt_modify:
			// Log.i("---->", "被点击了");
			Toast.makeText(getApplicationContext(), "您现在可以修改地址了☺", 0).show();
			select_province.setOnClickListener(this);
			select_city.setOnClickListener(this);
			select_street.setFocusable(true);
			select_street.setFocusableInTouchMode(true);
			break;

		case R.id.bt_determine:

			myShare.saveString(getApplicationContext(), "sp_name", sp_name
					.getText().toString());
			myShare.saveString(getApplicationContext(), "select_province",
					select_province.getText().toString());// 省
			myShare.saveString(getApplicationContext(), "select_city",
					select_city.getText().toString());// 市
			myShare.saveString(getApplicationContext(), "address",
					select_street.getText().toString());// 详细信息
			myShare.saveString(getApplicationContext(), "write_door_num",
					write_door_num.getText().toString());
			myShare.saveString(getApplicationContext(), "city_code",
					citycode_Map.get(select_city.getText().toString()));
			new GetDataTask().execute();
			// Intent intent = new Intent(getApplicationContext(),
			// MyOrder.class);
			// startActivity(intent);
			// finish();
			break;
		}

	}

	class GetDataTask extends AsyncTask<Void, Void, List<Obj>> {

		@Override
		protected List<Obj> doInBackground(Void... arg0) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Httptool http = new Httptool();
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					String select_city = myShare.getString(
							getApplicationContext(), "select_city", null);
					String select_street = myShare.getString(
							getApplicationContext(), "address", null);
					String sp_id = myShare.getString(getApplicationContext(),
							"sp_id", null);
					String sp_name = myShare.getString(getApplicationContext(),
							"sp_name", null);

					String city_code = myShare.getString(
							getApplicationContext(), "city_code", null);
					String address_detail = myShare.getString(
							getApplicationContext(), "write_door_num", null);
					String address_x = myShare.getString(
							getApplicationContext(), "x", null);
					String address_y = myShare.getString(
							getApplicationContext(), "y", null);
					Log.i("--------->提交的city_name", sp_name);
					if (LocationService.addressdetail.equals(select_street)) {
						params.add(new BasicNameValuePair("sp_id", sp_id));
						params.add(new BasicNameValuePair("city_name",
								select_city));
						params.add(new BasicNameValuePair("address",
								select_street));
						params.add(new BasicNameValuePair("sp_name", sp_name));
						// params.add(new BasicNameValuePair("sp_name",
						// sp_name));
						params.add(new BasicNameValuePair("city_code",
								city_code));
						params.add(new BasicNameValuePair("address_detail",
								write_door_num.getText().toString()));
						params.add(new BasicNameValuePair("address_x",
								address_x));
						params.add(new BasicNameValuePair("address_y",
								address_y));

					} else {
						params.add(new BasicNameValuePair("sp_id", sp_id));
						params.add(new BasicNameValuePair("city_name",
								select_city));
						params.add(new BasicNameValuePair("address",
								select_street));
						params.add(new BasicNameValuePair("sp_name", sp_name));
						// params.add(new BasicNameValuePair("sp_name",
						// sp_name));
						params.add(new BasicNameValuePair("city_code",
								city_code));
						params.add(new BasicNameValuePair("address_detail",
								write_door_num.getText().toString()));
					}
					try {
						JSONObject jsonObject = new JSONObject(ptool.httppost(
								HttpPathUtils.sp_address, params));
						if (jsonObject.getString("success").equals("true")) {
							// Toast.makeText(getApplicationContext(), "提交成功",
							// 0);
							JSONObject obj = jsonObject.getJSONObject("obj");
							// String address_x = obj.getString("address_x");
							// String address_y = obj.getString("address_y");
							// myShare.saveString(getApplicationContext(), "x",
							// address_x);
							// myShare.saveString(getApplicationContext(), "y",
							// address_y);
							// Log.i("------------>新的", address_x + "..");
							// Log.i("------------>新的", address_y + "..");
							Message msg = new Message();
							msg.what = 2;
							handler.sendMessage(msg);
							Intent intent_commit = new Intent(
									getApplicationContext(),
									Song_MyOrderActivity.class);
							startActivity(intent_commit);
							finish();
						} else {
							fail_message = jsonObject.getString("obj");
							Message msg = new Message();
							msg.what = 1;
							handler.sendMessage(msg);

						}
					} catch (ConnectTimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}).start();
			return null;
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(getApplicationContext(), "设置失败," + fail_message,
						0).show();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), "设置成功", 0).show();
				break;
			}
		}
	};

	class MyAdapter implements ListAdapter {
		List<String> stringList;

		MyAdapter(List stringList) {
			this.stringList = stringList;
		}

		@Override
		public int getCount() {
			return stringList.size();
		}

		@Override
		public Object getItem(int position) {
			return stringList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = View.inflate(Setting.this, R.layout.item_listview, null);
			} else {
				view = convertView;
			}
			TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
			tv_content.setText(stringList.get(position));

			return view;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub

		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean areAllItemsEnabled() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			return true;
		}
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (locationClient != null && locationClient.isStarted()) {
			locationClient.stop();
			locationClient = null;
		}
	}

}
