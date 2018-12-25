package com.ocean.service;

import com.ocean.pojo.UserInfo;

import java.util.List;

public interface IUserService {
    List findAllUser() throws  Exception;
    public List<UserInfo> getUserInfo() throws Exception;
}
