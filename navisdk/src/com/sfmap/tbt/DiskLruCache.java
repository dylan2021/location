package com.sfmap.tbt;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 原类名：ao
 */
public final class DiskLruCache implements Closeable {
	static final Pattern a = Pattern.compile("[a-z0-9_-]{1,120}");
	private final File c;
	private final File d;
	private final File e;
	private final File f;
	private final int g;
	private long h;
	private final int i;
	private long j = 0L;
	private Writer k;
	private final LinkedHashMap<String, InnerC> l = new LinkedHashMap(0, 0.75F,
			true);
	private int m;
	private FileOperationListener n;
	private long o = 0L;
	final ThreadPoolExecutor b = new ThreadPoolExecutor(0, 1, 60L,
			TimeUnit.SECONDS, new LinkedBlockingQueue());
	private final Callable<Void> p = new Callable<Void>() {

		@Override
		public Void call() throws Exception {
			// synchronized (this.a) {
			// if (ao.a(this.a) != null) break label21;
			// return null;
			//
			// label21: ao.b(this.a);
			// if (!(ao.c(this.a))) break label54;
			// ao.d(this.a);
			// label54: ao.a(this.a, 0);
			// }

			// synchronized (this.a) {
			// if (ao.a(this.a) != null) /*break label21;*/
			// return null;
			//
			// /*label21:*/
			// ao.b(this.a);
			// if (!(ao.c(this.a))) /*break label54;*/
			// ao.d(this.a);
			// /*label54:*/
			// ao.a(this.a, 0);
			// }
			return null;
		}

	};
	private static final OutputStream q = new IntegerOutputStream();

	public void a(FileOperationListener paramar) {
		this.n = paramar;
	}

	private DiskLruCache(File paramFile, int paramInt1, int paramInt2, long paramLong) {
		this.c = paramFile;
		this.g = paramInt1;
		this.d = new File(paramFile, "journal");
		this.e = new File(paramFile, "journal.tmp");
		this.f = new File(paramFile, "journal.bkp");
		this.i = paramInt2;
		this.h = paramLong;
	}

	public static DiskLruCache a(File paramFile, int paramInt1, int paramInt2,
								 long paramLong) throws IOException {
		if (paramLong <= 0L)
			throw new IllegalArgumentException("maxSize <= 0");

		if (paramInt2 <= 0) {
			throw new IllegalArgumentException("valueCount <= 0");
		}

		File localFile = new File(paramFile, "journal.bkp");

		if (localFile.exists()) {
			File localObject = new File(paramFile, "journal");

			if (((File) localObject).exists()) {
				localFile.delete();
			} else {
				a(localFile, (File) localObject, false);
			}

		}

		DiskLruCache localObject = new DiskLruCache(paramFile, paramInt1, paramInt2, paramLong);

		if (((DiskLruCache) localObject).d.exists())
			;
		try {
			((DiskLruCache) localObject).e();
			((DiskLruCache) localObject).f();
			((DiskLruCache) localObject).k = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(((DiskLruCache) localObject).d, true), at.a));

			return (DiskLruCache) localObject;
		} catch (IOException localIOException) {
			((DiskLruCache) localObject).c();

			paramFile.mkdirs();

			localObject = new DiskLruCache(paramFile, paramInt1, paramInt2, paramLong);
			((DiskLruCache) localObject).g();
		}
		return ((DiskLruCache) localObject);
	}

	private void e() throws IOException {
		int i1;
		StrictLineReader localas = new StrictLineReader(new FileInputStream(this.d), at.a);
		try {
			String str1 = localas.a();
			String str2 = localas.a();
			String str3 = localas.a();
			String str4 = localas.a();
			String str5 = localas.a();
			if ((!("libcore.io.DiskLruCache".equals(str1)))
					|| (!("1".equals(str2)))
					|| (!(Integer.toString(this.g).equals(str3)))
					|| (!(Integer.toString(this.i).equals(str4)))
					|| (!("".equals(str5)))) {
				throw new IOException("unexpected journal header: [" + str1
						+ ", " + str2 + ", " + str4 + ", " + str5 + "]");
			}

			i1 = 0;
		} finally {
			at.a(localas);
		}
	}

	private void d(String paramString) throws IOException {
		int i1 = paramString.indexOf(32);
		if (i1 == -1) {
			throw new IOException("unexpected journal line: " + paramString);
		}

		int i2 = i1 + 1;
		int i3 = paramString.indexOf(32, i2);

		if (i3 == -1) {
			String str = paramString.substring(i2);
			if ((i1 != "REMOVE".length())
					|| (!(paramString.startsWith("REMOVE")))) /* break label103 */
				;
			this.l.remove(str);
			return;
		}

		String str = paramString.substring(i2, i3);

		// /*label103:*/ c localc = (c)this.l.get(str);
		// if (localc == null) {
		// //localc = new c(str, null);
		// localc = new c(str);
		// this.l.put(str, localc);
		// }
		//
		// if ((i3 != -1) && (i1 == "CLEAN".length()) &&
		// (paramString.startsWith("CLEAN")))
		// {
		// String[] arrayOfString = paramString.substring(i3 + 1).split(" ");
		// c.a(localc, true);
		// c.a(localc, null);
		// localc.a(arrayOfString);
		// } else if ((i3 == -1) && (i1 == "DIRTY".length()) &&
		// (paramString.startsWith("DIRTY")))
		// {
		// //c.a(localc, new a(localc, null));
		// c.a(localc, new a(localc));
		// } else {
		// if ((i3 == -1) && (i1 == "READ".length()) &&
		// (paramString.startsWith("READ"))) { return;
		// }
		//
		// throw new IOException("unexpected journal line: " + paramString);
		// }
	}

	private void f() throws IOException {
		a(this.e);
		// for (Iterator localIterator = this.l.values().iterator();
		// localIterator.hasNext(); ) {
		// int i1;
		// c localc = (c)localIterator.next();
		// if (localc.e == null) {
		// for (i1 = 0; i1 < this.i; ++i1)
		// this.j += localc.c[i1];
		// }
		// else {
		// c.a(localc, null);
		// for (i1 = 0; i1 < this.i; ++i1) {
		// a(localc.a(i1));
		// a(localc.b(i1));
		// }
		// localIterator.remove();
		// }
		// }
	}

	private synchronized void g() throws IOException {
		if (this.k != null) {
			this.k.close();
		}

		BufferedWriter localBufferedWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(this.e), at.a));
		try {
			localBufferedWriter.write("libcore.io.DiskLruCache");
			localBufferedWriter.write("\n");
			localBufferedWriter.write("1");
			localBufferedWriter.write("\n");
			localBufferedWriter.write(Integer.toString(this.g));
			localBufferedWriter.write("\n");
			localBufferedWriter.write(Integer.toString(this.i));
			localBufferedWriter.write("\n");
			localBufferedWriter.write("\n");

			for (InnerC localc : this.l.values())
				if (localc.e != null)
					localBufferedWriter.write("DIRTY " + localc.b + '\n');
				else
					localBufferedWriter.write("CLEAN " + localc.b + localc.a()
							+ '\n');

		} finally {
			localBufferedWriter.close();
		}

		if (this.d.exists())
			a(this.d, this.f, true);

		a(this.e, this.d, false);
		this.f.delete();

		this.k = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(this.d, true), at.a));
	}

	private static void a(File paramFile) throws IOException {
		if ((paramFile.exists()) && (!(paramFile.delete())))
			throw new IOException();
	}

	private static void a(File paramFile1, File paramFile2, boolean paramBoolean)
			throws IOException {
		if (paramBoolean)
			a(paramFile2);

		if (!(paramFile1.renameTo(paramFile2)))
			throw new IOException();
	}

	public synchronized b a(String paramString) throws IOException {
		int i1;
		i();
		e(paramString);
		InnerC localc = (InnerC) this.l.get(paramString);
		if (localc == null) {
			return null;
		}

		if (!(localc.d)) {
			return null;
		}

		InputStream[] arrayOfInputStream = new InputStream[this.i];
		try {
			for (i1 = 0; i1 < this.i; ++i1)
				arrayOfInputStream[i1] = new FileInputStream(localc.a(i1));
		} catch (FileNotFoundException localFileNotFoundException) {
			for (int i2 = 0; (i2 < this.i) && (arrayOfInputStream[i2] != null); ++i2) {
				at.a(arrayOfInputStream[i2]);
			}

			return null;
		}

		this.m += 1;
		this.k.append("READ " + paramString + '\n');
		if (h()) {
			this.b.submit(this.p);
		}

		// return new b(paramString, localc.f, arrayOfInputStream, localc.c,
		// null);
		return new b(paramString, localc.f, arrayOfInputStream, localc.c);
	}

	public a b(String paramString) throws IOException {
		return a(paramString, -1L);
	}

	private synchronized a a(String paramString, long paramLong)
			throws IOException {
		i();
		e(paramString);
		InnerC localc = (InnerC) this.l.get(paramString);
		if ((paramLong != -1L)
				&& (((localc == null) || (localc.f != paramLong)))) {
			return null;
		}
		if (localc == null) {
			// localc = new c(paramString, null);
			localc = new InnerC(paramString);
			this.l.put(paramString, localc);
		} else if (localc.e != null) {
			return null;
		}

		// a locala = new a(localc, null);
		// c.a(localc, locala);
		a locala = new a(localc);
		// c.a(localc, locala); //????

		this.k.write("DIRTY " + paramString + '\n');
		this.k.flush();
		return locala;
	}

	private synchronized void a(a parama, boolean paramBoolean)
			throws IOException {
		InnerC localc = parama.b;
		if (localc.e != parama) {
			throw new IllegalStateException();
		}

		if ((paramBoolean) && (!(localc.d)))
			// for (i1 = 0; i1 < this.i; ++i1) {
			for (int i1 = 0; i1 < this.i; ++i1) {

				// if (parama.c[i1] == 0) {
				if (parama.c[i1] == false) {
					parama.b();
					throw new IllegalStateException(
							"Newly created entry didn't create value for index "
									+ i1);
				}

				if (!(localc.b(i1).exists())) {
					parama.b();
					return;
				}
			}

		for (int i1 = 0; i1 < this.i; ++i1) {
			File localFile1 = localc.b(i1);
			if (paramBoolean)
				if (localFile1.exists()) {
					File localFile2 = localc.a(i1);
					localFile1.renameTo(localFile2);

					long l1 = localc.c[i1];
					long l2 = localFile2.length();
					localc.c[i1] = l2;
					this.j = (this.j - l1 + l2);
				} else
					a(localFile1);

		}

		this.m += 1;
		// c.a(localc, null); ???
		if ((localc.d | paramBoolean)) {
			// c.a(localc, true); ???????
			// this.k.write("CLEAN " + localc.b + localc.a() + '\n');
			//
			// if (paramBoolean)
			// c.a(localc, this.o++); ?????
		} else {
			this.l.remove(localc.b);
			this.k.write("REMOVE " + localc.b + '\n');
		}
		this.k.flush();

		if ((this.j > this.h) || (h()))
			this.b.submit(this.p);
	}

	private boolean h() {
		int i1 = 2000;
		return ((this.m >= 2000) && (this.m >= this.l.size()));
	}

	public synchronized boolean c(String paramString) throws IOException {
		i();
		e(paramString);
		InnerC localc = (InnerC) this.l.get(paramString);
		if ((localc == null) || (localc.e != null)) {
			return false;
		}

		for (int i1 = 0; i1 < this.i; ++i1) {
			File localFile = localc.a(i1);
			if ((localFile.exists()) && (!(localFile.delete())))
				throw new IOException("failed to delete " + localFile);

			this.j -= localc.c[i1];
			localc.c[i1] = 0L;
		}

		this.m += 1;
		this.k.append("REMOVE " + paramString + '\n');
		this.l.remove(paramString);

		if (h()) {
			this.b.submit(this.p);
		}

		return true;
	}

	public synchronized boolean a() {
		return (this.k == null);
	}

	private void i() {
		if (this.k == null)
			throw new IllegalStateException("cache is closed");
	}

	public synchronized void b() throws IOException {
		i();
		j();
		this.k.flush();
	}

	public synchronized void close() throws IOException {
		if (this.k == null)
			return;

		// for (InnerC localc : new ArrayList(this.l.values())*/)
		for (InnerC localc : l.values())
			if (localc.e != null)
				localc.e.b();

		j();
		this.k.close();
		this.k = null;
	}

	private void j() throws IOException {
		while (this.j > this.h) {
			Map.Entry localEntry = (Map.Entry) this.l.entrySet().iterator()
					.next();

			String str = (String) localEntry.getKey();
			c(str);
			if (this.n != null)
				this.n.a(str);
		}
	}

	public void c() throws IOException {
		close();
		at.a(this.c);
	}

	private void e(String paramString) {
		Matcher localMatcher = a.matcher(paramString);
		if (!(localMatcher.matches()))
			throw new IllegalArgumentException(
					"keys must match regex [a-z0-9_-]{1,120}: \"" + paramString
							+ "\"");
	}

	private final class InnerC {
		private final String b;
		private final long[] c;
		private boolean d;
		private DiskLruCache.a e;
		private long f;

		private InnerC(String paramString) {
			this.b = paramString;
			this.c = new long[DiskLruCache.this.i];
		}

		public String a() throws IOException {
			StringBuilder localStringBuilder = new StringBuilder();
			for (long l : this.c)
				localStringBuilder.append(' ').append(l);

			return localStringBuilder.toString();
		}

		private void a(String[] paramArrayOfString) throws IOException {
			int i;
			if (paramArrayOfString.length != DiskLruCache.this.i)
				throw b(paramArrayOfString);

			try {
				for (i = 0; i < paramArrayOfString.length; ++i)
					this.c[i] = Long.parseLong(paramArrayOfString[i]);
			} catch (NumberFormatException localNumberFormatException) {
				throw b(paramArrayOfString);
			}
		}

		private IOException b(String[] paramArrayOfString) throws IOException {
			throw new IOException("unexpected journal line: "
					+ Arrays.toString(paramArrayOfString));
		}

		public File a(int paramInt) {
			return new File(DiskLruCache.this.c, this.b + "." + paramInt);
		}

		public File b(int paramInt) {
			return new File(DiskLruCache.this.c, this.b + "." + paramInt + ".tmp");
		}
	}

	public final class a {
		private final DiskLruCache.InnerC b;
		private final boolean[] c;
		private boolean d;
		private boolean e;

		private a(DiskLruCache.InnerC paramc) {
			this.b = paramc;
			this.c = ((paramc.d) ? null : new boolean[DiskLruCache.this.i]);
		}

		public OutputStream a(int paramInt) throws IOException {
			if ((paramInt < 0) || (paramInt >= DiskLruCache.this.i)) {
				throw new IllegalArgumentException(
						"Expected index "
								+ paramInt
								+ " to "
								+ "be greater than 0 and less than the maximum value count "
								+ "of " + DiskLruCache.this.i);
			}

			synchronized (DiskLruCache.this) {
				FileOutputStream localFileOutputStream;
				if (this.b.e != this)
					throw new IllegalStateException();

				if (!(this.b.d))
					this.c[paramInt] = true;

				File localFile = this.b.b(paramInt);
				try {
					localFileOutputStream = new FileOutputStream(localFile);
				} catch (FileNotFoundException localFileNotFoundException1) {
					DiskLruCache.this.c.mkdirs();
					try {
						localFileOutputStream = new FileOutputStream(localFile);
					} catch (FileNotFoundException localFileNotFoundException2) {
						/* monitorexit; */return DiskLruCache.q;
					}
				}
				// return new a(localFileOutputStream, null);
				return new InnerOutputStreamDecode(localFileOutputStream);
			}
		}

		public void a() throws IOException {
			if (this.d) {
				DiskLruCache.this.a(this, false);
				DiskLruCache.this.c(this.b.b);
			} else {
				DiskLruCache.this.a(this, true);
			}
			this.e = true;
		}

		public void b() throws IOException {
			DiskLruCache.this.a(this, false);
		}

		private class InnerOutputStreamDecode extends FilterOutputStream {
			private InnerOutputStreamDecode(OutputStream paramOutputStream) {
				super(paramOutputStream);
			}

			public void write(int paramInt) {
				try {
					this.out.write(paramInt);
				} catch (IOException localIOException) {
					// ao.a.a(ao.a.this, true); //unknown
				}
			}

			public void write(byte[] paramArrayOfByte, int paramInt1,
					int paramInt2) {
				try {
					this.out.write(paramArrayOfByte, paramInt1, paramInt2);
				} catch (IOException localIOException) {
					// ao.a.a(ao.a.this, true);
				}
			}

			public void close() {
				try {
					this.out.close();
				} catch (IOException localIOException) {
					// ao.a.a(ao.a.this, true);
				}
			}

			public void flush() {
				try {
					this.out.flush();
				} catch (IOException localIOException) {
					// ao.a.a(ao.a.this, true);
				}
			}
		}
	}

	public final class b implements Closeable {
		private final String b;
		private final long c;
		private final InputStream[] d;
		private final long[] e;

		private b(String paramString, long paramLong,
				InputStream[] paramArrayOfInputStream, long[] paramArrayOfLong) {
			this.b = paramString;
			this.c = paramLong;
			this.d = paramArrayOfInputStream;
			this.e = paramArrayOfLong;
		}

		public void close() {
			for (InputStream localInputStream : this.d)
				at.a(localInputStream);
		}
	}
}