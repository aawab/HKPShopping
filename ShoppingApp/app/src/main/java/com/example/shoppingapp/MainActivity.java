package com.example.shoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView ivCart;

    EditText etUsername, etPassword;
    Button btnLogin, btnRegisterLink;

    EditText etRegisterUsername, etRegisterEmail, etRegisterPassword, etRegisterConfirmPassword;
    Button btnRegisterUser;

    EditText etUploadName, etUploadPrice, etUploadDescription;
    Button btnUploadItem;

    RecyclerView rvList, rvCart;
    RecyclerView.Adapter<ItemAdapter.ViewHolder> myAdapter;
    //TODO declare cartAdapter w corresponding viewholder here, then initialize like with itemAdapter

    //TODO probably need to setup a basic cartItem class and adapter for the cartFragment exactly
    //how I did the shopFragment(or any way you think is better @Andrew)

    //TODO setup list or other data container to communicate w backend DB e.g ArrayList, etc.

    FragmentManager fragmentManager;
    Fragment fragNavigationLayout, fragLoginLayout, fragRegisterLayout, fragUploadLayout, fragShopLayout,
            fragCartLayout;

    Boolean isLoggedIn; //bool for use for buttonClicks

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
        rvCart = findViewById(R.id.rvCart);

        //TODO setup adapter constructor in ItemAdapter and create it accordingly
        // once we gain access to the backend API from Tony
        // example: myAdapter = new ItemAdapter(getApplicationContext(),insert list name here);

        fragmentManager = getSupportFragmentManager();

        //TODO temporarily swapped out navigationFrag for an actionBar for ease of use cuz i cant
        //figure out how to get rid of the default app banner tht shows on top of the navigationFrag
        fragNavigationLayout = fragmentManager.findFragmentById(R.id.fragNavigationLayout);
        fragLoginLayout = fragmentManager.findFragmentById(R.id.fragLoginLayout);
        fragRegisterLayout = fragmentManager.findFragmentById(R.id.fragRegisterLayout);
        fragUploadLayout = fragmentManager.findFragmentById(R.id.fragUploadLayout);
        fragShopLayout = fragmentManager.findFragmentById(R.id.fragShopLayout);
        fragCartLayout = fragmentManager.findFragmentById(R.id.fragCartLayout);

        //temporary, if we end up sticking to actionBar then we'll remove navFrag entirely
        fragmentManager.beginTransaction().hide(fragNavigationLayout)
                                            .show(fragLoginLayout)
                                            .hide(fragRegisterLayout)
                                            .hide(fragUploadLayout)
                                            .hide(fragShopLayout)
                                            .hide(fragCartLayout)
                                            .commit();
        isLoggedIn=false;
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO check w backend for valid user, showToast and set isLoggedIn to true if valid,
                // then beginTransaction
                // else Toast invalid user and stay at same layout
                fragmentManager.beginTransaction().hide(fragLoginLayout)
                                                    .hide(fragRegisterLayout)
                                                    .show(fragShopLayout)
                                                    .commit();
                isLoggedIn=true;
            }
        });

        btnRegisterLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                fragmentManager.beginTransaction().hide(fragLoginLayout)
                                                    .show(fragRegisterLayout).addToBackStack(null)
                                                    .commit();
            }
        });

        btnRegisterUser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO save to database then return to login screen, Toast register successful
                //if backend sends back successful response
                fragmentManager.beginTransaction().hide(fragRegisterLayout)
                                                .show(fragLoginLayout).addToBackStack(null)
                                                .commit();
            }
        });

        btnUploadItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO save to database possibly, but definitely update the current shop then return to shop
                //notifyDataSetChanged method if necessary, check if isLoggedIn is true
            }
        });
    }


    //ActionBar functionality here
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.cart:
                if(isLoggedIn)fragmentManager.beginTransaction().show(fragCartLayout)
                                                .hide(fragLoginLayout)
                                                .hide(fragRegisterLayout)
                                                .hide(fragUploadLayout)
                                                .hide(fragShopLayout).addToBackStack(null)
                                                .commit();
                else Toast.makeText(getApplicationContext(),"Not logged in, cannot access cart.",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.upload:
                if(isLoggedIn)fragmentManager.beginTransaction().show(fragUploadLayout)
                                                    .hide(fragLoginLayout)
                                                    .hide(fragRegisterLayout)
                                                    .hide(fragCartLayout)
                                                    .hide(fragShopLayout).addToBackStack(null)
                                                    .commit();
                else Toast.makeText(getApplicationContext(),"Not logged in, cannot upload item.",
                        Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}