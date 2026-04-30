package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;
    @Resource
    private DishFlavorMapper dishFlavorMapper;
    @Resource
    private SetmealMapper setmealMapper;

    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.add(dish);

        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        dishFlavorMapper.insertBatch(flavors);
    }

    @Override
    public PageResult pageFind(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> list = dishMapper.pageFind(dishPageQueryDTO);
        return new PageResult(list.getTotal(),list.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    @Override
    public void deleteById(List<Long> ids) {
        //查询当前菜品是否正在起售
        List<Long> onStatusId =  dishMapper.getByIdForStatus(ids);
        if (!onStatusId.isEmpty()){
            //起售中的菜品不能删除
            throw new RuntimeException(MessageConstant.DISH_ON_SALE);
        }
        //查询当前菜品是否绑定套餐
        List<Long> setmealId =  setmealMapper.getByIdForId(ids);
        for (Long l : setmealId) {
            if (l != null){
                //绑定套餐中的菜品不能删除
                throw new RuntimeException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }
        //删除菜品
        dishMapper.delete(ids);
        //删除菜品对应的口味表的数据
        dishFlavorMapper.deleteByDishId(ids);
    }

    /**
     * 根据id查询菜品和对应的口味
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        Dish dish =  dishMapper.getById(id);
        List<DishFlavor> dishFlavor = dishFlavorMapper.getById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavor);
        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void updateWithFalvor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.updateWithFlavor(dish);

        dishFlavorMapper.deleteByDishId(Collections.singletonList(dish.getId()));

        List<DishFlavor> flavor = dishFlavorMapper.getById(dish.getId());
        if(flavor != null && !flavor.isEmpty()){
            for (DishFlavor dishFlavor : flavor) {
                dishFlavor.setDishId(dish.getId());
            }
            dishFlavorMapper.insertBatch(flavor);
        }
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<DishVO> getByCategoryId(Long categoryId) {
        return dishMapper.getByCatecoryId(categoryId);
    }
}
