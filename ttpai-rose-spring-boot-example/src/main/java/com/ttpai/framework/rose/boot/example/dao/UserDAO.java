package com.ttpai.framework.rose.boot.example.dao;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;

@DAO
public interface UserDAO {

    @SQL("select count(*) from user")
    Integer count();

}
