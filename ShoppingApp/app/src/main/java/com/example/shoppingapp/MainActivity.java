package com.example.shoppingapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageView ivCart;

    EditText etUsername, etPassword;
    Button btnLogin, btnRegisterLink;

    EditText etRegisterUsername, etRegisterEmail, etRegisterPassword, etRegisterConfirmPassword;
    Button btnRegisterUser;

    EditText etUploadName, etUploadPrice, etUploadDescription;
    Button btnUploadItem;

    RecyclerView rvList;
    RecyclerView.Adapter myAdapter;

    FragmentManager fragmentManager;
    Fragment fragNavigationLayout, fragLoginLayout, fragRegisterLayout, fragUploadLayout, fragShopLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivCart = findViewById(R.id.ivCart);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id. etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterLink = findViewById(R.id.btnRegisterLink);

        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);

        btnRegisterUser = findViewById(R.id.btnRegisterUser);
        etUploadName = findViewById(R.id.etUploadName);
        etUploadPrice = findViewById(R.id.etUploadPrice);
        etUploadDescription = findViewById(R.id.etUploadDescription);
        btnUploadItem = findViewById(R.id.btnUploadItem);

        rvList = findViewById(R.id.rvList);

        //TODO setup adapter constructor in ItemAdapter and create it accordingly
        // once we gain access to the backend API from Tony
        // example: myAdapter = new ItemAdapter();

        fragmentManager = getSupportFragmentManager();

        fragNavigationLayout = fragmentManager.findFragmentById(R.id.fragNavigationLayout);
        fragLoginLayout = fragmentManager.findFragmentById(R.id.fragLoginLayout);
        fragRegisterLayout = fragmentManager.findFragmentById(R.id.fragRegisterLayout);
        fragUploadLayout = fragmentManager.findFragmentById(R.id.fragUploadLayout);
        fragShopLayout = fragmentManager.findFragmentById(R.id.fragShopLayout);

        fragmentManager.beginTransaction().show(fragNavigationLayout)
                                            .show(fragLoginLayout)
                                            .hide(fragRegisterLayout)
                                            .hide(fragUploadLayout)
                                            .hide(fragShopLayout)
                                            .commit();

        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO check w backend for valid user and showToast if valid, then beginTransaction
                // else
                fragmentManager.beginTransaction().show(fragNavigationLayout)
                                                    .hide(fragLoginLayout)
                                                    .hide(fragRegisterLayout)
                                                    .show(fragShopLayout)
                                                    .commit();
            }
        });

        btnRegisterLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                fragmentManager.beginTransaction().show(fragNavigationLayout)
                                                    .hide(fragLoginLayout)
                                                    .show(fragRegisterLayout)
                                                    .commit();
            }
        });

        btnRegisterUser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // save to database then return to login screen
                fragmentManager.beginTransaction().show(fragNavigationLayout)
                        .hide(fragRegisterLayout)
                        .show(fragLoginLayout)
                        .commit();
            }
        });

        btnUploadItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // save to database possibly, but definitely update the current shop then return to shop
            }
        });
    }
}