/*
 * Copyright (C) 2013 yixia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vov.vitamio.demo;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidtest.R;

public class VideoViewDemo extends Activity {

	/**
	 * TODO: Set the path variable to a streaming video URL or a local media
	 * file path.
	 */
	private String path = "http://shbcwl-m.qiniudn.com/tqyNH6kFDSN21GTyaiE1l3sQPTc=/lhCzZQTFSWkmRpfGKjwS_k87FRVN";
	private VideoView mVideoView;
	private EditText mEditText;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		setContentView(R.layout.videoview);
		mEditText = (EditText) findViewById(R.id.url);
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		if (path == "") {
			// Tell the user to provide a media file URL/path.
			Toast.makeText(VideoViewDemo.this,
					"Please edit VideoViewDemo Activity, and set path" + " variable to your media file URL/path",
					Toast.LENGTH_LONG).show();
			return;
		} else {
			/*
			 * Alternatively,for streaming media you can use
			 * mVideoView.setVideoURI(Uri.parse(URLstring));
			 */
			mVideoView.setVideoPath(path);
			mVideoView.setMediaController(new MediaController(this));
			mVideoView.requestFocus();

			mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mediaPlayer) {
					mediaPlayer.setPlaybackSpeed(1.0f);
				}
			});
		}

	}

	public void startPlay(View view) {
		String url = mEditText.getText().toString();
		path = url;
		if (!TextUtils.isEmpty(url)) {
			mVideoView.setVideoPath(url);
		}
	}

	int mVideoLayout = 0;

	public void openVideo(View View) {
		// mVideoView.setVideoPath(path);
		// mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
		mVideoLayout++;
		if (mVideoLayout == 2) {
			mVideoLayout = 0;
		}
		switch (mVideoLayout) {
		case 0:// 原始画面大小
			mVideoLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
			break;
		case 1:// 画面全屏
			mVideoLayout = VideoView.VIDEO_LAYOUT_SCALE;
			break;
		case 2:// 画面拉伸
			mVideoLayout = VideoView.VIDEO_LAYOUT_STRETCH;
			break;
		case 3:// 画面裁剪
			mVideoLayout = VideoView.VIDEO_LAYOUT_ZOOM;
			break;
		}
		if (mVideoLayout == 0) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		fullScreenChange();
		mVideoView.setVideoLayout(mVideoLayout, 0);
	}

	/**
	 * 全屏切换
	 */
	public void fullScreenChange() {
		SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean fullScreen = mPreferences.getBoolean("fullScreen", false);
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		System.out.println("fullScreen的值:" + fullScreen);
		if (fullScreen) {
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().setAttributes(attrs);
			// 取消全屏设置
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			mPreferences.edit().putBoolean("fullScreen", false).commit();
		} else {
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			getWindow().setAttributes(attrs);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			mPreferences.edit().putBoolean("fullScreen", true).commit();
		}
	}

}
