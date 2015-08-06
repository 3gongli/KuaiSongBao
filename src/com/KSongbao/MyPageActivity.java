package com.KSongbao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.KSongbao.utils.SharePrefUtil;
import com.umeng.analytics.MobclickAgent;

public class MyPageActivity extends Activity {
	private SharePrefUtil share = new SharePrefUtil();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mypage_activity);
		initView();
	}

	private void initView() {
		TextView txt_title = (TextView) findViewById(R.id.txt_title);
		Button bt_cancel = (Button) findViewById(R.id.bt_cancel);
		Button bt_modif_pass = (Button) findViewById(R.id.bt_modif_pass);
		TextView tv_shop = (TextView) findViewById(R.id.tv_shop);
		TextView tv_phone = (TextView) findViewById(R.id.tv_phone);
		TextView tv_name = (TextView) findViewById(R.id.tv_name);
		txt_title.setText("我的页面");
		tv_shop.setText(share.getString(getApplicationContext(), "sp_name",
				null));
		tv_phone.setText(share.getString(MyPageActivity.this, "admin", null));
		tv_name.setText(share.getString(getApplicationContext(), "real_name",
				null));
		ImageButton imgbtn_left = (ImageButton) findViewById(R.id.imgbtn_left);
		imgbtn_left.setImageDrawable(getResources().getDrawable(
				R.drawable.ic_exit));
		// 统计
		ImageButton btn_right = (ImageButton) findViewById(R.id.btn_right);
		btn_right.setImageDrawable(getResources().getDrawable(
				R.drawable.statistics));
		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						StatisticsActivity.class);
				startActivity(intent);
			}
		});
		// 注销
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
		bt_modif_pass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MyPageActivity.this,
						ModifPassActivity.class);
				startActivity(intent);
				finish();
			}
		});
		imgbtn_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						Song_MyOrderActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(MyPageActivity.this,
					Song_MyOrderActivity.class);
			startActivity(intent);
			finish();
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
}
