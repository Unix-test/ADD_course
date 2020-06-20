package com.appsnipp.modernlogin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class bridge_screen extends AppCompatActivity {

    //permissions constants
    private static final String TAG = "UploadAvatar";
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int PERMISSION_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    EditText name, alternative_name, dateofbirth, mobilenumber;
    CircleImageView mimageView;
    DatabaseReference reference;
    FirebaseUser user;
    //arrays of permissions to be request
    String[] cameraPermissions;
    String[] storagePermissions;

    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_bridge_screen );

        mimageView = findViewById( R.id.imageView2 );

        name = findViewById( R.id.name_input );
        alternative_name = findViewById( R.id.alter_input );
        dateofbirth = findViewById( R.id.birthday_input );
        mobilenumber = findViewById( R.id.mobile_no_input );
    }


    public void uploadAvartar(View view) {
//        new AlertDialog.Builder(this)
//                .setTitle("Delete entry")
//                .setMessage("Are you sure you want to delete this entry?")
//
//                // Specifying a listener allows you to take an action before dismissing the dialog.
//                // The dialog is automatically dismissed when a dialog button is clicked.
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Continue with delete operation
//                    }
//                })
//
//                // A null listener allows the button to dismiss the dialog and take no further action.
//                .setNegativeButton(android.R.string.no, null)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();

        new MaterialAlertDialogBuilder( this,
                R.style.Theme_MaterialComponents_DayNight_Dialog_MinWidth )
                .setTitle( "Pick image from" )
                .setMessage( "" )
                .setPositiveButton( "Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
                        intent.setType( "image/*" );
                        startActivityForResult( intent, IMAGE_PICK_GALLERY_CODE );

                    }
                } )
                .setNegativeButton( "Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pickImageFromCamera();
                    }
                } )
                .show();

    }

    private void pickImageFromCamera() {
//        Intent intent = new Intent( Intent.ACTION_PICK );
//        intent.setType( "image/*" );
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
        if (intent.resolveActivity( getPackageManager() ) != null) {
            startActivityForResult( intent, IMAGE_PICK_CAMERA_CODE );
            overridePendingTransition( R.anim.slide_in_left, android.R.anim.slide_out_right );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromCamera();
            } else {
                Toast.makeText( this, "Permission denied...", Toast.LENGTH_SHORT ).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == IMAGE_PICK_GALLERY_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri ImageData = data.getData();
                mimageView.setImageURI( ImageData );

                String uid = Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser() ).getUid();
                assert ImageData != null;
                final StorageReference update_avatar = FirebaseStorage.getInstance().getReference()
                        .child( "avartar" ).child( uid )
                        .child( uid + ImageData.getLastPathSegment() );

                update_avatar.putFile( ImageData ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl( update_avatar );
                    }
                } ).addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e( TAG, "onFailure", e.getCause() );
                    }
                } );
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Bitmap bitmap = (Bitmap) Objects.requireNonNull( data.getExtras() ).get( "data" );
                mimageView.setImageBitmap( bitmap );
                assert bitmap != null;
                handleUpload( bitmap );
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, outputStream );

        String uid = Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser() ).getUid();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child( "avartar" ).child( uid )
                .child( uid + ".jpeg" );

        storageReference.putBytes( outputStream.toByteArray() ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownloadUrl( storageReference );
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e( TAG, "onFailure", e.getCause() );
            }
        } );
    }

    private void getDownloadUrl(StorageReference storageReference) {
        storageReference.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d( TAG, "onSuccess:" + uri );
                setUderProfileUrl( uri );
                url = uri.toString();
            }
        } );
    }

//    public void uPdate(){
//        String profile_names = activityBridgeScreenBinding.nameInput.getText().toString();
//        String profile_alternative_name = activityBridgeScreenBinding.alterInput.getText().toString();
//        String profile_birthday = activityBridgeScreenBinding.birthdayInput.getText().toString();
//        String mobile_number = activityBridgeScreenBinding.mobileNoInput.getText().toString();
//
//        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        reference = FirebaseDatabase.getInstance().getReference("users").child( currentUser );
//        Map<String, Object> upDate = new HashMap<>();
//        upDate.put( "avatar", url );
//        upDate .put( "name", profile_names );
//        upDate .put( "alternativename", profile_alternative_name );
//        upDate .put( "profile_birthday", profile_birthday );
//        upDate .put( "phone", mobile_number );
//        reference.updateChildren( upDate );
//    }

    private void setUderProfileUrl(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri( uri )
                .build();

        assert user != null;
        user.updateProfile( request )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText( bridge_screen.this, "Uploaded successfully", Toast.LENGTH_SHORT ).show();
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( bridge_screen.this, "Disrupted loading ...", Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    public void Post(View view) {
        String profile_name = name.getText().toString();
        String profile_alternative_name = alternative_name.getText().toString();
        String profile_birthday = dateofbirth.getText().toString();
        String mobile_number = mobilenumber.getText().toString();

        String currentUser = Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser() ).getUid();
        reference = FirebaseDatabase.getInstance().getReference( "users" ).child( currentUser );

        Map<String, Object> upDate = new HashMap<>();
        upDate.put( "avatar", url );
        upDate.put( "name", profile_name );
        upDate.put( "alternativename", profile_alternative_name );
        upDate.put( "dateofbirth", profile_birthday );
        upDate.put( "phone", mobile_number );
        reference.updateChildren( upDate );
    }
}
