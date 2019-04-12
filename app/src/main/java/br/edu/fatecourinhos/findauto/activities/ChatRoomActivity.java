package br.edu.fatecourinhos.findauto.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.app.EndPoints;
import br.edu.fatecourinhos.findauto.app.MyApplication;
import br.edu.fatecourinhos.findauto.gcm.GCMRegistrationIntentService;
import br.edu.fatecourinhos.findauto.helper.Message;
import br.edu.fatecourinhos.findauto.helper.MyPreferenceManager;
import br.edu.fatecourinhos.findauto.helper.ThreadAdapter;

public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener {

    //Broadcast receiver to receive broadcasts
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    //Progress dialog
    private ProgressDialog dialog;

    //Recyclerview objects
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    //ArrayList of messages to store the thread messages
    private ArrayList<Message> messages;

    //Button to send new message on the thread
    private ImageButton buttonSend;

    //EditText to send new message on the thread
    private EditText editTextMessage;

    private int idUSerTo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        idUSerTo = getIntent().getIntExtra("id_user",0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvNome = (TextView) findViewById(R.id.cabecalhoChatTextViewNome);
        ImageView ivPerfil = (ImageView) findViewById(R.id.cabecalhoChatImageView);
        tvNome.setText(getIntent().getStringExtra("nome"));

        Glide.with(this)
                .load(EndPoints.BASE_URL + "/" + getIntent().getStringExtra("foto"))
                .error(R.drawable.default_user)
                //.bitmapTransform(new RoundedCornersTransformation(context, 10, 0))
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .into(ivPerfil);

        //Displaying dialog while the chat room is being ready
        dialog = new ProgressDialog(this);
        dialog.setMessage("Aguarde...");
        dialog.show();

        //Initializing recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Initializing message arraylist
        messages = new ArrayList<>();

        //Calling function to fetch the existing messages on the thread
        fetchMessages();

        //initializing button and edittext
        buttonSend = (ImageButton) findViewById(R.id.buttonSend);
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);

        //Adding listener to button
        buttonSend.setOnClickListener(this);

        //Creating broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {

                    //When gcm registration is success do something here if you need

                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_TOKEN_SENT)) {

                    //When the registration token is sent to ther server displaying a toast
                    //Toast.makeText(getApplicationContext(), "Chatroom Ready...", Toast.LENGTH_SHORT).show();

                    //When we received a notification when the app is in foreground
                } else if (intent.getAction().equals(MyPreferenceManager.PUSH_NOTIFICATION)) {
                    //Getting message data
                    String name = intent.getStringExtra("name");
                    String message = intent.getStringExtra("message");
                    String id = intent.getStringExtra("id");

                    //processing the message to add it in current thread
                    processMessage(name, message, id);
                }
            }
        };

        //if the google play service is not in the device app won't work
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }
        } else {
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }
    }

    //This method will fetch all the messages of the thread
    private void fetchMessages() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_FETCH_MESSAGES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();

                        try {
                            JSONObject res = new JSONObject(response);
                            JSONArray thread = res.getJSONArray("messages");
                            for (int i = 0; i < thread.length(); i++) {
                                JSONObject obj = thread.getJSONObject(i);
                                int userId = obj.getInt("userid");
                                String message = obj.getString("message");
                                String name = "";//obj.getString("name");
                                String sentAt = obj.getString("sentat");
                                Message messagObject = new Message(userId, message, sentAt, name);
                                messages.add(messagObject);
                            }

                            adapter = new ThreadAdapter(ChatRoomActivity.this, messages, Integer.parseInt(new MyPreferenceManager(ChatRoomActivity.this).getUser().getId()));
                            recyclerView.setAdapter(adapter);
                            scrollToBottom();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", new MyPreferenceManager(ChatRoomActivity.this).getUser().getId());
                params.put("id_to", String.valueOf(idUSerTo));
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }

    //Processing message to add on the thread
    private void processMessage(String name, String message, String id) {
        Message m = new Message(Integer.parseInt(id), message, getTimeStamp(), name);
        messages.add(m);
        scrollToBottom();
    }

    //This method will send the new message to the thread
    private void sendMessage() {
        final String message = editTextMessage.getText().toString().trim();
        if (message.equalsIgnoreCase(""))
            return;
        final int userId = Integer.parseInt(new MyPreferenceManager(ChatRoomActivity.this).getUser().getId());
        String name = new MyPreferenceManager(ChatRoomActivity.this).getUser().getName();
        String sentAt = getTimeStamp();

        Message m = new Message(userId, message, sentAt, name);
        messages.add(m);
        adapter.notifyDataSetChanged();

        scrollToBottom();

        editTextMessage.setText("");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_MESSAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", new MyPreferenceManager(ChatRoomActivity.this).getUser().getId());
                params.put("message", message);
                params.put("name", new MyPreferenceManager(ChatRoomActivity.this).getUser().getName());
                params.put("id_to", String.valueOf(idUSerTo));
                return params;
            }
        };

        //Disabling retry to prevent duplicate messages
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }

    //method to scroll the recyclerview to bottom
    private void scrollToBottom() {
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 1)
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
    }

    //This method will return current timestamp
    public static String getTimeStamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    //Registering broadcast receivers
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_TOKEN_SENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(MyPreferenceManager.PUSH_NOTIFICATION));
    }


    //Unregistering receivers
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }


    //Sending message onclick
    @Override
    public void onClick(View v) {
        if (v == buttonSend)
            sendMessage();
    }

    //Creating option menu to add logout feature
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Adding our menu to toolbar
        //getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //Adding logout option here
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}