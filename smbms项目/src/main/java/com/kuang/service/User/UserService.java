package com.kuang.service.User;
import com.kuang.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserService {
    //用户登录
    public User login(String userCode,String password);
    //根据用户id修改密码
    public boolean updatePwd(int id, String pwd) throws SQLException, ClassNotFoundException;
    //查询记录数
    public int getUserCount(String username,int userRole);
    //根据条件查询用户列表
    public List<User> getUserList(String queryUserName,int queryUserRole,int currentPageNo,int pageSize);
    //添加用户
    public boolean add(User user);
    //修改用户信息
    public Boolean modify(User user) throws Exception;

    //根据用户编码，判断用户是否存在
    public User selectUserCodeExist(String userCode);

    //根据用户id删除用户
    public boolean deleteUserById(Integer delId);

    //根据用户id得到当前用户
    public User getUserById(String id);

}
