package com.example.myproject.fragments;

import android.app.ProgressDialog;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.Models.UserModel;
import com.example.myproject.R;
import com.example.myproject.Ui.Message;
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

public class Friends extends Fragment
{
    List<UserModel> userModels = new ArrayList<> ();

    RecyclerView recyclerView;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    ProgressDialog dialog;

    static String uid;
    static UserModel myModel;

    View friendView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        friendView=inflater.inflate ( R.layout.activity_friends,null );
        return friendView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated ( savedInstanceState );

        initDialog();
        initRecycler();
        initFirebase();

        uid = auth.getUid ();
        getUsers(uid);
        getData ( uid );
    }

    private void initDialog()
    {
        dialog=new ProgressDialog ( getContext ());
        dialog.setMessage ( "please wait ..." );
        dialog.setCancelable ( false );

        dialog.show ();
    }

    private void getUsers(String uid)
    {
        databaseReference.child ( "friends" ).child ( uid ).addValueEventListener ( new ValueEventListener ()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                userModels.clear ();

                for(DataSnapshot d : dataSnapshot.getChildren ())
                {
                    UserModel userModel = d.getValue (UserModel.class);
                    userModels.add ( userModel );
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

    private void initFirebase()
    {
        auth=FirebaseAuth.getInstance ();
        databaseReference= FirebaseDatabase.getInstance ().getReference ();
    }

    private void initRecycler()
    {
        recyclerView=friendView.findViewById ( R.id.recycler );
        recyclerView.setLayoutManager ( new LinearLayoutManager ( getContext (),RecyclerView.VERTICAL,false ) );
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
            View view = getLayoutInflater().from (getContext ()).inflate ( R.layout.users_item,parent,false );
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
                    Intent intent = new Intent ( getContext (), Message.class );
                    intent.putExtra ("chat",userModel);
                    startActivity ( intent );
                }
            } );

            holder.cardView.setOnLongClickListener ( new View.OnLongClickListener ()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    databaseReference.child ( "friends" ).child ( uid ).child ( userModel.getUid () ).removeValue ();
                    databaseReference.child ( "friends" ).child ( userModel.getUid () ).child ( uid ).removeValue ();
                    Toast.makeText ( getContext (), "you and "+userModel.getUsername ()+" aren't Friends", Toast.LENGTH_SHORT ).show ();
                    return false;
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
            CardView cardView;

            public userVH(@NonNull View itemView)
            {
                super ( itemView );
                circularImageView=itemView.findViewById ( R.id.user_image );
                username=itemView.findViewById ( R.id.username_field );
                email=itemView.findViewById ( R.id.email_field );
                mobile=itemView.findViewById ( R.id.mobile_field );
                address=itemView.findViewById ( R.id.address_field );
                add=itemView.findViewById ( R.id.add_friend );
                cardView=itemView.findViewById ( R.id.remove );
                add.setText ( "send message" );
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
                Toast.makeText ( getContext (), databaseError.getMessage (), Toast.LENGTH_SHORT ).show ();
            }
        } );
    }
}
