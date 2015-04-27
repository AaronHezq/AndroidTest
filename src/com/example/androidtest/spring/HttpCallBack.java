package com.example.androidtest.spring;

public interface HttpCallBack {
	public void success(String value,int code);
	public void failure(String value,int code);
}
