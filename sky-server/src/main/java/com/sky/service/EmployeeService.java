package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import org.springframework.web.bind.annotation.PutMapping;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     *  新增员工
     * @param employee
     */
    void insert(EmployeeDTO employee);
    /**
     * 员工分页查询
     * @param page
     * @return
     */
    PageResult pageFind(EmployeePageQueryDTO page);

    /**
     *  修改员工状态
     * @param status
     * @param id
     */
    void StartOrProfit(Integer status, Integer id);

    /**
     * 根据Id查询员工
     * @param id
     * @return
     */
    Employee getById(Integer id);

    /**
     * 修改员工
     * @param employee
     */
    void update(EmployeeDTO employee);
}
