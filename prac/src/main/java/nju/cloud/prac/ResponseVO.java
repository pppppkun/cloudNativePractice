package nju.cloud.prac;

import java.util.List;

/**
 * @Author: pkun
 * @CreateTime: 2020-07-26 01:42
 */
public class ResponseVO {

    private Integer status;
    private String msg;
    private List<Object> data;

    public ResponseVO(Integer status, String msg, List<Object> data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static ResponseVO OK(String msg, List<Object> data) {
        return new ResponseVO(200, msg, data);
    }

    public static ResponseVO Error(Integer status, String msg) {
        return new ResponseVO(status, msg, null);
    }

}
