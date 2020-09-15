package org.xidea.el.json;

import java.lang.reflect.Type;
import java.util.List;

public class JSONDecoder {
    public @interface Transformer{
    }
	public interface TypeTransformer<T> {
		public boolean externalSetup();
		public T create(Object source);

	}
	private static JSONDecoder decoder = new JSONDecoder(false);
	private static ClassLoader defaultClassLoader = JSONDecoder.class.getClassLoader();
	private JSONTransformer transformer;
	private boolean strict = false;

	public JSONDecoder(boolean strict) {
		this.strict = strict;
		try{
		    transformer = new OldJSONTransformer();
		}catch(Throwable e){
		    transformer = new JSONTransformer();
		}
		transformer.setClassLoader(defaultClassLoader);
		if(decoder != null){
			for(TypeTransformer<? extends Object>le:decoder.transformer.objectFactory.values()){
				this.addTransformer(le);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T decode(String value) {
		return (T) decoder.decodeObject(value, (Type)null);
	}
	public void setClassLoader(ClassLoader loader){
	    transformer.setClassLoader(loader);
	}

	public static void setDefaultClassLoader(ClassLoader loader){
	    defaultClassLoader = loader;
	}


	@SuppressWarnings("unchecked")
	public static <T> T decode(String value, Type type) {
		return (T) decoder.decodeObject(value, type);
	}
	public TypeTransformer<? extends Object> addTransformer(TypeTransformer<? extends Object> factory){
		return transformer.addFactory(factory);
	}
	public static TypeTransformer<? extends Object> addDefaultTransformer(TypeTransformer<? extends Object> factory){
		return decoder.transformer.addFactory(factory);
	}
	@SuppressWarnings("unchecked")
	public <T> T decodeObject(String value, Type type) {
		try{
			Object result = new JSONTokenizer(value, strict).parse();
			if (type != null && type != Object.class) {
				result = transform(result, type);
			}
			return (T) result;
		}catch (RuntimeException e) {
			System.out.println("json error:"+value);
			throw e;
		}
	}
	@SuppressWarnings("unchecked")
	public <T> List<T> decodeList(String value, Class<T> type) {
		try{
			List<Object> result = (List<Object>) new JSONTokenizer(value, strict).parse();
			if (type != null && type != Object.class) {
				for (int i = result.size()-1; i>=0; i--) {
					result.set(i, transform(result.get(i),type));
				}
			}
			return (List<T>) result;
		}catch (RuntimeException e) {
		        System.out.println("json error:"+value);
			throw e;
		}
	}
	@SuppressWarnings("unchecked")
	public static <T> T transform(Object source,Class<?> type){
		return (T)decoder.transformer.transform(source, type);
	}
	@SuppressWarnings("unchecked")
	public <T> T transform(Object source,Type type){
		return (T)transformer.transform(source, type);
	}
}
