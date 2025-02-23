package com.inlym.lifehelper.user.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * 用户个人信息业务对象
 *
 * <h2>说明
 *
 * <p>业务层从数据库获取并完成数据处理后的信息
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2022-02-13
 * @since 1.0.0
 **/
@Data
@Builder
public class UserInfoBO {
    /** 用户昵称 */
    private String nickName;

    /** 用户头像图片的 URL */
    private String avatarUrl;
}
