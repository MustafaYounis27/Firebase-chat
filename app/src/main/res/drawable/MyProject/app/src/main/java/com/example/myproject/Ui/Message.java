package com.example.myproject.Ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.Models.UserModel;
import com.example.myproject.Models.chatModel;
import com.example.myproject.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Message extends AppCompatActivity
{
    List<chatModel> messages=new ArrayList<>();
    Uri imageUri;
    String imageUrl;

    RecyclerView recyclerView;
    EditText messageField;
    TextView username;
    CircularImageView circularImageView;
    ImageButton imageButton;

    FirebaseAuth auth;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    static String uid;
    private UserModel userModel,myModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_message );

        initFirebase();
        uid = auth.getUid ();
        userModel= (UserModel) getIntent ().getSerializableExtra ( "chat" );
        getUser(uid);
        initViews();
        initRecycler();
        getMessages ( uid );
        imageButton.setOnLongClickListener ( new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {
                Intent pickPhoto = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                startActivityForResult ( pickPhoto,1 );

                return false;
            }
        } );
    }

    private void getUser(String uid)
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
                Toast.makeText ( Message.this, databaseError.getMessage (), Toast.LENGTH_SHORT ).show ();
            }
        } );
    }

    private void initFirebase()
    {
        auth=FirebaseAuth.getInstance ();
        databaseReference= FirebaseDatabase.getInstance ().getReference ();
        databaseReference.keepSynced ( true );
    }

    private void getMessages(final String uid )
    {
        databaseReference.child ( "chats" ).child ( uid ).child ( userModel.getUid () ).addValueEventListener ( new ValueEventListener ()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                messages.clear ();

                for (DataSnapshot d : dataSnapshot.getChildren ())
                {
                    chatModel chatModel=d.getValue ( chatModel.class );
                    messages.add ( chatModel );
                }

                recyclerView.setAdapter ( new Adapter ( messages ) );

                if(messages.size () != 0)
                    recyclerView.scrollToPosition ( messages.size ()-1 );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        } );
    }

    private void initRecycler()
    {
        recyclerView=findViewById ( R.id.recycler );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager ( getApplicationContext () , RecyclerView.VERTICAL,false );
        recyclerView.setLayoutManager ( linearLayoutManager );
    }

    private void initViews()
    {
        messageField=findViewById ( R.id.message );
        username=findViewById ( R.id.username );
        circularImageView=findViewById ( R.id.user_image );
        imageButton=findViewById ( R.id.send_image );

        username.setText ( userModel.getUsername () );
        Picasso.get ()
                .load ( userModel.getImageURL () )
                .into ( circularImageView );

    }

    public void sendMessage(View view)
    {

        String msg = messageField.getText ().toString ();

        if (msg.isEmpty ()) {
            Toast.makeText ( this, "enter your message", Toast.LENGTH_SHORT ).show ();
            return;
        }

        sendMsg ( msg );
        messageField.setText ( "" );
    }

    private void sendMsg(String msg)
    {
        String key = databaseReference.child ( "chats" ).child ( uid ).child ( userModel.getUid () ).push ().getKey ();

        chatModel chatModel = new chatModel ( msg, myModel.getUsername (), myModel.getImageURL (), "05:00 pm", myModel.getUid (), userModel.getUid (), key );
        chatModel.setType ( 0 );

        if (key != null) {
            databaseReference.child ( "chats" ).child ( uid ).child ( userModel.getUid () ).child ( key ).setValue ( chatModel );
            databaseReference.child ( "chats" ).child ( userModel.getUid () ).child ( uid ).child ( key ).setValue ( chatModel );
        }
    }

    class Adapter extends RecyclerView.Adapter<Adapter.VH>
    {
        List<chatModel> chatModels;

        public Adapter(List<chatModel> chatModels)
        {
            this.chatModels = chatModels;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = getLayoutInflater ().from ( getApplicationContext () ).inflate ( R.layout.message_item,parent, false );
            return new VH ( view );
        }

        @Override
        public void onBindViewHolder(@NonNull final VH holder, int position)
        {
            chatModel chatModel = chatModels.get ( position );

            String message = chatModel.getMessage ();
            String time = chatModel.getTime ();
            String senderId = chatModel.getSenderId ();
            int type = chatModel.getType ();

            if(type == 1)
            {
                Picasso.get ()
                        .load ( message )
                        .into ( holder.chatImage );
                holder.card.setVisibility ( View.GONE );
            }else
                {
                    holder.chatImage.setVisibility ( View.GONE );
                    holder.message.setText ( message );
                    holder.time.setText ( time );
                }

            if(senderId.equals ( uid ))
            {
                holder.chatLin.setGravity ( Gravity.END );
                holder.colorLin.setBackgroundColor (getResources ().getColor ( R.color.gray ));
                holder.message.setTextColor (getResources ().getColor ( R.color.white ));
                holder.time.setTextColor (getResources ().getColor ( R.color.white ));
            }
        }

        @Override
        public int getItemCount()
        {
            return chatModels.size ();
        }

        class VH extends RecyclerView.ViewHolder
        {
            TextView message,time;
            LinearLayout chatLin,colorLin;
            ImageView chatImage;
            ImageButton sendImage;
            CardView card;

            public VH(@NonNull View itemView)
            {
                super ( itemView );
                message=itemView.findViewById ( R.id.messages );
                time=itemView.findViewById ( R.id.chat_time );
                chatLin=itemView.findViewById ( R.id.chat_lin );
                colorLin=itemView.findViewById ( R.id.chat_lin_color );
                chatImage=itemView.findViewById ( R.id.chat_image );
                sendImage=itemView.findViewById ( R.id.send_image );
                card=itemView.findViewById ( R.id.card );
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult ( requestCode, resultCode, data );

        if(requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData ();

            storageReference= FirebaseStorage.getInstance ().getReference ().child ( "chat image/"+imageUri.getLastPathSegment () );
            UploadTask uploadTask=storageReference.putFile ( imageUri );

            Task<Uri> task = uploadTask.continueWithTask ( new Continuation<UploadTask.TaskSnapshot, Task<Uri>> ()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    return storageReference.getDownloadUrl ();
                }
            } ).addOnCompleteListener ( new OnCompleteListener<Uri> ()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if(task.isSuccessful ())
                    {
                        Toast.makeText ( Message.this, "load", Toast.LENGTH_SHORT ).show ();
                        imageUri=task.getResult ();
                        imageUrl=imageUri.toString ();
                        String key = databaseReference.child ( "chats" ).child ( uid ).child ( userModel.getUid () ).push ().getKey ();

                        chatModel chatModel = new chatModel ( imageUrl, myModel.getUsername (), myModel.getImageURL (), "05:00 pm", myModel.getUid (), userModel.getUid (), key );
                        chatModel.setType ( 1 );
                        imageUri=null;

                        if (key != null) {
                            databaseReference.child ( "chats" ).child ( uid ).child ( userModel.getUid () ).child ( key ).setValue ( chatModel );
                            databaseReference.child ( "chats" ).child ( userModel.getUid () ).child ( uid ).child ( key ).setValue ( chatModel );
                        }
                    }else
                    {
                        Toast.makeText ( Message.this, task.getException ().getMessage (), Toast.LENGTH_SHORT ).show ();
                    }

                }
            } );
        }

    }

}
