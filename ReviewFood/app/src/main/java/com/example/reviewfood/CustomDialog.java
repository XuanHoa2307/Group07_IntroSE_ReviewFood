package com.example.reviewfood;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class CustomDialog {
    private Dialog dialog;
    private CheckBox checkBoxForever;
    private CheckBox checkBox3Day;
    private CheckBox checkBox7Day;
    private CheckBox checkBox30Day;

    public CustomDialog(Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custome_dialog_account_block);
        checkBoxForever = dialog.findViewById(R.id.lockForeverCheckbox);
        checkBox3Day = dialog.findViewById(R.id.lock3day);
        checkBox7Day = dialog.findViewById(R.id.lock7day);
        checkBox30Day = dialog.findViewById(R.id.lock30day);

        Button closeButton = dialog.findViewById(R.id.btn_close);
        Button blockButton = dialog.findViewById(R.id.btn_block_accountBlock);
        Button backButton = dialog.findViewById(R.id.btn_back_accountBlock);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        blockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle next button click (open confirm dialog, etc.)
                // For example, you can call a method or show another dialog.
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button click
                // For example, you can dismiss this dialog or go back to the previous state.
            }
        });
    }

    public void show() {
        dialog.show();
    }

    public boolean isCheckBoxForeverChecked() {
        return checkBoxForever.isChecked();
    }
    public boolean isCheckBox3dayChecked() {
        return checkBox3Day.isChecked();
    }
    public boolean isCheckBox7dayChecked() {
        return checkBox7Day.isChecked();
    }
    public boolean isCheckBox30dayChecked() {
        return checkBox30Day.isChecked();
    }
}
