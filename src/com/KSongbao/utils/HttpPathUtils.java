package com.KSongbao.utils;

public class HttpPathUtils {
	// public static final String HOST = "http://123.57.239.10:8080";
	public static String HOST = "http://open.3gongli.com";
	public static String sp_save_order = HOST + "/mobile/sp_save_order";
	public static String sp_mdpwd = HOST + "/mobile/sp_mdpwd";
	public static String sp_address = HOST + "/mobile/sp_setaddress";

	public static String query_sp_waybill = HOST + "/mobile/query_sp_waybill";
	public static String sp_today_count = HOST + "/mobile/sp_today_count";
	public static String shipper_checkversion = HOST + "/api/sp_version";
	public static String shipper_download_newVersion = HOST
			+ "/api/download/shippers_app?v=";

	public static String shipper_cencle_order = HOST
			+ "/mobile/sp_handle_waybill";
	public static String statistic_total = HOST + "/mobile/count_sp_waybill";
}
