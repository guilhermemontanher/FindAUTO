package br.edu.fatecourinhos.findauto.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import br.edu.fatecourinhos.findauto.model.User;

public class MyPreferenceManager {

    private String TAG = MyPreferenceManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "findauto";

    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_LASTNAME = "user_lastname";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_PHOTOURL = "user_photourl";
    private static final String KEY_USER_PHOTOBASE64 = "user_photobase64";
    private static final String KEY_USER_CIDADENOME = "user_cidadenome";
    private static final String KEY_USER_CIDADEID = "user_cidadeid";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String IS_LOGGED_IN= "is_logged_in";

    public static final String PUSH_NOTIFICATION = "pushnotification";
    public static final int NOTIFICATION_ID = 235;

    // Constructor
    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void clearPreferences(){
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        pref.edit().remove(KEY_USER_ID).commit();
        pref.edit().remove(KEY_USER_NAME).commit();
        pref.edit().remove(KEY_USER_LASTNAME).commit();
        pref.edit().remove(KEY_USER_EMAIL).commit();
        pref.edit().remove(KEY_USER_PHONE).commit();
        pref.edit().remove(KEY_USER_PHOTOURL).commit();
        pref.edit().remove(KEY_USER_PHOTOBASE64).commit();
        pref.edit().remove(KEY_USER_CIDADENOME).commit();
        pref.edit().remove(KEY_USER_CIDADEID).commit();
        pref.edit().remove(KEY_NOTIFICATIONS).commit();
        pref.edit().remove(PUSH_NOTIFICATION).commit();

    }


    public void storeUser(User user) {
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_LASTNAME, user.getLastname());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PHONE, user.getPhone());
        editor.putString(KEY_USER_PHOTOURL, user.getPhotoUrl());
        editor.putString(KEY_USER_PHOTOBASE64, user.getPhotoBase64());
        editor.putString(KEY_USER_CIDADENOME, user.getCidadeName());
        editor.putString(KEY_USER_CIDADEID, user.getCidadeId());

        editor.commit();

        Log.e(TAG, "User is stored in shared preferences. " + user.getName() + ", " + user.getEmail());
    }

    public User getUser() {
        if (pref.getString(KEY_USER_ID, null) != null) {
            String id, name, lastname, phone, photoUrl, photoBase64, cidadeName, cidadeId, email;
            id = pref.getString(KEY_USER_ID, null);
            name = pref.getString(KEY_USER_NAME, null);
            lastname = pref.getString(KEY_USER_LASTNAME, null);
            phone = pref.getString(KEY_USER_PHONE, null);
            photoUrl = pref.getString(KEY_USER_PHOTOURL, null);
            photoBase64 = pref.getString(KEY_USER_PHOTOBASE64, null);
            cidadeName = pref.getString(KEY_USER_CIDADENOME, null);
            cidadeId = pref.getString(KEY_USER_CIDADEID, null);
            email = pref.getString(KEY_USER_EMAIL, null);

            User user = new User(id, name, lastname, phone, photoUrl, photoBase64, cidadeName, cidadeId, email);
            return user;
        }
        return null;
    }

    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void setEnableNotifications(Boolean notification){
        editor.putBoolean(PUSH_NOTIFICATION, notification);
        editor.commit();
    }

    public Boolean getEnableNotifications(){
        return pref.getBoolean(PUSH_NOTIFICATION, true);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}
