package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextId, editTextPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 액션바 숨기기는 setContentView 전에
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_login);

        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력값 가져오기
                String id = editTextId.getText().toString().trim();
                String pw = editTextPassword.getText().toString().trim();

                // 입력 검증
                if(id.isEmpty()) {
                    editTextId.setError("아이디를 입력하세요");
                    editTextId.requestFocus();
                    return;
                }

                if(pw.isEmpty()) {
                    editTextPassword.setError("비밀번호를 입력하세요");
                    editTextPassword.requestFocus();
                    return;
                }

                // 임시 로그인 검증
                if(id.equals("1111") && pw.equals("1111")) {
                    // 로그인 성공
                    Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    // 로그인 실패
                    Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                    editTextId.setError("아이디를 확인해주세요");
                    editTextPassword.setError("비밀번호를 확인해주세요");
                }
            }
        });
    }
}