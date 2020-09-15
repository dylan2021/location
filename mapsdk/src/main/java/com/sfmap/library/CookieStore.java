package com.sfmap.library;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public interface CookieStore {

	public void addSetCookie(String setCookie);

	public void addCookie(Cookie cookie);

	public void clear();

	public boolean clearExpired();

	public List<Cookie> getCookies();

	public Cookie getCookie(String name);

	public String getCookie();

	public class Cookie {
		private String name;
		private String value;
		private String domain;
		private String path;
		private Date expiryDate;

		public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

		public Cookie() {
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public Date getExpiryDate() {
			return expiryDate;
		}

		public void setExpiryDate(Date expiryDate) {
			this.expiryDate = expiryDate;
		}

		public boolean isExpired() {
			return expiryDate != null && expiryDate.before(new Date());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (name != null && value != null) {
				sb.append(name).append("=").append(value);
			}
			if (domain != null) {
				sb.append(";domain=").append(domain);
			}
			if (path != null) {
				sb.append(";path=").append(path);
			}
			if (expiryDate != null) {
				sb.append(";expires=").append(DATE_FORMAT.format(expiryDate));
			}
			return sb.toString();
		}

		public String toElementString() {
			if (name != null && value != null) {
				return name + "=" + value + ";";
			} else {
				return "";
			}
		}
	}
}
