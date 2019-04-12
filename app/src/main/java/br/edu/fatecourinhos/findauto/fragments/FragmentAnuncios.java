package br.edu.fatecourinhos.findauto.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import br.edu.fatecourinhos.findauto.activities.ActivityVisualizarAnuncio;
import br.edu.fatecourinhos.findauto.adapters.AdapterCidades;
import br.edu.fatecourinhos.findauto.adapters.AnuncioAdapter;
import br.edu.fatecourinhos.findauto.app.EndPoints;
import br.edu.fatecourinhos.findauto.helper.MyPreferenceManager;
import br.edu.fatecourinhos.findauto.model.Anuncio;
import br.edu.fatecourinhos.findauto.utils.Utils;

/**
 * Created by Chiptronic on 19/10/2016.
 */
public class FragmentAnuncios extends Fragment {

    private TextView mTextViewSemAnuncios;
    private ArrayList<Anuncio> mListAnuncios = new ArrayList<>();
    private AnuncioAdapter mAdapter;

    private EditText mEtFiltro;
    private ImageButton mButtonBuscar;

    public static FragmentAnuncios newInstance() {
        Bundle args = new Bundle();
//        args.putString(STARTING_TEXT, "okok");

        FragmentAnuncios fragmentAnuncios = new FragmentAnuncios();
        fragmentAnuncios.setArguments(args);
        return fragmentAnuncios;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_anuncios, container, false);

        mTextViewSemAnuncios = (TextView) view.findViewById(R.id.listAnuncioTextViewVazio);

        mAdapter = new AnuncioAdapter(mListAnuncios, getActivity());
        ListView listView = (ListView) view.findViewById(R.id.listAnuncioListView);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ActivityVisualizarAnuncio.class);
                intent.putExtra("idAnuncio", mListAnuncios.get(i).getId());
                startActivity(intent);
            }
        });

        mEtFiltro = (EditText) view.findViewById(R.id.editTextFiltro);
        mButtonBuscar = (ImageButton) view.findViewById(R.id.imageButtonBuscar);
        mButtonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> params = new HashMap<String, String>();
                params.put("filtro",mEtFiltro.getText().toString());
                listarAnuncios(params);
            }
        });

        listarAnuncios(new HashMap<String, String>());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    
    private void listarAnuncios(final Map<String,String> params){
        //if (Utils.isOnline(getActivity())) {

            RequestQueue queue = Volley.newRequestQueue(getActivity());

            StringRequest sr = new StringRequest(Request.Method.POST, EndPoints.LIST_ANUNCIOS, new Response.Listener<String>() {
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
//            Toast.makeText(getActivity(), "Sem conexão com a internet.", Toast.LENGTH_SHORT).show();
//        }
    }
}
