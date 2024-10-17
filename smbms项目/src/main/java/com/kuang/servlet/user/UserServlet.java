package com.kuang.servlet.user;
import com.alibaba.fastjson.JSONArray;
import com.kuang.pojo.Role;
import com.kuang.pojo.User;
import com.kuang.service.Role.RoleService;
import com.kuang.service.Role.RoleServiceImpl;
import com.kuang.service.User.UserService;
import com.kuang.service.User.UserServiceImpl;
import com.kuang.util.Constants;
import com.kuang.util.PageSupport;
import com.mysql.cj.util.StringUtils;
import lombok.SneakyThrows;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
//实现servlet复用
public class UserServlet extends HttpServlet {
    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if(method!=null&&method.equals("savepwd")){
            this.updatepwd(req,resp);
        }else if(method!=null&&method.equals("pwdmodify")){
             this.modifyPwd(req,resp);
        }else if(method!=null&&method.equals("query")){
             this.query(req,resp);
        }else if(method!=null&&method.equals("add")){
            this.add(req,resp);
        }else if(method!=null&&method.equals("getrolelist")){
            this.getRoleList(req,resp);
        }else if(method != null && method.equals("ucexist")){
            //查询当前用户编码是否存在
            this.userCodeExist(req, resp);
        }else if(method != null && method.equals("deluser")){
            //删除用户
            this.delUser(req, resp);
        }else if(method != null && method.equals("view")){
            //通过用户id得到用户
            this.getUserById(req, resp,"userview.jsp");
        }else if(method != null && method.equals("modifyexe")){
            //验证用户
            this.modify(req, resp);
        }
    }

    public void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String queryUsername = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;
       UserServiceImpl userService = new UserServiceImpl();
       int pageSize = 5;
       int currentPageNo = 1;
        if(queryUsername==null){
            queryUsername="";
        }
        if(temp!=null&&!temp.equals("")){
            queryUserRole = Integer.parseInt(temp); //给查询赋值 0,1,2,3
        }
       if(pageIndex!=null){
        currentPageNo = Integer.parseInt(pageIndex);

       }
       //获取用户总数
        int totalCount = userService.getUserCount(queryUsername, queryUserRole);
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);
        //控制首页和尾页
        int totalPageCount = pageSupport.getTotalPageCount();
        //如果页面要小于1了，就显示第一页
        if(currentPageNo<1){
            currentPageNo = 1;
        }else if(currentPageNo>totalPageCount){
            currentPageNo = totalPageCount;
        }
        //获取用户列表展示
        List<User> userList = userService.getUserList(queryUsername, queryUserRole, currentPageNo, pageSize);
          req.setAttribute("userList",userList);
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList",roleList);
        req.setAttribute("totalCount",totalCount);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("queryUserName",queryUsername);
        req.setAttribute("queryUserRole",queryUserRole);
        req.setAttribute("totalPageCount",totalPageCount);
        //返回前端
        req.getRequestDispatcher("userlist.jsp").forward(req,resp);



    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
    @SneakyThrows
    public void updatepwd(HttpServletRequest req, HttpServletResponse resp){
        //从session里面拿到ID
        User o = (User)req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        System.out.println("UserServlet: "+newpassword);
        boolean flag = false;

        if(o!=null && !StringUtils.isNullOrEmpty(newpassword)){
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(o.getId(), newpassword);
            if(flag){
                req.setAttribute("message","修改密码成功，请退出,重新登录");
                //密码修改成功，移除session
                System.out.println("成功修改密码");
                req.getSession().removeAttribute(Constants.USER_SESSION);
            }else{
                req.setAttribute("message","修改密码失败");
                System.out.println("失败");
            }
        }else{
            req.setAttribute("message","新密码有问题");
        }
        req.getRequestDispatcher("pwdmodify.jsp").forward(req,resp);

    }
    //验证旧密码
    public void modifyPwd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");
        //万能的Map:结果集
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(o==null){  //session过期
            resultMap.put("result","sessionerror");
        }else if(StringUtils.isNullOrEmpty(oldpassword)){ //输入密码为空
            resultMap.put("result","error");
        }else{
            String userPassword = ((User) o).getUserPassword();//session中的密码
            if(oldpassword.equals(userPassword)){
                resultMap.put("result","true");
            }else{
                resultMap.put("result","false");
            }

        }
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        //JSONArray 阿里云工具类，转换格式
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }
    private void getRoleList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        List<Role> roleList = null;
        RoleService roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        //把roleList转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(roleList));
        outPrintWriter.flush();
        outPrintWriter.close();
    }
    private void delUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String id = req.getParameter("uid");
        Integer delId = 0;
        try{
            delId = Integer.parseInt(id);
        }catch (Exception e) {
            // TODO: handle exception
            delId = 0;
        }
        //需要判断是否能删除成功
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(delId <= 0){
            resultMap.put("delResult", "notexist");
        }else{
            UserService userService = new UserServiceImpl();
            if(userService.deleteUserById(delId)){
                resultMap.put("delResult", "true");
            }else{
                resultMap.put("delResult", "false");
            }
        }

        //把resultMap转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();
        outPrintWriter.close();
    }
    private void getUserById(HttpServletRequest req, HttpServletResponse resp,String url) throws ServletException, IOException{

        String id = req.getParameter("uid");
        if(!StringUtils.isNullOrEmpty(id)){
            //调用后台方法得到user对象
            UserService userService = new UserServiceImpl();
            User user = userService.getUserById(id);
            req.setAttribute("user", user);
            req.getRequestDispatcher(url).forward(req, resp);
        }
    }
    private void modify(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        //需要拿到前端传递进来的参数
        String id = req.getParameter("uid");;
        String userName = req.getParameter("userName");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        //创建一个user对象接收这些参数
        User user = new User();
        user.setId(Integer.valueOf(id));
        user.setUserName(userName);
        user.setGender(Integer.valueOf(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        user.setModifyBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());

        //调用service层
        UserServiceImpl userService = new UserServiceImpl();
        Boolean flag = userService.modify(user);

        //判断是否修改成功来决定跳转到哪个页面
        if(flag){
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
        }else{
            req.getRequestDispatcher("usermodify.jsp").forward(req, resp);
        }

    }
    @SneakyThrows
    public void add(HttpServletRequest req, HttpServletResponse resp){
        System.out.println("当前正在执行增加用户操作");
        //从前端得到页面的请求的参数即用户输入的值
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        //String ruserPassword = req.getParameter("ruserPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");
        //把这些值塞进一个用户属性中
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setAddress(address);
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        //查找当前正在登陆的用户的id
        user.setCreatedBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        UserServiceImpl userService = new UserServiceImpl();
        Boolean flag = userService.add(user);
        //如果添加成功，则页面转发，否则重新刷新，再次跳转到当前页面
        if(flag){
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
        }else{
            req.getRequestDispatcher("useradd.jsp").forward(req,resp);
        }



    }
    private void userCodeExist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        //先拿到用户的编码
        String userCode = req.getParameter("userCode");
        //用一个hashmap，暂存现在所有现存的用户编码
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(StringUtils.isNullOrEmpty(userCode)){
            //userCode == null || userCode.equals("")
            //如果输入的这个编码为空或者不存在，说明可用
            resultMap.put("userCode", "exist");
        }else{//如果输入的编码不为空，则需要去找一下是否存在这个用户
            UserService userService = new UserServiceImpl();
            User user = userService.selectUserCodeExist(userCode);
            if(null != user){
                resultMap.put("userCode","exist");
            }else{
                resultMap.put("userCode", "notexist");
            }
        }
        //把resultMap转为json字符串以json的形式输出
        //配置上下文的输出类型
        resp.setContentType("application/json");
        //从response对象中获取往外输出的writer对象
        PrintWriter outPrintWriter = resp.getWriter();
        //把resultMap转为json字符串 输出
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();//刷新
        outPrintWriter.close();//关闭流
    }


}
