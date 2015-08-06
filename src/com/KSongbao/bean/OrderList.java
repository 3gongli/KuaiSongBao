package com.KSongbao.bean;

public class OrderList {
	public String booking_fetch_time;
	public String cargo_price;
	public String create_time;
	public String fetch_buyer_fee;
	public String handover_fee;
	public String id;
	public String is_booking;
	public String pay_shipper_fee;
	public int payment_status;
	public String remarks;
	public String shipper_address = "北京";
	public String shipper_name;
	public String shipper_phone;
	public String shipper_x;
	public String shipper_y;
	public String waybill_status;
	public String goods_type;
	public String goods_num;
	public String goods_total_num;
	public String wait_time;
	public String name_phone;
	public String sys_notification;

	public String getSys_notification() {
		return sys_notification;
	}

	public void setSys_notification(String sys_notification) {
		this.sys_notification = sys_notification;
	}

	public String getName_phone() {
		return name_phone;
	}

	public void setName_phone(String name_phone) {
		this.name_phone = name_phone;
	}

	public String getWait_time() {
		return wait_time;
	}

	public void setWait_time(String wait_time) {
		this.wait_time = wait_time;
	}

	//
	public String getBooking_fetch_time() {
		return booking_fetch_time;
	}

	public String getGoods_type() {
		return goods_type;
	}

	public void setGoods_type(String goods_type) {
		this.goods_type = goods_type;
	}

	public String getGoods_num() {
		return goods_num;
	}

	public void setGoods_num(String goods_num) {
		this.goods_num = goods_num;
	}

	public String getGoods_total_num() {
		return goods_total_num;
	}

	public void setGoods_total_num(String goods_total_num) {
		this.goods_total_num = goods_total_num;
	}

	public void setBooking_fetch_time(String booking_fetch_time) {
		this.booking_fetch_time = booking_fetch_time;
	}

	public String getCargo_price() {
		return cargo_price;
	}

	public void setCargo_price(String cargo_price) {
		this.cargo_price = cargo_price;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getFetch_buyer_fee() {
		return fetch_buyer_fee;
	}

	public void setFetch_buyer_fee(String fetch_buyer_fee) {
		this.fetch_buyer_fee = fetch_buyer_fee;
	}

	public String getHandover_fee() {
		return handover_fee;
	}

	public void setHandover_fee(String handover_fee) {
		this.handover_fee = handover_fee;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIs_booking() {
		return is_booking;
	}

	public void setIs_booking(String is_booking) {
		this.is_booking = is_booking;
	}

	public String getPay_shipper_fee() {
		return pay_shipper_fee;
	}

	public void setPay_shipper_fee(String pay_shipper_fee) {
		this.pay_shipper_fee = pay_shipper_fee;
	}

	public int getPayment_status() {
		return payment_status;
	}

	public void setPayment_status(int payment_status) {
		this.payment_status = payment_status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getShipper_address() {
		return shipper_address;
	}

	public void setShipper_address(String shipper_address) {
		this.shipper_address = shipper_address;
	}

	public String getShipper_name() {
		return shipper_name;
	}

	public void setShipper_name(String shipper_name) {
		this.shipper_name = shipper_name;
	}

	public String getShipper_phone() {
		return shipper_phone;
	}

	public void setShipper_phone(String shipper_phone) {
		this.shipper_phone = shipper_phone;
	}

	public String getShipper_x() {
		return shipper_x;
	}

	public void setShipper_x(String shipper_x) {
		this.shipper_x = shipper_x;
	}

	public String getShipper_y() {
		return shipper_y;
	}

	public void setShipper_y(String shipper_y) {
		this.shipper_y = shipper_y;
	}

	public String getWaybill_status() {
		return waybill_status;
	}

	public void setWaybill_status(String waybill_status) {
		this.waybill_status = waybill_status;
	}

	@Override
	public String toString() {
		return "OrderList [booking_fetch_time=" + booking_fetch_time
				+ ", cargo_price=" + cargo_price + ", create_time="
				+ create_time + ", fetch_buyer_fee=" + fetch_buyer_fee
				+ ", handover_fee=" + handover_fee + ", id=" + id
				+ ", is_booking=" + is_booking + ", pay_shipper_fee="
				+ pay_shipper_fee + ", payment_status=" + payment_status
				+ ", remarks=" + remarks + ", shipper_address="
				+ shipper_address + ", shipper_name=" + shipper_name
				+ ", shipper_phone=" + shipper_phone + ", shipper_x="
				+ shipper_x + ", shipper_y=" + shipper_y + ", waybill_status="
				+ waybill_status + "]";
	}

}
