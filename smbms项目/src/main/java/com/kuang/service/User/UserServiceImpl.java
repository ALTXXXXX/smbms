package com.kuang.service.User;

import com.kuang.dao.BaseDao;
import com.kuang.dao.UserDao;
import com.kuang.dao.UserDaoImpl;
import com.kuang.pojo.User;
import lombok.SneakyThrows;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {
   //业务层会调用dao层，所以我们引入dao层
    private UserDao userDao;
    public UserServiceImpl() {
        userDao = new UserDaoImpl();

    }
    public User login(String userCode, String password){
        Connection connection = null;
        User user = null;

        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection,userCode);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        //通过业务层调用对应数据库操作
//            BaseDao.closeResource(connection,null,null);

        return user;
    }
    public boolean updatePwd(int id, String pwd) {
        System.out.println("UserServle:"+pwd);
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            if(userDao.updatePwd(connection,id,pwd)>0){
                flag = true;
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    @SneakyThrows
    @Override
    public int getUserCount(String username, int userRole) {
        Connection connection = BaseDao.getConnection();
        int userCount = userDao.getUserCount(connection, username, userRole);
         BaseDao.closeResource(connection,null,null);
         return userCount;

    }
    @SneakyThrows
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize){
        Connection connection = null;
       List<User> userList = null;
        System.out.println("UserServiceImpl:queryUserName--->"+queryUserName);
        System.out.println("UserServiceImpl:queryUserRole--->"+queryUserRole);
        System.out.println("UserServiceImpl:currentPageNo--->"+currentPageNo);
        System.out.println("UserServiceImpl:pageSize--->"+pageSize);
        connection = BaseDao.getConnection();
        List<User> userlist = userDao.getUserlist(connection, queryUserName, queryUserRole, currentPageNo, pageSize);
        BaseDao.closeResource(connection,null,null);
        return userlist;
    }
    public boolean add(User user) {
        boolean flag = false;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();//获得连接
            connection.setAutoCommit(false);//开启JDBC事务管理
            int updateRows = userDao.add(connection,user);
            connection.commit();
            if(updateRows > 0){
                flag = true;
                System.out.println("add success!");
            }else{
                System.out.println("add failed!");
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                System.out.println("rollback==================");
                connection.rollback();//失败就回滚
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }finally{
            //在service层进行connection连接的关闭
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }
    public Boolean modify(User user) {
        Boolean flag=false;
        Connection connection=null;
        try {
            connection=BaseDao.getConnection();
            connection.setAutoCommit(false);//开启JDBC事务
            int updateNum = userDao.modify(connection, user);//执行修改sql
            connection.commit();//提交事务
            if(updateNum>0){
                flag=true;
                System.out.println("修改用户成功");
            }else{
                System.out.println("修改用户失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //若抛出异常，则说明修改失败需要回滚
            System.out.println("修改失败，回滚事务");
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    public User selectUserCodeExist(String userCode) {

        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(connection, null, null);
        }
        return user;
    }

    @SneakyThrows
    public boolean deleteUserById(Integer delId) {
        Boolean flag=false;
        Connection connection=null;
        connection=BaseDao.getConnection();
        try {
            int deleteNum=userDao.deleteUserById(connection,delId);
            if(deleteNum>0)flag=true;
        } catch (Exception e) {
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    public User getUserById(String id) {
        User user = new User();
        Connection connection=null;
        try {
            connection=BaseDao.getConnection();
            user = userDao.getUserById(connection,id);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return  user;
    }


}
