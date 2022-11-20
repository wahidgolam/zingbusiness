package com.zingit.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmailLogin extends AppCompatActivity {

    EditText email,password;
    Button login;
    FirebaseFirestore db;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        loadingDialog = new LoadingDialog(EmailLogin.this, "Logging you in");

        db = FirebaseFirestore.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
    }

    public void Login() {
        loadingDialog.startLoadingDialog();
        String Email = email.getText().toString();
        String Password = password.getText().toString();
        int flag = 1;

        if (Email.length()<7)
            flag = 2;
        if (password.length() < 6)
            flag = 2;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (flag == 1) {
            mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        Log.d("Debug1", user.getUid());
                        DocumentReference userConcerned = db.collection("ownerUser").document(user.getUid());
                        userConcerned.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d("DEBUG!", "doc exists");
                                        //set Data-holder
                                        OwnerUser ownerUser = document.toObject(OwnerUser.class);
                                        Dataholder.ownerUser = ownerUser;
                                        Log.e("loginDetails", Dataholder.ownerUser.getOutletID());
                                        loadingDialog.dismissDialog();
                                        Toast.makeText(EmailLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), Homescreen_latest.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.d("Firestore access", "No such document");
                                        Toast.makeText(EmailLogin.this, "Authentication Error", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d("Firestore access", "Got failed with ", task.getException());
                                    Toast.makeText(EmailLogin.this, "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("FirebaseAuth", "signInWithCredential:failure", task.getException());
                        //Display failure information to user
                        Toast.makeText(EmailLogin.this, "Password is invalid", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismissDialog();
                    }
                }



            });
        }
        else
        {
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }

}