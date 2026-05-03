package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController("userStatusController")
@RequestMapping("/user/shop")
@Api(tags = "店铺相关管理")
public class shopController {

    private static final String KEY = "SHOP_STATUS";

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 获取店铺营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("用户端获取店铺营业状态")
    public Result<Object> getShopStatus(){
        log.info("获取店铺营业状态");
        return Result.success(redisTemplate.opsForValue().get(KEY));
    }
}
