package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (Objects.equals(employee.getStatus(), StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    public void insert(EmployeeDTO employee) {
        Employee emp = new Employee();

        //拷贝属性
        BeanUtils.copyProperties(employee, emp);

        //设置账号状态
        emp.setStatus(StatusConstant.ENABLE);

        //设置默认密码
        emp.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //设置操作/更新时间
//        emp.setUpdateTime(LocalDateTime.now());
//        emp.setCreateTime(LocalDateTime.now());
//
//        //设置操作人的id和更新
//        Long currentId = BaseContext.getCurrentId();
//        emp.setUpdateUser(currentId);
//        emp.setCreateUser(currentId);

        employeeMapper.insert(emp);
        BaseContext.removeCurrentId();
    }

    /**
     * 员工分页查询
     * @param page
     * @return
     */
    @Override
    public PageResult pageFind(EmployeePageQueryDTO page) {
        PageHelper.startPage(page.getPage(), page.getPageSize());
        Page<Employee> pageResult = (Page<Employee>) employeeMapper.pageFind(page);
        return new PageResult(pageResult.getTotal(),pageResult.getResult());
    }

    /**
     * 状态修改
     * @param status
     * @param id
     */
    @Override
    public void StartOrProfit(Integer status, Integer id) {
        Employee id1 = Employee.builder().status(status).id(Long.valueOf(id)).build();
        employeeMapper.update(id1);
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @Override
    public Employee getById(Integer id) {
        Employee emp = employeeMapper.getById(id);
        emp.setPassword("****");
        return emp;
    }

    /**
     * 修改员工信息
     * @param employee
     */
    @Override
    public void update(EmployeeDTO employee) {
        Employee emp = new Employee();
        BeanUtils.copyProperties(employee, emp);
//        emp.setUpdateTime(LocalDateTime.now());
//        emp.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(emp);
    }

}
