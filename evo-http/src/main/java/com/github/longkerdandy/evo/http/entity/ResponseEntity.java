package com.github.longkerdandy.evo.http.entity;

/**
 * Http response
 * Using JSend specification: http://labs.omniti.com/labs/jsend
 */
@SuppressWarnings("unused")
public class ResponseEntity<T> {

    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";
    public static final String ERROR = "error";

    private String status;
    private Integer code;
    private String message;
    private T data;

    public ResponseEntity(String status, T data) {
        this(status, null, null, data);
    }

    public ResponseEntity(String status, Integer code, String lang) {
        this(status, code, lang, null);
    }

    public ResponseEntity(String status, Integer code, String lang, T data) {
        this.status = status;
        this.code = code;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCode() {
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
