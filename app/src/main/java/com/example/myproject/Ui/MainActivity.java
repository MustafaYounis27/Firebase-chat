package com.example.myproject.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText emailField, passwordField;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        auth=FirebaseAuth.getInstance ();
        initViews();
        initDialog();
    }

    private void initViews()
    {
        emailField=findViewById ( R.id.email_field );
        passwordField=findViewById ( R.id.password_field );
    }

    private void initDialog()
    {
        dialog=new ProgressDialog ( MainActivity.this);
        dialog.setTitle ( "login" );
        dialog.setMessage ( "please wait ..." );
        dialog.setCancelable ( false );
    }

    public void register(View view)
    {
        startActivity ( new Intent (getApplicationContext (), Register.class) );
        finish ();
    }

    public void signIn(View view)
    {
        final String email = emailField.getText ().toString ();
        final String password = passwordField.getText ().toString ();

        if(email.isEmpty ())
        {
            Toast.makeText ( this, "enter email", Toast.LENGTH_SHORT ).show ();
            return;
        }

        if (password.isEmpty ())
        {
            Toast.makeText ( this, "enter password", Toast.LENGTH_SHORT ).show ();
            return;
        }
        dialog.show ();
        auth.signInWithEmailAndPassword ( email,password ).addOnCompleteListener ( new OnCompleteListener<AuthResult> ()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful ())
                {
                    dialog.dismiss ();
                    startActivity ( new Intent(getApplicationContext (), Started.class) );
                    finish ();
                }

                else
                {
                    dialog.dismiss ();
                    Toast.makeText ( MainActivity.this, task.getException ().getMessage (), Toast.LENGTH_SHORT ).show ();
                }
            }
        } );
    }

    @Override
    protected void onStart()
    {
        super.onStart ();

        FirebaseUser user = auth.getCurrentUser ();
        if(user != null)
            startActivity ( new Intent ( getApplicationContext (),Started.class ) );

    }
}
