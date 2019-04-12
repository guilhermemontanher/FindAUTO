package br.edu.fatecourinhos.findauto.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.util.HashMap;
import java.util.Map;

import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.app.EndPoints;
import br.edu.fatecourinhos.findauto.app.MyApplication;
import br.edu.fatecourinhos.findauto.helper.MyPreferenceManager;


/**
 * Created by Belal on 4/15/2016.
 */


public class GCMRegistrationIntentService extends IntentService {
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";
    public static final String REGISTRATION_TOKEN_SENT = "RegistrationTokenSent";

    public GCMRegistrationIntentService() {
        super("");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        registerGCM();
    }

    private void registerGCM() {
        Intent registrationComplete = null;
        String token = null;
        try {
            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//            token = instanceID.getToken(getString(R.string.app_name), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.w("GCMRegIntentService", "token:" + token);

            sendRegistrationTokenToServer(token);
            registrationComplete = new Intent(REGISTRATION_SUCCESS);
            registrationComplete.putExtra("token", token);
        } catch (Exception e) {
            Log.w("GCMRegIntentService", "Registration error");
            registrationComplete = new Intent(REGISTRATION_ERROR);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationTokenToServer(final String token) {
        //Getting the user id from shared preferences
        //We are storing gcm token for the user in our mysql database
        final int id = Integer.valueOf(new MyPreferenceManager(getApplicationContext()).getUser().getId());
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, EndPoints.URL_STORE_TOKEN + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Intent registrationComplete = new Intent(REGISTRATION_TOKEN_SENT);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registrationComplete);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                return params;
            }
        };
        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }
}