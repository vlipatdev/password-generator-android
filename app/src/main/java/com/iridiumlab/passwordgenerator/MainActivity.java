package com.example.passwordgenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private TextView textViewPassword;
    private Button buttonGenerate;
    private Button buttonCopy;
    private TextView textViewLength;
    private SeekBar seekBarLength;
    private CheckBox checkBoxUppercase;
    private CheckBox checkBoxLowercase;
    private CheckBox checkBoxNumber;
    private CheckBox checkBoxSymbol;

    private String generateString() {
        StringBuilder baseSb = new StringBuilder();
        String uppercaseStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercaseStr = "abcdefghijklmnopqrstuvwxyz";
        String numberStr = "1234567890";
        String symbolStr = "!@#$%^&*()";

        if (checkBoxUppercase.isChecked()) {
            baseSb.append(uppercaseStr);
        }
        if (checkBoxLowercase.isChecked()) {
            baseSb.append(lowercaseStr);
        }
        if (checkBoxNumber.isChecked()) {
            baseSb.append(numberStr);
        }
        if (checkBoxSymbol.isChecked()) {
            baseSb.append(symbolStr);
        }

        return baseSb.toString();
    }

    private String generatePassword(String string, int length) {
        StringBuilder passwordSb = new StringBuilder();
        int randInd;

        for(int i = 0; i < length; i++) {
            Random rand = new SecureRandom();
            randInd = rand.nextInt(string.length());
            passwordSb.append(string.charAt(randInd));
        }

        return passwordSb.toString();
    }

    private boolean passwordChecker(String password) {
        boolean passedUppercase = true;
        boolean passedLowercase = true;
        boolean passedNumber = true;
        boolean passedSymbol = true;

        if (checkBoxUppercase.isChecked()) {
            Pattern p = Pattern.compile("[A-Z]");
            Matcher m = p.matcher(password);
            passedUppercase = m.find();
        }

        if (checkBoxLowercase.isChecked()) {
            Pattern p = Pattern.compile("[a-z]");
            Matcher m = p.matcher(password);
            passedLowercase = m.find();
        }

        if (checkBoxNumber.isChecked()) {
            Pattern p = Pattern.compile("[0-9]");
            Matcher m = p.matcher(password);
            passedNumber = m.find();
        }

        if (checkBoxSymbol.isChecked()) {
            Pattern p = Pattern.compile("[!@#$%^&*()]");
            Matcher m = p.matcher(password);
            passedSymbol = m.find();
        }

        return(passedUppercase && passedLowercase && passedNumber && passedSymbol);
    }

    private boolean hasSelection() {
        return (checkBoxUppercase.isChecked() || checkBoxLowercase.isChecked() || checkBoxNumber.isChecked() || checkBoxSymbol.isChecked());
    }

    private void generateAndDisplayPassword() {
        String password = "";
        while (!passwordChecker(password)) {
            String baseStr = generateString();
            password = generatePassword(baseStr, seekBarLength.getProgress());
        }
        textViewPassword.setText(password);
    }

    private void displayMessage() {
        textViewPassword.setText("");
    }

    private void checkBoxOnClickFn() {
        if (hasSelection()) {
            generateAndDisplayPassword();
        } else {
            displayMessage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT < 29) {
            // set status bar color
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#FFFFFF"));

            // set status bar text color
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        textViewPassword = findViewById(R.id.textViewPassword);
        buttonGenerate = findViewById(R.id.buttonGenerate);
        buttonCopy = findViewById(R.id.buttonCopy);
        textViewLength = findViewById(R.id.textViewLength);
        seekBarLength = findViewById(R.id.seekBarLength);
        checkBoxUppercase = findViewById(R.id.checkBoxUppercase);
        checkBoxLowercase = findViewById(R.id.checkBoxLowercase);
        checkBoxNumber = findViewById(R.id.checkBoxNumber);
        checkBoxSymbol = findViewById(R.id.checkBoxSymbol);

        // set seekBar parameters
        seekBarLength.setMax(128);
        final int min = 5;
        seekBarLength.setProgress(16);

        // display initial seekBar progress
        textViewLength.setText(Integer.toString(seekBarLength.getProgress()));

        // set checkBox values
        checkBoxUppercase.setChecked(true);
        checkBoxLowercase.setChecked(true);
        checkBoxNumber.setChecked(true);
        checkBoxSymbol.setChecked(false);

        // display initial password
        generateAndDisplayPassword();

        // generate button onClick listener
        buttonGenerate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (hasSelection()) {
                    generateAndDisplayPassword();
                    buttonGenerate.animate().rotationBy(360).setDuration(250);
                } else {
                    displayMessage();
                }
            }
        });

        // copy button onClick listener
        buttonCopy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (hasSelection()) {
                    // copy to clipboard
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", textViewPassword.getText());
                    Objects.requireNonNull(clipboard).setPrimaryClip(clip);

                    Toast.makeText(getApplicationContext(), getString(R.string.password_copied), Toast.LENGTH_SHORT).show();
                } else {
                    displayMessage();
                }
            }
        });

        // seekBar onChange listener
        seekBarLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getProgress() < min) {
                    seekBar.setProgress(min);
                    textViewLength.setText(Integer.toString(min));
                } else {
                    textViewLength.setText(Integer.toString(progress));
                }

                if (hasSelection()) {
                    generateAndDisplayPassword();
                } else {
                    displayMessage();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // checkBox event listeners
        checkBoxUppercase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxOnClickFn();
            }
        });

        checkBoxLowercase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxOnClickFn();
            }
        });

        checkBoxNumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxOnClickFn();
            }
        });

        checkBoxSymbol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxOnClickFn();
            }
        });
    }
}