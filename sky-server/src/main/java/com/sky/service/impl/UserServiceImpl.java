package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Resource
    private  WeChatProperties weChatProperties;
    @Resource
    private  UserMapper userMapper;

    /**
     * 微信登录
     * @param user
     * @return
     */
    @Override
    public User getUser(UserLoginDTO user) throws Exception {

        String openId = getOpenId(user.getCode());

        //判断openId是否为空
        if(openId == null) throw new Exception(MessageConstant.LOGIN_FAILED);
        // 查询数据库，判断当前用户是否为新用户
        User user1 =  userMapper.getByOpenId(openId);
        // 如果是新用户，自动完成注册
        if(user1 == null){
            user1 = User.builder()
                    .openid(openId)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user1);
        }
        // 返回用户信息
        return user1;
    }

    /**
     * 获取微信用户的openid
     * @param code
     * @return
     */
    private String getOpenId(String code){
        Map<String,String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");

        String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);

        JSONObject parse = JSON.parseObject(json);

        return parse.getString("openid");
    }
}
