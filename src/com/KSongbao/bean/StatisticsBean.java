package com.KSongbao.bean;

public class StatisticsBean {
	private String date;
	private String complete;
	private String cancle;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getComplete() {
		return complete;
	}

	public void setComplete(String complete) {
		this.complete = complete;
	}

	public String getCancle() {
		return cancle;
	}

	public void setCancle(String cancle) {
		this.cancle = cancle;
	}

	@Override
	public String toString() {
		return "StatisticsBean [date=" + date + ", complete=" + complete
				+ ", cancle=" + cancle + "]";
	}

}
