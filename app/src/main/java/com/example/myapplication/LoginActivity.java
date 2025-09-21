package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.imageview.ShapeableImageView;
import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.OAuthLoginCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ShapeableImageView btnNaverLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // 네이버 로그인 SDK 초기화
        NaverIdLoginSDK.INSTANCE.initialize(
                this,
                getString(R.string.naver_client_id),
                getString(R.string.naver_client_secret),
                getString(R.string.app_name)
        );

        // 이미 로그인된 상태 확인
        String existingToken = NaverIdLoginSDK.INSTANCE.getAccessToken();
        if (existingToken != null && !existingToken.isEmpty()) {
            getUserProfile(existingToken);
            return;
        }

        // 시스템 바 패딩 적용
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 버튼 연결
        btnNaverLogin = findViewById(R.id.btn_naver_login);

        // 네이버 로그인 버튼 클릭 이벤트
        btnNaverLogin.setOnClickListener(v -> {
            NaverIdLoginSDK.INSTANCE.authenticate(this, new OAuthLoginCallback() {
                @Override
                public void onSuccess() {
                    String accessToken = NaverIdLoginSDK.INSTANCE.getAccessToken();
                    Log.d("NAVER_LOGIN", "Access Token: " + accessToken);

                    Toast.makeText(LoginActivity.this, "로그인 성공! 프로필 정보를 가져오는 중...", Toast.LENGTH_SHORT).show();

                    getUserProfile(accessToken);
                }

                @Override
                public void onFailure(int errorCode, String message) {
                    Log.e("NAVER_LOGIN", "Login Failed: " + message);
                    Toast.makeText(LoginActivity.this, "로그인 실패: " + message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(int errorCode, String message) {
                    onFailure(errorCode, message);
                }
            });
        });
    }

    // 기본 메인 이동
    private void moveToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // 프로필 정보와 함께 메인 이동
    private void moveToMain(String email, String nickname) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("user_email", email);
        intent.putExtra("user_nickname", nickname);
        startActivity(intent);
        finish();
    }

    // 네이버 프로필 정보 가져오기
    private void getUserProfile(String accessToken) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL("https://openapi.naver.com/v1/nid/me");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                int responseCode = urlConnection.getResponseCode();
                Log.d("NAVER_PROFILE", "Response Code: " + responseCode);

                InputStream inputStream;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = urlConnection.getInputStream();
                } else {
                    inputStream = urlConnection.getErrorStream();
                }

                if (inputStream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                }

                urlConnection.disconnect();

            } catch (Exception e) {
                Log.e("NAVER_PROFILE", "Error getting profile: " + e.getMessage(), e);

                // 오류 발생 시 기본 메인 이동
                runOnUiThread(this::moveToMain);
                return;
            }

            // 메인 스레드에서 JSON 파싱 실행
            final String jsonResult = result.toString();
            runOnUiThread(() -> parseUserProfile(jsonResult));
        });
    }

    // JSON 응답 파싱 + 서버 저장 호출
    private void parseUserProfile(String jsonString) {
        try {
            if (jsonString == null || jsonString.isEmpty()) {
                Log.e("NAVER_PROFILE", "Empty response");
                moveToMain();
                return;
            }

            JSONObject jsonObject = new JSONObject(jsonString);
            String resultCode = jsonObject.optString("resultcode", "");

            if ("00".equals(resultCode)) {
                JSONObject response = jsonObject.getJSONObject("response");
                String email = response.optString("email", "이메일 없음");
                String nickname = response.optString("nickname", "별명 없음");

                Log.d("NAVER_PROFILE", "Email: " + email);
                Log.d("NAVER_PROFILE", "Nickname: " + nickname);

                // 🔥 이 로그 추가
                Log.d("DEBUG", "🚀 서버 전송 시작!");

                sendUserInfoToServer(email, nickname);

                // 🔥 이 로그도 추가
                Log.d("DEBUG", "📱 메인 이동 시작!");

                moveToMain(email, nickname);
            }
        } catch (JSONException e) {
            Log.e("NAVER_PROFILE", "JSON Parse Error: " + e.getMessage(), e);
        }
    }

    // Spring Boot 서버로 회원정보 전송 (개선된 버전)
    // Spring Boot 서버로 회원정보 전송 (완성 버전)
    private void sendUserInfoToServer(String email, String nickname) {
        Log.d("SERVER", "🎯 sendUserInfoToServer 호출됨! Email: " + email + ", Name: " + nickname);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Log.d("SERVER", "🌐 네트워크 작업 시작!");

            HttpURLConnection conn = null;
            try {
                // ⚡ 서버 URL - 실제 환경에 맞게 수정하세요
                String serverUrl = "http://192.168.45.126:8080/api/users";
                // 에뮬레이터 사용시: "http://10.0.2.2:8080/api/users"

                Log.d("SERVER", "🌐 서버 URL: " + serverUrl);

                URL url = new URL(serverUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(15000); // 15초
                conn.setReadTimeout(15000);    // 15초

                // JSON 데이터 생성
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", email);
                jsonParam.put("name", nickname);

                String jsonString = jsonParam.toString();
                Log.d("SERVER", "📤 전송할 JSON: " + jsonString);

                // 데이터 전송
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                    os.flush();
                    Log.d("SERVER", "✅ 데이터 전송 완료");
                }

                // 응답 확인
                int responseCode = conn.getResponseCode();
                String responseMessage = conn.getResponseMessage();
                Log.d("SERVER", "📥 응답 코드: " + responseCode + " - " + responseMessage);

                // 응답 내용 읽기
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                responseCode >= 200 && responseCode < 300
                                        ? conn.getInputStream()
                                        : conn.getErrorStream(),
                                "utf-8"))) {

                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }

                Log.d("SERVER", "📋 서버 응답: " + response.toString());

                if (responseCode >= 200 && responseCode < 300) {
                    Log.d("SERVER", "🎉 서버 저장 성공!");
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "회원정보 저장 완료!", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    Log.e("SERVER", "❌ 서버 오류: " + responseCode + " - " + response.toString());
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "서버 저장 실패", Toast.LENGTH_SHORT).show()
                    );
                }

            } catch (Exception e) {
                Log.e("SERVER", "🚨 서버 통신 오류: " + e.getMessage(), e);
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "네트워크 오류: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }
}