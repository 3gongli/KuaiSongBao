package com.KSongbao.bean;

import java.util.List;

public class TodayCountBean {
	public boolean success;
	public String errors;
	public List<NumStatus> obj;

	public class NumStatus {
		public String num;
		public String status;
	}
}
