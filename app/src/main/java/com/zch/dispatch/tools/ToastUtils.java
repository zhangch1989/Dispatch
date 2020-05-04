package com.zch.dispatch.tools;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Author：zch  
 * create on ：2015年10月9日
 */
public class ToastUtils {
	public static Handler h;
	public static Context context;
	
	public static void showToast(final String info) {
		h.post(new Runnable() {

			@Override
			public void run() {
				try {
					Toast.makeText(context, info, Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public static void showToast(Context context, CharSequence text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
