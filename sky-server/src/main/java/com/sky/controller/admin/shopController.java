package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController("adminController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关管理")
public class shopController {

    private static final String KEY = "SHOP_STATUS";

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 设置店铺营业状态
     * @param status
     * @return
     */
    @ApiOperation("设置店铺营业状态")
    @PutMapping("/{status}")
    public Result setShopStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态，状态为{}", status==1?"营业中":"打洋中");
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }

    /**
     * 获取店铺营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Object> getShopStatus(){
        log.info("获取店铺营业状态");
        return Result.success(redisTemplate.opsForValue().get(KEY));
    }
}
