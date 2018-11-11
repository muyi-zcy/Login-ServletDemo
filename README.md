---
title: Android+tomcat+MySQL实现简单的登录注册
---
# 准备工作
事先准备好开发环境：
编译器：myeclipse+Android studio；
数据库：MySQL
服务器：Tomcat


# 服务器端环境
1. 新建一个Java Web Project；
2. 把必要的包先加入到lib然后在add to build path(因为需要用到数据库和json格式数据传输所以需要用到这两个[jar包](https://pan.baidu.com/s/1IFYz2IDTgXiZZUQxeyGW-w)点击下载)
3. 建立数据库，可以采用Navicat可视化的操作数据库，也可以采用命令行指令操作数据库。
这样服务器端的环境就准备好了，可以开始准备编写代码：

## 链接数据库

```java
package Util;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class DatabaseUtil {
	public static final String TABLE_ACCOUNT = "userInforma";//用户信息表

    // connect to MySql database  
    public static Connection getConnect() {  
        String url = "jdbc:mysql://localhost:3306/demo?autoReconnect=true&useUnicode=true&characterEncoding=gbk&mysqlEncoding=utf8";
        Connection connecter = null;  
        try {  
            Class.forName("com.mysql.jdbc.Driver"); // java反射，固定写法  
            connecter = (Connection) DriverManager.getConnection(url, "root", "*******");//******是自己的密码 
            LogUtil.log("创建数据库连接"); 
        } catch (ClassNotFoundException e) {  
            e.printStackTrace();  
        } catch (SQLException e) {  
            System.out.println("SQLException: " + e.getMessage());  
            System.out.println("SQLState: " + e.getSQLState());  
            System.out.println("VendorError: " + e.getErrorCode());   
        }  
        return connecter;  
    }  
    public static ResultSet query(String querySql) throws SQLException {
		Statement stateMent = (Statement) getConnect().createStatement();
		return stateMent.executeQuery(querySql);
	}
}

```
用来专门连接数据库的类。、
> 自己编写的日志打印类：

```java
package Util;
//自定义的日志打印工具类
public class LogUtil {
	public static void log(String message){
		System.out.println(message);
	}
}

```
## 用户类

```java
package Servlet;

public class User {

	private int id;//序号
	private String account;//手机号码
	private String name;//昵称
	private String password;//密码
	private String sex;//性别
	private String birth;//生日
	
	public User(){
		
	}
	
	public String Setuser(){
	
		return "id:"+id+",账户："+account+",昵称："+name+",密码："+password+",性别："+sex+",生日："+birth;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	
	public String getBirth() {
		return birth;
	}
	public void setBirth(String birth) {
		this.birth = birth;
	}
}

```
为每一个用户新建一个用户对象的用户类

## 注册

```java
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
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	        //设置一个用来返回的状态码
		    int code=0;
		    
		    //接受传进来的参数
	        String account = request.getParameter("userAccount");  
	        String password = request.getParameter("userPassword");
	        String name=request.getParameter("userName");
	        //打印接受的参数
	        LogUtil.log("userAccount:"+account + ";userPassword:" + password+";userName:"+name);  
	  
	        //创建链接
	        Connection connect = DatabaseUtil.getConnect();  
	        try {  
	        	Statement statement = (Statement) connect.createStatement();  
	        	//创建指令
	            String sql = "select userAccount from " + DatabaseUtil.TABLE_ACCOUNT + " where userAccount='" + account + "'";  
	            LogUtil.log(sql);  
	            //访问数据库
	            ResultSet result = statement.executeQuery(sql);  
	            if (result.next()) { 
	            // 能查到该账号，说明已经注册过了  
	                code = 300;  
	             //"该账号已存在";  
	            } else {  
	            
	                //插入数据指令
	                String sqlInsert = "insert into " + DatabaseUtil.TABLE_ACCOUNT + "(userAccount, userPassword, userName) values('"  
	                        + account + "', '" + password + "', '"+name+"')";  
	                LogUtil.log(sqlInsert);  
	                if (statement.executeUpdate(sqlInsert) > 0) { // 否则进行注册逻辑，插入新账号密码到数据库  
	                    code = 400;  
	                    //"注册成功";  
	                } else {  
	                    code =500;  
	                    //"注册失败";  
	                }  
	            }  
	        } catch (SQLException e) {  
	            e.printStackTrace();  
	        }  
	        response.setContentType("text/html;charset=utf-8"); // 设置响应报文的编码格式
	        
	        PrintWriter out = response.getWriter();
	        //把数据转换成JSON格式的字符串传递到APP
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

```
这时候在浏览器输入：
http://localhost:8080/LoginDemo/RegisterServlet?userAccount=0000&userPassword=0000&userName=安徽工程大学

会得到状态码：400

现在数据库就会得到一条新的数据。

## 登录

```java
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

```
浏览器输入：http://localhost:8080/LoginDemo/LoginServlet?userAccount=0000&userPassword=0000

得到状态码：200

## 查询用户信息

```java
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
		
		//创建用户对象
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
                //赋值用户对象属性
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
        //把user对象转换成JSON格式
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

```
浏览器输入：http://localhost:8080/LoginDemo/UserServlet?userAccount=0000

得到：{"account":"0000","id":0,"name":"安徽工程大学","password":"0000"} 

# APP
## 准备
1. 新建一个项目；
2. 在libs文件夹内加入之前和服务端一样的json的jar包，然后再build.gradle加入
```

    repositories {
        flatDir {
            dirs 'libs'
        }
    }
```
3. 在AndroidManifest.xml加入网络连接权限；
4. 因为高版本的Android版本可能抓取不到http请求，所以在res新建一个xml文件夹，在里面新建network_security_config.xml文件：
```
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```
之后在AndroidManifest.xml的    
```
<application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme"
        //
        android:networkSecurityConfig="@xml/network_security_config"
        >
```
5. 加入和服务器端一样的用户类用于后面解析JSON格式数据。
## UI
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <EditText
        android:id="@+id/account"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:hint="账户" />

    <EditText
        android:id="@+id/password"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:hint="密码" />

    <TextView
        android:id="@+id/text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:baselineAligned="false" />

    <Button
        android:id="@+id/login"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:text="登录" />

    <Button
        android:id="@+id/register"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:text="注册" />

    <Button
        android:id="@+id/user_list"
        android:text="用户信息"
        android:layout_width="match_parent"
        android:layout_height="50dp" />
</LinearLayout>
```

## 连接服务器工具类

```java
package com.example.logindemo;

/**
 * Created by lenovo on 2018/3/17.
 * 连接服务器工具类
 */

public class Constant {
    public static String URL = "http://192.168.43.38:8080/LoginDemo/";
    public static String URL_Register = URL + "RegisterServlet";
    public static String URL_Login = URL + "LoginServlet";
    public static String URL_User=URL+"UserServlet";

}
```


## 功能

```java
package com.example.logindemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText account;
    EditText password;
    public TextView textView;
    Button login;
    Button register;
    Button user_list;
    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            return false;
        }
    }){
        public void handleMessage(android.os.Message msg) {
            Bundle s=msg.getData();

            //解析出数据
            String name=s.getString("name");
            String sex=s.getString("sex");
            String birth=s.getString("birth");
            String account=s.getString("account");
            String password=s.getString("password");
            //UI线程，把数据在UI显示出来
            textView.setText("account:"+account+";password:"+password+";name:"+name+";sex:"+sex+";birth:"+birth);
        };
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        account=(EditText)findViewById(R.id.account);//输入账户
        password=(EditText)findViewById(R.id.password);//输入密码
        textView=(TextView)findViewById(R.id.text);
        login=(Button)findViewById(R.id.login);
        register=(Button)findViewById(R.id.register);
        user_list=(Button)findViewById(R.id.user_list);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(account.getText().toString().isEmpty())
                        && !(password.getText().toString().isEmpty())) {
                    login(account.getText().toString(), password.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "账号、密码都不能为空！", Toast.LENGTH_SHORT).show();
                }
            }

        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(account.getText().toString().isEmpty())
                        && !(password.getText().toString().isEmpty())) {
                    register(account.getText().toString(), password.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "账号、密码都不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });


        user_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url=new URL(Constant.URL_User + "?userAccount=" + account.getText().toString());
                            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            BufferedReader reader =new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String result="";
                            String line=null;
                            while((line=reader.readLine())!=null){
                                result+=line+"\n";
                            }
                            Log.d("JSON","RESULT"+result);
                            if(!"".equals(result)){
                                User user= JSON.parseObject(result,User.class);
                                Message message=handler.obtainMessage();
                                Bundle u=new Bundle();
                                //解析JSON格式数据,把类似于键值对类似的数据传递出去
                                u.putString("name",user.getName());
                                u.putString("sex",user.getSex());
                                u.putString("birth",user.getBirth());
                                u.putString("account",user.getAccount());
                                u.putString("password",user.getPassword());
                                message.setData(u);
                                message.sendToTarget();
                            }
                            reader.close();
                            conn.disconnect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }



    private void login(String account, String password) {
        String registerUrlStr = Constant.URL_Login + "?userAccount=" + account + "&userPassword=" + password;
        Log.d("JSON","指令："+registerUrlStr);
        Login_AsyncTask login_asyncTask =new Login_AsyncTask();
        login_asyncTask.execute(registerUrlStr);
    }
    private void register(String account, String password) {
        String registerUrlStr = Constant.URL_Register + "?userAccount=" + account + "&userPassword=" + password +"&userName=demo";
        Log.d("JSON","指令："+registerUrlStr);
        Login_AsyncTask login_asyncTask =new Login_AsyncTask();
        login_asyncTask.execute(registerUrlStr);
    }


    class Login_AsyncTask extends AsyncTask<String, Integer, String> {

        public Login_AsyncTask() {
            Log.d("JSON","验证前");

        }

        @Override
        public void onPreExecute() {
            Log.w("JSON", "开始验证.........");
        }

        /**
         * @param params 这里的params是一个数组，即AsyncTask在激活运行是调用execute()方法传入的参数
         */
        @Override
        public String doInBackground(String... params) {
            HttpURLConnection connection = null;
            StringBuilder response = new StringBuilder();
            try {
                URL url = new URL(params[0]); // 声明一个URL
                connection = (HttpURLConnection) url.openConnection(); // 打开该URL连接
                connection.setRequestMethod("GET"); // 设置请求方法，“POST或GET”，我们这里用GET，在说到POST的时候再用POST
                connection.setConnectTimeout(80000); // 设置连接建立的超时时间
                connection.setReadTimeout(80000); // 设置网络报文收发超时时间
                BufferedReader reader =new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response.toString(); // 这里返回的结果就作为onPostExecute方法的入参
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // 如果在doInBackground方法，那么就会立刻执行本方法
            // 本方法在UI线程中执行，可以更新UI元素，典型的就是更新进度条进度，一般是在下载时候使用
        }

        /**
         * 运行在UI线程中，所以可以直接操作UI元素
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            Log.d("JSON",s);//打印服务器返回标签
            //flag=true;
            switch (s){
                //判断返回的状态码，并把对应的说明显示在UI
                case "100":
                    textView.setText(s+"：登录失败，密码不匹配或账号未注册");
                    break;
                case "200":
                    textView.setText(s+"：登录成功");
                    break;
                case "300":
                    textView.setText(s+"：该账号已存在");
                    break;
                case "400":
                    textView.setText(s+"：注册成功");
                    break;
                case "500":
                    textView.setText(s+"：注册失败");
                    break;
                    default:
                        textView.setText("异常");
            }
            Log.d("JSON","验证后");
        }
    }
}

```
其中多线程的运行：
1. 用了AsyncTask进行登录、注册的请求
2. Handler

