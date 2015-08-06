package com.KSongbao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.KSongbao.bean.Obj;
import com.KSongbao.utils.HttpPathUtils;
import com.KSongbao.utils.Httptool;
import com.KSongbao.utils.SharePrefUtil;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;

public class NewOrderActivity extends BaseActivity implements OnClickListener {
	private ImageButton imgbtn_left;
	private TextView txt_title;
	private TextView tv_address, tv_name;
	private EditText et_goodsnumbeer;
	private EditText et_totalorder;
	private EditText et_remarks;
	private Button bt_commit;
	private Spinner spinner;
	SharePrefUtil sp;
	private String myAdress = "";
	private String street = "";
	private String door_num = "";
	private String sp_name = "";
	private String data = "";
	private String successMessage = "";
	private ArrayAdapter<String> adapter = null;
	private Httptool ptool = new Httptool();

	static final int SUBMIT_SUCCESS = 0;
	static final int SUBMIT_FAIL = 1;

	private String fail_message = "";

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		TestinAgent.setLocalDebug(true);
		setContentView(R.layout.neworder_activity);

		street = sp.getString(getApplicationContext(), "address", null);
		door_num = sp
				.getString(getApplicationContext(), "write_door_num", null);
		Log.i("---->address_detail", door_num + "");
		if (door_num == null) {
			myAdress = street;
		} else {

			myAdress = street + "" + door_num;
		}
		sp_name = sp.getString(getApplicationContext(), "sp_name", null);
		// Log.i("--------------->", myAdress);
		initView();
	}

	private void initView() {
		et_goodsnumbeer = (EditText) findViewById(R.id.et_goodsnumbeer);
		et_totalorder = (EditText) findViewById(R.id.et_totalorder);
		et_remarks = (EditText) findViewById(R.id.et_remarks);
		txt_title = (TextView) findViewById(R.id.txt_title);
		tv_address = (TextView) findViewById(R.id.tv_address);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_name.setText(sp_name);

		spinner = (Spinner) findViewById(R.id.spinner_classfy);
		String str[] = new String[] { "餐饮", "水果", "文件", "票务", "鲜花", "饮料", "生鲜",
				"其他" };
		spinner.setSelection(0, true);
		spinner.setPrompt("请选择货物类型:");
		adapter = new ArrayAdapter<String>(NewOrderActivity.this,
				android.R.layout.simple_spinner_item, str);
		// R.layout.goodstype_item
		//
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				data = arg0.getItemAtPosition(arg2).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		tv_address.setText(myAdress);
		imgbtn_left = (ImageButton) findViewById(R.id.imgbtn_left);
		bt_commit = (Button) findViewById(R.id.bt_commit);

		// EditText et_remarks2 = (EditText) findViewById(R.id.et_remarks2);
		// et_remarks2.setInputType(InputType.TYPE_NULL);

		txt_title.setText("新订单");
		imgbtn_left.setImageDrawable(getResources().getDrawable(
				R.drawable.ic_exit));

		imgbtn_left.setOnClickListener(this);
		bt_commit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_commit:
			new GetDataTask().execute();
			break;
		case R.id.imgbtn_left:
			Intent intent2 = new Intent(getApplicationContext(),
					Song_MyOrderActivity.class);
			startActivity(intent2);
			finish();
			break;
		default:
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
					String sp_id = sp.getString(getApplicationContext(),
							"sp_id", null);
					String city_code = sp.getString(getApplicationContext(),
							"city_code", null);
					String goods_num = et_goodsnumbeer.getText().toString();
					String total_num = et_totalorder.getText().toString();

					Log.i("---------->新的订单", city_code + ".." + goods_num
							+ ".." + total_num + ".." + data);

					// Log.i("------------>", "我的" +
					// city_name);
					String x = sp.getString(getApplicationContext(), "x", null);
					String y = sp.getString(getApplicationContext(), "y", null);
					// Log.i("------------>x", x + "..");
					// Log.i("------------>y", y + "..");
					// Log.i("---------->", city_name + "空的");
					params.add(new BasicNameValuePair("sp_id", sp_id));
					params.add(new BasicNameValuePair("remarks", et_remarks
							.getText().toString()));
					params.add(new BasicNameValuePair("city_code", city_code));
					params.add(new BasicNameValuePair("x", x));
					params.add(new BasicNameValuePair("y", y));
					params.add(new BasicNameValuePair("cargo_type", data));
					params.add(new BasicNameValuePair("cargo_num", goods_num));
					params.add(new BasicNameValuePair("num", total_num));
					try {
						JSONObject jsonObject = new JSONObject(ptool.httppost(
								HttpPathUtils.sp_save_order, params));
						if (jsonObject.getString("success").equals("true")) {
							// Toast.makeText(getApplicationContext(), "提交成功",
							// 0);
							successMessage = jsonObject.getString("obj");
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
				Toast.makeText(getApplicationContext(), "提交失败," + fail_message,
						0).show();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), successMessage + "", 0)
						.show();
				break;
			}
		}
	};

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
