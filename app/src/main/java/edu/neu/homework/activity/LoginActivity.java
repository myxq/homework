package edu.neu.homework.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.neu.homework.R;
import edu.neu.homework.entity.User;
import edu.neu.homework.util.CommonRequest;
import edu.neu.homework.util.CommonResponse;
import edu.neu.homework.util.Consts;
import edu.neu.homework.util.HttpUtil;
import edu.neu.homework.util.UserManager;
import edu.neu.homework.util.Util;
import okhttp3.Call;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGN_UP = 0;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Bind(R.id.input_email)
    EditText inputEmail;
    @Bind(R.id.input_password)
    EditText inputPassword;
    @Bind(R.id.btn_login)
    AppCompatButton btnLogin;
    @Bind(R.id.link_signup)
    TextView linkSignUp;
    @Bind(R.id.remember_pass)
    CheckBox rememberPass;
    @Bind(R.id.progressbar)
    ProgressBar progressBar;

    private String email;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        LitePal.getDatabase();
        UserManager.clear();
        // 记住密码功能
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isRemember = preferences.getBoolean("remember_pass", false);
        email = preferences.getString("email","");
        password = preferences.getString("password","");
        if(!email.equals("") && !password.equals("")){
            if(isRemember){
                inputEmail.setText(email);
                inputPassword.setText(password);
                rememberPass.setChecked(true);
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoStartActivity(SignUpActivity.class);
            }
        });


    }

    private void login() {
        Log.d(TAG, "login");

        // 创建请求体对象
        CommonRequest request = new CommonRequest();

        // 前端参数校验，防SQL注入
        email = Util.StringHandle(inputEmail.getText().toString());
        password = Util.StringHandle(inputPassword.getText().toString());

        // 检查数据格式是否正确

        if(!validate()){
            showResponse("请输入有效的邮箱和密码");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);// 显示进度条
        optionHandle(email,password);// 处理自动登录及记住密码

        // 填充参数
        request.addRequestParam("account",email);
        request.addRequestParam("pwd",password);

        // POST请求
        HttpUtil.sendPost(Consts.URL_Login, request.getJsonStr(), new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                CommonResponse res = new CommonResponse(response.body().string());
                String resCode = res.getResCode();
                String resMsg = res.getResMsg();
                // 登录成功
                if (resCode.equals(Consts.SUCCESSCODE_LOGIN)) {
                    // 查找本地数据库中是否已存在当前用户,不存在则新建用户并写入
                    User user = DataSupport.where("email=?",email).findFirst(User.class);
                    if(user == null){
                        user = new User();
                        user.setEmail(email);
                        user.setPassword(password);
                        user.save();
                    }
                    UserManager.setCurrentUser(user);// 设置当前用户

                    autoStartActivity(MainActivity.class);
                }
                showResponse(resMsg);
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showResponse("网络错误");
            }
        });

    }

    /**
     * 判断用户邮箱和密码是否合理
     * @return boolean
     */
    private boolean validate() {
        boolean valid = true;

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("请输入有效的邮箱地址");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            inputPassword.setError("请输入4到10个字符");
            valid = false;
        } else {
            inputPassword.setError(null);
        }
        return valid;
    }

    /**
     * 记住用户邮箱和密码
     * @param email 邮箱
     * @param password 密码
     */
    private void optionHandle(String email, String password){
        editor = preferences.edit();
        if(rememberPass.isChecked()){
            editor.putBoolean("remember_pass",true);
            editor.putString("email", email);
            editor.putString("password", password);
        }else{
            editor.clear();
        }
        editor.apply();
    }

    private void showResponse(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
