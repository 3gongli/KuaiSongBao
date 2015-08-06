package com.ksb.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.KSongbao.R;
import com.KSongbao.Song_MyOrder_WaitAcceptactivity;
import com.KSongbao.bean.OrderList;
import com.KSongbao.utils.HttpPathUtils;
import com.KSongbao.utils.Httptool;
import com.KSongbao.utils.SharePrefUtil;

public class OrderListAdapter extends BaseAdapter {
	private List<OrderList> myList = new ArrayList<OrderList>();
	private Context mContext;
	private int status;
	private LayoutInflater mInflater;
	String fail_message = "";
	SharePrefUtil sharePrefUtil = new SharePrefUtil();
	String id = "";

	public OrderListAdapter(List<OrderList> myList, Context mContext, int status) {

		super();
		this.myList.addAll(myList);
		this.mContext = mContext;
		this.status = status;
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return myList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return myList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_order, null);
			holder.goods_type = (TextView) convertView
					.findViewById(R.id.goods_type);
			holder.send_people = (TextView) convertView
					.findViewById(R.id.send_people);
			holder.goods_num = (TextView) convertView
					.findViewById(R.id.goods_num);
			holder.send_order_time = (TextView) convertView
					.findViewById(R.id.send_order_time);
			holder.linearLayout_order_status = (LinearLayout) convertView
					.findViewById(R.id.linearLayout_order_status);
			holder.goods_total_num = (TextView) convertView
					.findViewById(R.id.goods_total_num);
			holder.remarks = (TextView) convertView.findViewById(R.id.remarks);
			holder.wait_time = (TextView) convertView
					.findViewById(R.id.wait_time);
			holder.linearLayout_name_phone = (LinearLayout) convertView
					.findViewById(R.id.linearLayout_name_phone);
			holder.name_phone = (TextView) convertView
					.findViewById(R.id.name_phone);
			holder.linearLayout_notification = (LinearLayout) convertView
					.findViewById(R.id.linearLayout_notification);
			holder.linearLayout_remark = (LinearLayout) convertView
					.findViewById(R.id.linearLayout_remark);
			holder.textView_notification = (TextView) convertView
					.findViewById(R.id.textView_notification);
			holder.button_cancel = (Button) convertView
					.findViewById(R.id.button_cancel);
			holder.relativeLayout_cancel = (RelativeLayout) convertView
					.findViewById(R.id.relativeLayout_cancel);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (myList.get(position).getGoods_type().equals("null")) {
			holder.goods_type.setText("");
		} else {
			holder.goods_type.setText(myList.get(position).getGoods_type());
		}
		if (myList.get(position).getGoods_num().equals("null")) {
			holder.goods_num.setText("");
		} else {
			holder.goods_num.setText(myList.get(position).getGoods_num());
		}
		if (myList.get(position).getGoods_total_num().equals("null")) {
			holder.goods_total_num.setText("");
		} else {
			holder.goods_total_num.setText(myList.get(position)
					.getGoods_total_num());
		}
		if (myList.get(position).getRemarks().equals("null")) {
			holder.linearLayout_remark.setVisibility(View.GONE);
		} else {
			holder.remarks.setText(myList.get(position).getRemarks());
		}
		if (status == 2) {
			holder.linearLayout_order_status.setVisibility(View.VISIBLE);
			holder.linearLayout_name_phone.setVisibility(View.GONE);
			holder.wait_time.setText(myList.get(position).getWait_time());
		} else {
			holder.linearLayout_order_status.setVisibility(View.GONE);
			holder.linearLayout_name_phone.setVisibility(View.VISIBLE);
			holder.name_phone.setText(myList.get(position).getName_phone());
		}
		if (status == 2) {
			if (myList.get(position).getSys_notification().equals("")) {
				holder.linearLayout_notification.setVisibility(View.GONE);
			} else {
				holder.linearLayout_notification.setVisibility(View.VISIBLE);
				holder.textView_notification.setText(myList.get(position)
						.getSys_notification());
			}
		} else {
			holder.linearLayout_notification.setVisibility(View.GONE);
		}

		holder.send_order_time.setText(myList.get(position).getCreate_time());
		holder.send_people.setText(myList.get(position).getShipper_name());

		// 取消按钮,只有待接单有
		if (status == 2) {
			holder.relativeLayout_cancel.setVisibility(View.VISIBLE);
			holder.button_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					id = myList.get(position).getId();
					Builder builder = new AlertDialog.Builder(mContext);
					builder.setTitle("提示");
					builder.setMessage("确认取消吗?");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									new MyTask().execute();
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block

									}
									Intent refresh_waitaccept = new Intent(
											Song_MyOrder_WaitAcceptactivity.ACTION_REFRESH_WAITACCEPT_LISTVIEW);
									mContext.sendBroadcast(refresh_waitaccept);
								}
							});
					builder.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}

							});
					builder.create().show();
				}
			});
		} else {
			holder.relativeLayout_cancel.setVisibility(View.GONE);
		}

		return convertView;
	}

	class ViewHolder {
		public TextView goods_type;
		public TextView goods_num;
		public TextView goods_total_num;
		public TextView remarks;
		public TextView send_order_time;
		public TextView send_people;
		public TextView wait_time;
		private TextView name_phone;
		private TextView textView_notification;
		private LinearLayout linearLayout_order_status;
		private LinearLayout linearLayout_name_phone;
		private LinearLayout linearLayout_notification;
		private LinearLayout linearLayout_remark;
		private RelativeLayout relativeLayout_cancel;
		private Button button_cancel;
	}

	class MyTask extends AsyncTask<Void, Void, List<OrderList>> {

		@Override
		protected List<OrderList> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Httptool httptool = new Httptool();
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					String uid = sharePrefUtil.getString(mContext, "uid", null);
					String sp_id = sharePrefUtil.getString(mContext, "sp_id",
							null);
					// Log.i("-------->uid", uid + "..");
					params.add(new BasicNameValuePair("sp_uid", uid));
					params.add(new BasicNameValuePair("sp_id", sp_id));
					params.add(new BasicNameValuePair("id", id));
					params.add(new BasicNameValuePair("status", "-3"));
					// params.add(new BasicNameValuePair("remarks", value));
					try {
						JSONObject jsonObject = new JSONObject(httptool
								.httppost(HttpPathUtils.shipper_cencle_order,
										params));
						if (jsonObject.getString("success").equals("true")) {
							Message msg = new Message();
							msg.what = 1;
							handler.sendMessage(msg);
						} else {
							fail_message = jsonObject.getString("obj");
							Message msg = new Message();
							msg.what = 2;
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
				Toast.makeText(mContext, "取消成功", 0).show();
				break;
			case 2:
				Toast.makeText(mContext, "取消失败," + fail_message, 0).show();
				break;
			}
		}
	};

}
