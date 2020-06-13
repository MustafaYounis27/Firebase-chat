package com.example.myproject.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myproject.Models.UserModel;
import com.example.myproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Requests extends AppCompatActivity
{

    List<UserModel> userModels = new ArrayList<> ();

    RecyclerView recyclerView;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    ProgressDialog dialog;

    static String uid;
    static UserModel myModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_requests );

        initDialog();
        initRecycler();
        initFirebase();

        uid = auth.getUid ();
        getUsers(uid);
        getData (uid);
    }

    private void initDialog()
    {
        dialog=new ProgressDialog ( Requests.this);
        dialog.setMessage ( "please wait ..." );
        dialog.setCancelable ( false );

        dialog.show ();
    }

    private void getUsers(final String uid)
    {
        databaseReference.child ( "requests" ).child ( uid ).addValueEventListener ( new ValueEventListener ()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                userModels.clear ();

                for(DataSnapshot d : dataSnapshot.getChildren ())
                {
                    UserModel userModel = d.getValue (UserModel.class);
                    if(!userModel.getUid ().equals ( uid ))
                    {
                        userModels.add ( userModel );
                    }else
                        {
                            myModel=userModel;
                        }

                }
                recyclerView.setAdapter ( new Adapter ( userModels ) );
                dialog.dismiss ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText ( Requests.this, databaseError.getMessage (), Toast.LENGTH_SHORT ).show ();
            }
        } );
    }

    private void initFirebase()
    {
        auth=FirebaseAuth.getInstance ();
        databaseReference= FirebaseDatabase.getInstance ().getReference ();
    }

    private void initRecycler()
    {
        recyclerView=findViewById ( R.id.recycler );
        recyclerView.setLayoutManager ( new LinearLayoutManager ( getApplicationContext (),RecyclerView.VERTICAL,false ) );
    }

    class Adapter extends RecyclerView.Adapter<Adapter.userVH>
    {
        List<UserModel> userModels;

        public Adapter(List<UserModel> userModels) {
            this.userModels = userModels;
        }

        @NonNull
        @Override
        public userVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = getLayoutInflater().from (getApplicationContext ()).inflate ( R.layout.users_item,parent,false );
            return new userVH (view);
        }

        @Override
        public void onBindViewHolder(@NonNull final userVH holder, int position)
        {
            final UserModel userModel = userModels.get ( position );

            Picasso.get ()
                    .load ( userModel.getImageURL () )
                    .into ( holder.circularImageView );

            holder.username.setText ( userModel.getUsername () );
            holder.email.setText ( userModel.getEmail () );
            holder.mobile.setText ( userModel.getMobile () );
            holder.address.setText ( userModel.getAddress () );

            holder.add.setOnClickListener ( new View.OnClickListener ()
            {
                @Override
                public void onClick(View v)
                {
                    databaseReference.child ( "friends" ).child ( uid ).child ( userModel.getUid () ).setValue ( userModel ).addOnCompleteListener ( new OnCompleteListener<Void> ()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful ())
                            {
                                Toast.makeText ( Requests.this, "you and " + userModel.getUsername () + " are friends", Toast.LENGTH_SHORT ).show ();
                                databaseReference.child ( "requests" ).child ( uid ).child ( userModel.getUid () ).removeValue ();
                            } else
                            {
                                Toast.makeText ( Requests.this, task.getException ().getMessage (), Toast.LENGTH_SHORT ).show ();
                            }

                        }
                    } );

                    databaseReference.child ( "friends" ).child ( userModel.getUid () ).child ( uid ).setValue ( myModel ).addOnCompleteListener ( new OnCompleteListener<Void> ()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                        }
                    } );
                }
            } );
        }

        @Override
        public int getItemCount()
        {
            return userModels.size ();
        }

        class userVH extends RecyclerView.ViewHolder
        {
            TextView username,email,mobile,address;
            CircularImageView circularImageView;
            Button add;

            public userVH(@NonNull View itemView)
            {
                super ( itemView );
                circularImageView=itemView.findViewById ( R.id.user_image );
                username=itemView.findViewById ( R.id.username_field );
                email=itemView.findViewById ( R.id.email_field );
                mobile=itemView.findViewById ( R.id.mobile_field );
                address=itemView.findViewById ( R.id.address_field );
                add=itemView.findViewById ( R.id.add_friend );

                add.setText ( "accept" );
            }
        }

    }

    private void getData(String uid)
    {
        databaseReference.child ( "users" ).child ( uid ).addListenerForSingleValueEvent ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                myModel=dataSnapshot.getValue (UserModel.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText ( Requests.this, databaseError.getMessage (), Toast.LENGTH_SHORT ).show ();
            }
        } );
    }
}
