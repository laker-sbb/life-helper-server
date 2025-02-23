package com.inlym.lifehelper.user;

import com.inlym.lifehelper.external.oss.OssService;
import com.inlym.lifehelper.user.entity.User;
import com.inlym.lifehelper.user.mapper.UserMapper;
import com.inlym.lifehelper.user.pojo.UserInfoBO;
import com.inlym.lifehelper.user.pojo.UserInfoDTO;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户账户服务类
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2022-01-23
 * @since 1.0.0
 */
@Service
@Slf4j
public class UserService {
    private final UserMapper userMapper;

    private final OssService ossService;

    public UserService(UserMapper userMapper, OssService ossService) {
        this.userMapper = userMapper;
        this.ossService = ossService;
    }

    /**
     * 通过 openid 获取用户 ID（用户不存在时将自动创建用户）
     *
     * @param openid 微信小程序用户唯一标识
     *
     * @since 1.0.0
     */
    public int getUserIdByOpenid(@NonNull String openid) {
        User user = userMapper.findByOpenid(openid);
        if (user != null) {
            return user.getId();
        } else {
            User newUser = User
                .builder()
                .openid(openid)
                .build();

            userMapper.insert(newUser);
            log.info("新注册用户：{}", newUser);

            return newUser.getId();
        }
    }

    /**
     * 获取用户信息
     *
     * @param id 用户 ID
     *
     * @since 1.0.0
     */
    @SneakyThrows
    public UserInfoBO getUserInfo(int id) {
        User user = userMapper.findById(id);
        if (user != null) {
            return UserInfoBO
                .builder()
                .nickName(user.getNickName())
                .avatarUrl(ossService.concatUrl(user.getAvatar()))
                .build();
        }

        throw new Exception("查找的用户不存在");
    }

    /**
     * 更新用户信息（昵称和头像）
     *
     * @param userId 用户 ID
     * @param dto    前端传输的用户信息
     *
     * @since 1.0.0
     */
    public UserInfoBO updateUserInfo(int userId, UserInfoDTO dto) {
        // URL 最后一个数值代表正方形头像大小，默认132，0 代表 640x640 的正方形头像
        String avatarUrl = dto
            .getAvatarUrl()
            .replace("/132", "/0");

        User user = User
            .builder()
            .id(userId)
            .nickName(dto.getNickName())
            .avatar(ossService.dump(OssService.AVATAR_DIR, avatarUrl))
            .build();

        userMapper.update(user);

        return UserInfoBO
            .builder()
            .nickName(user.getNickName())
            .avatarUrl(ossService.concatUrl(user.getAvatar()))
            .build();
    }
}
