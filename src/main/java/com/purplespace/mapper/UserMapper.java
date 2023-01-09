package com.purplespace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.purplespace.entity.User;
import org.mapstruct.Mapper;

/**
 * @author loongzhang
 * @Description DOING
 * @date 2022-12-12-15:23
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {


    int reduceMoney(Long fromUserId, Long changeMoney);
}
