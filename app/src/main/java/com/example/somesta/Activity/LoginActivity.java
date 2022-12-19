package com.example.somesta.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private long mLastClickTime = 0;

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
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                String typedEmail = String.valueOf(email.getText());
                String typedPass = String.valueOf(password.getText());
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (TextUtils.isEmpty(email.getText())){
                    email.setError("Email Tidak Boleh Kosong!");
                    email.setBackground(getResources().getDrawable(R.drawable.loginbgwrong));
                }
                else if(!typedEmail.matches(emailPattern)){
                    email.setError("Masukkan Email Yang sesuai!");
                    email.setBackground(getResources().getDrawable(R.drawable.loginbgwrong));
                }
                else if(TextUtils.isEmpty(password.getText())){
                    password.setError("Password Tidak Boleh Kosong!");
                    password.setBackground(getResources().getDrawable(R.drawable.loginbgwrong));
                }
                else {
                    ExecutorService service = Executors.newSingleThreadExecutor();
                    service.execute(new Runnable() {
                        @Override
                        public void run() {
                            mAuth.signInWithEmailAndPassword(typedEmail, typedPass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(LoginActivity.this, "Berhasil login", Toast.LENGTH_SHORT).show();
                                                startActivity(intent);
                                            }
                                        });
//                                    Toast.makeText(LoginActivity.this, "Berhasil login", Toast.LENGTH_SHORT).show();
//                                    startActivity(intent);
                                }
                            });

                            mAuth.signInWithEmailAndPassword(typedEmail, typedPass).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, "Email / Password salah", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });

                }

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
    public static boolean emptyChecker(EditText[] editTexts, Activity activity) {
        /**
         * checking input field for empty or not
         */
        int totalLength = 0;
        for (int i = 0; i < editTexts.length; i++) {
            EditText editText = editTexts[i];
            if (editText.getText().toString().trim().equals("")) {
                Toast.makeText(activity, "Hola Mi Amigos", Toast.LENGTH_SHORT).show();
                break;
            } else {
                if (editText.getTag().toString().toLowerCase().equals("email".toLowerCase())) {
                    String email = editText.getText().toString().trim();
                    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                    if (!email.matches(emailPattern)) {
                        Toast.makeText(activity, "Hola", Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        totalLength++;
                    }
                } else {
                    totalLength++;
                }

            }
        }
        //true means all okay
        //false means at least one is empty
        return totalLength == editTexts.length;
    }

}