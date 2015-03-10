package com.example.androidtest.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.R.integer;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.dd.CircularProgressButton;
import com.example.androidtest.R;

/**
 * AsyncTask Test
 * 
 * @author hzq
 * 
 */
@SuppressLint("NewApi") 
public class TaskActivity extends Activity {

	CircularProgressButton progressBut;
	ImageView imageView;
	String img_path = "http://c.hiphotos.baidu.com/image/w%3D1366%3Bcrop%3D0%2C0%2C1366%2C768/sign=909f560278f40ad115e4c3e0611a2abc/72f082025aafa40fd11fafe7a864034f78f0192a.jpg";
	
	Bitmap bitmap = null;
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(msg.what==0) {//下载中
				if(msg.obj != null) {
					progressBut.setProgress((Integer)msg.obj);
				}
			}
			if(msg.what==1) {//下载完
				if(bitmap != null) {
//					imageView.setImageBitmap((Bitmap)msg.obj);
//					simulateSuccessProgress(progressBut);
					imageView.setImageBitmap(bitmap);
				}
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_main);
		imageView = (ImageView) findViewById(R.id.task_img);
		
		progressBut = (CircularProgressButton) findViewById(R.id.task_download_progressebutton);
		progressBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	//new MyTask().execute(img_path);
            	new Thread() {
            		public void run() {
            			
            			HttpClient httpClient = new DefaultHttpClient();
            			HttpGet httpGet = new HttpGet(img_path);
            			InputStream inputStream = null;
            			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            			try {
            				HttpResponse response = httpClient.execute(httpGet);
//            				bitmap = BitmapFactory.decodeStream(response.getEntity().getContent());
            				inputStream = response.getEntity().getContent();
            				long length = response.getEntity().getContentLength();
            				int len = 0;
            				byte[] data = new byte[1024];
            				int total_length = 0;
            				while ((len = inputStream.read(data)) != -1) {
            					total_length += len;
            					int value = (int)((total_length/(float)length) *100);
            					Message msg = Message.obtain();
            					msg.what = 0;
            					msg.obj = value;
            					handler.sendMessage(msg);
            					outputStream.write(data, 0, len);
            				}
            			} catch (IOException e) {
            				e.printStackTrace();
            			}finally {
            				if(inputStream !=null) {
            					try {
            						inputStream.close();
            					} catch (IOException e) {
            						e.printStackTrace();
            					}
            				}
            			}
            			byte[] result = outputStream.toByteArray();
            			bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
            			
            			Message msg = Message.obtain();
    					msg.what = 1;
    					handler.sendMessage(msg);
            		};
            	}.start();
            }
        });
	}
	
	private class MyTask extends AsyncTask<String, Integer, Bitmap> {
		
		
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = null;
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(params[0]);
			InputStream inputStream = null;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			try {
				HttpResponse response = httpClient.execute(httpGet);
//				bitmap = BitmapFactory.decodeStream(response.getEntity().getContent());
				inputStream = response.getEntity().getContent();
				long length = response.getEntity().getContentLength();
				int len = 0;
				byte[] data = new byte[1024];
				int total_length = 0;
				while ((len = inputStream.read(data)) != -1) {
					total_length += len;
					int value = (int)((total_length/(float)length) *100);
					publishProgress(value);
					outputStream.write(data, 0, len);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				if(inputStream !=null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			byte[] result = outputStream.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
			return bitmap;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			progressBut.setProgress(values[0]);
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			imageView.setImageBitmap(result);
			simulateSuccessProgress(progressBut);
		}
	}
	
	
	private void simulateSuccessProgress(final CircularProgressButton button) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 100);
        widthAnimation.setDuration(300);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
            }
        });
        widthAnimation.start();
    }

	
	
	
    private void simulateErrorProgress(final CircularProgressButton button) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 99);
        widthAnimation.setDuration(1500);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
                if (value == 99) {
                    button.setProgress(-1);
                }
            }
        });
        widthAnimation.start();
    }

}
