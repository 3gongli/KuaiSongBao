package com.KSongbao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.KSongbao.bean.StatisticsBean;
import com.KSongbao.utils.HttpPathUtils;
import com.KSongbao.utils.Httptool;
import com.KSongbao.utils.SharePrefUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksb.adapter.StatisticsAdapter;
import com.umeng.analytics.MobclickAgent;

public class StatisticsActivity extends Activity {
	private PullToRefreshListView pullToRefreshListView;
	public boolean isRefresh = false;
	private List<StatisticsBean> list;
	private Map<String, StatisticsBean> map = new TreeMap<String, StatisticsBean>();
	private String fail_message = "";
	private String key;
	private SharePrefUtil sharePrefUtil = new SharePrefUtil();

	private Button button_ok;
	private EditText editText_starttime;
	private EditText editText_endtime;

	// 选择开始日期年月日
	private int mYear;
	private int mMonth;
	private int mDay;
	// 选择结束日期年月日
	private int eYear;
	private int eMonth;
	private int eDay;

	private static final int SHOW_DATAPICK = 0;
	private static final int START_DATE_DIALOG_ID = 1;
	private static final int END_DATE_DIALOG_ID = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.statistics);

		// 日期选择部分
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		eYear = c.get(Calendar.YEAR);
		eMonth = c.get(Calendar.MONTH);
		eDay = c.get(Calendar.DAY_OF_MONTH);
		initView();

		editText_starttime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(START_DATE_DIALOG_ID);
			}
		});
		editText_endtime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(END_DATE_DIALOG_ID);
			}
		});
		button_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new MyTask().execute();
			}
		});

		// 数据加载部分
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						new MyTask().execute();

					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {

						new MyTask().execute();

					}

				});
	}

	class MyTask extends AsyncTask<Void, Void, List<StatisticsBean>> {

		@Override
		protected List<StatisticsBean> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						if (!isRefresh) {
							isRefresh = true;
							Httptool http = new Httptool();

							List<NameValuePair> params = new ArrayList<NameValuePair>();
							String sp_id = sharePrefUtil.getString(
									getApplicationContext(), "sp_id", null);
							String uid = sharePrefUtil.getString(
									getApplicationContext(), "uid", null);
							// Log.i("--->", starttime + "");
							// Log.i("--->", endtime + "");
							params.add(new BasicNameValuePair("st",
									editText_starttime.getText().toString()));
							params.add(new BasicNameValuePair("et",
									editText_endtime.getText().toString()));
							params.add(new BasicNameValuePair("sp_id", sp_id));
							params.add(new BasicNameValuePair("sp_uid", uid));

							String postRs = http.httppost(
									HttpPathUtils.statistic_total, params);

							JSONObject jsonObj = JSON.parseObject(postRs);
							// JSONObject object = new JSONObject(postRs);

							if (jsonObj.getString("success").equals("true")) {

								list = new ArrayList<StatisticsBean>(jsonToMap(
										jsonObj).values());
								Log.i("--------->list", list.toString());
								Message msg = new Message();
								msg.what = 1;
								handler.sendMessage(msg);
							} else {
								fail_message = jsonObj.getString("obj");
								Message msg = new Message();
								msg.what = 2;
								handler.sendMessage(msg);
							}
						}
					} catch (ConnectTimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			return list;
		}

		@Override
		protected void onPostExecute(List<StatisticsBean> result) {
			// mListItems.addFirst("Added after refresh...");
			// mAdapter.notifyDataSetChanged();
			// Call onRefreshComplete when the list has been refreshed.
			pullToRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}

	}

	public void initView() {
		editText_starttime = (EditText) findViewById(R.id.editText_starttime);
		editText_endtime = (EditText) findViewById(R.id.editText_endtime);
		button_ok = (Button) findViewById(R.id.button_ok);

	}

	// 开始日期控件事件
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDateDisplay(START_DATE_DIALOG_ID);
		}
	};
	// 结束日期控件事件
	private DatePickerDialog.OnDateSetListener eDateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			eYear = year;
			eMonth = monthOfYear;
			eDay = dayOfMonth;
			updateDateDisplay(END_DATE_DIALOG_ID);

		}
	};

	// 更新事件显示
	public void updateDateDisplay(int id) {
		switch (id) {
		case START_DATE_DIALOG_ID:
			editText_starttime.setText(new StringBuilder()
					.append(mYear)
					.append("-")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append("-")
					.append((mDay < 10) ? "0" + mDay : mDay));

			break;
		case END_DATE_DIALOG_ID:
			editText_endtime.setText(new StringBuilder()
					.append(eYear)
					.append("-")
					.append((eMonth + 1) < 10 ? "0" + (eMonth + 1)
							: (eMonth + 1)).append("-")
					.append((eDay < 10) ? "0" + eDay : eDay));

			break;
		default:
			break;
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case START_DATE_DIALOG_ID:
			return new DatePickerDialog(StatisticsActivity.this,
					mDateSetListener, mYear, mMonth, mDay);

		case END_DATE_DIALOG_ID:
			return new DatePickerDialog(StatisticsActivity.this,
					eDateSetListener, eYear, eMonth, eDay);
		default:
			break;
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		switch (id) {
		case START_DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		case END_DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(eYear, eMonth, eDay);
			break;
		default:
			break;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(getApplicationContext(), "查询成功", 0).show();
				ListView actualListView = pullToRefreshListView
						.getRefreshableView();
				actualListView.setBackgroundColor(getResources().getColor(
						R.color.white));
				registerForContextMenu(actualListView);
				StatisticsAdapter adapter = new StatisticsAdapter(list,
						StatisticsActivity.this);
				list.clear();
				// actualListView.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
				actualListView.setVisibility(View.VISIBLE);
				actualListView.setAdapter(adapter);
				isRefresh = false;
				break;
			case 2:
				Toast.makeText(getApplicationContext(), fail_message + "", 0)
						.show();
				isRefresh = false;
				break;
			default:
				break;
			}
		};
	};

	public Map jsonToMap(JSONObject jsonObject) {
		Map<String, StatisticsBean> map = new TreeMap<String, StatisticsBean>();
		JSONObject object2 = jsonObject.getJSONObject("obj");
		// .getJSONObject("obj");
		Iterator it = object2.keySet().iterator();
		while (it.hasNext()) {
			StatisticsBean bean = new StatisticsBean();
			key = String.valueOf(it.next());
			bean.setDate(key);
			JSONObject object3 = (JSONObject) object2.get(key);
			Object objwan = object3.get("5");
			if (objwan != null) {
				bean.setComplete(objwan.toString());
			} else {
				bean.setComplete("");
			}
			Object objqu = object3.get("-3");
			if (objqu != null) {
				bean.setCancle(objqu.toString());
			} else {
				bean.setCancle("");
			}
			map.put(key, bean);
		}
		return map;
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
