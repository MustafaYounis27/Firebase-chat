package com.example.myproject.fragments;

import android.app.ProgressDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.Models.UserModel;
import com.example.myproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Users extends Fragment
{
    List<UserModel> userModels = new ArrayList<> ();

    RecyclerView recyclerView;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    ProgressDialog dialog;

    static String uid;
    static UserModel myModel;

    View userView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        userView=inflater.inflate ( R.layout.activity_users,null );
        return userView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated ( savedInstanceState );

        initRecycler();
        initFirebase();
        initDialog ();
        getUsers();

    }

    private void initRecycler()
    {
        recyclerView=userView.findViewById ( R.id.recycler );
        recyclerView.setLayoutManager ( new LinearLayoutManager ( getContext (),RecyclerView.VERTICAL,false ) );
    }

    private void initFirebase()
    {
        auth=FirebaseAuth.getInstance ();
        databaseReference= FirebaseDatabase.getInstance ().getReference ();
    }

    private void initDialog()
    {
        dialog=new ProgressDialog ( getContext ());
        dialog.setMessage ( "please wait ..." );
        dialog.setCancelable ( false );
        dialog.show ();
    }

    private void getUsers()
    {
        databaseReference.child ( "users" ).addValueEventListener ( new ValueEventListener ()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                userModels.clear ();
                uid = auth.getUid ();
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
                Toast.makeText ( getContext (), databaseError.getMessage (), Toast.LENGTH_SHORT ).show ();
            }
        } );
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
            View view = getLayoutInflater ().from (getContext ()).inflate ( R.layout.users_item,parent,false );
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

            holder.isFriend ( userModel.getUid () );
            holder.isRequest ( userModel.getUid () );

            holder.add.setOnClickListener ( new View.OnClickListener ()
            {
                @Override
                public void onClick(View v)
                {
                    switch(holder.add.getText ().toString ())
                    {
                        case"Add friend":
                            databaseReference.child ( "requests" ).child ( userModel.getUid () ).child ( uid ).setValue ( myModel ).addOnCompleteListener ( new OnCompleteListener<Void> ()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful ())
                                    {
                                        Toast.makeText ( getContext (), "added successfully", Toast.LENGTH_SHORT ).show ();
                                    }
                                }
                            } );
                            break;
                        case"remove friend":
                            databaseReference.child ( "friends" ).child ( uid ).child ( userModel.getUid () ).removeValue ();
                            databaseReference.child ( "friends" ).child ( userModel.getUid () ).child ( uid ).removeValue ();
                            holder.add.setText ( "Add friend" );
                            break;
                        case"cancel request":
                            databaseReference.child ( "requests" ).child ( userModel.getUid () ).child ( uid ).removeValue ();
                            holder.add.setText ( "Add friend" );
                            break;
                    }
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

            userVH(@NonNull View itemView)
            {
                super ( itemView );
                circularImageView=itemView.findViewById ( R.id.user_image );
                username=itemView.findViewById ( R.id.username_field );
                email=itemView.findViewById ( R.id.email_field );
                mobile=itemView.findViewById ( R.id.mobile_field );
                address=itemView.findViewById ( R.id.address_field );
                add=itemView.findViewById ( R.id.add_friend );
            }

            void isFriend(final String id)
            {
                databaseReference.child ( "friends" ).child ( uid ).addValueEventListener ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.hasChild ( id ))
                        {
                            add.setText ( "remove friend" );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                } );
            }

            void isRequest(final String id)
            {
                databaseReference.child ( "requests" ).child ( id ).addValueEventListener ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.hasChild ( uid ))
                        {
                            add.setText ( "cancel request" );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                } );
            }
        }

    }
}
