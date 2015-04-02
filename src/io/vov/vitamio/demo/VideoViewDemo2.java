package io.vov.vitamio.demo;


import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidtest.R;

/**
 * ������Ƶ
 * 
 * @author hzq
 * 
 */
public class VideoViewDemo2 extends Activity {

	private String path;
	private String title;
	private VideoView mVideoView;
	private long msec = 0;

	private TextView progressValueView;

	private AudioManager mAudioManager;
	/** ������� */
	private int mMaxVolume;
	/** ��ǰ���� */
	private int mVolume = -1;
	/** ��ǰ���� */
	private float mBrightness = -1f;
	/** ��ǰ����ģʽ */
	private int mLayout = VideoView.VIDEO_LAYOUT_ZOOM;
	private GestureDetector mGestureDetector;
	private View mVolumeBrightnessLayout;
	private ImageView mOperationBg;
	private ImageView mOperationPercent;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		if (icicle != null) {  
			msec = icicle.getLong("msec");
        } 
		path = "http://shbcwl-m.qiniudn.com/jx0zQwki0sgJ6dv95KFlO01bfhQ=/ljghOhIC23fMrItEgp553eSgRNEB";
		title = getIntent().getStringExtra("title");
		setContentView(R.layout.activity_videoplay);
		progressValueView = (TextView) findViewById(R.id.videoplay_progress_value);
		if (path == null || path.equals("") || path.equals("null")) {
			finish();
		} else {
			mVideoView = (VideoView) findViewById(R.id.videoplay_videoview);
			mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
			mOperationBg = (ImageView) findViewById(R.id.operation_bg);
			mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
			mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			mVideoView.setVideoPath(path);
			mVideoView.seekTo(msec);
			MediaController mediaController = new MediaController(this);
			mVideoView.setMediaController(mediaController);
			mVideoView.setFileName(title);
			mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mediaPlayer) {
					mediaPlayer.setPlaybackSpeed(1.0f);
				}
			});
			mVideoView.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					progressValueView.setText("��Ƶ����ʧ��");
					return false;
				}
			});
			mVideoView.setOnInfoListener(new OnInfoListener() {
				@Override
				public boolean onInfo(MediaPlayer mp, int what, int extra) {
					mp.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
						@Override
						public void onBufferingUpdate(MediaPlayer mp, int percent) {
							if (mp.isPlaying()) {
								progressValueView.setVisibility(View.GONE);
							} else if (mp.isBuffering()) {
								//progressValueView.setText("����" + percent + "%");
								progressValueView.setVisibility(View.GONE);
							} else {
								progressValueView.setVisibility(View.GONE);
							}
						}
					});
					return false;
				}
			});
			mGestureDetector = new GestureDetector(this, new MyGestureListener());
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("msec", mVideoView.getCurrentPosition());
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		try {
			super.onRestoreInstanceState(savedInstanceState);
		} catch (Exception e) {
			e.printStackTrace();
		}
		savedInstanceState = null;
	}

	@Override
	protected void onResume() {
		mVideoView.start();
		if (mVideoView.isPlaying()) {
			progressValueView.setVisibility(View.GONE);
		}
		// �л���Ļ����ᵼ��activity�Ĵݻٺ��ؽ�  
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {  
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  
        }  
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mVideoView != null) {
			mVideoView.pause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mVideoView != null) {
			mVideoView.destroyDrawingCache();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event))
			return true;
		// �������ƽ���
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			endGesture();
			break;
		}

		return super.onTouchEvent(event);
	}

	/** ���ƽ��� */
	private void endGesture() {
		mVolume = -1;
		mBrightness = -1f;
		// ����
		mDismissHandler.removeMessages(0);
		mDismissHandler.sendEmptyMessageDelayed(0, 500);
	}

	/** ��ʱ���� */
	private Handler mDismissHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mVolumeBrightnessLayout.setVisibility(View.GONE);
		}
	};

	private class MyGestureListener extends SimpleOnGestureListener {

		/** ˫�� */
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (mLayout == VideoView.VIDEO_LAYOUT_ZOOM)
				mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
			else
				mLayout++;
			if (mVideoView != null)
				mVideoView.setVideoLayout(mLayout, 0);
			return true;
		}

		/** ���� */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			float mOldX = e1.getX(), mOldY = e1.getY();
			int y = (int) e2.getRawY();
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int windowWidth = dm.widthPixels;
			int windowHeight = dm.heightPixels;

			if (mOldX > windowWidth * 3.0 / 4)// �ұ߻���
				onVolumeSlide((mOldY - y) / windowHeight);
			else if (mOldX < windowWidth / 4.0)// ��߻���
				onBrightnessSlide((mOldY - y) / windowHeight);

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	/**
	 * �����ı�������С
	 * 
	 * @param percent
	 */
	private void onVolumeSlide(float percent) {
		if (mVolume == -1) {
			mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;

			// ��ʾ
			mOperationBg.setImageResource(R.drawable.video_volumn_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}

		int index = (int) (percent * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		// �������
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

		// ���������
		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = findViewById(R.id.operation_full).getLayoutParams().width * index / mMaxVolume;
		mOperationPercent.setLayoutParams(lp);
	}

	/**
	 * �����ı�����
	 * 
	 * @param percent
	 */
	private void onBrightnessSlide(float percent) {
		if (mBrightness < 0) {
			mBrightness = getWindow().getAttributes().screenBrightness;
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;
			// ��ʾ
			mOperationBg.setImageResource(R.drawable.video_brightness_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}
		WindowManager.LayoutParams lpa = getWindow().getAttributes();
		lpa.screenBrightness = mBrightness + percent;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		getWindow().setAttributes(lpa);

		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
		mOperationPercent.setLayoutParams(lp);
	}

}
