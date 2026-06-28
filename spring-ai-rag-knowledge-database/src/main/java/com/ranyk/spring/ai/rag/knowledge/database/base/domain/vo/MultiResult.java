package com.ranyk.spring.ai.rag.knowledge.database.base.domain.vo;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.util.List;

/**
 * CLASS_NAME: MultiResult.java
 *
 * @author ranyk
 * @version V1.0
 * @description: 多结果返回实体 VO 类
 * @date: 2026-06-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@SuppressWarnings("unused")
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(builderMethodName = "multiResultBuilder")
public class MultiResult<T> extends Result<List<T>> {
    @Serial
    private static final long serialVersionUID = -6278086580992809901L;

    /**
     * 总记录数
     */
    private long total;
    /**
     * 当前页码
     */
    private int page;
    /**
     * 每页记录数
     */
    private int size;

    /**
     * 构造函数 - 通过 是否成功 、 错误代码 、 错误信息 、 数据 、 总记录数 、 当前页码 、 每页记录数 构造
     *
     * @param success 是否成功
     * @param code    错误码
     * @param msg     错误信息
     * @param data    返回数据
     * @param total   总记录数
     * @param page    当前页码
     * @param size    每页记录数
     */
    public MultiResult(Boolean success, String code, String msg, List<T> data, long total, int page, int size) {
        super(code, success, msg, data);
        this.total = total;
        this.page = page;
        this.size = size;
    }

    /**
     * 多结果成功返回
     *
     * @param data  返回数据
     * @param total 总记录数
     * @param page  当前页码
     * @param size  每页记录数
     * @param <T>   泛型 T
     * @return 多结果返回对象 {@link MultiResult}
     */
    public static <T> MultiResult<T> successMulti(List<T> data, long total, int page, int size) {
        return new MultiResult<>(Boolean.TRUE, "200", "success", data, total, page, size);
    }

    /**
     * 多结果错误返回
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     * @param <T>       泛型 T
     * @return 多结果返回对象 {@link MultiResult}
     */
    public static <T> MultiResult<T> errorMulti(String errorCode, String errorMsg) {
        return new MultiResult<>(Boolean.FALSE, errorCode, errorMsg, null, 0, 0, 0);
    }
}
