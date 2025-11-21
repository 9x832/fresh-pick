package com.agrismart.agrimallbackend.dto.response;

import com.agrismart.agrimallbackend.common.bean.CodeMsg;

import java.io.Serializable;

/**
 * 统一 API 响应数据传输对象。
 *
 * 该 DTO 用于统一封装所有 API 接口的响应数据，包含响应码、响应消息和数据。
 * 使用泛型支持不同类型的数据。
 *
 * 响应结构：
 *
 * - code：响应码，0 表示成功，其他值表示失败
 * - msg：响应消息，用于描述操作结果或错误信息
 * - data：响应数据，可以是任意类型
 *
 * 使用场景：
 *
 * - 所有控制器方法的返回值都使用此 DTO 封装
 * - 通过静态方法快速创建成功或失败的响应
 *
 * @param <T> 响应数据的类型
 * @author agrimall
 * @see com.agrismart.agrimallbackend.common.bean.CodeMsg
 * @since 1.0
 */
public class ResponseVo<T> implements Serializable {

    /**
     * 响应码。
     * 0 表示成功，其他值表示失败。对应 {@link com.agrismart.agrimallbackend.common.bean.CodeMsg} 中的错误码。
     */
    private Integer code;

    /**
     * 响应消息。
     * 用于描述操作结果或错误信息。
     */
    private String msg;

    /**
     * 响应数据。
     * 可以是任意类型，根据具体接口返回不同的数据。
     */
    private T data;

    /**
     * 无参构造函数。
     */
    public ResponseVo() {
    }

    /**
     * 构造函数（仅包含响应码和消息）。
     *
     * @param code 响应码
     * @param msg  响应消息
     */
    private ResponseVo(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 构造函数（包含响应码、消息和数据）。
     *
     * @param code 响应码
     * @param msg  响应消息
     * @param data 响应数据
     */
    private ResponseVo(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 构造函数（仅包含响应码和数据）。
     *
     * @param code 响应码
     * @param data 响应数据
     */
    private ResponseVo(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 创建成功响应（仅包含数据）。
     *
     * 使用默认的成功响应码（0），不包含响应消息。
     *
     * @param <T>  响应数据类型
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> ResponseVo<T> success(T data) {
        return new ResponseVo<>(CodeMsg.SUCCESS.getCode(), data);
    }

    /**
     * 创建成功响应（包含数据和自定义消息）。
     *
     * 使用默认的成功响应码（0），包含自定义的响应消息。
     *
     * @param <T>  响应数据类型
     * @param data 响应数据
     * @param msg  自定义响应消息
     * @return 成功响应对象
     */
    public static <T> ResponseVo<T> successByMsg(T data, String msg) {
        return new ResponseVo<>(CodeMsg.SUCCESS.getCode(), msg, data);
    }

    /**
     * 创建错误响应。
     *
     * 根据 {@link com.agrismart.agrimallbackend.common.bean.CodeMsg} 创建错误响应，包含错误码和错误消息。
     *
     * @param <T>     响应数据类型
     * @param codeMsg 错误码和消息对象
     * @return 错误响应对象
     */
    public static <T> ResponseVo<T> errorByMsg(CodeMsg codeMsg) {
        return new ResponseVo<>(codeMsg.getCode(), codeMsg.getMsg());
    }
}

