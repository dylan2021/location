package com.sfmap.library.http.loader;




import com.sfmap.library.http.params.RequestParams;

import org.json.JSONObject;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class LoaderFactory {

    private LoaderFactory() {
    }

    /**
     * key: className
     */
    @SuppressWarnings("rawtypes")
	private static final HashMap<Class, Loader> converterHashMap = new HashMap<Class, Loader>();

    static {
	converterHashMap.put(Map.class, new MapLoader());
	converterHashMap.put(JSONObject.class, new JSONObjectLoader());
	converterHashMap.put(String.class, new StringLoader());
	converterHashMap.put(File.class, new FileLoader());
	converterHashMap.put(InputStream.class, new InputStreamLoader());
	converterHashMap.put(byte[].class, new ByteArrayLoader());
	BooleanLoader booleanLoader = new BooleanLoader();
	converterHashMap.put(boolean.class, booleanLoader);
	converterHashMap.put(Boolean.class, booleanLoader);
	IntegerLoader integerLoader = new IntegerLoader();
	converterHashMap.put(int.class, integerLoader);
	converterHashMap.put(Integer.class, integerLoader);
    }

    @SuppressWarnings("unchecked")
	public static <T> Loader<T> getLoader(Class<T> type, RequestParams params) {
	Loader<T> result = converterHashMap.get(type);
	if (result == null) {
	    result = new ObjectLoader<T>(type);
	} else {
	    result = result.newInstance();
	}
	result.setParams(params);
	return result;
    }

    public static <T> void registerLoader(Class<T> type, Loader<T> loader) {
	converterHashMap.put(type, loader);
    }
}
