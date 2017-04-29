package com.example.internetofvehicles;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button registrationButton;

    private Button loginButton;

    private EditText accountEdit;

    private EditText passwordEdit;

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    private CheckBox rememberPass;

    private ProgressDialog progressDialog;

    private AlertDialog.Builder alertDialog;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registrationButton = (Button) findViewById(R.id.registration_button);
        loginButton = (Button) findViewById(R.id.login_button);
        accountEdit = (EditText) findViewById(R.id.username_edit);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);
        registrationButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            String account = pref.getString("username", "");
            String password = pref.getString("password", "");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            rememberPass.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registration_button:
                //此处添加注册逻辑
                break;
            case R.id.login_button:
                final String username = accountEdit.getText().toString();
                final String password = passwordEdit.getText().toString();
                if (username.equals("")) {
                    Toast.makeText(this, "请输入帐号", Toast.LENGTH_SHORT).show();
                } else if (password.equals("")) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    int netType = GetNetType(this);
                    Log.d(TAG, "netType is " + netType);
                    if (netType == -1) {
                        Toast.makeText(this, "无网络连接", Toast.LENGTH_SHORT).show();
                    } else if (netType == 1) {
                        alertDialog = new AlertDialog.Builder(this);
                        alertDialog.setTitle("安全警告");
                        alertDialog.setMessage("您的网络环境并不安全，是否继续？");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("继续",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestLogin(username, password);
                                    }
                                });
                        alertDialog.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        alertDialog.show();
                    } else {
                        requestLogin(username, password);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在登录...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 判断网络类型
     */
    public static int GetNetType(Context context) {

        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo == null)
        {
            return netType;
        }
        int nType = networkInfo.getType();
        if(nType == ConnectivityManager.TYPE_MOBILE)
        {
            if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet"))
            {
                netType = 3;
            }
            else
            {
                netType = 2;
            }
        }
        else if(nType == ConnectivityManager.TYPE_WIFI)
        {
            netType = 1;
        }
        return netType;
    }

    /**
     * 请求登录
     */
    private void requestLogin(final String username, final String password) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("username", username)
                            .add("password", password)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://116.62.129.132:8080/OBD/login")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    String userId = null;
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            userId = jsonObject.getString("userId");
                            Log.d(TAG, "userId is " + userId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!userId.equals("0")) {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                editor = pref.edit();
                                if (rememberPass.isChecked()) {
                                    editor.putBoolean("remember_password", true);
                                    editor.putString("username", username);
                                    editor.putString("password", password);
                                } else {
                                    editor.clear();
                                }
                                editor.apply();
                                Intent mainIntent = new Intent(LoginActivity.this,
                                        MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        });
                    } else {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                Toast.makeText(LoginActivity.this, "帐号或密码错误",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("");
        alertDialog.setMessage("您要退出登录吗？");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginActivity.super.onBackPressed();
                    }
                });
        alertDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialog.show();
    }
}
