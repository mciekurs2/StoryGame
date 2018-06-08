package com.kristine.spaceadventure;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.editText_register_email);
        passwordEditText = findViewById(R.id.editText_register_password);

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        //nodrošina register pogas funkcionalitāti
        findViewById(R.id.button_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        //nodrošina pāreju uz loginActivity
        findViewById(R.id.textView_register_goToLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

    }

    /** nodrošina galveno funkcionalitāti lietotāja reģistrācijai */
    public void registerUser(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        //veic teksta validāciju katram elementam
        if (email.isEmpty()){
            emailEditText.setError("Email is needed");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Enter a valid email");
            emailEditText.requestFocus();
            return;
        }


        if (password.isEmpty()){
            passwordEditText.setError("Password is needed");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6){
            passwordEditText.setError("Minimum length of password is 6");
            passwordEditText.requestFocus();
            return;
        }

        //veic jauna lietotāja reģistrāciju, kad dati ir ievadīti pareizi
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //pārbauda, vai reģistrācija, ir bijusi veiksmīga
                        if (task.isSuccessful()){
                            //izveido jaunu ierakstu DB ar 20 iepsējamiem stāstiem
                            writeNewUser(mAuth.getUid());
                            //aiziet uz sākumu aktivitāti
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            //parāda ziņojumu par kļūdu
                            Toast.makeText(getApplicationContext(), Objects.requireNonNull
                                    (task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    //izveiido sākuma pozīcijas lietotājam (pagaidām statisks apjoms)
    private void writeNewUser(String userId) {
        User user = new User(0);

        for (int i = 0; i<20; i++)
        ref.child("users").child(userId).child(String.valueOf(i)).setValue(user);
    }


}
