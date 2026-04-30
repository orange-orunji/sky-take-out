package com.sky.mapper;


import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealWithDishMapper {

    /**
     * 批量插入套餐和菜品的关联关系
     * @param setmealDishes
     */
    @AutoFill(value = OperationType.INSERT)
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除套餐和菜品的关联关系
     * @param ids
     */
    void deleteBySetmealId(List<Long> ids);

    /**
     * 批量更新
     * @param setmealDishes
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(List<SetmealDish> setmealDishes);
}
