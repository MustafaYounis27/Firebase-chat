package com.example.myproject.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myproject.Models.UserModel;
import com.example.myproject.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class Register extends AppCompatActivity {

    //auth
    FirebaseAuth auth;
    //database
    DatabaseReference databaseReference;
    //storage
    StorageReference storageReference;

    EditText usernameField, emailField, passwordField, confirmPasswordField, mobileField, addressField;
    CircularImageView circularImageView;
    Uri imageUri;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_register );

        initFirebase();
        initViews();
        initDialog();
        chooseImage();
    }

    private void chooseImage()
    {
        circularImageView.setOnClickListener ( new View.OnClickListener ()
        {
            @Override
            public void onClick(View v)
            {
                Intent pickPhoto = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                startActivityForResult ( pickPhoto,1 );

            }
        } );
    }

    private void initDialog()
    {
        dialog=new ProgressDialog ( Register.this);
        dialog.setTitle ( "Register" );
        dialog.setMessage ( "please wait ..." );
        dialog.setCancelable ( false );
    }

    private void initViews()
    {
        usernameField=findViewById ( R.id.username_field );
        emailField=findViewById ( R.id.email_field );
        passwordField=findViewById ( R.id.password_field );
        confirmPasswordField=findViewById ( R.id.confirm_password_field );
        mobileField=findViewById ( R.id.mobile_field );
        addressField=findViewById ( R.id.address_field );
        circularImageView=findViewById ( R.id.user_image );
    }

    private void initFirebase()
    {
        auth=FirebaseAuth.getInstance ();
        databaseReference= FirebaseDatabase.getInstance ().getReference ();
    }

    public void signIn(View view)
    {
        startActivity ( new Intent ( getApplicationContext (), MainActivity.class ) );
        finish ();
    }

    public void signUp(View view)
    {
        final String username=usernameField.getText ().toString ();
        final String email=emailField.getText ().toString ();
        String password=passwordField.getText ().toString ();
        String confirmPassword=confirmPasswordField.getText ().toString ();
        final String mobile=mobileField.getText ().toString ();
        final String address=addressField.getText ().toString ();

        if(imageUri == null)
        {
            Toast.makeText ( this, "select an image", Toast.LENGTH_SHORT ).show ();
            return;
        }

        if(username.isEmpty ())
        {
            Toast.makeText ( this, "enter username", Toast.LENGTH_SHORT ).show ();
            return;
        }

        if(email.isEmpty ())
        {
            Toast.makeText ( this, "enter email", Toast.LENGTH_SHORT ).show ();
            return;
        }

        if(password.isEmpty ())
        {
            Toast.makeText ( this, "enter password", Toast.LENGTH_SHORT ).show ();
            return;
        }

        if(!confirmPassword.equals ( password ))
        {
            Toast.makeText ( this, "password don't match", Toast.LENGTH_SHORT ).show ();
            return;
        }

        if(mobile.isEmpty ())
        {
            Toast.makeText ( this, "enter mobile", Toast.LENGTH_SHORT ).show ();
            return;
        }

        if(address.isEmpty ())
        {
            Toast.makeText ( this, "enter address", Toast.LENGTH_SHORT ).show ();
            return;
        }

        dialog.show ();
        registerFirebase(username,email,password,mobile,address,imageUri);

    }

    private void registerFirebase(final String username, final String email, String password, final String mobile, final String address, final Uri imageUri)
    {
        auth.createUserWithEmailAndPassword ( email,password ).addOnCompleteListener ( new OnCompleteListener<AuthResult> ()
        {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {

                if(task.isSuccessful ())
                {
                    String uid = task.getResult ().getUser ().getUid ();

                    uploadData(username,email,mobile,address,imageUri,uid);
                }
                else
                {
                    Toast.makeText ( Register.this, task.getException ().getMessage (), Toast.LENGTH_SHORT ).show ();
                }
            }
        } );
    }

    private void uploadData(final String username, final String email, final String mobile, final String address, Uri imageUri, final String uid)
    {
        storageReference= FirebaseStorage.getInstance ().getReference ().child ( "users_image/"+imageUri.getLastPathSegment () );
        UploadTask uploadTask = storageReference.putFile ( imageUri );

        Task<Uri> task = uploadTask.continueWithTask ( new Continuation<UploadTask.TaskSnapshot, Task<Uri>> () {
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
                Uri imageUri = task.getResult ();
                String imageUrl = imageUri.toString ();

                uploadDatabase(username,email,mobile,address,imageUrl,uid);
            }
        } );
    }

    private void uploadDatabase(String username, String email, String mobile, String address, String imageUrl, String uid)
    {
        UserModel user = new UserModel ( username,email,mobile,address,imageUrl,uid );

        databaseReference.child ( "users" ).child ( uid ).setValue ( user ).addOnCompleteListener ( new OnCompleteListener<Void> ()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                dialog.dismiss ();
                startActivity ( new Intent(getApplicationContext (), Started.class) );
                finish ();
            }
        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if(requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData ();
            Picasso.get ()
                    .load ( imageUri )
                    .into ( circularImageView );
        }
    }
}
