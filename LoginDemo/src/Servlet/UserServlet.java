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
 * Servlet implementation class UserServlet
 */
@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        User user=new User();
        String account = request.getParameter("userAccount");
        LogUtil.log("userAccount:"+account);  
  
        Connection connect = DatabaseUtil.getConnect();  
        try {  
        	Statement statement = (Statement) connect.createStatement();  
            String sql = "select userAccount from " + DatabaseUtil.TABLE_ACCOUNT 
            		+ " where userAccount='" + account + "'";  
            LogUtil.log(sql);  
            ResultSet result = statement.executeQuery(sql);  
            if (result.next()) { // 能查到该账号，说明已经注册过了  
                  
                String sqlSelcet = "select userAccount,userPassword,userName,userSex,userBirth from " + DatabaseUtil.TABLE_ACCOUNT 
	            		+ " where userAccount='" + account + "'"; 
                
                LogUtil.log(sqlSelcet); 
                ResultSet result_select = DatabaseUtil.query(sqlSelcet);
                while (result_select.next()) { 
   	             user.setAccount(result_select.getString("userAccount"));
   	             user.setName(result_select.getString("userName"));
   	             user.setPassword(result_select.getString("userPassword"));
   	             user.setSex(result_select.getString("userSex"));
   	             user.setBirth(result_select.getString("userBirth"));
                } 
              
            } else {
            }  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
        response.setContentType("text/html;charset=utf-8"); // 设置响应报文的编码格式
        
        PrintWriter out = response.getWriter();
        String json=JSON.toJSONString(user);
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
