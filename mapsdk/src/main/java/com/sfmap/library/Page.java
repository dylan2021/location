package com.sfmap.library;

/**
 * 页面回调接口
 * @date:
 */
public interface Page {
	/**
	 * 设置页面监听
	 * @param listener
     */
	void setPageStateListener(PageStateListener listener);

	/**
	 * 页面接口
	 */
	public interface PageStateListener {
		/**
		 * 页面隐藏方法
		 */
		void onPageHidden();
	}
}
