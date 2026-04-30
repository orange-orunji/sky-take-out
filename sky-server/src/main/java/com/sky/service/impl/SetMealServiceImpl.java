package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Resource
    private SetmealMapper setMealMapper;

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
}
