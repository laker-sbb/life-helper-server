package com.inlym.lifehelper.weixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信服务端登录凭证校验响应数据
 *
 * @see <a href="https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html">auth.code2Session</a>
 */
@Data
public class Code2SessionResponse {
    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;

    /**
     * 用户唯一标识
     */
    private String openid;

    /**
     * 用户在开放平台的唯一标识符
     */
    private String unionid;

    /**
     * 会话密钥
     */
    @JsonProperty("session_key")
    private String sessionKey;
}
