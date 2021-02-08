package com.ttpai.framework.rose.boot.example.dao;

import com.ttpai.framework.rose.boot.example.model.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select count(*) from user")
    Integer count();

    List<UserVO> selectAll();

}
