package com.kuang.servlet.user;
import com.kuang.pojo.User;
import com.kuang.service.User.UserServiceImpl;
import com.kuang.util.Constants;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class LoginServlet extends HttpServlet {
   //控制层调用业务层
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("LoginServlet---start...");
        //获得前端页面用户名和密码
        String username = req.getParameter("userCode");
        String password = req.getParameter("userPassword");
        UserServiceImpl userService = new UserServiceImpl();
        User user = userService.login(username, password); //已经把登录的人查出来了
        if(user!=null&&user.getUserPassword().equals(password)){  //查有此人，可以登录
            //将用户放入session中
            req.getSession().setAttribute(Constants.USER_SESSION, user);
            //跳转到主页 重定向
            resp.sendRedirect("jsp/frame.jsp");
        }else{ //查无此人，无法登录 顺便提示用户名或密码错误
            req.setAttribute("error","用户名或者密码不正确");

            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }


    }
}
