package com.appsnipp.modernlogin.fragmentatiions;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.modernlogin.R;
import com.appsnipp.modernlogin.controllfeatures.composer.post_controller.MyViewHolder;
import com.appsnipp.modernlogin.controllfeatures.composer.post_controller.post_contents;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class Explore_activity extends Fragment {

    LinearLayout Edit, Delete;

    RecyclerView recyclerView;
    DatabaseReference reference;
    FirebaseRecyclerOptions<post_contents> post_contentsFirebaseRecyclerOptions;
    FirebaseRecyclerAdapter<post_contents, MyViewHolder> adapter;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private String save_current_day, save_current_time;

    public Explore_activity() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String currentUser = Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser() ).getUid();
        reference = FirebaseDatabase.getInstance().getReference( "users" )
                .child( currentUser )
                .child( "Posts" ).child( "post" );


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference( "users" );

        View view = inflater.inflate( R.layout.fragment_explore_activity, container, false );
        recyclerView = (RecyclerView) view.findViewById( R.id.recycleview );
        recyclerView.setHasFixedSize( true );

        recyclerView.setLayoutManager( new LinearLayoutManager( requireContext().getApplicationContext() ) );
        LoadData();

        return view;
    }

    private void LoadData() {
        post_contentsFirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<post_contents>().setQuery( reference, post_contents.class ).build();
        adapter = new FirebaseRecyclerAdapter<post_contents, MyViewHolder>( post_contentsFirebaseRecyclerOptions ) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull final MyViewHolder holder, final int position, @NonNull post_contents model) {

                Calendar date = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat( "dd-MMMM-yyyy" );
                save_current_day = currentDate.format( date.getTime() );

                Calendar time = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat( "HH:mm" );
                save_current_time = currentTime.format( time.getTime() );

                holder.contents.setText( model.getContents() );
                holder.status.setText( save_current_day + " at " + save_current_time );

                try {
                    Picasso.get().load( model.getImage() ).into( holder.post_image );
                } catch (Exception ignored) {

                }

                holder.more.setOnClickListener( new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText( getContext(), "Permission denied...", Toast.LENGTH_SHORT ).show();
                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog( requireActivity() );
                        Window window = bottomSheetDialog.getWindow();
                        bottomSheetDialog.setContentView( R.layout.edit_delete );
                        assert window != null;
                        window.findViewById( com.google.android.material.R.id.container ).setFitsSystemWindows( false );
                        bottomSheetDialog.setCanceledOnTouchOutside( true );
                        bottomSheetDialog.show();

                        Edit = bottomSheetDialog.findViewById( R.id.editLinearLayout );
                        Delete = bottomSheetDialog.findViewById( R.id.deleteLinearLayout );

                        assert Delete != null;
                        Delete.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String key = getRef( position ).getKey();
                                String currentUser = Objects.requireNonNull( FirebaseAuth.getInstance().getCurrentUser() ).getUid();
                                assert key != null;
                                databaseReference.child( currentUser ).child( "Posts" ).child( "post" ).child( key ).removeValue();
                                bottomSheetDialog.dismiss();
                            }
                        } );
                    }
                } );

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
        recyclerView.setAdapter( adapter );
    }

}
