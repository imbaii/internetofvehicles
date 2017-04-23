package com.example.internetofvehicles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button registration;

    private Button login;

    private EditText accountEdit;

    private EditText passwordEdit;

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    private CheckBox rememberPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        Toolbar loginToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        registration = (Button) findViewById(R.id.registration);
        login = (Button) findViewById(R.id.login);
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);
        setSupportActionBar(loginToolbar);
        registration.setOnClickListener(this);
        login.setOnClickListener(this);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            //passwordEdit.setSelection(passwordEdit.length());
            rememberPass.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registration:
                //此处添加注册逻辑
                break;
            case R.id.login:
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                if (account.equals("admin") && password.equals("123456")) {
                    editor = pref.edit();
                    if (rememberPass.isChecked()) {
                        editor.putBoolean("remember_password", true);
                        editor.putString("account", account);
                        editor.putString("password", password);
                    } else {
                        editor.clear();
                    }
                    editor.apply();
                    Intent mainIntent = new Intent(this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else if (account.equals("")) {
                    Toast.makeText(this, "请输入帐号", Toast.LENGTH_SHORT).show();
                } else if (password.equals("")) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "帐号或密码错误", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
