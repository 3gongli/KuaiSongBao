package com.KSongbao.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class Httptool {

	public String httppost(String URL, List<NameValuePair> params)
			throws ConnectTimeoutException {

		HttpPost httpPost = new HttpPost(URL);
		// 配置Post请求参数。
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new
		// BasicNameValuePair("username",ET_Username.getText().toString()));
		// params.add(newBasicNameValuePair("pwd",ET_PWD.getText().toString()));
		// 设置字符集。

		try {

			HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			// 设置参数实体。
			httpPost.setEntity(entity);
			// 获取HttpClient对象及HttpResponse实例。
			HttpClient httpclient = new DefaultHttpClient();
			// 请求超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			// 读取超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 10000);
			try {
				HttpResponse httpResponse = httpclient.execute(httpPost);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String strResult = EntityUtils.toString(httpResponse
							.getEntity());
					return strResult;
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

}
