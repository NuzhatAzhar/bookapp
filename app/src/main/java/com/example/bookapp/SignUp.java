package com.example.bookapp;

import android.content.Intent;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    EditText signup_email;
    EditText signup_pass;
    EditText signup_name;
    Button signup_btn;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser user;
    DatabaseReference userREf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=signup_email.getText().toString();
                String pass=signup_pass.getText().toString();
                String name=signup_name.getText().toString();

                authUser(name,email,pass);

            }
        });
    }

    private void authUser(final String name, final String email, final String pass) {

        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user=auth.getCurrentUser();
                    saveData(name,email,pass,user.getUid());
                }

                else {
                    Toast.makeText(SignUp.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveData(String name, String email, String pass, String uid) {

        User user=new User(name,uid,email,pass);
        userREf.child(uid).setValue(user);
        startActivity(new Intent(this,BookList.class));
    }

    private void init() {

        signup_email=findViewById(R.id.signup_email);
        signup_pass=findViewById(R.id.signup_pass);
        signup_name=findViewById(R.id.signup_name);
        signup_btn=findViewById(R.id.signup_btn);

        firebaseDatabase=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        userREf=firebaseDatabase.getReference("users");

    }
}
