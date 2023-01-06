package com.purplespace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.purplespace.entity.Dept;

import java.util.List;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-21-17:00
 */
public interface DeptMapper extends BaseMapper<Dept> {
    int addBranch(List<Dept> deptsList);
    int addOne(Dept dept);
    int updateBranch(List<Dept> deptList);
    int delBranch(List<Long> longsList);
}
