package com.zch.dispatch.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Author：zch create on ：2015年10月9日
 */
public class DialogUtils {
	private static final int DATE_DIALOG = 0;
	public static Handler h;

	/**
	 * 只有一个“取消”按钮的弹出框
	 * @param ac
	 * @param info
	 * @param title
	 */
	public static void showDialog(Context ac, String info, String title) {
		try {
			new AlertDialog.Builder(ac)
					.setPositiveButton("取消", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).setMessage(info).setTitle(title).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void showTimeDialog(final Activity ac, int type,
			final TextView tv) {
		Calendar calendar = Calendar.getInstance();
		if (type == DATE_DIALOG) {
			DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker datePicker, int year,
						int month, int dayOfMonth) {
					tv.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
				}
			};
			DatePickerDialog dlg = new DatePickerDialog(ac, dateListener,
					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
			dlg.show();
		} else {
			TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker timePicker, int hourOfDay,
						int minute) {
					tv.setText(hourOfDay + ":" + minute + ":00");
				}
			};
			new TimePickerDialog(ac, timeListener,
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), true).show();
		}

	}

	public static ProgressDialog mypDialog;

	public static void dismissProcessDialog() {
		h.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mypDialog != null) {
					mypDialog.dismiss();
					mypDialog = null;
				}
			}
		});

	}

	public static ProgressDialog showProcessDialog(Context ac, String message) {
		if (mypDialog != null) {
			try {
				mypDialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mypDialog.setMessage(message);
			return mypDialog;
		}
		mypDialog = new ProgressDialog(ac);
		mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mypDialog.setMessage(message);
		mypDialog.setCancelable(false);
		mypDialog.incrementProgressBy(30);
		mypDialog.incrementSecondaryProgressBy(70);
		mypDialog.setIndeterminate(true);
		mypDialog.setCanceledOnTouchOutside(false);
		try {
			if (null != mypDialog) {
				mypDialog.show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mypDialog;

	}

	public static ProgressDialog showProcessDialog(Context ac, String message, boolean canCancele) {
		if (mypDialog != null) {
			try {
				mypDialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mypDialog.setMessage(message);
			return mypDialog;
		}
		mypDialog = new ProgressDialog(ac);
		mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mypDialog.setMessage(message);
		mypDialog.setCancelable(false);
		mypDialog.incrementProgressBy(30);
		mypDialog.incrementSecondaryProgressBy(70);
		mypDialog.setIndeterminate(true);
		mypDialog.setCanceledOnTouchOutside(canCancele);
		try {
			if (null != mypDialog) {
				mypDialog.show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mypDialog;

	}

	public static ProgressDialog showProcessDialog(Context ac, String message,
			Drawable indeterminateDrawable) {
		if (mypDialog != null) {
			try {
				mypDialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mypDialog.setMessage(message);
			return mypDialog;
		}
		mypDialog = new ProgressDialog(ac);
		mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mypDialog.setMessage(message);
		mypDialog.setCancelable(false);
		mypDialog.incrementProgressBy(30);
		mypDialog.incrementSecondaryProgressBy(70);
		mypDialog.setIndeterminate(false);
		if (null != indeterminateDrawable) {
			mypDialog.setIndeterminateDrawable(indeterminateDrawable);
			mypDialog.setIndeterminate(false);
			mypDialog.setInverseBackgroundForced(false);
			indeterminateDrawable.setBounds(20, 20, 20, 20);
		}
		mypDialog.setCancelable(false);
		try {
			if (null != mypDialog) {
				mypDialog.show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		mypDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (null != mypDialog) {
					mypDialog.dismiss();
				}
				mypDialog = null;
				return false;
			}
		});

		return mypDialog;
	}

	public static ProgressDialog showProcessDialog(Context ac, int id) {
		if (mypDialog != null) {
			try {
				mypDialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mypDialog.setMessage(ac.getResources().getString(id));
			return mypDialog;
		}
		mypDialog = new ProgressDialog(ac);
		mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mypDialog.setMessage(ac.getResources().getString(id));
		mypDialog.incrementProgressBy(30);
		mypDialog.incrementSecondaryProgressBy(70);
		mypDialog.setCancelable(false);
		mypDialog.setIndeterminate(false);
		mypDialog.setCancelable(false);
		try {
			if (null != mypDialog) {
				mypDialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mypDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (null != mypDialog) {
					mypDialog.dismiss();
				}
				mypDialog = null;
				return false;
			}
		});

		return mypDialog;
	}
}
