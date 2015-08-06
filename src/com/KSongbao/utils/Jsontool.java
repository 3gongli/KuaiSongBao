package com.KSongbao.utils;

public class Jsontool {
	public static String isnull(String strnull) {
		if (strnull.equals(null) || strnull.equals("")) {
			return "0";
		}
		return strnull;
	}
}
