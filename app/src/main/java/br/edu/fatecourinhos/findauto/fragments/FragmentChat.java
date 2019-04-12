package br.edu.fatecourinhos.findauto.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import br.edu.fatecourinhos.findauto.activities.ChatRoomActivity;
import br.edu.fatecourinhos.findauto.activities.ListChatRoomActivity;
import br.edu.fatecourinhos.findauto.adapters.AdapterUsers;
import br.edu.fatecourinhos.findauto.app.EndPoints;
import br.edu.fatecourinhos.findauto.app.MyApplication;
import br.edu.fatecourinhos.findauto.helper.MyPreferenceManager;
import br.edu.fatecourinhos.findauto.model.User;

/**
 * Created by Chiptronic on 19/10/2016.
 */
public class FragmentChat extends Fragment {

    private ListView mListView;
    private TextView mTextViewListaVazia;
    private AdapterUsers adapter;
    private ArrayList<User> listUser = new ArrayList<>();

    //Progress dialog
//    private ProgressDialog dialog;

    public static FragmentChat newInstance() {
        Bundle args = new Bundle();

        FragmentChat fragmentChat = new FragmentChat();
        fragmentChat.setArguments(args);
        return fragmentChat;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_chat, container, false);

        mTextViewListaVazia = (TextView) view.findViewById(R.id.fragmentChatTextViewListaVazia);

        mListView = (ListView) view.findViewById(R.id.listViewChatRooms);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                intent.putExtra("id_user",Integer.parseInt(listUser.get(i).getId()));
                intent.putExtra("nome", listUser.get(i).getName());
                intent.putExtra("foto", listUser.get(i).getPhotoUrl());
                startActivity(intent);

            }
        });
        fetchChatRooms();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    private void fetchChatRooms() {
        //Displaying dialog while the chat room is being ready
//        dialog = new ProgressDialog(getActivity());
//        dialog.setMessage("Loading chat rooms...");
//        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_GET_CHAT_ROOMS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        dialog.dismiss();

                        try {
                            JSONObject res = new JSONObject(response);
                            JSONArray thread = res.getJSONArray("users");
                            for (int i = 0; i < thread.length(); i++) {
                                JSONObject obj = thread.getJSONObject(i);
                                User user = new User();
                                user.setId(String.valueOf(obj.has("id")? obj.getInt("id"): 0));
                                user.setName(obj.getString("name"));
                                user.setEmail(obj.getString("email"));
                                user.setPhotoUrl(obj.getString("photo"));
                                listUser.add(user);
                            }

                            if (listUser.size() > 0)
                                mTextViewListaVazia.setVisibility(View.INVISIBLE);
                            else
                                mTextViewListaVazia.setVisibility(View.VISIBLE);

                            adapter = new AdapterUsers(listUser, getActivity());
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
                params.put("id_user", new MyPreferenceManager(getActivity()).getUser().getId());
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }
}
