package com.sfmap.tbt;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public final class at {
	public static final Charset a = Charset.forName("US-ASCII");
	static final Charset b = Charset.forName("UTF-8");

	static void a(File paramFile) throws IOException {
		File[] arrayOfFile1 = paramFile.listFiles();
		if (arrayOfFile1 == null)
			throw new IOException("not a readable directory: " + paramFile);

		for (File localFile : arrayOfFile1) {
			if (localFile.isDirectory())
				a(localFile);

			if (!(localFile.delete()))
				throw new IOException("failed to delete file: " + localFile);
		}
	}

	static void a(Closeable paramCloseable) {
		if (paramCloseable != null)
			try {
				paramCloseable.close();
			} catch (RuntimeException localRuntimeException) {
				throw localRuntimeException;
			} catch (Exception localException) {
			}
	}
}