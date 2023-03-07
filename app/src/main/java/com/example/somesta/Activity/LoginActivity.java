package com.example.somesta.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.somesta.utility.KeyboardUtils;
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
    private boolean available = true;
    private boolean keyboardVisibility = false;
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


        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // Login button logic
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // cek terakhir kali di klik, supaya proses yang berjalan hanya 1 saja. Waktu terhitung dalam milisecond
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                //Mendapatkan data dari input
                String typedEmail = String.valueOf(email.getText()).trim();
                String typedPass = String.valueOf(password.getText());
                //Pola abc@abc.abc, tidak bisa dengan email yang lebih dari 1 dot (.)
//                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String emailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
                // cek apakah input sudah terisi dan sesuai pola email
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
                    // Proses login menggunakan firebase Authentication
                    available = false;
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
                                                if (!available){
                                                    available = true;
                                                    Toast.makeText(LoginActivity.this, "Berhasil login", Toast.LENGTH_SHORT).show();
                                                    startActivity(intent);}
                                            }
                                        });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!available) {
                                            if (e.toString().equals("com.google.firebase.FirebaseNetworkException: A network error (such as timeout, interrupted connection or unreachable host) has occurred."))
                                            {Toast.makeText(LoginActivity.this, "Tidak Ada Koneksi Internet", Toast.LENGTH_LONG).show();}
                                            if (e.toString().equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password."))
                                            {password.setError("Password Salah!");
                                                password.setBackground(getResources().getDrawable(R.drawable.loginbgwrong));}
                                            if(e.toString().equals("com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                                email.setError("Email Salah!");
                                                email.setBackground(getResources().getDrawable(R.drawable.loginbgwrong));
                                            }
                                        }available = true;}
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });
        // Utility untuk cek kondisi keyboard smartphone
        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                keyboardVisibility = isVisible;
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(this, "Sudah Login.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    //Cek jika kita menekan tombol back dan keyboard sedang tidak terlihat,
    // maka akan muncul popup konfirmasi keluar aplikasi
    @Override
    public void onBackPressed() {
        if (!keyboardVisibility){
            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
            alert.setTitle("Perhatian");
            alert.setMessage("Apakah anda ingin keluar dari aplikasi?");
            alert.setPositiveButton("Keluar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    finishAffinity();
                    finish();
                }
            });
            alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            alert.show();
        }
        else {super.onBackPressed();}
    }
}
