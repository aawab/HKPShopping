package com.example.shoppingapp;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Helper class to provide validationUtility to MainActivity and help readability slightly
 */

public class ValidUtils {

    protected static Boolean isValidUsername(String inputUsername)
    {
        if ( inputUsername.isEmpty() || (inputUsername == null) || (inputUsername.length() < 1) )
        {
            return false;
        }

        return true;
    }

    protected static Boolean isValidPassword(String inputPassword)
    {
        if ( inputPassword.isEmpty() || (inputPassword == null) || (inputPassword.length() < 1) )
        {
            return false;
        }

        return true;
    }

    protected static Boolean isValidEmail(String inputEmail)
    {
        return (!TextUtils.isEmpty(inputEmail) && Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches());
    }
}
