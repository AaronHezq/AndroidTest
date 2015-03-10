package com.example.androidtest.listview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;

import com.example.androidtest.R;

/**
 * AsyncTask Test
 * 
 * @author hzq
 * 
 */
@SuppressLint("NewApi")
public class ListViewActivity extends Activity {

	ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_main);
		listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(new ListViewAdapter(this));
	}

}
