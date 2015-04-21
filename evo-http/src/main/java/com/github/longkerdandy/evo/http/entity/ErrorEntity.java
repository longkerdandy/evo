package com.github.longkerdandy.evo.http.entity;

/**
 * Error Response
 */
@SuppressWarnings("unused")
public class ErrorEntity<T> {

    private int code;
    private String message;
    private T data;

    public ErrorEntity(int code, String lang) {
        this(code, lang, null);
    }

    public ErrorEntity(int code, String lang, T data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
