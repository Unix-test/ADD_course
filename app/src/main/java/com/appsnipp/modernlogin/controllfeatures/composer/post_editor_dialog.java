package com.appsnipp.modernlogin.controllfeatures.composer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.appsnipp.modernlogin.R;
import com.appsnipp.modernlogin.controllfeatures.composer.post_controller.post_contents;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class post_editor_dialog extends AppCompatDialogFragment {

    private static final String TAG = "choosePicPost";
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int PERMISSION_CODE = 400;
    private static final int CAMERA_REQUEST_CODE = 200;

    String photo_url;

    CircleImageView mini_avatar;
    TextView username, content, post;
    ImageView photo_post;
    VideoView videoView;

    DatabaseReference reference;
    FirebaseUser user;

    ImageButton photo, video;
    Uri ImageData;

    String contents, url, ploychecking, imageurl;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder( getActivity(), R.style.FullscreenDialog );

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate( R.layout.fragment_post_compose_editor, null );
        builder.setView( view );

        mini_avatar = view.findViewById( R.id.mini_avatar );
        username = view.findViewById( R.id.username );
        content = view.findViewById( R.id.composer_editor_zone );

        photo = view.findViewById( R.id.imageButton );
        video = view.findViewById( R.id.imageButton3 );

        photo_post = view.findViewById( R.id.pics_images_view );
        videoView = view.findViewById( R.id.pics_video_view );

        post = view.findViewById( R.id.post );


        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child( "users" );
        Query query = reference.orderByChild( "email" ).equalTo( user.getEmail() );
        query.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dt : dataSnapshot.getChildren()) {

                    String yourfullname = "" + dt.child( "name" ).getValue();
                    String avatar = "" + dt.child( "avatar" ).getValue();

                    username.setText( yourfullname );

                    try {

                        Picasso.get().load( avatar ).into( mini_avatar );

                    } catch (Exception e) {
                        System.out.println( "No image is loaded" );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        //UPLOAD PICK PHOTOS
        photo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
                intent.setType( "image/*" );
                startActivityForResult( intent, IMAGE_PICK_GALLERY_CODE );
            }
        } );

        post.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                contents = content.getText().toString();

                final String currentUser = Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser() ).getUid();
                final DatabaseReference database = FirebaseDatabase.getInstance().getReference( "users" ).child( currentUser ).child( "Posts" ).child( "post" );

                if (ImageData != null) {

                    final StorageReference update_avatar = FirebaseStorage.getInstance().getReference()
                            .child( "post_photo" ).child( currentUser )
                            .child( currentUser + ImageData.getLastPathSegment() );

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

                    url = currentUser + ImageData.getLastPathSegment();
                }

                post_contents post = new post_contents( contents, currentUser, url, "", "" );

                database.push().setValue( post, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        builder.setOnDismissListener( new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                Toast.makeText( getActivity(), "Posted", Toast.LENGTH_LONG ).show();
                            }
                        } );
                    }
                } );
            }
        } );

        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == IMAGE_PICK_GALLERY_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                ImageData = data.getData();
                photo_post.setImageURI( ImageData );
            }
        }
    }

    private void getDownloadUrl(StorageReference update_avatar) {
        update_avatar.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d( TAG, "onSuccess:" + uri );
                photo_url = uri.toString();
                update_url( photo_url );
            }
        } );
    }

    private void update_url(final String photo_url) {
        final String currentUser = Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser() ).getUid();
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference( "users" ).child( currentUser ).child( "Posts" ).child( "post" );
        Query query = firebaseDatabase.orderByChild( "image" ).equalTo( url );
        query.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dt : dataSnapshot.getChildren()) {
                    String imageId = (String) dt.child( "image" ).getValue();
                    String image = dt.getKey();

                    assert imageId != null;
                    if (imageId.equals( url )) {
                        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference( "users" ).child( currentUser ).child( "Posts" ).child( "post" ).child( image );
                        Map<String, Object> upDate = new HashMap<>();
                        upDate.put( "image", photo_url );
                        firebaseDatabase.updateChildren( upDate );
                    }
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }
}
