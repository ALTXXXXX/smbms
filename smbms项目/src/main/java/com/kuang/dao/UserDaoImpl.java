package com.kuang.dao;

import com.kuang.pojo.Role;
import com.kuang.pojo.User;
import com.mysql.cj.util.StringUtils;
import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    public User getLoginUser(Connection connection, String userCode){
        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = null;
        if(connection != null){
            String sql = "select * from smbms_user where userCode=?";
            Object[] params = {userCode};
            try {
              rs = BaseDao.execute(connection,pstm,rs,sql,params);
               if(rs.next()){
                   user = new User();
                   user.setId(rs.getInt("id"));
                   user.setUserCode(rs.getString("userCode"));
                   user.setUserName(rs.getString("userName"));
                   user.setUserPassword(rs.getString("userPassword"));
                   user.setGender(rs.getInt("gender"));
                   user.setBirthday(rs.getDate("birthday"));
                   user.setPhone(rs.getString("phone"));
                   user.setAddress(rs.getString("address"));
                   user.setUserRole(rs.getInt("userRole"));
                   user.setCreatedBy(rs.getInt("createdBy"));
                   user.setCreationDate(rs.getDate("creationDate"));
                   user.setModifyBy(rs.getInt("modifyBy"));
                   user.setModifyDate(rs.getTimestamp("modifyDate"));
               }
               BaseDao.closeResource(null,pstm,rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
            return user;
    }
    public int updatePwd(Connection connection,int id,String password){
        System.out.println("UserServle:"+password);
        PreparedStatement pstm = null;
        int execute = 0;
        if(connection!=null){
            String sql = "update smbms_user set userPassword = ? where id = ?";
            Object[] params = {password,id};
            try {
                execute =  BaseDao.execute(connection,pstm,sql,params);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            BaseDao.closeResource(null,pstm,null);

        }
        return execute;

    }
    //查询用户总数 根据用户名或者角色
    @Override
    public int getUserCount(Connection connection, String username, int userRole) throws SQLException {
       PreparedStatement pstm = null;
       ResultSet rs = null;
       int count = 0;
       if(connection!=null){
           StringBuilder sql = new StringBuilder();
            sql.append("select count(1) as count from smbms_user u,smbms_role r where u.userRole = r.id");
           ArrayList<Object> list = new ArrayList<>();
           if(!StringUtils.isNullOrEmpty(username)){
                sql.append(" and u.userName like ?");
                list.add("%"+username+"%");
            }
            if(userRole>0){
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }
           Object[] params = list.toArray();
            System.out.println("UserDaoImp1->getUserCount:"+sql.toString());
           rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
                if(rs.next()){
                   count =  rs.getInt("count"); //从结果集中获取数量
                }
                BaseDao.closeResource(null,pstm,rs);
       }

        return count;
    }

    @Override
    public List<User> getUserlist(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<User> userList = new ArrayList<>();
        if(connection!=null){
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole = r.id");
            List<Object> list = new ArrayList<>();
            if(!StringUtils.isNullOrEmpty(userName)){
                sql.append(" and u.userName like ?");
                list.add("%"+userName+"%");
            }
            if(userRole>0){
                sql.append(" and userRole = ?");
                list.add(userRole);
            }

            sql.append(" order by creationDate desc limit ?,?");
            currentPageNo = (currentPageNo-1) * pageSize;
            list.add(currentPageNo);
            list.add(pageSize);
            Object[] params = list.toArray();
            System.out.println("sql --->"+sql.toString());
            rs = BaseDao.execute(connection,pstm,rs,sql.toString(),params);
            while(rs.next()){
                User _user = new User();
                _user.setId(rs.getInt("id"));
                _user.setUserCode(rs.getString("userCode"));
                _user.setUserName(rs.getString("userName"));
                _user.setGender(rs.getInt("gender"));
                _user.setBirthday(rs.getDate("birthday"));
                _user.setPhone(rs.getString("phone"));
                _user.setUserRole(rs.getInt("userRole"));
                _user.setUserRoleName(rs.getString("userRoleName"));
                userList.add(_user);
            }

            BaseDao.closeResource(null,pstm,rs);
        }
        return userList;





    }

    @SneakyThrows
    @Override
    public int add(Connection connection, User user) {
        PreparedStatement pstm=null;
        int updateNum=0;
        if(connection!=null) {
            String sql = "insert into smbms_user (userCode,userName,userPassword," +
                    "userRole,gender,birthday,phone,address,creationDate,createdBy) " +
                    "values(?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {user.getUserCode(), user.getUserName(), user.getUserPassword(),
                    user.getUserRole(), user.getGender(), user.getBirthday(),
                    user.getPhone(), user.getAddress(), user.getCreationDate(), user.getCreatedBy()};
            updateNum = BaseDao.execute(connection, pstm, sql, params);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateNum;
    }

    //根据用户id删除该用户
    public int deleteUserById(Connection connection, Integer delId) throws Exception {
        PreparedStatement pstm=null;
        int deleteNum=0;
        if(connection!=null){
            String sql="DELETE FROM `smbms_user` WHERE id=?";
            Object[] params={delId};
            deleteNum=BaseDao.execute(connection, pstm, sql, params);
            BaseDao.closeResource(null,pstm,null);
        }
        return deleteNum;
    }

    //通过userId查看当前用户信息
    public User getUserById(Connection connection, String id) throws Exception {
        PreparedStatement pstm=null;
        ResultSet rs=null;
        User user = new User();
        if(connection!=null){
            String sql="select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.id=? and u.userRole = r.id";
            Object[] params={id};
            rs = BaseDao.execute(connection, pstm, rs, sql, params);
            while(rs.next()){
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
                user.setUserRoleName(rs.getString("userRoleName"));
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return user;
    }

    //修改用户的信息
    public int modify(Connection connection, User user) throws Exception {
        int updateNum = 0;
        PreparedStatement pstm = null;
        if(null != connection){
            String sql = "update smbms_user set userName=?,"+
                    "gender=?,birthday=?,phone=?,address=?,userRole=?,modifyBy=?,modifyDate=? where id = ? ";
            Object[] params = {user.getUserName(),user.getGender(),user.getBirthday(),
                    user.getPhone(),user.getAddress(),user.getUserRole(),user.getModifyBy(),
                    user.getModifyDate(),user.getId()};
            updateNum = BaseDao.execute(connection,pstm,sql,params);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateNum;
    }


}
