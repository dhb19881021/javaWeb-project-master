package com.ocean.service.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocean.dao.UserInfoMapper;
import com.ocean.pojo.UserInfo;
import com.ocean.service.IUserService;
import com.ocean.utils.JedisUtil;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service(value = "userService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserInfoMapper userDao;//这里可能会报错，但是并不会影响


    @Autowired
    private JedisUtil.Strings jedisStrings;


    // Redis的安装和配置      启动之后再来运行这个是可以的  也就是走缓存了
    //    https://www.jianshu.com/p/6b5eca8d908b


    @Autowired
    private JedisUtil.Keys jedisKeys;


    private static String userList = "userList";
    private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public List<UserInfo> getUserInfo() throws Exception {
        String key = userList;
        List<UserInfo> userList = null;
        ObjectMapper mapper = new ObjectMapper();
        //判断是否有缓存
        if (!jedisKeys.exists(key)) {
            //没哟缓存  则查询数据库
            userList = userDao.findAllUser();
            String jsonString = mapper.writeValueAsString(userList);
            jedisStrings.set(key, jsonString);
        } else {
            //有缓存  这个时候从缓存中通过key获取到缓存的数据  然后转换为我们需要的数据
            String jsonString = jedisStrings.get(key);
            JavaType javaType = mapper.getTypeFactory()
                    .constructParametricType(List.class, UserInfo.class);
            userList = mapper.readValue(jsonString, javaType);
        }
        return userList;
    }
    @Override
    public List findAllUser() throws Exception{
       return getUserInfo();
    }
}
