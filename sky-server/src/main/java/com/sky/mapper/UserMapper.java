package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String openid);

    /**
     * 插入数据
     * @param user1
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(User user1);

    /**
     * 根据id查询
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{id}")
    User getById(Long userId);

    /**
     * 根据条件统计用户数量
     * @param map
     * @return
     */
    Integer getUserCount(Map map);
}
