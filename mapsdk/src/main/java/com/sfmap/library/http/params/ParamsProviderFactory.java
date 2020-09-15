package com.sfmap.library.http.params;



import java.util.HashSet;

/**
 *
 */
public class ParamsProviderFactory {
	private ParamsProviderFactory() {
	}

	private static final HashSet<ParamsProvider> paramsProviderSet = new HashSet<ParamsProvider>();

	static {
		paramsProviderSet.add(new BaseParamsProvider());
	}

	public static void registerParamsProvider(final ParamsProvider paramsProvider) {
		paramsProviderSet.add(paramsProvider);
	}

	public static ParamsProvider getParamsProvider(final String url, final RequestParams params) {
		ParamsProvider result = null;

		for (ParamsProvider provider : paramsProviderSet) {
			if (provider.match(url, params)) {
				result = provider.newInstance();
				break;
			}
		}
		return result;
	}
}
