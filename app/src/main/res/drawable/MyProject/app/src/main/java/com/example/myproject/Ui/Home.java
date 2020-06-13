package com.example.myproject.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myproject.Models.UserModel;
import com.example.myproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class Home extends AppCompatActivity
{

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    TextView username,email,mobile,address;
    CircularImageView circularImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_home );

        initViews();

        initFirebase ();
        databaseReference.addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText ( Home.this, databaseError.toException ().getMessage (), Toast.LENGTH_SHORT ).show ();
            }
        } );
    }

    private void initViews()
    {
        circularImageView=findViewById ( R.id.user_image );
        username=findViewById ( R.id.username_field );
        email=findViewById ( R.id.email_field );
        mobile=findViewById ( R.id.mobile_field );
        address=findViewById ( R.id.address_field );
    }

    private void initFirebase()
    {
        auth=FirebaseAuth.getInstance ();
        databaseReference= FirebaseDatabase.getInstance ().getReference ("users");
    }

    public void signOut(View view)
    {
        auth.signOut ();
        startActivity ( new Intent (getApplicationContext (), MainActivity.class) );
        finish ();
    }

    public void users(View view)
    {
        startActivity ( new Intent (getApplicationContext (), Users.class ) );
    }

    public void request(View view)
    {
        startActivity ( new Intent(getApplicationContext (),Requests.class) );
    }

    public void friends(View view)
    {
        startActivity ( new Intent ( getApplicationContext (),Friends.class ) );
    }
}
