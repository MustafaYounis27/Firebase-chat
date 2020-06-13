package com.example.myproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myproject.Models.UserModel;
import com.example.myproject.R;
import com.example.myproject.Ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class Home extends Fragment
{
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    TextView username,email,mobile,address;
    CircularImageView circularImageView;
    Button signOut;

    View mainView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mainView=inflater.inflate ( R.layout.activity_home,null );
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated ( savedInstanceState );

        initViews();
        initFirebase ();
        databaseReference.addListenerForSingleValueEvent ( new ValueEventListener ()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                UserModel userModel = dataSnapshot.child ( auth.getCurrentUser ().getUid () ).getValue (UserModel.class);

                Picasso.get ()
                        .load ( userModel.getImageURL () )
                        .into ( circularImageView );

                username.setText ( userModel.getUsername () );
                email.setText ( userModel.getEmail () );
                mobile.setText ( userModel.getMobile () );
                address.setText ( userModel.getAddress () );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText ( getContext (), databaseError.toException ().getMessage (), Toast.LENGTH_SHORT ).show ();
            }
        } );

        signOut.setOnClickListener ( new View.OnClickListener ()
        {
            @Override
            public void onClick(View v)
            {
                signOut();
            }
        } );
    }

    private void initViews()
    {
        circularImageView=mainView.findViewById ( R.id.user_image );
        username=mainView.findViewById ( R.id.username_field );
        email=mainView.findViewById ( R.id.email_field );
        mobile=mainView.findViewById ( R.id.mobile_field );
        address=mainView.findViewById ( R.id.address_field );
        signOut=mainView.findViewById ( R.id.signOut );
    }

    private void initFirebase()
    {
        auth=FirebaseAuth.getInstance ();
        databaseReference= FirebaseDatabase.getInstance ().getReference ("users");
        databaseReference.keepSynced ( true );
    }

    public void signOut()
    {
        auth.signOut ();
        startActivity ( new Intent (getContext (), MainActivity.class) );
    }
}
