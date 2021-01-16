package com.example.shoppingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoppingapp.ValidUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements ItemAdapter.ItemClicked{


    EditText etUsername, etPassword;
    Button btnLogin, btnRegisterLink;

    EditText etRegisterUsername, etRegisterEmail, etRegisterPassword, etRegisterConfirmPassword;
    Button btnRegisterUser;

    EditText etUploadName, etUploadPrice, etUploadDescription;
    Button btnUploadItem, btnAddImage;
    ImageView ivImage;
    String filePath;

    TextView tvSubtotal;
    Button btnCheckout;

    RecyclerView rvShop, rvCart;
    RecyclerView.Adapter<ItemAdapter.ViewHolder> itemsAdapter;
    RecyclerView.Adapter<CartAdapter.ViewHolder> cartAdapter;

    ArrayList<Item> items;
    ArrayList<Item> cart;

    FragmentManager fragmentManager;
    Fragment fragLoginLayout, fragRegisterLayout, fragUploadLayout, fragShopLayout,
            fragCartLayout;

    Boolean isLoggedIn; //bool for use for buttonClicks
    Boolean isAdmin; //bool for creating items if admin
    String jwtToken;

    double subTotal;

    @Override
    public void onItemClicked(int position)
    {
        boolean found=false;

        for(Item a:cart)
        {
            if(a.getName().equals(items.get(position).getName())){
               a.setQuantity(a.getQuantity()+1);
               found=true;
            }
        }
        if(!found){
            Item i = new Item(items.get(position).getName(),items.get(position).getPrice(),items.get
                    (position).getDescription());
            cart.add(i);

            new CartAddOnlineInBackground().execute(i);
        }

        cartAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id. etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterLink = findViewById(R.id.btnRegisterLink);

        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);

        btnRegisterUser = findViewById(R.id.btnRegisterUser);
        etUploadName = findViewById(R.id.tvDetailName);
        etUploadPrice = findViewById(R.id.tvDetailPrice);
        etUploadDescription = findViewById(R.id.tvDetailDesc);
        btnUploadItem = findViewById(R.id.btnAddToCart);
        btnAddImage = findViewById(R.id.btnAddImage);
        ivImage = findViewById(R.id.ivSelectedImage);
        ivImage.setVisibility(View.GONE);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        btnCheckout = findViewById(R.id.btnCheckout);

        rvShop = findViewById(R.id.rvShop);
        rvCart = findViewById(R.id.rvCart);

        items = new ArrayList<Item>();
        cart = new ArrayList<Item>();

        itemsAdapter = new ItemAdapter(this,items);
        cartAdapter = new CartAdapter(this,cart);

        rvShop.setAdapter(itemsAdapter);
        rvCart.setAdapter(cartAdapter);

        fragmentManager = getSupportFragmentManager();

        fragLoginLayout = fragmentManager.findFragmentById(R.id.fragLoginLayout);
        fragRegisterLayout = fragmentManager.findFragmentById(R.id.fragRegisterLayout);
        fragUploadLayout = fragmentManager.findFragmentById(R.id.fragUploadLayout);
        fragShopLayout = fragmentManager.findFragmentById(R.id.fragShopLayout);
        fragCartLayout = fragmentManager.findFragmentById(R.id.fragCartLayout);

        //temporary, if we end up sticking to actionBar then we'll remove navFrag entirely
        fragmentManager.beginTransaction() .show(fragLoginLayout)
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
                // show shop fragment after web server login request
                if ( ValidUtils.isValidUsername(etUsername.getText().toString()) && ValidUtils.isValidPassword(etPassword.getText().toString()) )
                {
                    items.clear();
                    cart.clear();
                    new LoginInBackground().execute(etUsername.getText().toString(), etPassword.getText().toString());
                }
                else
                {
                    Log.i("shopLogin", "Username status: " + ValidUtils.isValidUsername(etUsername.getText().toString()) + "\n" +
                            "Password status: " + ValidUtils.isValidPassword(etPassword.getText().toString()));
                }
            }
        });

        btnRegisterLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                fragmentManager.beginTransaction()
                        .hide(fragLoginLayout)
                        .show(fragRegisterLayout).addToBackStack(null)
                        .commit();
            }
        });

        btnRegisterUser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // save to database then return to login screen
                if ( ValidUtils.isValidUsername(etRegisterUsername.getText().toString())
                        && ValidUtils.isValidPassword(etRegisterPassword.getText().toString())
                        && etRegisterPassword.getText().toString().equals(etRegisterConfirmPassword.getText().toString())
                        && ValidUtils.isValidEmail(etRegisterEmail.getText().toString()) ) {
                    new RegisterInBackground().execute(etRegisterUsername.getText().toString(), etRegisterPassword.getText().toString(), etRegisterEmail.getText().toString());
                }
                else
                {
                    Log.i( "shopLogin", "Username status: " + ValidUtils.isValidUsername(etRegisterUsername.getText().toString()) + "\n" +
                            "Password status: " + ValidUtils.isValidPassword(etRegisterPassword.getText().toString()) + "\n" +
                            "Confirm Password status: " + etRegisterPassword.getText().toString().equals(etRegisterConfirmPassword.getText().toString()) + "\n" +
                            "Email status: " + ValidUtils.isValidEmail(etRegisterEmail.getText().toString()) );
                }
            }
        });

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });
        btnUploadItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // save to database possibly, but definitely update the current shop then return to shop
                if ( (etUploadName.getText().toString().isEmpty() == false) && (etUploadPrice.getText().toString().isEmpty() == false) )
                {
                    new UploadInBackground().execute(etUploadName.getText().toString(), etUploadPrice.getText().toString(), etUploadDescription.getText().toString(), jwtToken);
                }
            }
        });
        
        btnCheckout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                if(cart.size()==0)
                {
                    Toast.makeText(getApplicationContext(),"Cart is empty!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    new CheckoutCartInBackground().execute();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                Uri imageUri = data.getData();
                btnAddImage.setVisibility(View.GONE);
                ivImage.setVisibility(View.VISIBLE);
                ivImage.setImageURI(imageUri);

                filePath=imageUri.getPath();
                File file = new File(filePath);
            }
        }
    }

    //public String getPath(Uri uri) {
    //    String[] projection = {MediaColumns.DATA};
    //    Cursor cursor = managedQuery(uri, projection, null, null, null);
    //    column_index = cursor
    //            .getColumnIndexOrThrow(MediaColumns.DATA);
    //    cursor.moveToFirst();
    //    imagePath = cursor.getString(column_index);
    //
    //    return cursor.getString(column_index);
    //}

    @Override
    public void onBackPressed() {
        if(fragmentManager.findFragmentById(R.id.fragCartLayout).isVisible())
        {
            for(int i =fragmentManager.getBackStackEntryCount(); i >2 ; i--){
                fragmentManager.popBackStack();
            }

        }
        super.onBackPressed();

        //Log.i("shopLog",""+fragmentManager.getBackStackEntryAt(0).toString());

        Log.i("shopLog","num " + fragmentManager.getBackStackEntryCount());
        if(fragmentManager.getBackStackEntryCount() == 0 )
        {
            isLoggedIn=false;
            isAdmin=false;
            jwtToken="";
            items.clear();
            cart.clear();
            itemsAdapter.notifyDataSetChanged();
            cartAdapter.notifyDataSetChanged();
        }

        if(fragmentManager.findFragmentById(R.id.fragCartLayout).isVisible())
        {
            for(int i =fragmentManager.getBackStackEntryCount(); i >2 ; i--){
                fragmentManager.popBackStack();
            }

        }

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
                if(isLoggedIn){
                    subTotal=0;
                    for(Item i:cart){
                        subTotal+= Double.parseDouble(i.getPrice())*i.getQuantity();
                    }
                    tvSubtotal.setText("Subtotal: $"+String.valueOf(subTotal));
                    fragmentManager.beginTransaction().hide(fragUploadLayout).commit();
                    fragmentManager.beginTransaction().hide(fragShopLayout).show(fragCartLayout)
                            .addToBackStack(null)
                            .commit();


                }
                else Toast.makeText(getApplicationContext(),"Not logged in, cannot access cart.",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.upload:
                if(isLoggedIn){
                    if(isAdmin) {
                        fragmentManager.beginTransaction().hide(fragCartLayout).commit();
                        fragmentManager.beginTransaction().hide(fragShopLayout).show(fragUploadLayout).addToBackStack(null)
                                .commit();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Must be admin to upload!",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                else Toast.makeText(getApplicationContext(),"Not logged in, cannot upload item.",
                        Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class LoginInBackground extends AsyncTask<String, Integer, Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... userData) {
            Log.i("shopData", "user: " + userData[0] + " password: " + userData[1]);

            String urlString = "https://install-gentoo.herokuapp.com/users/login";

            try
            {
                String urlParameters  = "username=" + userData[0] +  "&password=" + userData[1];
                byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                DataOutputStream writer = new DataOutputStream(urlConnection.getOutputStream());
                writer.write(postData);
                urlConnection.connect();

                writer.flush();
                writer.close();
//                out.close();

                String response = "";
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    String line;

                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

//                    while ((line = responseReader.readLine()) != null)
//                    {
//                        response += line;
//                    }
//                    isAdmin = response.indexOf("admin") > 0;
                    // JsonParser.parseString(response).

                    JsonObject jsonObj = new JsonParser().parse(responseReader).getAsJsonObject();

                    if (jsonObj.get("user").getAsJsonObject().get("role").toString().equals("\"admin\""))
                    {
                       isAdmin = true;
                    }
                    else
                    {
                       isAdmin = false;
                    }

                    Log.i("shopLog", "isAdmin: " + jsonObj.get("user").getAsJsonObject().get("role").toString());

                    jwtToken = jsonObj.get("token").getAsString();
                    Log.i("shopLog", "jwtToken: " + jwtToken);

                    Log.i("shopLog", response);
                    return true;
                }
                else
                {
                    response = "ERR";

                    Log.i("shopLog", response);
                    return false;
                }
            }
            catch (Exception e)
            {
                Log.i("shopLog", "Error:" + e);
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean loginStatus) {
            super.onPostExecute(loginStatus);

            // login success
            if (loginStatus)
            {
                // switch to shop fragment
                new DownloadInBackground().execute();

                new CartSyncInBackground().execute();

            }
            else // login failure
            {
                Toast.makeText(getApplicationContext(),"Login failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class RegisterInBackground extends AsyncTask<String, Integer, Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... userData) {
            Log.i("shopData", "user: " + userData[0] + " password: " + userData[1] + "email: " + userData[2]);

            String urlString = "https://install-gentoo.herokuapp.com/users/signup";

            try
            {
                String urlParameters  = "username=" + userData[0] +  "&password=" + userData[1] + "&email=" + userData[2];
                byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                DataOutputStream writer = new DataOutputStream(urlConnection.getOutputStream());
                writer.write(postData);
                urlConnection.connect();

                writer.flush();
                writer.close();
//                out.close();

                String response = "";
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    String line;
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    while ((line = responseReader.readLine()) != null)
                    {
                        response += line;
                    }

                    Log.i("shopLog", response);
                    return true;
                }
                else
                {
                    response = "ERR";

                    Log.i("shopLog", response);
                    return false;
                }
            }
            catch (Exception e)
            {
                Log.i("shopLog", "Error:" + e);
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean registerStatus) {
            super.onPostExecute(registerStatus);

            // login success
            if (registerStatus)
            {
                // switch to shop fragment
                fragmentManager.beginTransaction()
                        .hide(fragRegisterLayout)
                        .show(fragLoginLayout)
                        .addToBackStack(null)
                        .commit();
            }
            else // login failure
            {
                Toast.makeText(getApplicationContext(),"Registration failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class UploadInBackground extends AsyncTask<String, Integer, Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... itemData) {
            Log.i("shopData", "item name: " + itemData[0] + " price: " + itemData[1] + "description: " + itemData[2]);

            String urlString = "https://install-gentoo.herokuapp.com/items";

            try
            {
                String urlParameters  = "itemname=" + itemData[0] +  "&price=" + itemData[1] + "&description=" + itemData[2];
                byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("authorization", "Bearer " + itemData[3]);

                Log.i("shopLog",  "Bearer " + itemData[3]);

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);


                DataOutputStream writer = new DataOutputStream(urlConnection.getOutputStream());
                writer.write(postData);
                urlConnection.connect();

                writer.flush();
                writer.close();
//                out.close();

                String response = "";
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    String line;
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    while ((line = responseReader.readLine()) != null)
                    {
                        response += line;
                    }

                    Log.i("shopLog", response);
                    return true;
                }
                else
                {
                    response = "ERR";

                    Log.i("shopLog", response);
                    return false;
                }
            }
            catch (Exception e)
            {
                Log.i("shopLog", "Error:" + e);
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean uploadStatus) {
            super.onPostExecute(uploadStatus);

            // login success
            if (uploadStatus)
            {
                // switch to shop fragment
                items.add( new Item( etUploadName.getText().toString(), etUploadPrice.getText().toString(), etUploadDescription.getText().toString() ) );
                itemsAdapter.notifyDataSetChanged();

                fragmentManager.beginTransaction()
                        .hide(fragUploadLayout)
                        .show(fragShopLayout)
                        .addToBackStack(null)
                        .commit();
            }
            else // login failure
            {
                Toast.makeText(getApplicationContext(),"Upload failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class DownloadInBackground extends AsyncTask<String, Integer, Boolean>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String...userNames) {

            String urlString = "https://install-gentoo.herokuapp.com/items";

            try
            {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("authorization", "Bearer " + jwtToken);

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                JsonObject jsonObj = new JsonParser().parse(reader).getAsJsonObject();

                JsonArray itemsJsonArray = jsonObj.get("items").getAsJsonArray();

                for (int index = 0; index < itemsJsonArray.size(); index++)
                {
                    JsonElement jsonElement = itemsJsonArray.get(index);
                    JsonObject itemJsonObject = jsonElement.getAsJsonObject();

                    items.add( new Item( itemJsonObject.get("itemname").getAsString(), itemJsonObject.get("price").getAsString(), itemJsonObject.get("description").getAsString() ) );
                    itemsAdapter.notifyDataSetChanged();
                }

            }
            catch (Exception e)
            {
                Log.i("shopLog", "Exception:" + e);
                return false;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean shopStatus) {
            super.onPostExecute(shopStatus);

            if (shopStatus)
            {
                itemsAdapter.notifyDataSetChanged();

                isLoggedIn=true;
                fragmentManager.beginTransaction()
                        .hide(fragLoginLayout)
                        .hide(fragCartLayout)
                        .hide(fragUploadLayout)
                        .show(fragShopLayout)
                        .addToBackStack(null).commit();
            }
            else
            {
                isLoggedIn=false;
                Toast.makeText(getApplicationContext(),"Shop download failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class CartSyncInBackground extends AsyncTask<String,Integer,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... username) {

            String urlString = "https://install-gentoo.herokuapp.com/users/cart-items";

            try
            {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("authorization","Bearer " + jwtToken);

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                JsonObject jsonObj = new JsonParser().parse(reader).getAsJsonObject();
                JsonObject jsonCartObj = jsonObj.get("cart").getAsJsonObject();
                
                Set<String> keys = jsonCartObj.keySet();

                for (String key: keys)
                {
                    JsonObject jsonCartItemObj = jsonCartObj.get(key).getAsJsonObject();

                    Item i = new Item( jsonCartItemObj.get("itemname").getAsString(),
                    jsonCartItemObj.get("price").getAsString(),
                    jsonCartItemObj.get("description").getAsString());
                    
                    i.setQuantity(jsonCartItemObj.get("quantity").getAsInt());

                    cart.add(i);

                    cartAdapter.notifyDataSetChanged();
                }
            }

            catch(Exception e){
                Log.i("shopLog","Exception: " + e.getMessage());
                return false;
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean hasSynced) {
            if(hasSynced){
                Log.i("shopLog","Cart synced successfully!");
            }

            else{
                Toast.makeText(getApplicationContext(),"Cart is Empty or Failed to sync cart.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class CartAddOnlineInBackground extends AsyncTask<Item, Integer, Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Item...cart) {

            String urlString = "https://install-gentoo.herokuapp.com/users/cart-items";

            try
            {
                String urlParameters  = "itemname=" + cart[0].getName() +  "&price=" + cart[0].getPrice() + 
                                        "&description=" + cart[0].getDescription() + "&quantity=" + cart[0].getQuantity();
                byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("authorization", "Bearer " + jwtToken);

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);


                DataOutputStream writer = new DataOutputStream(urlConnection.getOutputStream());
                writer.write(postData);
                urlConnection.connect();

                writer.flush();
                writer.close();
//                out.close();

                String response = "";
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    String line;
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    while ((line = responseReader.readLine()) != null)
                    {
                        response += line;
                    }

                    Log.i("shopLog", response);
                    return true;
                }
                else
                {
                    response = "ERR";

                    Log.i("shopLog", response);
                    return false;
                }
            }
            catch (Exception e)
            {
                Log.i("shopLog", "Error:" + e);
                return false;
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean cartUploadStatus) {
            super.onPostExecute(cartUploadStatus);

            if (cartUploadStatus)
            {
                Toast.makeText(getApplicationContext(),"cart item saved",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"cart item upload failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class CheckoutCartInBackground extends AsyncTask<String,Integer,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... nothing) {

            String urlString = "https://install-gentoo.herokuapp.com/users/cart-items/checkout"; //REPLACE W CART DB URL

            try{
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("authorization", "Bearer " + jwtToken);

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    Log.i("shopLog", "OK");
                }
                else
                {
                    Log.i("shopLog", "ERR");
                    return false;
                }
            }

            catch(Exception e){
                Log.i("shopLog","Exception: " + e.getMessage());
                return false;
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean hasSynced) {
            if(hasSynced)
            {
                Log.i("shopLog","Checked out successfully!");
                cart.clear();
                subTotal=0.00;
                tvSubtotal.setText("Subtotal: " + String.format("$%.2f",subTotal));
                cartAdapter.notifyDataSetChanged();
            }

            else
            {
                Toast.makeText(getApplicationContext(),"Failed to checkout.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}