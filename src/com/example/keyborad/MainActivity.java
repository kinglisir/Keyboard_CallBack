package com.example.keyborad;
import java.lang.reflect.Field;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	private LinearLayout ll_root;//activate的跟布局
	// 软键盘的高度
	private int keyboardHeight;
	private int statusBarHeight;//状态栏的高度
	// 软键盘的显示状态
	private boolean isShowKeyboard;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		statusBarHeight = getStatusBarHeight();
		//给activity最外层的布局设置一个  布局完成监听
		ll_root.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
	}

	private void initView() {
		ll_root = (LinearLayout) findViewById(R.id.ll_root);
	}

	/**获取状态栏高度, 
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	public  int getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	//布局完成监听
	private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
		//布局完成时会调用该方法
		public void onGlobalLayout() {
			// 应用可以显示的区域。此处包括应用占用的区域，
						// 以及ActionBar和状态栏，但不含设备底部的虚拟按键。
						Rect r = new Rect();
						ll_root.getWindowVisibleDisplayFrame(r);// activity布局的最外层layout,
						// ActionBar和状态栏，但不含设备底部的虚拟按键。

						// 再通过调用getRootView().getHeight()，获取整个屏幕的高度。同样，这个高度也不含虚拟按键的高度。
						int screenHeight = ll_root.getRootView().getHeight();
						// 最外层layout和整个屏幕的高度, 通过比较这两个高度，可以推断出软键盘是否显示，并且获取到软键盘的高度
						int heightDiff = screenHeight - (r.bottom - r.top);

						// 在不显示软键盘时，heightDiff等于状态栏的高度
						// 在显示软键盘时，heightDiff会变大，等于软键盘加状态栏的高度。
						// 所以heightDiff大于状态栏高度时表示软键盘出现了，
						// 这时可算出软键盘的高度，即heightDiff减去状态栏的高度
						if (keyboardHeight == 0 && heightDiff > statusBarHeight) {// heightDiff大于状态栏高度时表示软键盘出现了
							keyboardHeight = heightDiff - statusBarHeight;// 软键盘的高度
						}

						if (isShowKeyboard) {
							// 如果软键盘是弹出的状态，并且heightDiff小于等于状态栏高度，
							// 说明这时软键盘已经收起
							if (heightDiff <= statusBarHeight) {
								isShowKeyboard = false;
								onHideKeyboard();//软键盘隐藏
							}
						} else {
							// 如果软键盘是收起的状态，并且heightDiff大于状态栏高度，
							// 说明这时软键盘已经弹出
							if (heightDiff > statusBarHeight) {
								isShowKeyboard = true;
								onShowKeyboard();//软键盘弹出
							}
						}
		}

		private void onShowKeyboard() {
			Toast.makeText(MainActivity.this, "软键盘弹出", 0).show();
		}

		private void onHideKeyboard() {
			Toast.makeText(MainActivity.this, "软键盘隐藏", 0).show();
		}
	};
	
	/**
	 * 点击空白位置 隐藏软键盘
	 */
	public boolean onTouchEvent(MotionEvent event) {
		if (null != this.getCurrentFocus()) {
			InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			return mInputMethodManager.hideSoftInputFromWindow(this
					.getCurrentFocus().getWindowToken(), 0);
		}
		return super.onTouchEvent(event);
	}
	
	protected void onDestroy() {
		super.onDestroy();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			ll_root.getViewTreeObserver().removeGlobalOnLayoutListener(globalLayoutListener);
		} else {
			ll_root.getViewTreeObserver().removeOnGlobalLayoutListener(
					globalLayoutListener);
		}
	}
	
}
