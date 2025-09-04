package com.example.test1;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class TextWatcherHelper {

    public interface OnTextChangeListener {
        void onTextChanged(String text);
    }

    public static void addTextWatcher(EditText editText, OnTextChangeListener listener) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No implementation needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No implementation needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) {
                    listener.onTextChanged(s.toString());
                }
            }
        });
    }
}