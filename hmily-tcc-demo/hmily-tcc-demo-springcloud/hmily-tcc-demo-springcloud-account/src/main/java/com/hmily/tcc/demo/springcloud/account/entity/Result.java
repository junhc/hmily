package com.hmily.tcc.demo.springcloud.account.entity;

/**
 * Name: Result
 * Function:
 *
 * @Author: K.K
 * Create Time: 2025/6/14 17:33
 * Modified By:
 * Modified Time:
 * Description:
 * Version:
 */
public class Result<T> {
    private String code;
    private String message;
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<T>();
        result.setCode("0");
        result.setData(data);
        return result;
    }
}
