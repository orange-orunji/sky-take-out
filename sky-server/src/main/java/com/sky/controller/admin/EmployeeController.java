package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;
    @Resource
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result<String> insert(@RequestBody EmployeeDTO employee){
        log.info("新增员工：{}", employee);
        employeeService.insert(employee);
        return Result.success();
    }

    /**
    * 员工分页查询
    * @param page
    *  @return
     */
    @ApiOperation("员工分页查询")
    @GetMapping("/page")
    public Result<PageResult> pageFind(EmployeePageQueryDTO  page){
        log.info("员工分页查询,参数为：{}", page);
        PageResult o = employeeService.pageFind(page);
        return Result.success(o);
    }

    /**
     * 状态修改
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("员工状态修改")
    @PostMapping("/status/{status}")
    public Result<String> setStatus(@PathVariable Integer status,@RequestParam("id") Integer id){
        log.info("修改员工状态，id{}状态为{}", id, status);
        employeeService.StartOrProfit(status,id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据Id员工查询")
    public Result<Employee> getById(@PathVariable Integer id){
        log.info("根据Id员工查询，id{}", id);
        return Result.success(employeeService.getById(id));
    }

    /**
     * 修改员工信息
     * @param employee
     */
    @PutMapping
    @ApiOperation("修改员工信息")
    public Result update(@RequestBody EmployeeDTO employee){
        log.info("修改员工信息：{}", employee);
        employeeService.update(employee);
        return Result.success();
    }
}
