package com.purplespace.controller;


import com.purplespace.entity.Dept;
import com.purplespace.mapper.DeptMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-12-15:28
 */
@RequestMapping("/dept")
@RestController
public class DeptController {
    @Resource
    private DeptMapper deptMapper;
    private List<Dept> createDept(){
        List<Dept> deptArrayList = new ArrayList<>();
        Dept dept = new Dept(1L, "研发部", "1");
        Dept dept1 = new Dept(2L, "研发部", "1");
        Dept dept2 = new Dept(3L, "研发部", "1");
        deptArrayList.add(dept);
        deptArrayList.add(dept1);
        deptArrayList.add(dept2);
        return deptArrayList;
    }
    @RequestMapping("/add")
    public String test() {
        List<Dept> dept = createDept();
        Dept dept1 = dept.get(0);
//        int i = deptMapper.addOne(dept1);
//        System.out.println(i);
        int add = deptMapper.addBranch(dept);
        if (add>0){
            return "oK";
        }
        return "fail";
    }

    @RequestMapping("/updateBranch")
    public Integer updateBranch() {
        List<Dept> deptArrayList = new ArrayList<>();
        Dept dept = new Dept(1L, "市场部", "2");
        Dept dept1 = new Dept(2L, "人事部", "2");
        Dept dept2 = new Dept(3L, "研发部", "2");
        deptArrayList.add(dept);
        deptArrayList.add(dept1);
        deptArrayList.add(dept2);
        int i = deptMapper.updateBranch(deptArrayList);
        return i;
    }
    @RequestMapping("/delBranch")
    public Integer delBranch(){
        List<Long> longList = new ArrayList<>();
        longList.add(1L);
        longList.add(2L);
        longList.add(3L);
        return deptMapper.delBranch(longList);
    }

}
