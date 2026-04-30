package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
public class SetMealController {
    @Resource
    private SetMealService setMealService;

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("根据id查询套餐：{}", id);
        return Result.success(setMealService.getById(id));
    }

    /**
     * 套餐分页查询
     * @param "page"
     * @param "pageSize"
     * @param "name"
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO  dto){
        log.info("分页查询：{}", dto);
        return Result.success(setMealService.page(dto));
    }

    /**
     * 新增套餐
     * @param "dishDTO"
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO dto){
        log.info("新增套餐：{}", dto);
        setMealService.saveWithDish(dto);
        return Result.success();
    }


    /**
     * 批量删除
     * @param ids
     */
    @ApiOperation("批量删除")
    @DeleteMapping
    public Result deleteByIds(@RequestParam List<Long> ids){
        log.info("批量删除：{}", ids);
        setMealService.deleteByIds(ids);
        return Result.success();
    }

}
