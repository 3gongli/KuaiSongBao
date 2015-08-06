package com.KSongbao.bean;

import java.util.List;

public class ReleaseOrderBean {
	public String errors;
	public int page;
	public int start;
	public boolean success;
	public int limit;
	public int tatalCount;
	public List<OrderDetail> obj;

	public class OrderDetail {
		public String booking_fetch_time;
		public float cargo_price;
		public String create_time;
		public String fetch_buyer_fee;
		public String handover_fee;
		public String id;
		public String is_booking;
		public String pay_shipper_fee;
		public int payment_status;
		public String remarks;
		public String shipper_address;
		public String shipper_name;
		public String shipper_phone;
		public String shipper_x;
		public String shipper_y;
		public String waybill_status;
	}

}
