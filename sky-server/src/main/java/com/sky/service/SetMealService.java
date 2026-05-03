package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {
    /**
     * 套餐分页查询
     *
     * @param dto
     * @return
     */
    PageResult page(SetmealPageQueryDTO dto);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 新增套餐
     * @param dto
     */
    void saveWithDish(SetmealDTO dto);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 套餐起售停售
     * @param id
     * @param status
     */
    void setStatus(Integer id, Integer status);

    /**
     * 修改套餐
     * @param dto
     */
    void update(SetmealDTO dto);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
     List<SetmealVO> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
