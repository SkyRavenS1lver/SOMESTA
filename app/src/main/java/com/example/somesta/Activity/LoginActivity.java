package com.example.somesta.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.somesta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button submit;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        submit = findViewById(R.id.loginSubmit);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


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

//        username.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.length() > 0){
//                    username.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
//                }
//                else {username.setCompoundDrawablesWithIntrinsicBounds(0,0, com.arlib.floatingsearchview.R.drawable.ic_arrow_back_black_24dp,0);}
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });



        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typedEmail = String.valueOf(email.getText());
                String typedPass = String.valueOf(password.getText());
                mAuth.signInWithEmailAndPassword(typedEmail, typedPass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(LoginActivity.this, "Berhasil login", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                });

                mAuth.signInWithEmailAndPassword(typedEmail, typedPass).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Email / Password salah", Toast.LENGTH_SHORT).show();
                    }
                });

//                if (typedEmail.equals("test") && typedPass.equals("test")){
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                }else{
//                    Toast.makeText(LoginActivity.this, "Login Error!", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(this, "Already logged in.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }


}
