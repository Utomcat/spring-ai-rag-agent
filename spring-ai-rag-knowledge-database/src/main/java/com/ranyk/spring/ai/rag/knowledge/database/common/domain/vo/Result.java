package com.ranyk.spring.ai.rag.knowledge.database.common.domain.vo;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * CLASS_NAME: Result.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 返回单结果 VO 类
 * @date: 2026-02-08
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@SuppressWarnings("unused")
public class Result<T> implements Serializable{
    @Serial
    private static final long serialVersionUID = 1088505742827320434L;
    /**
     * 状态码
     */
    private String code;
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 提示信息
     */
    private String msg;
    /**
     * 返回数据
     */
    private T data;

    /**
     * 构造方法 - 通过 是否成功 返回信息 返回数据 构造
     *
     * @param success 是否成功
     * @param msg     返回信息
     * @param data    返回数据
     */
    public Result(Boolean success, String msg, T data) {
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 构造成功返回结果
     *
     * @param data 返回数据
     * @param <T>  泛型 T
     * @return 返回结果 {@link Result} 对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(Boolean.TRUE);
        result.setCode("200");
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    /**
     * 构造错误返回结果
     *
     * @param code 错误码
     * @param msg  错误信息
     * @param <T>  泛型 T
     * @return 错误结果 {@link Result} 对象
     */
    public static <T> Result<T> error(String code, String msg) {
        Result<T> result = new Result<>();
        result.setSuccess(Boolean.FALSE);
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
}
