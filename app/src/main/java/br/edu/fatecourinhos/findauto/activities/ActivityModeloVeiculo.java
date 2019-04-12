package br.edu.fatecourinhos.findauto.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.model.Ano;
import br.edu.fatecourinhos.findauto.model.Modelo;
import br.edu.fatecourinhos.findauto.model.Montadora;
import br.edu.fatecourinhos.findauto.utils.Utils;

public class ActivityModeloVeiculo extends AppCompatActivity {

    private CheckBox checkBoxFipe;
    private View viewComFipe;
    private View viewSemFipe;

    private Button buttonConfirmar;

    private ImageView imgMoto,imgCarro,imgCaminhao;
    private ProgressBar mProgressMontadora, mProgressModelo,mProgressAno, mProgressPrecoMedio;
    private Spinner mSpinnerMontadora, mSpinnerModelo, mSpinnerAno;
    private TextView mTextViewPrecoMedio;
    private EditText mEtMontadora, mEtModelo, mEtAno;

    private ArrayList<Montadora> montadoras;
    private ArrayList<Modelo> modelos;
    private ArrayList<Ano> anos;
    private List<String> montadorasExib;
    private List<String> modelosExib;
    private List<String> anosExib;

    private int selectedType = -1;
    private boolean refFipe = false;
    private String selectedMontadora;
    private String selectedModelo;
    private String selectedAno;
    private String precoFipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modelo_veiculo);

        viewComFipe = findViewById(R.id.viewComFipe);
        viewSemFipe = findViewById(R.id.viewSemFipe);

        mProgressMontadora = (ProgressBar) findViewById(R.id.progressMontadora);
        mProgressModelo = (ProgressBar) findViewById(R.id.progressModelo);
        mProgressAno = (ProgressBar) findViewById(R.id.progressAno);
        mProgressPrecoMedio = (ProgressBar) findViewById(R.id.progressPrecoMedio);

        mTextViewPrecoMedio = (TextView) findViewById(R.id.textViewPrecoMedio);
        mEtMontadora = (EditText) findViewById(R.id.editTextMontadora);
        mEtModelo = (EditText) findViewById(R.id.editTextModelo);
        mEtAno = (EditText) findViewById(R.id.editTextAno);

        mSpinnerMontadora = (Spinner) findViewById(R.id.spinnerMontadora);
        mSpinnerMontadora.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!montadorasExib.get(i).equals("")) {
                    String selectedMontadora = montadoras.get(i-1).getId();
                    ActivityModeloVeiculo.this.selectedMontadora = montadoras.get(i-1).getName();

                    mSpinnerModelo.setSelection(0);
                    mSpinnerAno.setSelection(0);
                    mTextViewPrecoMedio.setText("");

                    switch (selectedType) {
                        case 1:
                            loadModelos("http://fipeapi.appspot.com/api/1/motos/marcas/veiculos/" + selectedMontadora + ".json");
                            break;
                        case 2:
                            loadModelos("http://fipeapi.appspot.com/api/1/carros/marcas/veiculos/" + selectedMontadora + ".json");
                            break;
                        case 3:
                            loadModelos("http://fipeapi.appspot.com/api/1/caminhoes/marcas/veiculos/" + selectedMontadora + ".json");
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mSpinnerModelo = (Spinner) findViewById(R.id.spinnerModelo);
        mSpinnerModelo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!modelosExib.get(i).equals("")) {
                    String selectedMontadora = montadoras.get(mSpinnerMontadora.getSelectedItemPosition()-1).getId();
                    String selectedModelo = modelos.get(i-1).getId();

                    ActivityModeloVeiculo.this.selectedModelo = modelos.get(i-1).getName();

                    mSpinnerAno.setSelection(0);
                    mTextViewPrecoMedio.setText("");

                    switch (selectedType) {
                        case 1:
                            loadAnos("http://fipeapi.appspot.com/api/1/motos/marcas/veiculo/" + selectedMontadora + "/" + selectedModelo + ".json");
                            break;
                        case 2:
                            loadAnos("http://fipeapi.appspot.com/api/1/carros/marcas/veiculo/" + selectedMontadora + "/" + selectedModelo + ".json");
                            break;
                        case 3:
                            loadAnos("http://fipeapi.appspot.com/api/1/caminhoes/marcas/veiculo/" + selectedMontadora + "/" + selectedModelo + ".json");
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSpinnerAno = (Spinner) findViewById(R.id.spinnerAno);
        mSpinnerAno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!anosExib.get(i).equals("")) {
                    ActivityModeloVeiculo.this.selectedAno = anos.get(i-1).getName();

                    String selectedMontadora = montadoras.get(mSpinnerMontadora.getSelectedItemPosition()-1).getId();
                    String selectedModelo = modelos.get(mSpinnerModelo.getSelectedItemPosition()-1).getId();
                    String selectedAno = anos.get(i-1).getId();

                    switch (selectedType) {
                        case 1:
                            loadPrecoMedio("http://fipeapi.appspot.com/api/1/motos/marcas/veiculo/" + selectedMontadora + "/" + selectedModelo + "/" + selectedAno + ".json");
                            break;
                        case 2:
                            loadPrecoMedio("http://fipeapi.appspot.com/api/1/carros/marcas/veiculo/" + selectedMontadora + "/" + selectedModelo + "/" + selectedAno + ".json");
                            break;
                        case 3:
                            loadPrecoMedio("http://fipeapi.appspot.com/api/1/caminhoes/marcas/veiculo/" + selectedMontadora + "/" + selectedModelo + "/" + selectedAno + ".json");
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        checkBoxFipe = (CheckBox) findViewById(R.id.checkBoxFipe);
        checkBoxFipe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                refFipe = b;
                if(b) {
                    viewComFipe.setVisibility(View.VISIBLE);
                    viewSemFipe.setVisibility(View.GONE);

                    if (montadoras == null) {
                        if (selectedType > -1) {
                            if (modelos != null) {
                                modelos.clear();
                                modelosExib.clear();
                            }
                            if (anos != null) {
                                anos.clear();
                                anosExib.clear();
                            }
                            switch (selectedType) {
                                case 1:
                                    loadMontadoras("http://fipeapi.appspot.com/api/1/motos/marcas.json");
                                    break;
                                case 2:
                                    loadMontadoras("http://fipeapi.appspot.com/api/1/carros/marcas.json");
                                    break;
                                case 3:
                                    loadMontadoras("http://fipeapi.appspot.com/api/1/caminhoes/marcas.json");
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                } else {
                    viewComFipe.setVisibility(View.GONE);
                    viewSemFipe.setVisibility(View.VISIBLE);
                }
            }
        });

        imgMoto = (ImageView) findViewById(R.id.img_tipo_moto);
        imgMoto.setOnClickListener(clickImage());

        imgCarro = (ImageView) findViewById(R.id.img_tipo_carro);
        imgCarro.setOnClickListener(clickImage());

        imgCaminhao = (ImageView) findViewById(R.id.img_tipo_caminhao);
        imgCaminhao.setOnClickListener(clickImage());

        buttonConfirmar = (Button) findViewById(R.id.buttonConfirmarVeiculo);
        buttonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = getIntent();
                if (!refFipe){
                    i.putExtra("montadora",mEtMontadora.getText().toString());
                    i.putExtra("modelo",mEtModelo.getText().toString());
                    i.putExtra("ano",mEtAno.getText().toString());
                } else {
                    i.putExtra("montadora",selectedMontadora);
                    i.putExtra("modelo",selectedModelo);
                    i.putExtra("ano",selectedAno);
                    i.putExtra("preco",precoFipe);
                }
                i.putExtra("refFipe",refFipe);
                i.putExtra("tipo", selectedType);
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    private View.OnClickListener clickImage(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgMoto.setColorFilter(ContextCompat.getColor(ActivityModeloVeiculo.this,R.color.colorPrimary));
                imgCarro.setColorFilter(ContextCompat.getColor(ActivityModeloVeiculo.this,R.color.colorPrimary));
                imgCaminhao.setColorFilter(ContextCompat.getColor(ActivityModeloVeiculo.this,R.color.colorPrimary));

                ImageView img = (ImageView) v;
                img.setColorFilter(ContextCompat.getColor(ActivityModeloVeiculo.this,R.color.colorAccent));

                switch (v.getId()){
                    case R.id.img_tipo_moto:
                        selectedType = 1;
                        if(refFipe)
                            loadMontadoras("http://fipeapi.appspot.com/api/1/motos/marcas.json");
                        break;
                    case R.id.img_tipo_carro:
                        selectedType = 2;
                        if(refFipe)
                            loadMontadoras("http://fipeapi.appspot.com/api/1/carros/marcas.json");
                        break;
                    case R.id.img_tipo_caminhao:
                        selectedType = 3;
                        if(refFipe)
                            loadMontadoras("http://fipeapi.appspot.com/api/1/caminhoes/marcas.json");
                        break;
                    default:
                        selectedType = 1;
                        break;
                }
            }
        };
    }

    private void loadMontadoras(final String url) {
        montadoras = new ArrayList<>();
        montadorasExib = new ArrayList<>();
        montadorasExib.add("");

        if (modelos != null){
            modelosExib.clear();
            modelos.clear();
            mSpinnerModelo.setAdapter(null);
        }

        if (anos != null){
            anosExib.clear();
            anos.clear();
            mSpinnerAno.setAdapter(null);
        }

        final String mUrl = url;

        if(Utils.isOnline(this)) {
            new AsyncTask<Void, Void, String>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mProgressMontadora.setVisibility(View.VISIBLE);
                }

                @Override
                protected String doInBackground(Void... params) {
                    URL url = null;
                    try {
                        url = new URL(mUrl);
                        URLConnection con = url.openConnection();
                        con.setConnectTimeout(15000);
                        con.setReadTimeout(15000);
                        String json = Utils.getStringFromInputStream(con.getInputStream());
                        return json;

                    } catch (SocketTimeoutException ste) {
                        ste.printStackTrace();
                        return "0";

                    } catch (RuntimeException er) {
                        er.printStackTrace();
                        return "-1";

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        return "-1";

                    } catch (IOException e1) {
                        e1.printStackTrace();
                        return "-1";
                    }
                }

                @Override
                protected void onPostExecute(String json) {
                    Log.d("jsonResult", json);
                    try {

                        JSONArray resultJSON = new JSONArray(json);

                        for (int i = 0; i < resultJSON.length() - 1; i++) {
                            JSONObject jsonObject = (JSONObject) resultJSON.get(i);

                            Montadora montadora = new Montadora();
                            montadora.setFipeName(jsonObject.getString("fipe_name"));
                            montadora.setId(jsonObject.getString("id"));
                            montadora.setKey(jsonObject.getString("key"));
                            montadora.setName(jsonObject.getString("name"));
                            montadora.setOrder(jsonObject.getString("order"));
                            Log.d("Montadora", montadora.getFipeName());
                            montadorasExib.add(montadora.getName());
                            montadoras.add(montadora);
                        }

                        ArrayAdapter arrayAdapter = new ArrayAdapter(ActivityModeloVeiculo.this,
                                android.R.layout.simple_spinner_item, montadorasExib);
                        mSpinnerMontadora.setAdapter(arrayAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mProgressMontadora.setVisibility(View.GONE);
                }
            }.execute();
        } else {
            Toast.makeText(ActivityModeloVeiculo.this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadModelos(final String url) {
        modelos = new ArrayList<>();
        modelosExib = new ArrayList<>();
        modelosExib.add("");

        if (anos != null){
            anosExib.clear();
            anos.clear();
            mSpinnerAno.setAdapter(null);
        }

        final String mUrl = url;
        Log.d("URL",url);

        if(Utils.isOnline(this)) {
            new AsyncTask<Void, Void, String>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mProgressModelo.setVisibility(View.VISIBLE);
                }

                @Override
                protected String doInBackground(Void... params) {
                    URL url = null;
                    try {
                        url = new URL(mUrl);
                        URLConnection con = url.openConnection();
                        con.setConnectTimeout(15000);
                        con.setReadTimeout(15000);
                        String json = Utils.getStringFromInputStream(con.getInputStream());
                        return json;

                    } catch (SocketTimeoutException ste) {
                        ste.printStackTrace();
                        return "0";

                    } catch (RuntimeException er) {
                        er.printStackTrace();
                        return "-1";

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        return "-1";

                    } catch (IOException e1) {
                        e1.printStackTrace();
                        return "-1";
                    }
                }

                @Override
                protected void onPostExecute(String json) {
                    Log.d("jsonResult", json);
                    try {

                        JSONArray resultJSON = new JSONArray(json);

                        for (int i = 0; i < resultJSON.length() - 1; i++) {
                            JSONObject jsonObject = (JSONObject) resultJSON.get(i);

                            Modelo modelo = new Modelo();
                            modelo.setKey(jsonObject.getString("key"));
                            modelo.setId(jsonObject.getString("id"));
                            modelo.setFipeName(jsonObject.getString("fipe_name"));
                            modelo.setFipeMarca(jsonObject.getString("fipe_marca"));
                            modelo.setMarca(jsonObject.getString("marca"));
                            modelo.setName(jsonObject.getString("name"));
                            modelosExib.add(modelo.getName());
                            modelos.add(modelo);
                        }

                        ArrayAdapter arrayAdapter = new ArrayAdapter(ActivityModeloVeiculo.this,
                                android.R.layout.simple_spinner_item, modelosExib);
                        mSpinnerModelo.setAdapter(arrayAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mProgressModelo.setVisibility(View.GONE);
                }
            }.execute();
        } else {
            Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAnos(final String url) {
        anos = new ArrayList<>();
        anosExib = new ArrayList<>();
        anosExib.add("");
        final String mUrl = url;
        Log.d("URL",url);

        if(Utils.isOnline(this)) {
            new AsyncTask<Void, Void, String>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mProgressAno.setVisibility(View.VISIBLE);
                }

                @Override
                protected String doInBackground(Void... params) {
                    URL url = null;
                    try {
                        url = new URL(mUrl);
                        URLConnection con = url.openConnection();
                        con.setConnectTimeout(15000);
                        con.setReadTimeout(15000);
                        String json = Utils.getStringFromInputStream(con.getInputStream());
                        return json;

                    } catch (SocketTimeoutException ste) {
                        ste.printStackTrace();
                        return "0";

                    } catch (RuntimeException er) {
                        er.printStackTrace();
                        return "-1";

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        return "-1";

                    } catch (IOException e1) {
                        e1.printStackTrace();
                        return "-1";
                    }
                }

                @Override
                protected void onPostExecute(String json) {
                    Log.d("jsonResult", json);
                    try {

                        JSONArray resultJSON = new JSONArray(json);

                        for (int i = 0; i < resultJSON.length() - 1; i++) {
                            JSONObject jsonObject = (JSONObject) resultJSON.get(i);

                            Ano ano = new Ano();
                            ano.setName(jsonObject.getString("name"));
                            ano.setMarca(jsonObject.getString("marca"));
                            ano.setFipeMarca(jsonObject.getString("fipe_marca"));
                            ano.setFipeCodigo(jsonObject.getString("fipe_codigo"));
                            ano.setId(jsonObject.getString("id"));
                            ano.setKey(jsonObject.getString("key"));
                            ano.setVeiculo(jsonObject.getString("veiculo"));
                            anosExib.add(ano.getName());
                            anos.add(ano);
                        }

                        ArrayAdapter arrayAdapter = new ArrayAdapter(ActivityModeloVeiculo.this,
                                android.R.layout.simple_spinner_item, anosExib);
                        mSpinnerAno.setAdapter(arrayAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mProgressAno.setVisibility(View.GONE);
                }
            }.execute();
        } else {
            Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPrecoMedio(final String url) {
        final String mUrl = url;
        Log.d("URL",url);

        if(Utils.isOnline(this)) {
            new AsyncTask<Void, Void, String>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mProgressPrecoMedio.setVisibility(View.VISIBLE);
                }

                @Override
                protected String doInBackground(Void... params) {
                    URL url = null;
                    try {
                        url = new URL(mUrl);
                        URLConnection con = url.openConnection();
                        con.setConnectTimeout(15000);
                        con.setReadTimeout(15000);
                        String json = Utils.getStringFromInputStream(con.getInputStream());
                        return json;

                    } catch (SocketTimeoutException ste) {
                        ste.printStackTrace();
                        return "0";

                    } catch (RuntimeException er) {
                        er.printStackTrace();
                        return "-1";

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        return "-1";

                    } catch (IOException e1) {
                        e1.printStackTrace();
                        return "-1";
                    }
                }

                @Override
                protected void onPostExecute(String json) {
                    Log.d("jsonResult", json);
                    try {

                        JSONObject resultJSON = new JSONObject(json);
                        mTextViewPrecoMedio.setText(resultJSON.getString("preco"));
                        precoFipe = resultJSON.getString("preco");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mProgressPrecoMedio.setVisibility(View.GONE);
                }
            }.execute();
        } else {
            Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
        }
    }

}
