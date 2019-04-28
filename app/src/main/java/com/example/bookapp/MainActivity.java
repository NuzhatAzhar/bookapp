package com.example.bookapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText login_email;
    EditText login_pass;
    Button login_btn;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        if(user!=null){
            startActivity(new Intent(this,BookList.class));
        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=login_email.getText().toString();
                String pass=login_pass.getText().toString();

                authUser(email,pass);

            }
        });
    }

    private void authUser(String email, String pass) {

        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user=auth.getCurrentUser();
                    startActivity(new Intent(MainActivity.this,BookList.class));
                    finish();
                }
            }
        });
    }

    private void init() {

        login_email=findViewById(R.id.login_email);
        login_pass=findViewById(R.id.login_pass);
        login_btn=findViewById(R.id.login_btn);

        firebaseDatabase=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();


    }

    public void signup(View view) {

        startActivity(new Intent(this,SignUp.class));
    }
}
