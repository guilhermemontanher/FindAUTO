package br.edu.fatecourinhos.findauto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.edu.fatecourinhos.findauto.MainActivity;
import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.app.MyApplication;
import br.edu.fatecourinhos.findauto.helper.MyPreferenceManager;
import br.edu.fatecourinhos.findauto.model.User;
import br.edu.fatecourinhos.findauto.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText senha;
    private ProgressBar progressLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.editTextLogin);
        senha = (EditText) findViewById(R.id.editTextSenha);
        progressLogin = (ProgressBar) findViewById(R.id.progressLogin);

    }

    public void login(final View v){
        v.setVisibility(View.GONE);
        progressLogin.setVisibility(View.VISIBLE);

        String url = "http://10.0.2.2:8080/ComercVeiculos/login.php?email="+email.getText().toString()+"&senha="+senha.getText().toString()+"";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        v.setVisibility(View.VISIBLE);
                        progressLogin.setVisibility(View.GONE);
                        Log.d("Response JSON",response.toString());

                        try {
                            JSONArray jsonArray = response.getJSONArray("usuario");

                            if(jsonArray.getJSONObject(0) != null){
                                JSONObject jsonObject = (JSONObject) jsonArray.getJSONObject(0);
                                User user = new User();
                                user.setId(jsonObject.get("id").toString());
                                user.setName(jsonObject.get("name").toString());
                                user.setLastname(jsonObject.get("lastname").toString());
                                user.setPhone(jsonObject.get("phone").toString());
                                user.setPhotoUrl(jsonObject.get("photo").toString());
                                user.setCidadeName(jsonObject.get("nome").toString()+"-"+jsonObject.get("uf").toString());
                                user.setCidadeId(jsonObject.get("cidade_id").toString());
                                user.setEmail(jsonObject.get("email").toString());

                                new MyPreferenceManager(LoginActivity.this).storeUser(user);

                                Toast.makeText(LoginActivity.this, "Bem vindo "+jsonObject.get("name")+" "+jsonObject.get("lastname"), Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Falha na autenticação.", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("JSON REST", "Error: " + error.getMessage());
                v.setVisibility(View.VISIBLE);
                progressLogin.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Falha na comunicação com o servidor", Toast.LENGTH_LONG).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(jsonObjReq, "json_obj_req");
    }

    public void cadastro(View v){
        startActivity(new Intent(LoginActivity.this,ActivityCadastro.class));
    }
}
