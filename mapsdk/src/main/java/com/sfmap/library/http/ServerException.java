package com.sfmap.library.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 */
public class ServerException extends RuntimeException {


    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ExceptionType {
	Class<? extends ServerException> value();
    }

    protected int code;

    public ServerException(int code, String message) {
	super(message);
	this.code = code;
    }

    public ServerException(int code, String message, Throwable cause) {
	super(message, cause);
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
