package com.sfmap.tbt;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * 原类名：as
 */
public class StrictLineReader implements Closeable {
	private final InputStream a;
	private final Charset b;
	private byte[] c;
	private int d;
	private int e;

	public StrictLineReader(InputStream paramInputStream, Charset paramCharset) {
		this(paramInputStream, 8192, paramCharset);
	}

	public StrictLineReader(InputStream paramInputStream, int paramInt, Charset paramCharset) {
		if ((paramInputStream == null) || (paramCharset == null))
			throw new NullPointerException();

		if (paramInt < 0)
			throw new IllegalArgumentException("capacity <= 0");

		if (!(paramCharset.equals(at.a))) {
			throw new IllegalArgumentException("Unsupported encoding");
		}

		this.a = paramInputStream;
		this.b = paramCharset;
		this.c = new byte[paramInt];
	}

	public void close() throws IOException {
		synchronized (this.a) {
			if (this.c != null) {
				this.c = null;
				this.a.close();
			}
		}
	}

	public String a() throws IOException {
		synchronized (this.a) {
			if (this.c == null) {
				throw new IOException("LineReader is closed");
			}

			if (this.d >= this.e) {
				b();
			}

			for (int i = this.d; i != this.e; ++i) {
				if (this.c[i] == 10) {
					// j = ((i != this.d) && (this.c[(i - 1)] == 13)) ? i - 1 :
					// i;
					int j = ((i != this.d) && (this.c[(i - 1)] == 13)) ? i - 1
							: i;
					String str = new String(this.c, this.d, j - this.d,
							this.b.name());

					this.d = (i + 1);
					return str;
				}

			}

			// local1 = new ByteArrayOutputStream(this.e - this.d + 80)
			ByteArrayOutputStream local1 = new ByteArrayOutputStream(this.e
					- this.d + 80) {
				public String toString() {
					int i = ((this.count > 0) && (this.buf[(this.count - 1)] == 13)) ? this.count - 1
							: this.count;
					try {
						return new String(this.buf, 0, i, StrictLineReader.this.b.name());
					} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
						throw new AssertionError(
								localUnsupportedEncodingException);
					}

				}

			};
			local1.write(this.c, this.d, this.e - this.d);

			this.e = -1;
			b();

			int j = this.d;
			while (true) {
				if (j == this.e) /* break label261; */
					if (this.c[j] == 10) {
						if (j != this.d)
							local1.write(this.c, this.d, j - this.d);

						this.d = (j + 1);
						return local1.toString();
					}
				/* label261: */++j;
			}
		}
	}

	private void b() throws IOException {
		int i = this.a.read(this.c, 0, this.c.length);
		if (i == -1)
			throw new EOFException();

		this.d = 0;
		this.e = i;
	}
}