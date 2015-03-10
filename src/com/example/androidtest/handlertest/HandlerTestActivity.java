package com.example.androidtest.handlertest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class HandlerTestActivity extends Activity {
	
	
	private Handler handler2 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				System.out.println("msg2");
				break;
			}
		};
	};
	
	private Handler handler1 = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case 0:
//				System.out.println("msg1");
//				break;
//			}
//		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Message msg =Message.obtain(handler2, 0);
		//msg.setTarget(handler2);
		msg.sendToTarget();
		handler1.sendMessage(msg);
		
	}
}
