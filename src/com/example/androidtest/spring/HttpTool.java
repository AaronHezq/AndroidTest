package com.example.androidtest.spring;

import java.util.List;

import org.apache.http.NameValuePair;

public class HttpTool {
	
	public static void post(String url,List<NameValuePair> valuePairs,HttpCallBack httpCallBack) {
		Parameter params = new Parameter();
		params.url = url;
		params.valuePairs = valuePairs;
		new HttpTask(httpCallBack).execute(params);
	}
	
}
