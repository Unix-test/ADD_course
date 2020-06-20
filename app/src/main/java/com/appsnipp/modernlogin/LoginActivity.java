package com.appsnipp.modernlogin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.appsnipp.modernlogin.databinding.LoginActivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassowrd";
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    private LoginActivityBinding loginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR );
        }
        setContentView( R.layout.login_activity );


        loginBinding = LoginActivityBinding.inflate( getLayoutInflater() );
        setContentView( loginBinding.getRoot() );

        firebaseAuth = FirebaseAuth.getInstance();
        loginBinding.cirLoginButton.setOnClickListener( this );

    }

    public void onLoginClick(View View) {
        startActivity( new Intent( this, RegisterActivity.class ) );
        overridePendingTransition( R.anim.slide_in_right, R.anim.stay );
    }

    private boolean undervaluation() {
        boolean valid = true;
        String email = loginBinding.editTextEmail.getText().toString();

        if (TextUtils.isEmpty( email )) {
            loginBinding.textInputEmail.setError( "Email is required" );
            valid = false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher( email ).matches()) {
            loginBinding.textInputEmail.setError( "Invalid email" );
            valid = false;

        } else {
            loginBinding.textInputEmail.setError( null );
        }

        String password = loginBinding.editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty( password )) {
            loginBinding.textInputPassword.setError( "Password is required" );
            valid = false;
        } else {
            loginBinding.textInputPassword.setError( null );
        }
        return valid;
    }

    private void logIn(String email, String password) {
        Log.d( TAG, "login" + email );
        if (!undervaluation()) {
            return;
        } else {
            loginBinding.progressBar5.setVisibility( View.VISIBLE );
            loginBinding.progressBar5.setIndeterminate( true );

            firebaseAuth.signInWithEmailAndPassword( email, password ).addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d( TAG, "Authorize:success" );
                        Toast.makeText( LoginActivity.this, "Authorization success!",
                                Toast.LENGTH_SHORT ).show();

                        reference = FirebaseDatabase.getInstance().getReference().child( "users" );
                        Query query = reference.orderByChild( "name" );
                        query.addValueEventListener( new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dt : dataSnapshot.getChildren()) {

                                    String yourfullname = "" + dt.child( "name" ).getValue();

                                    if (TextUtils.isEmpty( yourfullname )) {
                                        startActivity( new Intent( LoginActivity.this, bridge_screen.class ) );
                                        overridePendingTransition( R.anim.slide_in_left, android.R.anim.slide_out_right );
                                        return;
                                    } else {
                                        startActivity( new Intent( LoginActivity.this, Home.class ) );
                                        overridePendingTransition( R.anim.slide_in_left, android.R.anim.slide_out_right );
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        } );

                    } else {
                        Log.w( TAG, "loginWithEmail:failure", task.getException() );
                        Toast.makeText( LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT ).show();
                    }

                    if (!task.isSuccessful()) {
                        Toast.makeText( LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT ).show();
                    }
                    loginBinding.progressBar5.setVisibility( View.GONE );
                }
            } );
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.cirLoginButton) {
            logIn( loginBinding.editTextEmail.getText().toString().trim(), loginBinding.editTextPassword.getText().toString() );
        }
    }
}
