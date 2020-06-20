package com.appsnipp.modernlogin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.appsnipp.modernlogin.controllfeatures.userInformation;
import com.appsnipp.modernlogin.databinding.RegisterActivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth firebaseAuth;
    private RegisterActivityBinding mBinding;
    private TextWatcher Field_email_Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (!TextUtils.isEmpty( mBinding.regiseditTextEmail.getText().toString().trim() )) {
                mBinding.textInputEmail.setErrorEnabled( false );
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };
    private TextWatcher Field_password_Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (!TextUtils.isEmpty( mBinding.regiseditTextPassword.getText().toString().trim() )) {
                mBinding.textInputPassword.setErrorEnabled( false );
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.register_activity );
        changeStatusBarColor();

        mBinding = RegisterActivityBinding.inflate( getLayoutInflater() );
        setContentView( mBinding.getRoot() );
        mBinding.cirRegisterButton.setOnClickListener( this );

        firebaseAuth = FirebaseAuth.getInstance();
        findViewById( R.id.cirRegisterButton ).setOnClickListener( this );

    }

    //MARK: Field_user_Watcher

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.regiseditTextEmail.addTextChangedListener( Field_email_Watcher );
        mBinding.regiseditTextPassword.addTextChangedListener( Field_password_Watcher );

    }

    //MARK: Field_password_Watcher

    private boolean unifiedValidation() {
        boolean valid = true;

        String email = mBinding.regiseditTextEmail.getText().toString().trim();
        if (TextUtils.isEmpty( email )) {
            mBinding.textInputEmail.setError( "Email is required" );
            valid = false;

        } else {
            mBinding.textInputEmail.setError( null );
        }

        String password = mBinding.regiseditTextPassword.getText().toString().trim();
        if (TextUtils.isEmpty( password )) {
            mBinding.textInputPassword.setError( "Required." );
            valid = false;

        } else if (password.length() < 8) {
            mBinding.textInputPassword.setError( "Password length should be at least 8 characters" );

        } else {
            mBinding.textInputPassword.setError( null );
        }

        return valid;
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags( WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor( getResources().getColor( R.color.register_bk_color ) );
        }
    }

    public void onLoginClick(View view) {
        startActivity( new Intent( this, LoginActivity.class ) );
        overridePendingTransition( R.anim.slide_in_left, android.R.anim.slide_out_right );
    }


    private void updateUIoncreate(FirebaseUser user) {
        if (user != null) {
            String email = user.getEmail();
            String uid = user.getUid();
//            HashMap<Object,String> hashMap = new HashMap<>();
//            hashMap.put("name", "");
//            hashMap.put("quotes","");
//            hashMap.put("email", email);
//            hashMap.put("phone", "");
//            hashMap.put("dateofbirth","");
//            hashMap.put("avatar", "");
//            hashMap.put("coverimage","");
//            hashMap.put("location","");

            userInformation userinfos = new userInformation( "", "", email, "", "", "", ""
                    , "", "" );

            DatabaseReference database = FirebaseDatabase.getInstance().getReference( "users" );
            database.child( uid ).setValue( userinfos, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    Toast.makeText( RegisterActivity.this, "Registered successfully", Toast.LENGTH_LONG ).show();
                }
            } );
        } else {
            Log.d( TAG, "No account" );
            Toast.makeText( RegisterActivity.this, "This email is not registered, yet",
                    Toast.LENGTH_LONG ).show();
        }
    }

    private boolean checkEmail(String email) {
        final boolean[] valid = {false};
        firebaseAuth.fetchSignInMethodsForEmail( email ).addOnCompleteListener( new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                boolean check = !Objects.requireNonNull( Objects.requireNonNull( task.getResult() ).getSignInMethods() ).isEmpty();

                if (check) {
                    Toast.makeText( RegisterActivity.this, "Account is existed",
                            Toast.LENGTH_LONG ).show();
                    valid[0] = true;
                }
            }
        } );
        return valid[0];
    }

    private void registerUser(String email, String password) {
        Log.d( TAG, "Registered" + email );
        if (!unifiedValidation()) {
            return;

        } else if (mBinding.regiseditTextPassword.getText().toString().length() < 8) {
            return;

        } else if (checkEmail( email )) {
            return;

        } else {
            mBinding.progressBar4.setVisibility( View.VISIBLE );
            firebaseAuth.createUserWithEmailAndPassword( email, password ).addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d( TAG, "Registered:success" );
                        Toast.makeText( RegisterActivity.this, "Done.",
                                Toast.LENGTH_SHORT ).show();

                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (Objects.requireNonNull( Objects.requireNonNull( task.getResult() ).getAdditionalUserInfo() ).isNewUser()) {
                            updateUIoncreate( user );
                        }
                        finish();
                    } else {
                        Log.w( TAG, "createUserWithEmail:failure", task.getException() );
                        Toast.makeText( RegisterActivity.this, "Register failed.",
                                Toast.LENGTH_SHORT ).show();
                    }
                    mBinding.progressBar4.setVisibility( View.GONE );
                }
            } );
        }
    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.cirRegisterButton) {
            registerUser( mBinding.regiseditTextEmail.getText().toString().trim(), mBinding.regiseditTextPassword.getText().toString() );
        }
    }
}
