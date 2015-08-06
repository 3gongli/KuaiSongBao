package com.KSongbao.bean;

import java.util.List;

public class CityBean {
	public String errors;
	public boolean success;
	public List<City> obj;
	public String city_code;

	public class City {
		public String province;
		public List<String> city;
	}
}
