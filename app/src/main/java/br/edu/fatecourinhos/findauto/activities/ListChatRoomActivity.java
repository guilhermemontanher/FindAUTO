package br.edu.fatecourinhos.findauto.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.app.EndPoints;
import br.edu.fatecourinhos.findauto.app.MyApplication;
import br.edu.fatecourinhos.findauto.helper.MyPreferenceManager;

public class ListChatRoomActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listUser = new ArrayList<>();
    private ArrayList<Integer> listUserId = new ArrayList<>();

    //Progress dialog
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat_room);

        mListView = (ListView) findViewById(R.id.listViewChatRooms);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ListChatRoomActivity.this, ChatRoomActivity.class);
                intent.putExtra("id_user",listUserId.get(i));
                startActivity(intent);

            }
        });
        fetchChatRooms();
    }

    private void fetchChatRooms() {
        //Displaying dialog while the chat room is being ready
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading chat rooms...");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_GET_CHAT_ROOMS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();

                        try {
                            JSONObject res = new JSONObject(response);
                            JSONArray thread = res.getJSONArray("users");
                            for (int i = 0; i < thread.length(); i++) {
                                JSONObject obj = thread.getJSONObject(i);
                                int userId = obj.has("id")? obj.getInt("id"): 0;
                                String nome = obj.getString("name");
                                String email = obj.getString("email");
                                listUser.add(nome);
                                listUserId.add(userId);
                            }

                            adapter = new ArrayAdapter<String>(ListChatRoomActivity.this,
                                    android.R.layout.simple_list_item_1, android.R.id.text1, listUser);
                            mListView.setAdapter(adapter);
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
                params.put("id_user", new MyPreferenceManager(ListChatRoomActivity.this).getUser().getId());
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }
}
