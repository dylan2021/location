package com.sfmap.tbt;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

class HostnameVerifierImpl implements HostnameVerifier {
	public boolean verify(String paramString, SSLSession paramSSLSession) {
		HostnameVerifier localHostnameVerifier = HttpsURLConnection
				.getDefaultHostnameVerifier();

		String hostname = "*." + AppInfoUtilDecode.CONFIT_KEY_COMPANY + ".com";
		return localHostnameVerifier.verify(hostname, paramSSLSession);
	}
}