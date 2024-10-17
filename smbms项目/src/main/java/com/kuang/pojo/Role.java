package com.kuang.pojo;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
@Getter
@Setter
public class Role {
    private Integer id; //id
    private String roleCode; //角色编号
    private String roleName; //角色名称
    private Integer createdBy; //创建者
    private Date creationDate; //创建时间
    private Integer modifyBy; //更新者
    private Date modifyDate; //更新时间


}
