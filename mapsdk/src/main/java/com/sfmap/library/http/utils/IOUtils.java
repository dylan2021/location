/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sfmap.library.http.utils;

import android.database.Cursor;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 */
public class IOUtils {

	private IOUtils() {
	}

	public static void closeQuietly(Cursor cursor) {
		if (cursor != null) {
			try {
				cursor.close();
			} catch (Throwable e) {
			}
		}
	}

	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (final Exception ignored) {
			}
		}
	}

	public static byte[] loadBytesAndClose(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			byte[] buf = new byte[1024];
			int c;
			while ((c = in.read(buf)) >= 0) {
				out.write(buf, 0, c);
			}
		} finally {
			in.close();
		}
		return out.toByteArray();
	}

	public static String loadTextAndClose(InputStream in, String encoding) {
		if (in == null) {
			return null;
		}
		try {
			try {
				Reader r = new InputStreamReader(in, encoding == null ? "utf-8" : encoding);
				StringBuilder out = new StringBuilder();
				char[] buf = new char[1024];
				int c;
				while ((c = r.read(buf)) >= 0) {
					out.append(buf, 0, c);
				}
				return out.toString();
			} finally {
				in.close();
			}
		} catch (IOException e) {
			return null;
		}
	}


	/**
	 * Reads next 16-bit value, LSB first
	 *
	 * @throws IOException
	 */
	public static int readShort(InputStream in) throws IOException {
		// read 16-bit value, LSB first
		return in.read() | (in.read() << 8);
	}

	/**
	 * Reads next 32-bit value, LSB first
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 * @see DataInputStream#readInt()
	 */
	public static int readInt(InputStream in) throws IOException {
		return in.read() | (in.read() << 8) | (in.read() << 16) | (in.read() << 24);
	}

	public static int skip(InputStream in, int blockSize) throws IOException {

		int total = 0;
		if (blockSize > 0) {
			int count = 0;
			while (total < blockSize) {
				count = (int) in.skip(blockSize - total);
				if (count == -1)
					break;
				total += count;
			}
		}
		return total;
	}

	public static boolean startsWith(InputStream in, byte[] bs) throws IOException {
		for (byte b : bs) {
			if (in.read() != (b & 0xFF)) {
				return false;
			}
		}
		return true;
	}

	public static byte[] read(InputStream in, byte[] bs, int count) throws IOException {
		int i = 0;
		while (i < count) {
			i += in.read(bs, i, count - i);
		}
		return bs;
	}

	public static byte[] loadBytesAndUncompress(InputStream is) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		int len = -1;
		byte[] buffer = new byte[512];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = bis.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		byte[] data = bos.toByteArray();
		closeQuietly(bis);
		closeQuietly(bos);
		return data;
	}


	public static boolean isGZIP(BufferedInputStream bis) throws IOException {
		if (bis == null) {
			return false;
		}
		bis.mark(2);
		final byte[] header = new byte[2];
		int readByte = bis.read(header);
		bis.reset();
		return (readByte != -1 && (header[0] & 0xff) == 0x1f && (header[1] & 0xff) == 0x8b);
	}
}
