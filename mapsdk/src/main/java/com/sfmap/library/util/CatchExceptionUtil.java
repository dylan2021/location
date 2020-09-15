package com.sfmap.library.util;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class CatchExceptionUtil {

    // private static final boolean THROWEXCEPTION = BuildConfig.DEBUG;

    private static final boolean THROWEXCEPTION = true;

    public static void normalPrintStackTrace(Error e) {
	if (THROWEXCEPTION) {
	    throw new RuntimeException(e);
	}
	if (e != null)
	    e.printStackTrace();
    }

    public static void normalPrintStackTrace(Throwable e) {
	if (THROWEXCEPTION) {
	    if (!isIgnoreException(e)) {
		throw new RuntimeException(e);
	    }
	}
	if (e != null)
	    e.printStackTrace();
    }

    private static boolean isIgnoreException(Throwable e) {
	if (e instanceof InterruptedException) {
	    return true;
	} else if (e instanceof SocketException) {
	    return true;
	} else if (e instanceof ConnectException) {
	    return true;
	} else if(e instanceof IOException) {
	    return true;
	} else if(e instanceof SocketTimeoutException) {
	    return true;
	} else if(e instanceof UnknownHostException) {
	    return true;
	}
	return false;
    }

}
