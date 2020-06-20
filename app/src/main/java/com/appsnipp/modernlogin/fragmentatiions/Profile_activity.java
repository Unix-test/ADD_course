package com.appsnipp.modernlogin.fragmentatiions;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.modernlogin.R;
import com.appsnipp.modernlogin.controllfeatures.composer.post_controller.MyViewHolder;
import com.appsnipp.modernlogin.controllfeatures.composer.post_controller.post_contents;
import com.appsnipp.modernlogin.controllfeatures.composer.post_editor_dialog;
import com.appsnipp.modernlogin.controllfeatures.user_controller;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile_activity extends Fragment {

    private static final String TAG = "UploadAvatar";
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int PERMISSION_CODE = 400;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_WALLPAPPER_CODE = 500;
    private static final int CAMERA_REQUEST_WALLPAPPER_CODE = 600;
    private static final AtomicInteger count = new AtomicInteger( 0 );
    RecyclerView recyclerView_profile;
    DatabaseReference reference, outbound_reference;
    FirebaseRecyclerOptions<post_contents> post_contentsFirebaseRecyclerOptions;
    FirebaseRecyclerAdapter<post_contents, MyViewHolder> adapter;
    TextView username, quotes, locations, dateofbird, more;
    ImageView avatars, uploadavatarbutton, wallpapperView;
    View seperateLine, profilepostseperator;
    Button compose_navigator;
    ImageButton button, replacewallpaper_profile;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, post_references;
    String currentPicsPath;
    private int imageID;
    private String url;


    public Profile_activity() {
        // Required empty public constructor
    }


    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_profile_activity, container, false );

        //init Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference( "users" );


        String currentUser = Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser() ).getUid();
        post_references = FirebaseDatabase.getInstance().getReference( "users" )
                .child( currentUser )
                .child( "Posts" ).child( "post" );

        username = view.findViewById( R.id.textView2 );
        quotes = view.findViewById( R.id.memo );
        locations = view.findViewById( R.id.location );
        dateofbird = view.findViewById( R.id.dateofbird );
        avatars = view.findViewById( R.id.avatar );
        more = view.findViewById( R.id.textView25 );

        seperateLine = view.findViewById( R.id.view2 );
        profilepostseperator = view.findViewById( R.id.view6 );
        replacewallpaper_profile = view.findViewById( R.id.replacingwallpaper );

        wallpapperView = view.findViewById( R.id.imageView6 );

        button = (ImageButton) view.findViewById( R.id.button2 );
        uploadavatarbutton = (ImageView) view.findViewById( R.id.imageView8 );
        compose_navigator = (Button) view.findViewById( R.id.button25 );
        recyclerView_profile = view.findViewById( R.id.recycleview_profile );
        recyclerView_profile.setLayoutManager( new LinearLayoutManager( this.getContext() ) );


        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getActivity(), user_controller.class );
                startActivity( intent );
                requireActivity().finish();

            }
        } );

        //Edit avatar
        uploadavatarbutton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder( requireActivity(),
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
        } );

        replacewallpaper_profile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialAlertDialogBuilder( requireActivity(),
                        R.style.Theme_MaterialComponents_DayNight_Dialog_MinWidth )
                        .setTitle( "Pick image from" )
                        .setMessage( "" )
                        .setPositiveButton( "Gallery", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
                                intent.setType( "image/*" );
                                startActivityForResult( intent, IMAGE_PICK_GALLERY_WALLPAPPER_CODE );
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
        } );

        //Open composing editor
        compose_navigator.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        } );

        Query query = databaseReference.orderByChild( "email" ).equalTo( user.getEmail() );
        query.addValueEventListener( new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dt : dataSnapshot.getChildren()) {
                    String avatar = "" + dt.child( "avatar" ).getValue();
                    String yourfullname = "" + dt.child( "name" ).getValue();
                    String alternative_name = "" + dt.child( "alternativename" ).getValue();
                    String quote = "" + dt.child( "quotes" ).getValue();
                    String email = "" + dt.child( "phone" ).getValue();
                    String location = "" + dt.child( "location" ).getValue();
                    String birthday = "" + dt.child( "dateofbirth" ).getValue();
                    String coverimage = "" + dt.child( "coverimage" ).getValue();

                    //display data

                    if (TextUtils.isEmpty( alternative_name )) {
                        SpannableString pre = new SpannableString( yourfullname );
                        pre.setSpan( new StyleSpan( Typeface.BOLD ), 0, pre.length(), 0 );
                        username.append( pre );
                    } else {
                        SpannableString pre = new SpannableString( yourfullname );
                        SpannableString pos = new SpannableString( alternative_name );
                        pre.setSpan( new StyleSpan( Typeface.BOLD ), 0, pre.length(), 0 );
                        username.append( pre );
                        username.append( " " );
                        username.append( "(" + pos + ")" );

                    }

                    if (!(TextUtils.isEmpty( location ))) {
                        more.setVisibility( View.VISIBLE );
                        seperateLine.setVisibility( View.VISIBLE );
                        locations.setVisibility( View.VISIBLE );
                        locations.setText( location );
                    }

                    if (!(TextUtils.isEmpty( birthday ))) {
                        String born_date = "Born on " + birthday;
                        more.setVisibility( View.VISIBLE );
                        seperateLine.setVisibility( View.VISIBLE );
                        profilepostseperator.setVisibility( View.VISIBLE );
                        dateofbird.setVisibility( View.VISIBLE );
                        dateofbird.setText( born_date );
                    }

                    if (!(TextUtils.isEmpty( quote ))) {
                        quotes.setVisibility( View.VISIBLE );
                        quotes.setText( quote );
                    }

                    locations.setText( location );
                    dateofbird.setText( birthday );

                    try {
                        if (!(TextUtils.isEmpty( avatar ))) {
                            Picasso.get().load( avatar ).into( avatars );
                        }

                        if (!(TextUtils.isEmpty( coverimage ))) {
                            Picasso.get().load( coverimage ).into( wallpapperView );
                        }

                    } catch (Exception e) {
                        System.out.println( "No image is loaded" );
                    }

                    if (!(TextUtils.isEmpty( yourfullname ))) {
                        username.setVisibility( View.VISIBLE );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        } );

        LoadData();

        return view;
    }

    private void openDialog() {
        post_editor_dialog post_editor_dialog = new post_editor_dialog();
        post_editor_dialog.show( requireActivity().getSupportFragmentManager(), "post_editor_dialog" );
    }

    // Loading information from Firebase to recyclerView
    private void LoadData() {
        post_contentsFirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<post_contents>().setQuery( post_references, post_contents.class ).build();
        adapter = new FirebaseRecyclerAdapter<post_contents, MyViewHolder>( post_contentsFirebaseRecyclerOptions ) {
            @Override
            protected void onBindViewHolder(@NonNull final MyViewHolder holder, int position, @NonNull post_contents model) {
                holder.contents.setText( model.getContents() );

                Query query = databaseReference.orderByChild( "email" ).equalTo( user.getEmail() );
                query.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dt : dataSnapshot.getChildren()) {
                            String avatar = "" + dt.child( "avatar" ).getValue();
                            String username = "" + dt.child( "name" ).getValue();
                            holder.username.setText( username );
                            try {

                                Picasso.get().load( avatar ).into( holder.avatars );

                            } catch (Exception e) {
                                System.out.println( "No image is loaded" );
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.cardview, parent, false );
                return new MyViewHolder( view );
            }
        };

        adapter.startListening();
        recyclerView_profile.setAdapter( adapter );
    }


    //PICKIMAGEACTION
    private void pickImageFromCamera() {
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
        if (intent.resolveActivity( requireActivity().getPackageManager() ) != null) {
            startActivityForResult( intent, CAMERA_REQUEST_CODE );
            requireActivity().overridePendingTransition( R.anim.slide_in_left, android.R.anim.slide_out_right );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromCamera();
                } else {
                    Toast.makeText( getActivity(), "Permission denied...", Toast.LENGTH_SHORT ).show();
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == IMAGE_PICK_GALLERY_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri ImageData = data.getData();
                avatars.setImageURI( ImageData );

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
                Bundle extras = data.getExtras();
                assert extras != null;
                Bitmap bitmap = (Bitmap) extras.get( "data" );
                avatars.setImageBitmap( bitmap );
                assert bitmap != null;
                handleUpload( bitmap );
            }
        } else if (requestCode == IMAGE_PICK_GALLERY_WALLPAPPER_CODE) {

            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri ImageData = data.getData();
                wallpapperView.setImageURI( ImageData );

                String uid = Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser() ).getUid();
                assert ImageData != null;
                final StorageReference update_avatar = FirebaseStorage.getInstance().getReference()
                        .child( "wallpapper" ).child( uid )
                        .child( uid + ImageData.getLastPathSegment() );

                update_avatar.putFile( ImageData ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadWallPaperUrl( update_avatar );
                    }
                } ).addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e( TAG, "onFailure", e.getCause() );
                    }
                } );
            }
        }
    }

    private void getDownloadWallPaperUrl(StorageReference update_avatar) {
        update_avatar.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d( TAG, "onSuccess:" + uri );
                setUderProfileUrl( uri );
                url = uri.toString();
                update_wallpapper_url( url );
            }
        } );
    }

    private void update_wallpapper_url(String url) {
        String currentUser = Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser() ).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference( "users" ).child( currentUser );
        Map<String, Object> upDate = new HashMap<>();
        upDate.put( "coverimage", url );
        databaseReference.updateChildren( upDate );
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, outputStream );

        int minimum = 0;
        int maximum = Integer.MAX_VALUE;
        Random random = new Random();
        int ID = random.nextInt( maximum - minimum ) + 1;

        String imageid = Integer.toString( ID );
        String uid = Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser() ).getUid();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child( "avartar" ).child( uid )
                .child( uid + "image:" + imageid );

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


    private void getDownloadUrl(StorageReference update_avatar) {
        update_avatar.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d( TAG, "onSuccess:" + uri );
                setUderProfileUrl( uri );
                url = uri.toString();
                update_url( url );
            }
        } );
    }

    private void update_url(String url) {
        String currentUser = Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser() ).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference( "users" ).child( currentUser );
        Map<String, Object> upDate = new HashMap<>();
        upDate.put( "avatar", url );
        databaseReference.updateChildren( upDate );
    }


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
                        Toast.makeText( getActivity(), "Uploaded successfully", Toast.LENGTH_SHORT ).show();
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( getActivity(), "Disrupted loading ...", Toast.LENGTH_SHORT ).show();
            }
        } );
    }
}
