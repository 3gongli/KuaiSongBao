package com.ksb.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.KSongbao.R;
import com.KSongbao.bean.StatisticsBean;

public class StatisticsAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private List<StatisticsBean> data;

	public StatisticsAdapter(List<StatisticsBean> list, Context context) {
		this.data = new ArrayList<StatisticsBean>();
		this.data.addAll(list);
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHold holder;
		if (convertView == null) {
			holder = new ViewHold();
			convertView = mInflater.inflate(R.layout.statisticsadapter, null);
			holder.textView_date = (TextView) convertView
					.findViewById(R.id.textView_date);
			holder.textView_complete = (TextView) convertView
					.findViewById(R.id.textView_complete);
			holder.textView_cancle = (TextView) convertView
					.findViewById(R.id.textView_cancle);
			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}
		holder.textView_date.setText(data.get(position).getDate());
		holder.textView_complete.setText(data.get(position).getComplete());
		holder.textView_cancle.setText(data.get(position).getCancle());
		return convertView;
	}

	class ViewHold {
		private TextView textView_date;
		private TextView textView_complete;
		private TextView textView_cancle;
	}

}
