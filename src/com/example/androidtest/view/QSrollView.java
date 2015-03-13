package com.example.androidtest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/*
 * @FileName��QSrollView.java
 * @Version��V1.0
 * @Date: 2015-2-1 Create
 * @author: edsheng
 * */
public class QSrollView extends ViewGroup {
	public final static String TAG = QSrollView.class.getSimpleName();
	public final static int TOUCH_STATE_SROLLING = 1; // ��ǰ�ڻ���״̬
	public final static int TOUCH_STATE_FLING = 2; // ��ǰfling״̬
	public final static int TOUCH_STATE_DEFALUT = 0; // Ĭ��

	private int mTouchState = TOUCH_STATE_DEFALUT;
	private int mTouchSlop = 0; // ��ǰ������ֵ

	private int mLastMontionY; // ��¼�ϴ�y��λ��

	Scroller mScroller; // ����������

	private int mTotalLength = 0; // �����ؼ��ĳ���
	private int mMaxmumVelocity = 0; // Velocity�ķ�ֵ
	private VelocityTracker mVelocityTracker; // Velocity

	int mPointID = 0; // pointID

	public QSrollView(Context context) {
		super(context);
		init();
	}

	public QSrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mScroller = new Scroller(getContext());
		mTouchSlop = ViewConfiguration.getTouchSlop();
		mMaxmumVelocity = ViewConfiguration.getMaximumFlingVelocity();
	}

	@Override
	public void scrollBy(int x, int y) {
		// �жϵ�ǰ��ͼ�Ƿ񳬹��˶������߶��������������ľ���Ϊ1/3��������Խ��Խ��������Ч��
		if (getScrollY() < 0 || getScrollY() + getHeight() > mTotalLength) {
			super.scrollBy(x, y / 3);
		} else {
			super.scrollBy(x, y);
		}
	}

	/**
	 * �¼�����
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		// ��ʾ�Ѿ���ʼ�����ˣ�����Ҫ�߸�Action_MOVE������(��һ��ʱ���ܵ���)��
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_DEFALUT)) {
			return true;
		}
		int y = (int) ev.getY();
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(mLastMontionY - y);
			// ��������С��������
			if (xDiff > mTouchSlop) {
				mTouchState = TOUCH_STATE_SROLLING;
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mPointID = ev.getPointerId(ev.getActionIndex()); // ��¼��ǰpointID
			break;
		case MotionEvent.ACTION_DOWN:
			mLastMontionY = y;
			Log.e(TAG, mScroller.isFinished() + "");
			if (!mScroller.isFinished()) // ��������û�н�����ʱ��ǿ�ƽ���
			{
				mScroller.abortAnimation();
				mScroller.forceFinished(true);
			}
			mTouchState = TOUCH_STATE_DEFALUT;
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_DEFALUT;
			break;
		}
		Log.e(TAG, mTouchState + "====" + TOUCH_STATE_DEFALUT);
		return mTouchState != TOUCH_STATE_DEFALUT;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int touchIndex = event.getActionIndex();
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mPointID = event.getPointerId(0);
			mLastMontionY = (int) event.getY();// ��¼���µĵ�
			break;
		case MotionEvent.ACTION_POINTER_DOWN: // ��Ӷ�㴥�صĴ���
			mPointID = event.getPointerId(touchIndex);
			mLastMontionY = (int) (event.getY(touchIndex) + 0.5f); // ��¼���µĵ�
			break;

		case MotionEvent.ACTION_MOVE:
			touchIndex = event.findPointerIndex(mPointID);
			if (touchIndex < 0) // ��ǰindexС��0�ͷ�false����������һ���¼�
				return false;
			int detaY = (int) (mLastMontionY - event.getY(touchIndex)); // ���㻬���ľ���
			scrollBy(0, detaY); // ���û�������
			mLastMontionY = (int) event.getY(touchIndex); // ��¼��һ�ΰ��µĵ�
			break;
		case MotionEvent.ACTION_UP:
			Log.d("edsheng", "Action UP");
			mVelocityTracker.computeCurrentVelocity(1000);
			if (Math.abs(mVelocityTracker.getYVelocity()) > mMaxmumVelocity && !checkIsBroad()) {
				mScroller.fling(getScrollX(), getScrollY(), 0, -(int) mVelocityTracker.getYVelocity(), 0, 0, 0, mTotalLength
						- getHeight());
			} else {
				actionUP(); // �ص�Ч��
			}

			mTouchState = TOUCH_STATE_DEFALUT;

			break;
		case MotionEvent.ACTION_POINTER_UP:
			// ��Ӷ�㴥�ص�֧��
			if (event.getPointerId(touchIndex) == mPointID) {
				final int newIndex = touchIndex == 0 ? 1 : 0;
				mPointID = event.getPointerId(newIndex);
				mLastMontionY = (int) (event.getY(newIndex) + 0.5f);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_DEFALUT;
			break;
		default:
			break;
		}
		// super.onTouchEvent(event);
		return true;
	}

	/**
	 * �ص�����
	 */
	private void actionUP() {
		if (getScrollY() < 0 || getHeight() > mTotalLength) // �����ص�
		{
			Log.d("edsheng", "�����ص���������");
			mScroller.startScroll(0, getScrollY(), 0, -getScrollY()); // �����ص�Ч��
			invalidate();
		} else if (getScrollY() + getHeight() > mTotalLength) // �ײ��ص�
		{
			// �����ײ��ص�
			mScroller.startScroll(0, getScrollY(), 0, -(getScrollY() + getHeight() - mTotalLength));
			invalidate();
		}
	}

	/***
	 * ��⵱ǰ�Ƿ�ɻص�
	 * 
	 * @return
	 */
	boolean checkIsBroad() {
		if (getScrollY() < 0 || getScrollY() + getHeight() > mTotalLength) // �����ص�)
			// //�����ص�
			return true;
		else
			return false;
	}

	/**
	 * ��дonMeasure��������
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int size = getChildCount();
		final int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);
		final int paretnHeightSize = MeasureSpec.getSize(heightMeasureSpec);
		for (int i = 0; i < size; ++i) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				LayoutParams childLp = child.getLayoutParams();
				final boolean childWidthWC = childLp.width == LayoutParams.WRAP_CONTENT;
				final boolean childHeightWC = childLp.height == LayoutParams.WRAP_CONTENT;
				int childWidthMeasureSpec;
				int childHeightMeasureSpec;
				if (child.getLayoutParams() instanceof MarginLayoutParams) {
					MarginLayoutParams childMarginLp = (MarginLayoutParams) childLp;
					childWidthMeasureSpec = childWidthWC ? MeasureSpec.makeMeasureSpec(parentWidthSize, MeasureSpec.UNSPECIFIED)
							: getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight()
									+ childMarginLp.leftMargin + childMarginLp.rightMargin, childLp.width);
					childHeightMeasureSpec = childHeightWC ? MeasureSpec.makeMeasureSpec(paretnHeightSize,
							MeasureSpec.UNSPECIFIED) : getChildMeasureSpec(heightMeasureSpec, getPaddingTop()
							+ getPaddingBottom() + childMarginLp.topMargin + childMarginLp.bottomMargin, childMarginLp.height);
				} else {
					childWidthMeasureSpec = childWidthWC ? MeasureSpec.makeMeasureSpec(parentWidthSize, MeasureSpec.UNSPECIFIED)
							: getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), childLp.width);
					childHeightMeasureSpec = childHeightWC ? MeasureSpec.makeMeasureSpec(paretnHeightSize,
							MeasureSpec.UNSPECIFIED) : getChildMeasureSpec(heightMeasureSpec, getPaddingTop()
							+ getPaddingBottom(), childLp.height);
				}
				child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
			}
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/***
	 * ��дlayout����
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		int childStartPostion = 0;
		mTotalLength = 0;
		final int count = getChildCount();
		if (count == 0) {
			return;
		}
		childStartPostion = getPaddingTop();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child != null && child.getVisibility() != View.GONE) {
				LayoutParams lp = child.getLayoutParams();
				final int childHeight = child.getMeasuredHeight();
				int leftMargin = 0;
				int rightMargin = 0;
				int topMargin = 0;
				int bottomMargin = 0;
				if (lp instanceof MarginLayoutParams) {
					MarginLayoutParams mlp = (MarginLayoutParams) lp;
					leftMargin = mlp.leftMargin;
					rightMargin = mlp.rightMargin;
					topMargin = mlp.topMargin;
					bottomMargin = mlp.bottomMargin;
				}

				childStartPostion += topMargin;
				int startX = (getWidth() - leftMargin - rightMargin - child.getMeasuredWidth()) / 2 + leftMargin;
				child.layout(startX, childStartPostion, startX + child.getMeasuredWidth(), childStartPostion + childHeight);
				childStartPostion += (childHeight + bottomMargin);
			}
		}
		childStartPostion += getPaddingBottom();
		mTotalLength = childStartPostion;

	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) // ���㵱ǰλ��
		{
			// ����
			scrollTo(0, mScroller.getCurrY());
			postInvalidate();
		}
	}
}
