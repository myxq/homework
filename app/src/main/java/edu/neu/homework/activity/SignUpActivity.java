package edu.neu.homework.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.neu.homework.R;
import edu.neu.homework.util.CommonRequest;
import edu.neu.homework.util.CommonResponse;
import edu.neu.homework.util.Consts;
import edu.neu.homework.util.HttpUtil;
import edu.neu.homework.util.Util;
import okhttp3.Call;
import okhttp3.Response;

public class SignUpActivity extends BaseActivity {

    private static final String TAG = "SignUpActivity";

    @Bind(R.id.input_name)
    EditText inputName;
    @Bind(R.id.input_email)
    EditText inputEmail;
    @Bind(R.id.input_password)
    EditText inputPassword;
    @Bind(R.id.confirm_password)
    EditText confirmPassword;
    @Bind(R.id.btn_signUp)
    AppCompatButton btnSignUp;
    @Bind(R.id.link_login)
    TextView linkLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(SignUpActivity.this);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回到登录页
                autoStartActivity(LoginActivity.class);
                finish();
            }
        });
    }

    public void signUp() {
        Log.d(TAG, "SignUp");

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();


    }

    private void register(){
        CommonRequest request = new CommonRequest();

        // 前端参数校验，防SQL注入
//        String account = Util.StringHandle(inputName.getText().toString());
        String email = Util.StringHandle(inputEmail.getText().toString());
        String password = Util.StringHandle(inputPassword.getText().toString());
        String confirmPwd = Util.StringHandle(confirmPassword.getText().toString());

        // 检查数据格式是否正确
        if(!valueData(email,password,confirmPwd)){
//            showResponse("数据输入格式不正确");
            return;
        }

        // 填充参数
        request.addRequestParam("email",email);
        request.addRequestParam("password",password);

        // POST请求
        HttpUtil.sendPost(Consts.URL_Register, request.getJsonStr(), new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                CommonResponse res = new CommonResponse(response.body().string());
                String resCode = res.getResCode();
                String resMsg = res.getResMsg();
                // 显示注册结果
                showResponse(resMsg);
                // 注册成功
                if (resCode.equals(Consts.SUCCESSCODE_REGISTER)) {
                    finish();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showResponse("Network ERROR");
            }
        });
    }

    private void showResponse(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public boolean valueData(String email, String password, String confirmPwd) {
        String name = inputName.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPwd)){
            showResponse("请填写完整的账户信息");
            return false;
        }

        if (name.length() < 3) {
            inputName.setError("至少设置3个字符");
            return false;
        } else {
            inputName.setError(null);
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("请输入有效的邮箱地址");
            return false;
        } else {
            inputEmail.setError(null);
        }

        if (password.length()<4 || password.length()>10){
            inputPassword.setError("请输入4-10个字符");
            return false;
        }

        if (!confirmPwd.equals(password)){
            confirmPassword.setError("两次输入密码不相同");
            return false;
        } else {
            confirmPassword.setError(null);
        }
        return true;
    }

}
