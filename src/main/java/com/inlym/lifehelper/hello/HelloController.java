package com.inlym.lifehelper.hello;

import com.inlym.lifehelper.common.annotation.UserId;
import com.inlym.lifehelper.common.annotation.UserPermission;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用接口调试控制器
 *
 * <h2>主要用途
 * <p>用于接口调试，用于定义一些与业务无关的接口。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2022-01-22
 * @since 1.0.0
 */
@RestController
public class HelloController {
    /**
     * 根路由
     *
     * <h2>主要用途
     * <p>确认项目是否正常启动时，会直接输入域名访问，这个接口主要用于这个调试。
     */
    @GetMapping("/")
    public String index() {
        return "hello lifehelper";
    }

    /**
     * 接口连通性测试
     *
     * <h2>主要用途
     * <p>用于负载均衡健康检查，只要项目没挂，这个接口就可以正常返回。
     *
     * <h2>注意事项
     * <p>项目内部进行日志记录时，不要记录当前接口，因为负载均衡的健康检查频率为1次/秒，记录日志会造成大量无效日志。
     */
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    /**
     * 反射请求内容
     *
     * <h2>主要用途
     * <p>用于将请求内容解析并作为响应数据返回。
     *
     * @param params  请求参数
     * @param headers 请求头
     * @param body    请求数据
     */
    @RequestMapping("/debug")
    public Map<String, Object> debug(@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers, @RequestBody(required = false) Object body) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("params", params);
        map.put("headers", headers);

        if (body != null) {
            map.put("body", body);
        }

        return map;
    }

    /**
     * 用户登录鉴权调试
     *
     * <h2>说明
     * <p>携带鉴权信息访问时，如果鉴权通过会返回用户 ID，否则报错。
     */
    @GetMapping("/userid")
    @UserPermission
    public int getUserId(@UserId int userId) {
        return userId;
    }
}
