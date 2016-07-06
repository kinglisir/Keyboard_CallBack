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

	private LinearLayout ll_root;//activate�ĸ�����
	// ����̵ĸ߶�
	private int keyboardHeight;
	private int statusBarHeight;//״̬���ĸ߶�
	// ����̵���ʾ״̬
	private boolean isShowKeyboard;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		statusBarHeight = getStatusBarHeight();
		//��activity�����Ĳ�������һ��  ������ɼ���
		ll_root.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
	}

	private void initView() {
		ll_root = (LinearLayout) findViewById(R.id.ll_root);
	}

	/**��ȡ״̬���߶�, 
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
	
	//������ɼ���
	private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
		//�������ʱ����ø÷���
		public void onGlobalLayout() {
			// Ӧ�ÿ�����ʾ�����򡣴˴�����Ӧ��ռ�õ�����
						// �Լ�ActionBar��״̬�����������豸�ײ������ⰴ����
						Rect r = new Rect();
						ll_root.getWindowVisibleDisplayFrame(r);// activity���ֵ������layout,
						// ActionBar��״̬�����������豸�ײ������ⰴ����

						// ��ͨ������getRootView().getHeight()����ȡ������Ļ�ĸ߶ȡ�ͬ��������߶�Ҳ�������ⰴ���ĸ߶ȡ�
						int screenHeight = ll_root.getRootView().getHeight();
						// �����layout��������Ļ�ĸ߶�, ͨ���Ƚ��������߶ȣ������ƶϳ�������Ƿ���ʾ�����һ�ȡ������̵ĸ߶�
						int heightDiff = screenHeight - (r.bottom - r.top);

						// �ڲ���ʾ�����ʱ��heightDiff����״̬���ĸ߶�
						// ����ʾ�����ʱ��heightDiff���󣬵�������̼�״̬���ĸ߶ȡ�
						// ����heightDiff����״̬���߶�ʱ��ʾ����̳����ˣ�
						// ��ʱ���������̵ĸ߶ȣ���heightDiff��ȥ״̬���ĸ߶�
						if (keyboardHeight == 0 && heightDiff > statusBarHeight) {// heightDiff����״̬���߶�ʱ��ʾ����̳�����
							keyboardHeight = heightDiff - statusBarHeight;// ����̵ĸ߶�
						}

						if (isShowKeyboard) {
							// ���������ǵ�����״̬������heightDiffС�ڵ���״̬���߶ȣ�
							// ˵����ʱ������Ѿ�����
							if (heightDiff <= statusBarHeight) {
								isShowKeyboard = false;
								onHideKeyboard();//���������
							}
						} else {
							// ���������������״̬������heightDiff����״̬���߶ȣ�
							// ˵����ʱ������Ѿ�����
							if (heightDiff > statusBarHeight) {
								isShowKeyboard = true;
								onShowKeyboard();//����̵���
							}
						}
		}

		private void onShowKeyboard() {
			Toast.makeText(MainActivity.this, "����̵���", 0).show();
		}

		private void onHideKeyboard() {
			Toast.makeText(MainActivity.this, "���������", 0).show();
		}
	};
	
	/**
	 * ����հ�λ�� ���������
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
