package com.sfmap.library.io.imp;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.util.Base64;

import com.sfmap.library.io.WebStorage;
import com.sfmap.plugin.IMPluginManager;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class DynamicStorageImpl implements WebStorage {

	private final SharedPreferences storage;
	private Editor editor;

	public DynamicStorageImpl(String key) {
		storage = IMPluginManager.getApplication().getSharedPreferences(key+"__v2__", 0);
		bugfix700(key);

	}
	private void bugfix700(String name) {
		if(storage.getAll().isEmpty()){
			SharedPreferences oldStorage = IMPluginManager.getApplication().getSharedPreferences(name, 0);
			Map<String, ?> all = oldStorage.getAll();
			if(all.size()>0){
				Editor  editor = storage.edit();
				for(Map.Entry<String, ?> e: all.entrySet()){
					String key = obscureKey700(e.getKey());
					Object value = e.getValue();
					if(value instanceof String){
						String content = obscureKey700((String)value);
						editor.putString(encode(key), encode(content));
					}

				}
				apply(editor);
			}
			apply(oldStorage.edit().clear());
		}
	}
	@Deprecated
	private static String obscureKey700(String key) {
			char[] bs = key.toCharArray();
			for (int i = 0; i < bs.length; i++) {
				bs[i] = (char) ((bs[i] ^ '^') & 0xFFFF);
			}
			return new String(bs);
	}

	@Override
	public DynamicStorageImpl beginTransaction() {
		return this;
	}

	@Override
	public void commit() {
		Editor editor = this.editor;
		if (editor != null) {
			apply(editor);
		}
	}
	private void apply(Editor editor) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			editor.commit();
		}else {
			editor.apply();
		}
	}

	@Override
	public DynamicStorageImpl reset() {
		boolean localEditor = this.editor == null;
		Editor editor = localEditor ? storage.edit() : this.editor;
		editor.clear();
		if (localEditor) {
			apply(editor);
		}
		return this;
	}

	@Override
	public WebStorage remove(String key) {
		return set(key, null);
	}

	@Override
	public DynamicStorageImpl set(String key, String value) {
		boolean localEditor = this.editor == null;
		Editor editor = localEditor ? storage.edit() : this.editor;
		if(value == null){
			editor.remove(encode(key));
		}else{
			editor.putString(encode(key),
				encode(value));
		}
		if (localEditor) {
			apply(editor);
		}
		return this;
	}

	@Override
	public String get(String key) {
		String rtv = storage.getString(encode(key), "");
		return decode(rtv);
	}


	private static String encode(String value){
		if( value !=null && value.length()>0){
			value = obscureKey700(value);
			try {
				value = Base64.encodeToString(value.getBytes("UTF-8"),Base64.NO_WRAP);
			} catch (UnsupportedEncodingException e) {
			}
		}
		return value;
	}
	private static String decode(String value){
		if( value !=null && value.length()>0){
			try {
				value =  new String(Base64.decode(value,Base64.NO_WRAP),"UTF-8");
				value =obscureKey700(value);
			} catch (UnsupportedEncodingException e) {
			}
		}
		return value;
	}

	@Override
	public Iterator<String> iterator() {
		final Iterator<String> it = new HashSet<String>(storage.getAll().keySet()).iterator();
		return new Iterator<String>() {
			private String key;
			@Override
			public void remove() {
				it.remove();
				set(key, null);
			}

			@Override
			public String next() {
				key = decode(it.next());
				return key;
			}

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}
		};
	}

    @Override
    public int size() {
        return storage.getAll().size();
    }
}
