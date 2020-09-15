package com.sfmap.util;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class IOUtils {
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
            emptyException(ioe);
        }
    }

    public static void closeQuietly(final InputStream input) {
        closeQuietly((Closeable) input);
    }

    public static void closeQuietly(final OutputStream output) {
        closeQuietly((Closeable) output);
    }

    public static void closeQuietly(final Writer output) {
        closeQuietly((Closeable) output);
    }

    public static void closeQuietly(final Reader input) {
        closeQuietly((Closeable) input);
    }

    public static void emptyException(Throwable e){
        Log.w("err", e.toString());
    }
}
