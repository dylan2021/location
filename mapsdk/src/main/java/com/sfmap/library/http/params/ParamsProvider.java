package com.sfmap.library.http.params;




import com.sfmap.library.http.url.URIRequest;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 */
public interface ParamsProvider {

	ParamsProvider newInstance();

	boolean match(final String url, final RequestParams params);

	URL buildURL(final String url, final URIRequest request) throws MalformedURLException, URISyntaxException;

	void initConnection(final HttpURLConnection conn, final RequestParams params);

	void syncHeaders(final HttpURLConnection conn);
}
