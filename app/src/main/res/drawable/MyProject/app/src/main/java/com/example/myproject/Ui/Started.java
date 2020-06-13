package com.example.myproject.Ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.myproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Started extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_started );

        setupGraph();
    }

    private void setupGraph()
    {
        BottomNavigationView navView = findViewById(R.id.bottom_nav);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
