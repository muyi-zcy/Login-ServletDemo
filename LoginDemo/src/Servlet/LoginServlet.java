package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import Util.DatabaseUtil;
import Util.LogUtil;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		int code = 0;  
  
        String account = request.getParameter("userAccount");  
        String password = request.getParameter("userPassword");  
        LogUtil.log("userAccount:"+account + ";userPassword:" + password);  
  
        Connection connect = DatabaseUtil.getConnect();  
        try {  
            Statement statement = (Statement) connect.createStatement();  
            String sql = "select userAccount from " + DatabaseUtil.TABLE_ACCOUNT +
            		" where userAccount='" + account  
                    + "' and userPassword='" + password + "'";  
            LogUtil.log(sql);  
            ResultSet result = statement.executeQuery(sql);  
            if (result.next()) { 
            	// 能查到该账号，说明已经注册过了  
                code = 200;  
               //"登陆成功";  
            } else {  
                code = 100;  
               //"登录失败，密码不匹配或账号未注册";  
            }  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
        response.setContentType("text/html;charset=utf-8"); // 设置响应报文的编码格式
         
        PrintWriter out = response.getWriter();
        String json=JSON.toJSONString(code);
		out.println(json);		
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
