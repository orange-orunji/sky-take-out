package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.SetmealWithDishMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Resource
    private SetmealMapper setMealMapper;
    @Resource
    private SetmealWithDishMapper SetmealWithDishMapper;

    /**
     * 套餐分页查询
     *
     * @param dto
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(),dto.getPageSize());
        Page<SetmealVO> page = setMealMapper.page(dto);
        return new PageResult(page.getTotal(),page.getResult());
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

    /**
     * 新增套餐
     * @param dto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDTO dto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(dto,setmeal);
        setMealMapper.save(setmeal);
        List<SetmealDish> setmealDishes = dto.getSetmealDishes();
        if(setmealDishes!=null && !setmealDishes.isEmpty()){
            Long id = setmeal.getId();
            setmealDishes.forEach(dish -> dish.setSetmealId(id));
            SetmealWithDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        SetmealWithDishMapper.deleteBySetmealId(ids);
        List<Object> byIds = setMealMapper.getByIds(ids);
        if (byIds!=null && !byIds.isEmpty()){
            throw new RuntimeException(MessageConstant.SETMEAL_ON_SALE);
        }
        setMealMapper.deleteByIds(ids);
    }

    /**
     * 套餐起售停售
     * @param id
     * @param status
     */
    @Override
    public void setStatus(Integer id, Integer status) {
        setMealMapper.setStatus(id,status);
    }
}
