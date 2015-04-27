package com.example.androidtest.spring;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidtest.R;

public class SpringActivity extends Activity {

	private EditText edit_username, edit_pass;
	private Button loginbtn;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spring_main);
		edit_username = (EditText) findViewById(R.id.username);
		edit_pass = (EditText) findViewById(R.id.pass);
		loginbtn = (Button) findViewById(R.id.login_btn);


		loginbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				HttpTool.post("http://192.168.1.138:8080/SpringMvc/test/login", params, new HttpCallBack() {
					
					@Override
					public void success(String value, int code) {
						System.out.println("success:" + value);
						Toast.makeText(SpringActivity.this, "success:" + value, Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void failure(String value, int code) {
						System.out.println("failure:" + value);
						System.out.println("code:" + code);
						Toast.makeText(SpringActivity.this, "code:"+code+"\nfailure:" + value, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

	}
}