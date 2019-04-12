package br.edu.fatecourinhos.findauto.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.edu.fatecourinhos.findauto.MainActivity;
import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.app.EndPoints;
import br.edu.fatecourinhos.findauto.fragments.FragmentFoto;
import br.edu.fatecourinhos.findauto.helper.MyPreferenceManager;
import br.edu.fatecourinhos.findauto.model.Anuncio;
import br.edu.fatecourinhos.findauto.model.Montadora;
import br.edu.fatecourinhos.findauto.model.User;
import br.edu.fatecourinhos.findauto.utils.Utils;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.relex.circleindicator.CircleIndicator;

public class ActivityVisualizarAnuncio extends AppCompatActivity {

    private ViewPager vpPager;
    private TextView mTextViewTitulo;
    private TextView mTextViewDescricao;
    private TextView mTextViewTituloPreco;
    private TextView mTextViewCategoria;
    private TextView mTextViewData;
    private TextView mTextViewMontadora;
    private TextView mTextViewModelo;
    private TextView mTextViewAno;
    private TextView mTextViewQuilometragem;

    private TextView mTextViewPrecoFipe;

    private ImageView mImageViewUsuario;
    private TextView mTextViewNomeUsuario;
    private TextView mTextViewCidiadeUsuario;

    private View viewPrecoFIPE;

    private ArrayList<String> listFotos = new ArrayList<>();
    private FragmentPagerAdapter adapterViewPager;

    private Anuncio anuncio = new Anuncio();
    private User user = new User();

    private Boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_anuncio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        vpPager = (ViewPager) findViewById(R.id.anuncioVisualizarViewPager);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(vpPager);

        mTextViewTitulo = (TextView) findViewById(R.id.anuncioVisualizarTextViewTitulo);
        mTextViewDescricao = (TextView) findViewById(R.id.anuncioVisualizarTextViewDescricao);
        mTextViewTituloPreco = (TextView) findViewById(R.id.anuncioVisualizarTextViewPreco);
        mTextViewCategoria = (TextView) findViewById(R.id.anuncioVisualizarTextViewCategoria);
        mTextViewData = (TextView) findViewById(R.id.anuncioVisualizarTextViewData);
        mTextViewMontadora = (TextView) findViewById(R.id.anuncioVisualizarTextViewMontadora);
        mTextViewModelo = (TextView) findViewById(R.id.anuncioVisualizarTextViewModelo);
        mTextViewAno = (TextView) findViewById(R.id.anuncioVisualizarTextViewano);
        mTextViewQuilometragem = (TextView) findViewById(R.id.anuncioVisualizarTextViewQuilometragem);
        mTextViewPrecoFipe = (TextView) findViewById(R.id.anuncioVisualizarTextViewPrecoFIPE);
        mImageViewUsuario = (ImageView) findViewById(R.id.anuncioVisualizarImageViewUsuario);
        mTextViewNomeUsuario = (TextView) findViewById(R.id.anuncioVisualizarTextViewNome);
        mTextViewCidiadeUsuario = (TextView) findViewById(R.id.anuncioVisualizarTextViewCidade);

        viewPrecoFIPE = findViewById(R.id.anuncioVisualizarViewPrecoFIPE);

        findViewById(R.id.anuncioVisualizarButtonLigar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(ActivityVisualizarAnuncio.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ActivityVisualizarAnuncio.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            1);
                    return;
                }

                String number = "tel:" + user.getPhone().toString().trim();
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                startActivity(callIntent);
            }
        });

        findViewById(R.id.anuncioVisualizarButtonChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityVisualizarAnuncio.this, ChatRoomActivity.class);
                intent.putExtra("id_user", Integer.valueOf(user.getId()));
                intent.putExtra("nome", user.getName());
                intent.putExtra("foto", user.getPhotoUrl());
                startActivity(intent);
            }
        });


        int idAnuncio = getIntent().getIntExtra("idAnuncio", 0);

        if (idAnuncio > 0)
            carregarAnuncio(idAnuncio);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_visualizar_anuncio, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_anuncio_favorito:
                break;
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void carregarAnuncio(final int idAnuncio) {
        final String mUrl = EndPoints.ANUNCIO_COMPLETO;

        //if (Utils.isOnline(this)) {
//            ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setMessage("Aguarde...");
//            progressDialog.setCancelable(false);
//            progressDialog.show();

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest sr = new StringRequest(Request.Method.POST, mUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //progressDialog.hide();
                    try {
                        Log.i("Response", response);

                        JSONObject jsonResp = new JSONObject(response);
                        anuncio.setId(Integer.valueOf(jsonResp.get("id").toString()));
                        anuncio.setUserId(Integer.valueOf(jsonResp.get("user_id").toString()));
                        anuncio.setData(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(jsonResp.getString("data")));
                        anuncio.setTipo(Integer.valueOf(jsonResp.get("tipo").toString()));
                        anuncio.setPreco(Integer.valueOf(jsonResp.get("preco").toString()));
                        anuncio.setReferencia(Integer.valueOf(jsonResp.get("referencia_fipe").toString()));
                        anuncio.setPrecoFIPE(jsonResp.get("preco_fipe").toString());
                        anuncio.setMontadora(jsonResp.get("montadora").toString());
                        anuncio.setModelo(jsonResp.get("modelo").toString());
                        anuncio.setAno(jsonResp.get("ano").toString());
                        anuncio.setQuilometragem(Integer.valueOf(jsonResp.get("quilometragem").toString()));
                        anuncio.setTitulo(jsonResp.get("titulo").toString());
                        anuncio.setDescricao(jsonResp.get("descricao").toString());
                        anuncio.setStatus(Integer.valueOf(jsonResp.get("status").toString()));

                        user.setId(jsonResp.get("user_id").toString());
                        user.setName(jsonResp.get("name").toString());
                        user.setLastname(jsonResp.get("lastname").toString());
                        user.setPhone(jsonResp.get("phone").toString());
                        user.setPhotoUrl(jsonResp.get("photo").toString());
                        user.setCidadeId(jsonResp.get("cidade_id").toString());
                        user.setCidadeName(jsonResp.get("nome").toString() + "-" + jsonResp.get("uf").toString());

                        mTextViewTitulo.setText(anuncio.getTitulo());
                        mTextViewDescricao.setText(anuncio.getDescricao());
                        mTextViewTituloPreco.setText("R$" + String.format("%.2f", anuncio.getPreco()));
                        switch (anuncio.getTipo()) {
                            case 1:
                                mTextViewCategoria.setText("Motocicletas");
                                break;
                            case 2:
                                mTextViewCategoria.setText("Carros");
                                break;
                            case 3:
                                mTextViewCategoria.setText("Caminhões");
                                break;
                        }
                        mTextViewData.setText(getFormatedDate(anuncio.getData()));
                        mTextViewMontadora.setText(anuncio.getMontadora());
                        mTextViewModelo.setText(anuncio.getModelo());
                        mTextViewAno.setText(anuncio.getAno());
                        mTextViewQuilometragem.setText(String.valueOf(anuncio.getQuilometragem()));
                        mTextViewPrecoFipe.setText(anuncio.getPrecoFIPE());
                        mTextViewNomeUsuario.setText(user.getName() + " " + user.getLastname());
                        mTextViewCidiadeUsuario.setText(user.getCidadeName());

                        if (anuncio.getReferencia() == 1) {
                            viewPrecoFIPE.setVisibility(View.VISIBLE);
                        } else {
                            viewPrecoFIPE.setVisibility(View.INVISIBLE);
                        }

                        Glide.with(ActivityVisualizarAnuncio.this)
                                .load(EndPoints.BASE_URL + "/" + user.getPhotoUrl())
                                .error(R.drawable.default_user)
                                .bitmapTransform(new RoundedCornersTransformation(ActivityVisualizarAnuncio.this, 10, 0))
                                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                                //.skipMemoryCache(true)
                                .into(mImageViewUsuario);

                        JSONArray jsonArrayFotos = jsonResp.getJSONArray("fotos");
                        for (int i = 0; i < jsonArrayFotos.length(); i++) {
                            listFotos.add(jsonArrayFotos.get(i).toString());
                        }
                        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
                        vpPager.setAdapter(adapterViewPager);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error Volley", error.toString());
                   // progressDialog.hide();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("anuncio_id", String.valueOf(idAnuncio));
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(sr);


//        } else {
//            Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
//        }
    }

    private String getFormatedDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);

        String monthStr = "";
        switch (month) {
            case 1:
                monthStr = "Janeiro";
                break;
            case 2:
                monthStr = "Fevereiro";
                break;
            case 3:
                monthStr = "Março";
                break;
            case 4:
                monthStr = "Abril";
                break;
            case 5:
                monthStr = "Maio";
                break;
            case 6:
                monthStr = "Junho";
                break;
            case 7:
                monthStr = "Julho";
                break;
            case 8:
                monthStr = "Agosto";
                break;
            case 9:
                monthStr = "Setembro";
                break;
            case 10:
                monthStr = "Outubro";
                break;
            case 11:
                monthStr = "Novembro";
                break;
            case 12:
                monthStr = "Dezembro";
                break;
        }


        return day + " " + monthStr + " " + hour + ":" + minute;
    }

    private void isFavorite(final int idAnuncio) {
        final String mUrl = EndPoints.IS_FAVORITE;

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.POST, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //progressDialog.hide();
                try {
                    Log.i("Response", response);

                    JSONObject jsonResp = new JSONObject(response);
                    if(jsonResp.getBoolean("isFavorite")){

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error Volley", error.toString());
                // progressDialog.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("anuncio_id", String.valueOf(idAnuncio));
                params.put("user_id", new MyPreferenceManager(ActivityVisualizarAnuncio.this).getUser().getId());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    String number = "tel:" + user.getPhone().toString().trim();
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ActivityVisualizarAnuncio.this, "Permissão para ligação bloqueada", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return listFotos.size();
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            return FragmentFoto.newInstance(listFotos.get(position));
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

    }
}
