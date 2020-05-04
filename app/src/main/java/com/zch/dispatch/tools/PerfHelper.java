package com.zch.dispatch.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @description 配置信息读写工具类
 */

public class PerfHelper {

	 
	private static final String P_NAME = "dispatch";

	private static SharedPreferences sp;
	private static PerfHelper ph;
	private static Context context;

	private PerfHelper() {

	}

	public static PerfHelper getPerferences(Context a) {
		if (ph == null) {
			ph = new PerfHelper();
			sp = a.getSharedPreferences(P_NAME, 0);
			context = a;
		}
		return ph;
	}

	public static PerfHelper getPerferences() {
		return ph;
	}

	public static void setInfo(String name, String data) {
		if (ph == null) {
			getPerferences(context);
		}
		SharedPreferences.Editor e = sp.edit().putString(name, data);
		e.commit();
	}

	public static void setInfo(String name, int data) {
		if (ph == null) {
			getPerferences(context);
		}
		SharedPreferences.Editor e = sp.edit().putInt(name, data);
		e.commit();
	}

	public static void setInfo(String name, boolean data) {
		if (ph == null) {
			getPerferences(context);
		}
		SharedPreferences.Editor e = sp.edit().putBoolean(name, data);
		e.commit();
	}

	public static int getIntData(String name) {
		if (ph == null) {
			getPerferences(context);
		}
		return sp.getInt(name, 0);
	}

	public static String getStringData(String name) {
		if (ph == null) {
			getPerferences(context);
		}
		return sp.getString(name, "");
	}

	public static boolean getBooleanData(String name) {
		if (ph == null) {
			getPerferences(context);
		}
		return sp.getBoolean(name, false);
	}

	public static void setInfo(String name, long data) {
		if (ph == null) {
			getPerferences(context);
		}
		SharedPreferences.Editor e = sp.edit().putLong(name, data);
		e.commit();
	}

	public static long getLongData(String name) {
		if (ph == null) {
			getPerferences(context);
		}
		return sp.getLong(name, 0);
	}

	public static void clearInfo(String name){
		if (ph == null) {
			getPerferences(context);
		}
		SharedPreferences.Editor e = sp.edit().remove(name);
		e.commit();
	}
}
