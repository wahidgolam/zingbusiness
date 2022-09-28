package com.zingit.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.acl.Owner;
import java.util.Objects;

public class Login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 101;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    GoogleSignInAccount account;
    OwnerUser ownerUser;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_web_client))
                .requestEmail()
                .build();
        GoogleSignInOptions gso1 = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        mAuth = FirebaseAuth.getInstance();
    }
    public void signInPressed(View view){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadingDialog = new LoadingDialog(Login.this, "Logging you in");
        loadingDialog.startLoadingDialog();
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        if(completedTask.isSuccessful()){
            try {
                account = completedTask.getResult(ApiException.class);
                if(account!=null){
                    //Firebase Auth with Google Credentials
                    String idToken = account.getIdToken();
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            firebaseAuthWithGoogle(idToken);
                        }
                    }, 100);
                }
            } catch (ApiException e) {
                Log.d("Google Authentication Error", "signInResult:failed" +  e.getLocalizedMessage());
                //Display failure information to user
                Toast.makeText(Login.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Log.d("Google Authentication Unsuccessful", "Sign-in unsuccessful");
            //Display failure information to user
            Toast.makeText(Login.this, "Authentication failed " + completedTask.getException(), Toast.LENGTH_SHORT).show();
        }

    }
    public void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("FirebaseAuth", "signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();
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
                                    loadingDialog.dismissDialog();
                                    Intent intent = new Intent(getApplicationContext(), Homescreen.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.d("Firestore access", "No such document");
                                    createUser();
                                }
                            } else {
                                Log.d("Firestore access", "Got failed with ", task.getException());
                            }
                        }
                    });
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("FirebaseAuth", "signInWithCredential:failure", task.getException());
                    //Display failure information to user
                    Toast.makeText(Login.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void createUser(){
        Log.d("DEBUG!", "user created");
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String email = user.getEmail();
        String fullName = account.getDisplayName();
        String id = user.getUid();
        //assign campusID
        Toast.makeText(this, "Logged in as "+ email, Toast.LENGTH_SHORT).show();
        //updating db
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ownerUser = new OwnerUser(email, fullName, id);
                //adding user data to Data-holder
                Dataholder.ownerUser = ownerUser;
                //updating database
                db.collection("ownerUser").document(id).set(ownerUser);
                //navigate to homepage
                loadingDialog.dismissDialog();
                Intent intent = new Intent(getApplicationContext(), Homescreen.class);
                startActivity(intent);
                finish();
            }
        }, 100);
    }

    @Override
    public void onBackPressed() {
        loadingDialog.dismissDialog();
        super.onBackPressed();
    }
}