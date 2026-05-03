package com.sky.controller.user;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@Api("C端员工相关接口")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private JwtProperties jwtProperties;

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/user/login")
    @ApiOperation("微信用户登录")
    public Result<UserLoginVO> Longin(@RequestBody UserLoginDTO userLoginDTO) throws Exception {
        log.info("微信用户登录：{}", userLoginDTO);
        //获取微信用户信息
        User user = userService.getUser(userLoginDTO);
        //生成Jwt令牌
        Map<String,Object> claim = new HashMap<>();
        claim.put("id",user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claim);
        return Result.success(new UserLoginVO(user.getId(),user.getOpenid(),token));
    }
}
