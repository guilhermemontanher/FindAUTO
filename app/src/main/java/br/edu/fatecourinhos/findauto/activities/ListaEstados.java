package br.edu.fatecourinhos.findauto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.adapters.AdapterCidades;
import br.edu.fatecourinhos.findauto.adapters.AdapterEstados;
import br.edu.fatecourinhos.findauto.app.EndPoints;
import br.edu.fatecourinhos.findauto.app.MyApplication;
import br.edu.fatecourinhos.findauto.model.Cidade;
import br.edu.fatecourinhos.findauto.model.Estado;
import br.edu.fatecourinhos.findauto.utils.Utils;

public class ListaEstados extends AppCompatActivity {

    ListView listEstados;
    ListView listCidades;
    ProgressBar progress;
    ArrayList<Estado> estados = new ArrayList<>();
    ArrayList<Cidade> cidades = new ArrayList<>();
    AdapterEstados mAdapterEstados;
    AdapterCidades mAdapterCidades;
    Button buttonEstados;
    Button buttonCidades;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_estados);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listEstados = (ListView) findViewById(R.id.listEstados);
        listCidades = (ListView) findViewById(R.id.listCidades);
        progress = (ProgressBar) findViewById(R.id.lista_estados_progress);
        buttonEstados = (Button) findViewById(R.id.lista_estado_button_estado);
        buttonCidades = (Button) findViewById(R.id.lista_estado_button_cidade);

        mAdapterEstados = new AdapterEstados(estados, this);
        mAdapterCidades = new AdapterCidades(cidades, this);

        listEstados.setAdapter(mAdapterEstados);
        listCidades.setAdapter(mAdapterCidades);

        listEstados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listEstados.setVisibility(View.GONE);
                listCidades.setVisibility(View.VISIBLE);
                listarCidades(estados.get(position).getId());
            }
        });

        listCidades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                i.putExtra("cidade_id",cidades.get(position).getId());
                i.putExtra("cidade_nome",cidades.get(position).getNome());

                setResult(RESULT_OK,i);
                finish();
            }
        });
        buttonEstados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listEstados.setVisibility(View.VISIBLE);
                listCidades.setVisibility(View.GONE);
                buttonCidades.setEnabled(false);
                estados.clear();
                cidades.clear();
                listarEstados();
            }
        });

        listarEstados();
    }

    private void listarEstados() {

        if (Utils.isOnline(this)) {
            progress.setVisibility(View.VISIBLE);
            String url = EndPoints.BASE_URL+"/getEstados.php";
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            progress.setVisibility(View.GONE);
                            Log.d("Response JSON", response.toString());

                            try {
                                JSONArray jsonArray = response.getJSONArray("estados");

                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject objEstado = jsonArray.getJSONObject(i);

                                    Estado estado = new Estado();
                                    if(objEstado.has("id"))
                                        estado.setId(objEstado.getString("id"));

                                    if(objEstado.has("uf"))
                                        estado.setUf(objEstado.getString("uf"));

                                    if(objEstado.has("nome"))
                                        estado.setNome(objEstado.getString("nome"));

                                    estados.add(estado);
                                }
                                mAdapterEstados.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Response JSON", "Error: " + error.getMessage());
                    progress.setVisibility(View.GONE);
                    Toast.makeText(ListaEstados.this, "Falha na comunicação com o servidor", Toast.LENGTH_LONG).show();
                }
            });
            MyApplication.getInstance().addToRequestQueue(jsonObjReq, "json_obj_req");
        } else {
            Toast.makeText(ListaEstados.this, "Sem internet.", Toast.LENGTH_LONG).show();
        }
    }

    private void listarCidades(String idEstado) {

        if (Utils.isOnline(this)) {
            progress.setVisibility(View.VISIBLE);
            String url = EndPoints.BASE_URL+"/getCidades.php?id_estado="+idEstado;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            progress.setVisibility(View.GONE);
                            Log.d("Response JSON", response.toString());

                            try {
                                JSONArray jsonArray = response.getJSONArray("cidades");

                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject objEstado = jsonArray.getJSONObject(i);

                                    Cidade cidade = new Cidade();
                                    if(objEstado.has("id"))
                                        cidade.setId(objEstado.getString("id"));

                                    if(objEstado.has("nome"))
                                        cidade.setNome(objEstado.getString("nome"));

                                    cidades.add(cidade);
                                }
                                mAdapterCidades.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Response JSON", "Error: " + error.getMessage());
                    progress.setVisibility(View.GONE);
                    Toast.makeText(ListaEstados.this, "Falha na comunicação com o servidor", Toast.LENGTH_LONG).show();
                }
            });
            MyApplication.getInstance().addToRequestQueue(jsonObjReq, "json_obj_req");
        } else {
            Toast.makeText(ListaEstados.this, "Sem internet.", Toast.LENGTH_LONG).show();
        }
    }

}
