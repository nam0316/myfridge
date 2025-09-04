package com.example.test1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity {

    private Spinner spinnerItemIcon, spinnerStorage;
    private EditText etItemName, etQuantity;
    private TextView tvExpiryDate;
    private Button btnSave;

    private String expiryDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // View 연결
        spinnerItemIcon = findViewById(R.id.spinner_item_icon);
        etItemName = findViewById(R.id.et_item_name);
        spinnerStorage = findViewById(R.id.spinner_storage);
        etQuantity = findViewById(R.id.et_quantity);
        tvExpiryDate = findViewById(R.id.tv_expiry_date);
        btnSave = findViewById(R.id.btn_save_item);

        // 날짜 선택
        tvExpiryDate.setOnClickListener(v -> showDatePicker());

        // 저장 버튼 클릭
        btnSave.setOnClickListener(v -> saveItem());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    // 날짜 포맷을 더 보기 좋게 변경 (예: 2025-01-01)
                    String monthStr = String.format("%02d", selectedMonth + 1);
                    String dayStr = String.format("%02d", selectedDay);
                    expiryDate = selectedYear + "-" + monthStr + "-" + dayStr;
                    tvExpiryDate.setText(expiryDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void saveItem() {
        String itemName = etItemName.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();

        // 입력값 유효성 검사
        if (TextUtils.isEmpty(itemName)) {
            Toast.makeText(this, "상품명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(quantityStr)) {
            Toast.makeText(this, "수량을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(expiryDate)) {
            Toast.makeText(this, "유통기한을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String storage = spinnerStorage.getSelectedItem().toString();
        int quantity;

        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "수량은 숫자로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String iconName = spinnerItemIcon.getSelectedItem().toString();

        // 데이터 Intent에 담기
        Intent resultIntent = new Intent();
        resultIntent.putExtra("itemName", itemName);
        resultIntent.putExtra("storage", storage);
        resultIntent.putExtra("quantity", quantity);
        resultIntent.putExtra("expiryDate", expiryDate);
        resultIntent.putExtra("iconName", iconName);

        setResult(RESULT_OK, resultIntent);
        finish(); // 액티비티 종료
    }
}