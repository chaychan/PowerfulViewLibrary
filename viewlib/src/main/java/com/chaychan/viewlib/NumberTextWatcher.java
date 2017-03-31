package com.chaychan.viewlib;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * 银行卡号输入框格式（每4位有个空格）
 *
 * @author Administrator
 */
class NumberTextWatcher implements TextWatcher {

    private EditText mEditText;
    private char mDivider;

    int beforeTextLength = 0;
    int onTextLength = 0;
    boolean isChanged = false;

    int location = 0;// 记录光标的位置
    private char[] tempChar;
    private StringBuffer buffer = new StringBuffer();
    int konggeNumberB = 0;

    public NumberTextWatcher(EditText editText, char divider) {
        mEditText = editText;
        mDivider = divider;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before,
                              int count) {
        onTextLength = s.length();
        buffer.append(s.toString());
        if (onTextLength == beforeTextLength || onTextLength <= 3
                || isChanged) {
            isChanged = false;
            return;
        }
        isChanged = true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        beforeTextLength = s.length();
        if (buffer.length() > 0) {
            buffer.delete(0, buffer.length());
        }
        konggeNumberB = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == mDivider) {
                konggeNumberB++;
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isChanged) {
            location = mEditText.getSelectionEnd();
            int index = 0;
            while (index < buffer.length()) {
                if (buffer.charAt(index) == mDivider) {
                    buffer.deleteCharAt(index);
                } else {
                    index++;
                }
            }

            index = 0;
            int spaceNumberCount = 0;//空格的个数
            while (index < buffer.length()) {
                if ((index == 4 || index == 9 || index == 14 || index == 19 || index == 24 || index == 29)) {
                    buffer.insert(index, mDivider);
                    spaceNumberCount++;
                }
                index++;
            }

            if (spaceNumberCount > konggeNumberB) {
                location += (spaceNumberCount - konggeNumberB);
            }

            tempChar = new char[buffer.length()];
            buffer.getChars(0, buffer.length(), tempChar, 0);
            String str = buffer.toString();
            if (location > str.length()) {
                location = str.length();
            } else if (location < 0) {
                location = 0;
            }
            mEditText.setText(str);
            Editable etable = mEditText.getText();
            Selection.setSelection(etable, location);
            isChanged = false;
        }
    }
}