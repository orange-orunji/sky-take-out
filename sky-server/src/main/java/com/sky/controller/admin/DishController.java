package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
public class DishController {
    @Resource
    private DishService dishService;
    @Resource
    private DishMapper dishMapper;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    @CacheEvict(cacheNames = "categoryCache",allEntries = true)
    public Result add(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
//        String s = "dish_" + dishDTO.getCategoryId();
//        redisTemplate.delete(s);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        return Result.success(dishService.pageFind(dishPageQueryDTO));
    }
    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    @CacheEvict(cacheNames = "categoryCache",allEntries = true)
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品：{}", ids);
//        cleanCache();
        dishService.deleteById(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}", id);
        return Result.success(dishService.getById(id));
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    @CacheEvict(cacheNames = "categoryCache",allEntries = true)
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}", dishDTO);
//        cleanCache();
        dishService.updateWithFalvor(dishDTO);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    @CacheEvict(cacheNames = "categoryCache",  key= "#categoryId")
    public Result<Object> getBySetMealId(@RequestParam("categoryId") Long categoryId){
        log.info("根据分类id查询菜品：{}", categoryId);
        return Result.success(dishService.getByCategoryId(categoryId));
    }

    /**
     * 起售、停售菜品
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("起售、停售菜品")
    @CacheEvict(cacheNames = "categoryCache",allEntries = true)
    public Result<Object> setStatus(@PathVariable Integer status,@RequestParam("id") Long id){
        log.info("起售、停售菜品：{}", status);
        dishMapper.setStatus(status,id);
//        cleanCache();
        return Result.success();
    }

//    private void cleanCache(){
//        Set o = redisTemplate.keys("dish_*");
//        redisTemplate.delete(o);
//    }
}
