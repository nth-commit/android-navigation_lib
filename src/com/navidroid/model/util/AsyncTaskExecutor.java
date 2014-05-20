package com.navidroid.model.util;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

public class AsyncTaskExecutor {
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static <T> void execute(AsyncTask<T, ?, ?> asyncTask, T... params) {
	    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
	    else
	        asyncTask.execute(params);
	}
}
