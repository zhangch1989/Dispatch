package com.zch.dispatch.base;

import java.io.IOException;

//
//接口类 
//用于请求之后的回调
//
public interface BaseCallbackListener {

	/**
	 * 请求成功
	 * 
	 * @param code
	 * @param response
	 */
	public void onComplete(int code, String response);

	/**
	 * 请求发生IO异常
	 * 
	 * @param code
	 * @param e
	 */
	public void onIOException(int code, IOException e);

	/**
	 * 请求发生错误
	 * 
	 * @param code
	 * @param response
	 */
	public void onError(int code, String response);

}
