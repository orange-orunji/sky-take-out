package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Resource
    private DishService dishService;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Cacheable(cacheNames = "categoryCache", key = "#categoryId")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
//
//        log.info("根据分类id查询菜品：{}", categoryId);
//        //创建redis查询字符串
//        String KEY = "dish_" + categoryId;
//        //查询redis中是否存在缓存
//        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(KEY);
//        //缓存存在
//        if (list != null && !list.isEmpty()) {
//            return Result.success(list);
//        }

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        //缓存不存在
        List<DishVO>list = dishService.listWithFlavor(dish);
//        redisTemplate.opsForValue().set(KEY, list);
        return Result.success(list);
    }

}
