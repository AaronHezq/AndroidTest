package com.example.androidtest.pulltorefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

public class CustomFrameLayout extends FrameLayout {

	private float mLastMotionX, mLastMotionY;//记录手指触摸的位置X，Y坐标
	private float mDeltaX, mDeltaY;//记录当前手指拉动的X和Y偏移量
	
	private ScrollToHomeRunnable mScrollToHomeRunnable;//用来从偏移点回到原点的
	
	//当前出于什么状态：正在刷新？水平拉动？垂直拉动？正常状态？
	private enum State{
		REFRESHING,
		PULLING_HORIZONTAL,
		PULLING_VERTICAL,
		NORMAL,
	}
	
	//记录拉动的方向：水平？垂直？
	private enum Orientation{
		HORIZONTAL,
		VERTICAL
	}
	
	private State mState; //当前状态
	private Orientation mOrientation;//当前拉动方向
	
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
	
	//初始化方法，初始化的时候只需要将mState 设置为 NORMAL状态即可。
	private void init(Context context){
		mState = State.NORMAL;
	}

	/**
	 * 重写onTouchEvent方法，检测用户滑动的距离，方向等，
	 * 然后调用scrollTo来让整个View偏移，这可谓是核心代码了
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mState == State.REFRESHING){
			return true;
		}
		
		int action = event.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			//记录X，Y坐标，恢复mDeltaX和mDeltaY为0
			mLastMotionX = event.getX();
			mLastMotionY = event.getY();
			mDeltaY = .0F;
			mDeltaX = .0F;
			break;
			
		case MotionEvent.ACTION_MOVE:
			/**
		     * 根据当前触摸点 - 上次记录的x或者y坐标，得到增量，然后应用到scrollTo方法上去，
		     * 然后重新记录x，y坐标
		     */
			float innerDeltaY = event.getY() - mLastMotionY;//记录Y的差值
			float innerDeltaX = event.getX() - mLastMotionX;//记录X的差值
			float absInnerDeltaY = Math.abs(innerDeltaY);//Y差值绝对值
			float absInnerDeltaX = Math.abs(innerDeltaX);//X差值绝对值
			//当Y差值绝对值 大于 X差值绝对值的时候，我们可以认为用户正在上下滑动
			if(absInnerDeltaY > absInnerDeltaX && mState != State.PULLING_HORIZONTAL){
				mOrientation = Orientation.VERTICAL;//将当前滑动方向置为垂直滑动
				mState = State.PULLING_VERTICAL;//滑动状态：正在垂直拉动
				if(innerDeltaY > 1.0F){ //innerDeltaY为正数，用户正在向下拉动，1.0F可看做阈值，下面类似
					mDeltaY -= absInnerDeltaY;//注意这个地方是-=，即累减的过程
					pull(mDeltaY);
				}else if(innerDeltaY < -1.0F){//innerDeltaY为负数，用户正在向上拉动
					mDeltaY += absInnerDeltaY;//累加
					pull(mDeltaY);
				}//下面的代码是水平滑动
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
			//重新记录新的坐标值
			mLastMotionX = event.getX();
			mLastMotionY = event.getY();
			break;
			
		case MotionEvent.ACTION_UP:
			/**
		     * 用户松开手指之后，View自动回到偏移量为0的位置
		     */
			switch(mOrientation){//根据ACTION_MOVE的时候所确定的方向开始判断
			case VERTICAL:
				smoothScrollTo(mDeltaY);//垂直拉动，让View重新mDeltaY，重新回到Y的原点
				break;
				
			case HORIZONTAL:
				smoothScrollTo(mDeltaX);//如果水平拉动，让View重新滑动mDeltaX，重新回到X的原点
				break;
				
			default:
				break;
			}
			break;
		}
		return true;
	}
	
	private void pull(float diff){
		int value = Math.round(diff / 2.0F);//diff就是偏移量，除以2.0相当于一个缩放
		if(mOrientation == Orientation.VERTICAL){
			scrollTo(0, value);//注意这里是核心了，Y方向上移动value距离，X方向上保持不变
		}else if(mOrientation == Orientation.HORIZONTAL){
			scrollTo(value, 0);//X方向上移动value距离，Y方向上保持不变
		}
	}
	
	private void smoothScrollTo(float diff){
		int value = Math.round(diff / 2.0F);
		mScrollToHomeRunnable = new ScrollToHomeRunnable(value, 0);
		mState = State.REFRESHING;//当前状态为正在刷新
		post(mScrollToHomeRunnable);//view自身有一个post方法，我们提交一个scrollTo的任务给它
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
					scrollTo(current, 0);//水平scroll
				}else if(mOrientation == Orientation.VERTICAL){
					scrollTo(0, current);//垂直scroll
				}
			}
			
			if(current != target){//没有回到原点：在经过16毫秒之后继续postDelayed这个任务
				postDelayed(this, 16);
			}else{
				mState = State.NORMAL;//回到原点，mState置为NORMAL状态
			}
		}
	}
}
