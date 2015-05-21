package com.example.androidtest.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.androidtest.R;


public class FileWebViewActivity extends Activity {
	WebView webView;
	ValueCallback<Uri> mUploadMessage;
	ValueCallback<Uri[]> mUploadMessages;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filewebview_activity);
		webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient() {
			// Android > 4.1.1 调用这个方法
			
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
				System.out.println("========1");
				mUploadMessage = uploadMsg;
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"), 1);
			}

			// 3.0 + 调用这个方法
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
				System.out.println("========2");
				mUploadMessage = uploadMsg;
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"), 1);
			}

			// Android < 3.0 调用这个方法
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				System.out.println("========3");
				mUploadMessage = uploadMsg;
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"), 1);
			}
			@SuppressLint("NewApi") @Override
			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
					FileChooserParams fileChooserParams) {
				System.out.println("========4");
				mUploadMessages = filePathCallback;
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"), 2);
				return true;
			}
		});
		webView.loadUrl("http://192.168.1.58:3000/test.html");
		//webView.loadUrl("file:///android_asset/test.html");
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 1) { 
	        if (null == mUploadMessage) 
	            return; 
	        Uri result = intent == null || resultCode != RESULT_OK ? null 
	                : intent.getData();
	        mUploadMessage.onReceiveValue(result); 
	        mUploadMessage = null;
	    }else if(requestCode == 2) { 
	        if (null == mUploadMessages) 
	            return; 
	        Uri result = intent == null || resultCode != RESULT_OK ? null 
	                : intent.getData();
	        System.out.println(result.getPath());
	        System.out.println(result.getPort());
	        mUploadMessages.onReceiveValue(new Uri[]{result}); 
	    }
		System.out.println("》》》》》onActivityResult");
		super.onActivityResult(requestCode, resultCode, intent);
	}



}
