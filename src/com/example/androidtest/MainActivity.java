package com.example.androidtest;

import io.vov.vitamio.demo.VideoViewDemo2;
import io.vov.vitamio.demo.VitamioListActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import refresh.rentals.PullToRefreshActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.androidtest.handlertest.HandlerTestActivity;
import com.example.androidtest.listview.ListViewActivity;
import com.example.androidtest.opengl.ParticleOpenglActivity;
import com.example.androidtest.progress.MyProgressActivity;
import com.example.androidtest.pulltorefresh.PullRefreshTestActivity;
import com.example.androidtest.pullzool.PullMainActivity;
import com.example.androidtest.residemenu.ResideMenu;
import com.example.androidtest.residemenu.ResideMenu.OnMenuListener;
import com.example.androidtest.selectcity.CityList;
import com.example.androidtest.task.TaskActivity;

/**
 * 
 * @author hzq
 * 
 */
public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setMenu();
	}

	private ResideMenu resideMenu;

	private void setMenu() {
		resideMenu = new ResideMenu(this);
		resideMenu.attachToActivity(this);
		resideMenu.setMenuListener(menuListener);
		resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
		resideMenu.setScaleValue(0.5f);
	}

	private OnMenuListener menuListener = new OnMenuListener() {
		@Override
		public void openMenu() {
		}

		@Override
		public void closeMenu() {

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.select_city) {
			startActivity(new Intent(MainActivity.this, CityList.class));
			return true;
		}
		if (id == R.id.progress) {
			startActivity(new Intent(MainActivity.this, MyProgressActivity.class));
			return true;
		}
		if (id == R.id.handlertest) {
			startActivity(new Intent(MainActivity.this, HandlerTestActivity.class));
			return true;
		}
		if (id == R.id.tasktest) {
			startActivity(new Intent(MainActivity.this, TaskActivity.class));
			return true;
		}
		if (id == R.id.listviewtest) {
			startActivity(new Intent(MainActivity.this, ListViewActivity.class));
			return true;
		}
		if (id == R.id.action_settings) {
			startActivity(new Intent(MainActivity.this, PullMainActivity.class));
			return true;
		}
		if (id == R.id.vitamio) {
			startActivity(new Intent(MainActivity.this, VitamioListActivity.class));
			return true;
		}
		if (id == R.id.opengl) {
			startActivity(new Intent(MainActivity.this, ParticleOpenglActivity.class));
			return true;
		}
		if (id == R.id.pulltorefresh) {
			startActivity(new Intent(MainActivity.this, PullToRefreshActivity.class));
			return true;
		}
		if (id == R.id.pullrefreshtest) {
			startActivity(new Intent(MainActivity.this, PullRefreshTestActivity.class));
			return true;
		}
		if (id == R.id.VideoViewDemo2) {
			startActivity(new Intent(MainActivity.this, VideoViewDemo2.class));
			return true;
		}
		return super.onOptionsItemSelected(item);

	}

	public void postTest(View view) {
		postTest();
	}

	public <T> void postTest() {
		// String url = "http://192.168.1.217:3000/v1/homes/basic_data";
		// String url = "http://demo2.dfc.cn/sjll/v1/homes/basic_data";
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new BasicNameValuePair("uni_code", "23737485393442");
		// params.add(new BasicNameValuePair("area_id", "2");
		// HttpUtils http = new HttpUtils();
		// http.configSoTimeout(5 * 1000);
		// http.send(HttpMethod.POST, url, params, new HttpCallBack() {
		//
		// @Override
		// public void success(String content, int code) {
		// System.out.println("正确：" + content);
		// }
		//
		// @Override
		// public void onFailure(HttpException error, String msg) {
		// System.out.println("错误：" + error.getMessage());
		// }
		// });
		sendPost();
	}

	public void sendPost() {
		new Thread() {
			public void run() {
				String result = "";
				String url = "http://demo2.dfc.cn/sjll/v1/homes/basic_data";
				// HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("uni_code", "23737485393442"));
				params.add(new BasicNameValuePair("area_id", "2"));

				try {
					httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						HttpEntity httpEntity = httpResponse.getEntity();
						result = EntityUtils.toString(httpEntity);// 取出应答字符串
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					result = e.getMessage().toString();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					result = e.getMessage().toString();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					result = e.getMessage().toString();
				}
				System.out.println("result:" + result);
			};
		}.start();
	}

}
