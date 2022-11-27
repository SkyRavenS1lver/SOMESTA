package com.example.somesta.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.somesta.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText username = findViewById(R.id.loginUsername);
//        EditText password = findViewById(R.id.loginPassword);
//        Boolean viewed = false;
//        password.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    if(motionEvent.getRawX() >= password.getRight() - password.getTotalPaddingRight()) {
//                        // your action for drawable click event
//                        if(viewed){password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);}
//                        else{password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);}
//                    }
//                }
//                return true;
//            }
//        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    username.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                }
                else {username.setCompoundDrawablesWithIntrinsicBounds(0,0, com.arlib.floatingsearchview.R.drawable.ic_arrow_back_black_24dp,0);}
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        findViewById(R.id.loginSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
