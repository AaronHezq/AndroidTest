package com.example.androidtest.webview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.androidtest.R;


public class FileWebViewActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filewebview_activity);
		WebView webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient() {
			// Android > 4.1.1 调用这个方法
			
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
				System.out.println("========1");
				ValueCallback<Uri> mUploadMessage = uploadMsg;
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"), 1);
			}

			// 3.0 + 调用这个方法
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
				System.out.println("========2");
				ValueCallback<Uri> mUploadMessage = uploadMsg;
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"), 1);
			}

			// Android < 3.0 调用这个方法
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				System.out.println("========3");
				ValueCallback<Uri> mUploadMessage = uploadMsg;
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"), 1);

			}
		});
		webView.loadUrl("http://demo2.dfc.cn/activity/primary/index.html");
	}




}
