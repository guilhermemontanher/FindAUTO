package br.edu.fatecourinhos.findauto.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.edu.fatecourinhos.findauto.MainActivity;
import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.activities.ActivityCadastro;
import br.edu.fatecourinhos.findauto.activities.ActivityMeusAnuncios;
import br.edu.fatecourinhos.findauto.activities.ActivityVisualizarAnuncio;
import br.edu.fatecourinhos.findauto.activities.ListaEstados;
import br.edu.fatecourinhos.findauto.activities.LoginActivity;
import br.edu.fatecourinhos.findauto.adapters.AdapterMinhaContaOpcoes;
import br.edu.fatecourinhos.findauto.app.EndPoints;
import br.edu.fatecourinhos.findauto.app.MyApplication;
import br.edu.fatecourinhos.findauto.helper.MyPreferenceManager;
import br.edu.fatecourinhos.findauto.model.Cidade;
import br.edu.fatecourinhos.findauto.model.OpcoesMinhaConta;
import br.edu.fatecourinhos.findauto.model.User;
import br.edu.fatecourinhos.findauto.utils.Casting;
import br.edu.fatecourinhos.findauto.utils.Utils;

public class FragmentMinhaConta extends Fragment {

    private static int REQUEST_UPDATE_USER = 9367;

    private AdapterMinhaContaOpcoes mAdapter;

    ArrayList<OpcoesMinhaConta> mListOpcoes;
    private TextView mTvNome;
    private TextView mTvCidade;
    private ImageView mIvPerfil;
    private FloatingActionButton mFabAlterar;

    public static FragmentMinhaConta newInstance() {

        FragmentMinhaConta fragmentMinhaConta = new FragmentMinhaConta();
        return fragmentMinhaConta;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_minha_conta, container, false);

        ListView listOpcoes = (ListView) view.findViewById(R.id.list_opcoes_minha_conta);
        mTvNome = (TextView) view.findViewById(R.id.text_view_nome_minha_conta);
        mTvCidade = (TextView) view.findViewById(R.id.text_view_cidade_minha_conta);
        mIvPerfil = (ImageView) view.findViewById(R.id.foto_perfil_minha_conta);

        MyPreferenceManager myPreferenceManager = new MyPreferenceManager(getActivity());
        User user = myPreferenceManager.getUser();
        myPreferenceManager.storeUser(user);
        mTvNome.setText(user.getName()+" "+user.getLastname());
        mTvCidade.setText(user.getCidadeName());

        try {
            String foto = user.getPhotoBase64();
            if (foto != null){
                mIvPerfil.setImageBitmap(Casting.StringToBitmap(foto));
            } else {
                Glide.with(getActivity())
                        .load(EndPoints.BASE_URL + "/" + user.getPhotoUrl())
                        .error(R.drawable.default_user)
                        //.bitmapTransform(new RoundedCornersTransformation(context, 10, 0))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(false)
                        .into(mIvPerfil);
            }
        } catch (Exception e){
            e.printStackTrace();
            Glide.with(getActivity())
                    .load(EndPoints.BASE_URL + "/" + user.getPhotoUrl())
                    .error(R.drawable.default_user)
                    //.bitmapTransform(new RoundedCornersTransformation(context, 10, 0))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(false)
                    .into(mIvPerfil);
        }

        mListOpcoes = new ArrayList<>();

        OpcoesMinhaConta omc = new OpcoesMinhaConta();
        omc.setNome("Meus anuncios");
        omc.setQuantidade(0);
        mListOpcoes.add(omc);

        omc = new OpcoesMinhaConta();
        omc.setNome("Anuncios favoritos");
        omc.setQuantidade(0);
        mListOpcoes.add(omc);

        mAdapter = new AdapterMinhaContaOpcoes(mListOpcoes, getActivity());
        listOpcoes.setAdapter(mAdapter);

        listOpcoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ActivityMeusAnuncios.class);
                intent.putExtra("tipo", position);
                startActivity(intent);
            }
        });

        loadCountAnunciosEFavoritos();

        view.findViewById(R.id.button_loggout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyPreferenceManager(getActivity()).clearPreferences();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadCountAnunciosEFavoritos(){
        Log.d("user_id",new MyPreferenceManager(getActivity()).getUser().getId());
        //if (Utils.isOnline(getActivity())) {
            String url = EndPoints.BASE_URL+"/getCountAnunciosEFavoritos.php";

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonResp = new JSONObject(response);

                        Log.d("Response JSON", jsonResp.toString());
                        try {
                            if(jsonResp.has("anuncios"))
                                mListOpcoes.get(0).setQuantidade(Integer.valueOf(jsonResp.getString("anuncios")));

                            if(jsonResp.has("favoritos"))
                                mListOpcoes.get(1).setQuantidade(Integer.valueOf(jsonResp.getString("favoritos")));

                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error Volley",error.toString());
                }
            }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("user_id", new MyPreferenceManager(getActivity()).getUser().getId());

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(sr);
//        } else {
//            Toast.makeText(getActivity(), "Sem internet.", Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_minha_conta, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_minha_conta_editar:
                Intent i = new Intent(getActivity(), ActivityCadastro.class);
                i.putExtra("operation", "update");
                startActivityForResult(i, REQUEST_UPDATE_USER);
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_UPDATE_USER){
                MyPreferenceManager myPreferenceManager = new MyPreferenceManager(getActivity());
                User user = myPreferenceManager.getUser();
                mTvNome.setText(user.getName()+" "+user.getLastname());
                mTvCidade.setText(user.getCidadeName());
                mIvPerfil.setImageBitmap(Casting.StringToBitmap(user.getPhotoBase64()));
            }
        }
    }
}
