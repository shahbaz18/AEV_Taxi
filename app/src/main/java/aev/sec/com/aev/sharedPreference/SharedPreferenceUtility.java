package aev.sec.com.aev.sharedPreference;

import android.content.Context;
import android.content.SharedPreferences;

import aev.sec.com.aev.R;


public class SharedPreferenceUtility
{
    private final SharedPreferences mSharedPreferences;
    private static final String USER_NAME = "USER_NAME";
    private static final String USER_EMAIL = "USER_EMAIL";
    
    
  


    public String getUserEmail() {

        return mSharedPreferences.getString(USER_EMAIL, "");
    }

    public void setUserEmail(String UserEmail) {

        mSharedPreferences.edit()
                .putString(USER_EMAIL, UserEmail)
                .apply();
    }

    public SharedPreferenceUtility(Context context) {
        mSharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_file),
                Context.MODE_PRIVATE);
    }
    

    public String getUserName() {

        return mSharedPreferences.getString(USER_NAME, null);
    }
    public void setUserName(String UserName) {

        mSharedPreferences.edit()
                .putString(USER_NAME, UserName)
                .apply();
    }



}
