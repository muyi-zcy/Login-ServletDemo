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
