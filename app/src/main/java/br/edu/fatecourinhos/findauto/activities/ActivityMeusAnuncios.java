package br.edu.fatecourinhos.findauto.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.adapters.AnuncioAdapter;
import br.edu.fatecourinhos.findauto.app.EndPoints;
import br.edu.fatecourinhos.findauto.helper.MyPreferenceManager;
import br.edu.fatecourinhos.findauto.model.Anuncio;
import br.edu.fatecourinhos.findauto.model.User;
import br.edu.fatecourinhos.findauto.utils.Utils;

public class ActivityMeusAnuncios extends AppCompatActivity {

    private TextView mTextViewSemAnuncios;
    private ArrayList<Anuncio> mListAnuncios = new ArrayList<>();
    private AnuncioAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextViewSemAnuncios = (TextView) findViewById(R.id.listAnuncioTextViewVazio);

        mAdapter = new AnuncioAdapter(mListAnuncios, this);
        ListView listView = (ListView) findViewById(R.id.meusAnunciosListView);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ActivityMeusAnuncios.this, ActivityVisualizarAnuncio.class);
                intent.putExtra("idAnuncio", mListAnuncios.get(i).getId());
                startActivity(intent);
            }
        });

        int tipo = getIntent().getIntExtra("tipo",0);
        switch (tipo){
            case 0:
                getSupportActionBar().setTitle("Meus Anúncios");
                listarMeusAnuncios();
                break;
            case 1:
                getSupportActionBar().setTitle("Meus Favoritos");
                listarMeusAnunciosFavoritos();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void listarMeusAnuncios(){
        if (Utils.isOnline(this)) {

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest sr = new StringRequest(Request.Method.POST, EndPoints.MEUS_ANUNCIOS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Response", response);

                    try {
                        JSONObject jsonResp = new JSONObject(response);
                        JSONArray anuncios = jsonResp.getJSONArray("anuncios");

                        mListAnuncios.clear();
                        for(int i = 0; i < anuncios.length(); i++){
                            JSONObject jsonAnuncio = anuncios.getJSONObject(i);
                            Anuncio anuncio = new Anuncio();
                            anuncio.setId(Integer.valueOf(jsonAnuncio.getString("id")));
                            anuncio.setUserId(Integer.valueOf(jsonAnuncio.getString("user_id")));

                            try {
                                anuncio.setData(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(jsonAnuncio.getString("data")));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            anuncio.setTipo(Integer.valueOf(jsonAnuncio.getString("tipo")));
                            anuncio.setPreco(Float.valueOf(jsonAnuncio.getString("preco")));
                            anuncio.setReferencia(Integer.valueOf(jsonAnuncio.getString("referencia_fipe")));
                            anuncio.setPrecoFIPE(jsonAnuncio.getString("preco_fipe"));
                            anuncio.setMontadora(jsonAnuncio.getString("montadora"));
                            anuncio.setModelo(jsonAnuncio.getString("modelo"));
                            anuncio.setAno(jsonAnuncio.getString("ano"));
                            anuncio.setQuilometragem(Integer.valueOf(jsonAnuncio.getString("quilometragem")));
                            anuncio.setTitulo(jsonAnuncio.getString("titulo"));
                            anuncio.setDescricao(jsonAnuncio.getString("descricao"));
                            anuncio.setStatus(Integer.valueOf(jsonAnuncio.getString("status")));
                            anuncio.setFotoPrincipal(jsonAnuncio.getString("foto"));

                            mListAnuncios.add(anuncio);
                        }
                        if (mListAnuncios.size() > 0) {
                            mTextViewSemAnuncios.setVisibility(View.INVISIBLE);
                        } else {
                            mTextViewSemAnuncios.setVisibility(View.VISIBLE);
                        }
                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
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
                    params.put("user_id",new MyPreferenceManager(ActivityMeusAnuncios.this).getUser().getId());
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
        } else {
            Toast.makeText(this, "Sem conexão com a internet.", Toast.LENGTH_SHORT).show();
        }
    }

    private void listarMeusAnunciosFavoritos(){
        if (Utils.isOnline(this)) {

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest sr = new StringRequest(Request.Method.POST, EndPoints.MEUS_ANUNCIOS_FAVORITOS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Response", response);

                    try {
                        JSONObject jsonResp = new JSONObject(response);
                        JSONArray anuncios = jsonResp.getJSONArray("anuncios");

                        mListAnuncios.clear();
                        for(int i = 0; i < anuncios.length(); i++){
                            JSONObject jsonAnuncio = anuncios.getJSONObject(i);
                            Anuncio anuncio = new Anuncio();
                            anuncio.setId(Integer.valueOf(jsonAnuncio.getString("id")));
                            anuncio.setUserId(Integer.valueOf(jsonAnuncio.getString("user_id")));

                            try {
                                anuncio.setData(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(jsonAnuncio.getString("data")));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            anuncio.setTipo(Integer.valueOf(jsonAnuncio.getString("tipo")));
                            anuncio.setPreco(Float.valueOf(jsonAnuncio.getString("preco")));
                            anuncio.setReferencia(Integer.valueOf(jsonAnuncio.getString("referencia_fipe")));
                            anuncio.setPrecoFIPE(jsonAnuncio.getString("preco_fipe"));
                            anuncio.setMontadora(jsonAnuncio.getString("montadora"));
                            anuncio.setModelo(jsonAnuncio.getString("modelo"));
                            anuncio.setAno(jsonAnuncio.getString("ano"));
                            anuncio.setQuilometragem(Integer.valueOf(jsonAnuncio.getString("quilometragem")));
                            anuncio.setTitulo(jsonAnuncio.getString("titulo"));
                            anuncio.setDescricao(jsonAnuncio.getString("descricao"));
                            anuncio.setStatus(Integer.valueOf(jsonAnuncio.getString("status")));
                            anuncio.setFotoPrincipal(jsonAnuncio.getString("foto"));

                            mListAnuncios.add(anuncio);
                        }
                        if (mListAnuncios.size() > 0) {
                            mTextViewSemAnuncios.setVisibility(View.INVISIBLE);
                        } else {
                            mTextViewSemAnuncios.setVisibility(View.VISIBLE);
                        }
                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
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
                    params.put("user_id",new MyPreferenceManager(ActivityMeusAnuncios.this).getUser().getId());
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
        } else {
            Toast.makeText(this, "Sem conexão com a internet.", Toast.LENGTH_SHORT).show();
        }
    }
}
