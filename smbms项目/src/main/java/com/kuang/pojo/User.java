package com.kuang.pojo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
public class User {
    private Integer id;  //id
    private String userCode;  //用户编码
    private String userName;  //用户名
    private String userPassword;  //用户密码
    private Integer gender;  //性别
    private Date birthday;  //生日
    private String phone;  //电话
    private String address;  //地址
    private Integer userRole;  //用户角色（id）
    private Integer createdBy;  //创建者
    private Date creationDate;  //创建时间
    private Integer modifyBy;  //更新者
    private Date modifyDate;  //更新时间
    private Integer age; //年龄
    private String userRoleName; //用户角色名称
    public User(){

    }
    public Integer getAge(){
        Date date = new Date();
        Integer age = date.getYear()-birthday.getYear();
        return age;

    }


}
