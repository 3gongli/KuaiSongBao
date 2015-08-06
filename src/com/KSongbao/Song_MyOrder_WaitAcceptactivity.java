package com.KSongbao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.KSongbao.bean.OrderList;
import com.KSongbao.utils.HttpPathUtils;
import com.KSongbao.utils.Httptool;
import com.KSongbao.utils.SharePrefUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ksb.adapter.OrderListAdapter;
import com.umeng.analytics.MobclickAgent;

public class Song_MyOrder_WaitAcceptactivity extends Activity {
	public static final String ACTION_REFRESH_WAITACCEPT_LISTVIEW = "action_refresh_waitaccept_listview";
	private PullToRefreshListView mPullRefreshListView;
	private SharePrefUtil sp = new SharePrefUtil();
	private JSONObject obj;
	private List<OrderList> list = new ArrayList<OrderList>();;
	public boolean isRefresh = false;
	private BaseActivity base = new BaseActivity();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_list);
		try {
			init();
			registerBoradcastReceiver();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MobclickAgent.reportError(getApplicationContext(), e);
		}
	}

	void init() {

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.take_list);

		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						new GetDataTask().execute();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						new GetDataTask().execute();
					}

				});

		new GetDataTask().execute();
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_REFRESH_WAITACCEPT_LISTVIEW)) {
				new GetDataTask().execute();
			}
		}

	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_REFRESH_WAITACCEPT_LISTVIEW);
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	class GetDataTask extends AsyncTask<Void, Void, List<OrderList>> {

		protected List<OrderList> doInBackground(Void... params) {
			// Simulates a background job.
			new Thread(new Runnable() {
				public void run() {
					try {
						if (!isRefresh) {
							isRefresh = true;
							Httptool http = new Httptool();
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							String sp_id = sp.getString(
									getApplicationContext(), "sp_id", null);
							params.add(new BasicNameValuePair("sp_id", sp_id));
							params.add(new BasicNameValuePair("page", "1"));
							params.add(new BasicNameValuePair("status", "2")); // 未取
							obj = new JSONObject(http.httppost(
									HttpPathUtils.query_sp_waybill, params));
							Log.i("----------->obj", obj.toString() + "..");
							if (obj.getString("success").equals("true")) {
								Song_MyOrderActivity.waitAcceptNum = obj
										.getString("totalCount");
								for (int i = 0; i < ((obj.getInt("limit") < obj
										.getInt("totalCount") ? obj
										.getInt("limit") : obj
										.getInt("totalCount"))); i++) {
									list.add(getMap((JSONObject) obj
											.getJSONArray("obj").get(i)));
									Log.i("Es_activit",
											"data"
													+ String.valueOf(obj
															.getJSONArray("obj")
															.get(i)));
								}
								for (int n = 2; n < ((obj.getInt("totalCount") % obj
										.getInt("limit")) > 0 ? (obj
										.getInt("totalCount") / obj
										.getInt("limit")) + 1 : (obj
										.getInt("totalCount") / obj
										.getInt("limit"))) + 1; n++) {
									List<NameValuePair> params2 = new ArrayList<NameValuePair>();
									params2.add(new BasicNameValuePair("sp_id",
											sp_id));
									params2.add(new BasicNameValuePair("page",
											String.valueOf(n)));
									params2.add(new BasicNameValuePair(
											"status", "2")); // 未取
									obj = new JSONObject(http.httppost(
											HttpPathUtils.query_sp_waybill,
											params2));
									for (int i = 0; i < ((obj.getInt("limit") < (obj
											.getInt("totalCount") - (n - 1) * 10) ? obj
											.getInt("limit")
											: (obj.getInt("totalCount") - (n - 1) * 10))); i++) {
										list.add(getMap((JSONObject) obj
												.getJSONArray("obj").get(i)));
									}
								}
								Message msg = new Message();
								msg.what = Song_MyOrderActivity.MSG_GET_WAITACCEPT_LIST_OK;
								handler.sendMessage(msg);

							} else {
								Message msg = new Message();
								msg.what = Song_MyOrderActivity.MSG_GET_WAITACCEPT_LIST_FAILED;
								handler.sendMessage(msg);
								isRefresh = false;
							}
						}
					} catch (JSONException e) {
						isRefresh = false;
						e.printStackTrace();
					} catch (ConnectTimeoutException e) {
						isRefresh = false;
						e.printStackTrace();
					}
				}

			}).start();
			Log.i("------------->list", list.toString() + "..");
			return list;
		}

		@Override
		protected void onPostExecute(List<OrderList> result) {
			// mListItems.addFirst("Added after refresh...");
			// mAdapter.notifyDataSetChanged();
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}

	public static OrderList getMap(JSONObject oneDataObj) throws JSONException {
		OrderList takeOrderData = new OrderList();
		takeOrderData.setId(oneDataObj.getString("id"));
		takeOrderData.setShipper_name(oneDataObj.getString("shipper_name"));
		takeOrderData.setGoods_type(oneDataObj.getString("cargo_type"));
		takeOrderData.setGoods_num(oneDataObj.getString("cargo_num"));
		takeOrderData.setGoods_total_num(oneDataObj.getString("waybill_num"));
		takeOrderData.setRemarks(oneDataObj.getString("remarks"));
		takeOrderData.setCreate_time(oneDataObj.getString("create_time"));
		takeOrderData.setSys_notification(oneDataObj
				.getString("sys_notification"));
		takeOrderData.setWait_time(oneDataObj.getString("wait_time"));
		// Log.i("---->执行到了这里", takeOrderData.toString());
		return takeOrderData;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Song_MyOrderActivity.MSG_GET_WAITACCEPT_LIST_OK:
				Toast.makeText(Song_MyOrder_WaitAcceptactivity.this,
						"刷新待接单列表成功", 0).show();
				ListView actualListView = mPullRefreshListView
						.getRefreshableView();
				actualListView.setBackgroundColor(getResources().getColor(
						R.color.white));
				registerForContextMenu(actualListView);
				OrderListAdapter adapter = new OrderListAdapter(list,
						Song_MyOrder_WaitAcceptactivity.this, 2);
				list.clear();
				// actualListView.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
				actualListView.setVisibility(View.VISIBLE);
				actualListView.setAdapter(adapter);
				TextView tv = new TextView(getApplicationContext());
				tv.setText("暂无信息");
				actualListView.setEmptyView(tv);

				isRefresh = false;

				Intent intent_refresh_take_radiobutton = new Intent(
						Song_MyOrderActivity.ACTION_REFRESH_REDIOBUTTON_2);
				sendBroadcast(intent_refresh_take_radiobutton);
				// Es_activity.esactivity.update("1");
				break;
			case Song_MyOrderActivity.MSG_GET_WAITACCEPT_LIST_FAILED:
				// Log.i("Es_activity", "MSG_GET_TAKE_LIST_FAILED");
				Toast.makeText(Song_MyOrder_WaitAcceptactivity.this,
						"刷新带接单列表失败", 0).show();

				break;
			default:
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
