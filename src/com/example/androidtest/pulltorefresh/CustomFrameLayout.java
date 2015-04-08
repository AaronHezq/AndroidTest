package com.example.androidtest.pulltorefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

public class CustomFrameLayout extends FrameLayout {

	private float mLastMotionX, mLastMotionY;//��¼��ָ������λ��X��Y����
	private float mDeltaX, mDeltaY;//��¼��ǰ��ָ������X��Yƫ����
	
	private ScrollToHomeRunnable mScrollToHomeRunnable;//������ƫ�Ƶ�ص�ԭ���
	
	//��ǰ����ʲô״̬������ˢ�£�ˮƽ��������ֱ����������״̬��
	private enum State{
		REFRESHING,
		PULLING_HORIZONTAL,
		PULLING_VERTICAL,
		NORMAL,
	}
	
	//��¼�����ķ���ˮƽ����ֱ��
	private enum Orientation{
		HORIZONTAL,
		VERTICAL
	}
	
	private State mState; //��ǰ״̬
	private Orientation mOrientation;//��ǰ��������
	
	@SuppressLint("NewApi")
	public CustomFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CustomFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CustomFrameLayout(Context context) {
		super(context);
		init(context);
	}
	
	//��ʼ����������ʼ����ʱ��ֻ��Ҫ��mState ����Ϊ NORMAL״̬���ɡ�
	private void init(Context context){
		mState = State.NORMAL;
	}

	/**
	 * ��дonTouchEvent����������û������ľ��룬����ȣ�
	 * Ȼ�����scrollTo��������Viewƫ�ƣ����ν�Ǻ��Ĵ�����
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mState == State.REFRESHING){
			return true;
		}
		
		int action = event.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			//��¼X��Y���꣬�ָ�mDeltaX��mDeltaYΪ0
			mLastMotionX = event.getX();
			mLastMotionY = event.getY();
			mDeltaY = .0F;
			mDeltaX = .0F;
			break;
			
		case MotionEvent.ACTION_MOVE:
			/**
		     * ���ݵ�ǰ������ - �ϴμ�¼��x����y���꣬�õ�������Ȼ��Ӧ�õ�scrollTo������ȥ��
		     * Ȼ�����¼�¼x��y����
		     */
			float innerDeltaY = event.getY() - mLastMotionY;//��¼Y�Ĳ�ֵ
			float innerDeltaX = event.getX() - mLastMotionX;//��¼X�Ĳ�ֵ
			float absInnerDeltaY = Math.abs(innerDeltaY);//Y��ֵ����ֵ
			float absInnerDeltaX = Math.abs(innerDeltaX);//X��ֵ����ֵ
			//��Y��ֵ����ֵ ���� X��ֵ����ֵ��ʱ�����ǿ�����Ϊ�û��������»���
			if(absInnerDeltaY > absInnerDeltaX && mState != State.PULLING_HORIZONTAL){
				mOrientation = Orientation.VERTICAL;//����ǰ����������Ϊ��ֱ����
				mState = State.PULLING_VERTICAL;//����״̬�����ڴ�ֱ����
				if(innerDeltaY > 1.0F){ //innerDeltaYΪ�������û���������������1.0F�ɿ�����ֵ����������
					mDeltaY -= absInnerDeltaY;//ע������ط���-=�����ۼ��Ĺ���
					pull(mDeltaY);
				}else if(innerDeltaY < -1.0F){//innerDeltaYΪ�������û�������������
					mDeltaY += absInnerDeltaY;//�ۼ�
					pull(mDeltaY);
				}//����Ĵ�����ˮƽ����
			}else if(absInnerDeltaY < absInnerDeltaX && mState != State.PULLING_VERTICAL){
				mOrientation = Orientation.HORIZONTAL;
				mState = State.PULLING_HORIZONTAL;
				if(innerDeltaX > 1.0F){
					mDeltaX -= absInnerDeltaX;
					pull(mDeltaX);
				}else if(innerDeltaX < -1.0F){
					mDeltaX += absInnerDeltaX;
					pull(mDeltaX);
				}
			}
			//���¼�¼�µ�����ֵ
			mLastMotionX = event.getX();
			mLastMotionY = event.getY();
			break;
			
		case MotionEvent.ACTION_UP:
			/**
		     * �û��ɿ���ָ֮��View�Զ��ص�ƫ����Ϊ0��λ��
		     */
			switch(mOrientation){//����ACTION_MOVE��ʱ����ȷ���ķ���ʼ�ж�
			case VERTICAL:
				smoothScrollTo(mDeltaY);//��ֱ��������View����mDeltaY�����»ص�Y��ԭ��
				break;
				
			case HORIZONTAL:
				smoothScrollTo(mDeltaX);//���ˮƽ��������View���»���mDeltaX�����»ص�X��ԭ��
				break;
				
			default:
				break;
			}
			break;
		}
		return true;
	}
	
	private void pull(float diff){
		int value = Math.round(diff / 2.0F);//diff����ƫ����������2.0�൱��һ������
		if(mOrientation == Orientation.VERTICAL){
			scrollTo(0, value);//ע�������Ǻ����ˣ�Y�������ƶ�value���룬X�����ϱ��ֲ���
		}else if(mOrientation == Orientation.HORIZONTAL){
			scrollTo(value, 0);//X�������ƶ�value���룬Y�����ϱ��ֲ���
		}
	}
	
	private void smoothScrollTo(float diff){
		int value = Math.round(diff / 2.0F);
		mScrollToHomeRunnable = new ScrollToHomeRunnable(value, 0);
		mState = State.REFRESHING;//��ǰ״̬Ϊ����ˢ��
		post(mScrollToHomeRunnable);//view������һ��post�����������ύһ��scrollTo���������
	}
	
	final class ScrollToHomeRunnable implements Runnable{
		
		private final Interpolator mInterpolator;
		private int target;
		private int current;
		private long mStartTime = -1;
		
		public ScrollToHomeRunnable(int current, int target){
			this.target = target;
			this.current = current;
			mInterpolator = new DecelerateInterpolator();
		}
		
		@Override
		public void run() {
			if(mStartTime == -1){
				mStartTime = System.currentTimeMillis();
			}else{
				long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) / 200;
				normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);
				final int delta = Math.round((current - target)
						* mInterpolator.getInterpolation(normalizedTime / 1000f));
				
				current = current - delta;				
				if(mOrientation == Orientation.HORIZONTAL){
					scrollTo(current, 0);//ˮƽscroll
				}else if(mOrientation == Orientation.VERTICAL){
					scrollTo(0, current);//��ֱscroll
				}
			}
			
			if(current != target){//û�лص�ԭ�㣺�ھ���16����֮�����postDelayed�������
				postDelayed(this, 16);
			}else{
				mState = State.NORMAL;//�ص�ԭ�㣬mState��ΪNORMAL״̬
			}
		}
	}
}
