package com.sky.service.impl;

import com.sky.dto.SetmealPageQueryDTO;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Resource
    private SetmealMapper setMealMapper;
    @Resource
    private SetMealDishMapper setMealDishMapper;

    /**
     * 套餐分页查询
     * @param dto
     * @return
     */
    @Override
    public SetmealVO page(SetmealPageQueryDTO dto) {
//        PageHelper.startPage(dto.getPage(), dto.getPageSize());
//        Setmeal setmeal = setMealMapper.page(dto);
//        SetmealDish setmealDish = setMealMapper.page(dto);
//        SetmealVO result = new SetmealVO();
//        BeanUtils.copyProperties(setmeal, result);
        return null;
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        return setMealMapper.getById(id);
    }
}
