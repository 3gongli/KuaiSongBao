package com.KSongbao;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends FragmentActivity {
	private long exitTime = 0;

	public void ExitApp() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(BaseActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT)
					.show();
			exitTime = System.currentTimeMillis();
		} else {
			BaseActivity.this.finish();
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
}
