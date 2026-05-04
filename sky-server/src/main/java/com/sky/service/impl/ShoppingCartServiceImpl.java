package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Resource
    private ShoppingCartMapper shoppingCaryMapper;
    @Resource
    private DishMapper dishMapper;
    @Resource
    private SetmealMapper setMealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {

    //查询当前商品是否存在购物车
    ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

    List<ShoppingCart> list = shoppingCaryMapper.list(shoppingCart);

    //若存在，则更新数量
        if(list != null && !list.isEmpty()){
        ShoppingCart cart = list.get(0);
        cart.setNumber(cart.getNumber()+1);
        shoppingCaryMapper.update(cart);
    }
    //不存在则添加
        else{
        Long setmealId = shoppingCartDTO.getSetmealId();
        //判断传入的是否为套餐
        Long dishId = shoppingCartDTO.getDishId();
        if(dishId != null){
            //本次添加到购物车的是菜品
            Dish dish = dishMapper.getById(dishId);
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
        }else{
            //本次添加到购物车的是套餐
            SetmealVO setmeal = setMealMapper.getById(setmealId);
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());
        }
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCaryMapper.insert(shoppingCart);
    }}

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        ShoppingCart.ShoppingCartBuilder shoppingCartBuilder =
                ShoppingCart.builder().userId(BaseContext.getCurrentId());
        return shoppingCaryMapper.list(shoppingCartBuilder.build());
    }
    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        shoppingCaryMapper.clean(BaseContext.getCurrentId());
    }

    /**
     * 减购物车
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart cart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,cart);
        cart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCaryMapper.list(cart);
        if(list != null && !list.isEmpty()){
            cart = list.get(0);
            if(cart.getNumber() == 1){
                shoppingCaryMapper.delete(cart.getId());
            }else{
                cart.setNumber(cart.getNumber()-1);
                shoppingCaryMapper.update(cart);
            }
        }
    }
    //查询当前添加的是否在购物车中
//    ShoppingCart shoppingCart = new ShoppingCart();
//        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
//        shoppingCart.setUserId(BaseContext.getCurrentId());
//    List<ShoppingCart> list = shoppingCaryMapper.list(shoppingCart);
//    //若存在更新数量
//        if (list != null && !list.isEmpty()) {
//        ShoppingCart cart = list.get(0);
//        cart.setNumber(cart.getNumber()+1);
//        shoppingCaryMapper.update(cart);
//    }
//    //不存在则添加
//        else{
//        Long setmealId = shoppingCartDTO.getSetmealId();
//        Long dishId = shoppingCartDTO.getDishId();
//        if(setmealId != null){
//            SetmealVO setmealVO = setMealMapper.getById(setmealId);
//            shoppingCart.setImage(setmealVO.getImage());
//            shoppingCart.setName(setmealVO.getName());
//            shoppingCart.setAmount(setmealVO.getPrice());
//        }else{
//            Dish byId = dishMapper.getById(dishId);
//            shoppingCart.setImage(byId.getImage());
//            shoppingCart.setName(byId.getName());
//            shoppingCart.setAmount(byId.getPrice());
//        }
//        shoppingCart.setNumber(1);
//        shoppingCart.setCreateTime(LocalDateTime.now());
//        shoppingCaryMapper.insert(shoppingCart);
//    }


}
