package com.KSongbao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.KSongbao.bean.Obj;
import com.KSongbao.utils.HttpPathUtils;
import com.KSongbao.utils.Httptool;
import com.KSongbao.utils.SharePrefUtil;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;

public class ModifPassActivity extends Activity {
	private ImageButton ib_back;
	private EditText et_oldpass;
	private EditText et_newpass;
	private EditText et_again_newpass;
	private Button bt_complete;

	private SharePrefUtil myShare = new SharePrefUtil();

	private TextView tv_title;

	private Httptool ptool = new Httptool();

	static final int MOFIFY_SUCCESS = 0;
	static final int MODIFY_FAIL = 1;
	private String fail_message = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		TestinAgent.setLocalDebug(true);
		setContentView(R.layout.modif_pass_activity);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		ib_back = (ImageButton) findViewById(R.id.imgbtn_left);
		et_oldpass = (EditText) findViewById(R.id.et_oldpass);
		et_newpass = (EditText) findViewById(R.id.et_newpass);
		et_again_newpass = (EditText) findViewById(R.id.et_again_newpass);
		bt_complete = (Button) findViewById(R.id.bt_modif_pass);
		tv_title = (TextView) findViewById(R.id.txt_title);
		tv_title.setText("修改密码");
		ib_back.setImageDrawable(getResources().getDrawable(R.drawable.ic_exit));
		myShare.saveString(getApplicationContext(), "newpass", et_newpass
				.getText().toString());
		// myShare.saveString(getApplicationContext(), "password", et_newpass
		// .getText().toString());
		ib_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ModifPassActivity.this,
						MyPageActivity.class);
				startActivity(intent);
				finish();
			}
		});
		bt_complete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new GetDataTask().execute();
			}
		});
	}

	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// switch (v.getId()) {
	// case R.id.imgbtn_left:
	// Log.i("----------->", "点击了");
	// Toast.makeText(getApplicationContext(), "dianjile", 0).show();
	// Intent intent = new Intent(ModifPassActivity.this,
	// MyPageActivity.class);
	// startActivity(intent);
	// finish();
	//
	// break;
	// case R.id.bt_commit:
	// Toast.makeText(getApplicationContext(), "dianjile", 0).show();
	// break;
	// default:
	// break;
	// }
	// }
	class GetDataTask extends AsyncTask<Void, Void, List<Obj>> {

		@Override
		protected List<Obj> doInBackground(Void... arg0) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Httptool http = new Httptool();
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					String username = myShare.getString(
							getApplicationContext(), "admin", null);
					String password = myShare.getString(
							getApplicationContext(), "password", null);
					String newpassword = myShare.getString(
							getApplicationContext(), "newpass", null);
					// Log.i("---------->", sp_id);
					params.add(new BasicNameValuePair("username", username));
					params.add(new BasicNameValuePair("password", password));
					params.add(new BasicNameValuePair("newpassword",
							newpassword));

					try {
						JSONObject jsonObject = new JSONObject(ptool.httppost(
								HttpPathUtils.sp_mdpwd, params));
						if (jsonObject.getString("success").equals("false")) {
							// Toast.makeText(getApplicationContext(), "提交成功",
							// 0);
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
				Toast.makeText(getApplicationContext(), "修改失败," + fail_message,
						0).show();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), "修改成功", 0).show();
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
