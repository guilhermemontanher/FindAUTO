package br.edu.fatecourinhos.findauto.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.edu.fatecourinhos.findauto.MainActivity;
import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.activities.ActivityCadastro;
import br.edu.fatecourinhos.findauto.activities.ActivityModeloVeiculo;
import br.edu.fatecourinhos.findauto.activities.ListaEstados;
import br.edu.fatecourinhos.findauto.app.EndPoints;
import br.edu.fatecourinhos.findauto.helper.MyPreferenceManager;
import br.edu.fatecourinhos.findauto.model.User;
import br.edu.fatecourinhos.findauto.utils.BitmapSizeHelper;
import br.edu.fatecourinhos.findauto.utils.Casting;
import br.edu.fatecourinhos.findauto.utils.NumberTextWatcher;
import br.edu.fatecourinhos.findauto.utils.Utils;


/**
 * Created by Chiptronic on 19/10/2016.
 */
public class FragmentNovoAnuncio extends Fragment implements View.OnClickListener {

    private static int REQUEST_MODELO = 1390;

    private EditText mEtTitulo;
    private EditText mEtDescricao;
    private EditText mEtVeiculo;
    private EditText mEtPreco;
    private EditText mEtQuilometragem;

    private TextInputLayout mInputTitulo;
    private TextInputLayout mInputDescricao;
    private TextInputLayout mInputVeiculo;
    private TextInputLayout mInputPreco;
    private TextInputLayout mInputQuilometragem;

    private ImageView mIvFoto1;
    private ImageView mIvFoto2;
    private ImageView mIvFoto3;
    private ImageView mIvFoto4;
    private ImageView mIvFoto5;
    private ImageView mIvFoto6;

    private Button mButtonFinalizar;

    private int selectedType = -1;
    private boolean refFipe = false;
    private String selectedMontadora;
    private String selectedModelo;
    private String selectedAno;
    private String precoFipe = "";

    private static final int REQUEST_PERMISSION_IMAGE_CAPTURE = 351;
    private static final int REQUEST_LOAD_IMAGE = 98;
    private static final int REQUEST_IMAGE_CAPTURE = 99;
    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 521;
    private Uri imageUri;
    Bitmap bm;

    private ImageView selectedImage;

    private int qtdFoto = 0;
    private boolean useImage1,useImage2,useImage3,useImage4,useImage5,useImage6;
    private ArrayList<String> listFotoBase64 = new ArrayList<>();

    public static FragmentNovoAnuncio newInstance() {
        Bundle args = new Bundle();

        FragmentNovoAnuncio fragmentNovoAnuncio = new FragmentNovoAnuncio();
        fragmentNovoAnuncio.setArguments(args);
        return fragmentNovoAnuncio;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_novo_anuncio, container, false);

        mEtTitulo = (EditText) view.findViewById(R.id.editTextTitulo);
        mEtDescricao = (EditText) view.findViewById(R.id.editTextDescricao);
        mEtQuilometragem = (EditText) view.findViewById(R.id.editTextQuilometragem);

        mEtVeiculo = (EditText) view.findViewById(R.id.editTextVeiculo);
        mEtVeiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), ActivityModeloVeiculo.class),REQUEST_MODELO);
            }
        });
        mEtPreco = (EditText) view.findViewById(R.id.editTextPreco);
        mEtPreco.addTextChangedListener(new NumberTextWatcher(mEtPreco, "#.##"));

        mInputTitulo = (TextInputLayout) view.findViewById(R.id.inputTitulo);
        mInputDescricao = (TextInputLayout) view.findViewById(R.id.inputDescricao);
        mInputVeiculo = (TextInputLayout) view.findViewById(R.id.inputVeiculo);
        mInputPreco = (TextInputLayout) view.findViewById(R.id.inputPreco);
        mInputQuilometragem = (TextInputLayout) view.findViewById(R.id.inputQuilometragem);

        mIvFoto1 = (ImageView) view.findViewById(R.id.imageAnuncio1);
        mIvFoto2 = (ImageView) view.findViewById(R.id.imageAnuncio2);
        mIvFoto3 = (ImageView) view.findViewById(R.id.imageAnuncio3);
        mIvFoto4 = (ImageView) view.findViewById(R.id.imageAnuncio4);
        mIvFoto5 = (ImageView) view.findViewById(R.id.imageAnuncio5);
        mIvFoto6 = (ImageView) view.findViewById(R.id.imageAnuncio6);

        mIvFoto1.setOnClickListener(this);
        mIvFoto2.setOnClickListener(this);
        mIvFoto3.setOnClickListener(this);
        mIvFoto4.setOnClickListener(this);
        mIvFoto5.setOnClickListener(this);
        mIvFoto6.setOnClickListener(this);

        mButtonFinalizar = (Button) view.findViewById(R.id.buttonSalvarAnuncio);
        mButtonFinalizar.setOnClickListener(clickButtonFinalizar());

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageAnuncio1:
                selectedImage = mIvFoto1;
                break;
            case R.id.imageAnuncio2:
                selectedImage = mIvFoto2;
                break;
            case R.id.imageAnuncio3:
                selectedImage = mIvFoto3;
                break;
            case R.id.imageAnuncio4:
                selectedImage = mIvFoto4;
                break;
            case R.id.imageAnuncio5:
                selectedImage = mIvFoto5;
                break;
            case R.id.imageAnuncio6:
                selectedImage = mIvFoto6;
                break;
        }
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.getMenuInflater()
                .inflate(R.menu.menu_selecionar_foto_1, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_tirar_foto:
                        requestPermission();
                        break;
                    case R.id.menu_escolher:
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQUEST_LOAD_IMAGE);
                        break;
                }

                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_novo_anuncio, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_novo_anuncio_limpar:
                mEtTitulo.setText("");
                mEtDescricao.setText("");
                mEtVeiculo.setText("");
                mEtPreco.setText("");
                mEtQuilometragem.setText("");

                mIvFoto1.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_camera_black_36dp,null));
                mIvFoto2.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_camera_black_36dp,null));
                mIvFoto3.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_camera_black_36dp,null));
                mIvFoto4.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_camera_black_36dp,null));
                mIvFoto5.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_camera_black_36dp,null));
                mIvFoto6.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_camera_black_36dp,null));
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_MODELO){
                selectedType = data.getIntExtra("tipo",-1);
                refFipe = data.getBooleanExtra("refFipe",false);
                selectedMontadora = data.getStringExtra("montadora");
                selectedModelo = data.getStringExtra("modelo");
                selectedAno = data.getStringExtra("ano");
                if (refFipe){
                    precoFipe = data.getStringExtra("preco");
                }
                if (selectedMontadora != null)
                    mEtVeiculo.setText(selectedMontadora+" - "+selectedModelo+"("+selectedAno+")");
            }else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                try {
                    Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                            getActivity().getContentResolver(), imageUri);
                    bm = BitmapSizeHelper.transformBitmap(thumbnail, 400, BitmapSizeHelper.Type.NORMAL);
                    selectedImage.setImageBitmap(bm);
                    switch (selectedImage.getId()){
                        case R.id.imageAnuncio1:
                            useImage1 = true;
                            break;
                        case R.id.imageAnuncio2:
                            useImage2 = true;
                            break;
                        case R.id.imageAnuncio3:
                            useImage3 = true;
                            break;
                        case R.id.imageAnuncio4:
                            useImage4 = true;
                            break;
                        case R.id.imageAnuncio5:
                            useImage5 = true;
                            break;
                        case R.id.imageAnuncio6:
                            useImage6 = true;
                            break;
                    }

                    if (!thumbnail.isRecycled())
                        thumbnail.recycle();

                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_LOAD_IMAGE) {
                try {
                    Uri selectedUri = data.getData();
                    imageUri = data.getData();

                    Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                            getActivity().getContentResolver(), selectedUri);
                    bm = BitmapSizeHelper.transformBitmap(thumbnail, 400, BitmapSizeHelper.Type.NORMAL);
                    selectedImage.setImageBitmap(bm);

                    switch (selectedImage.getId()){
                        case R.id.imageAnuncio1:
                            useImage1 = true;
                            break;
                        case R.id.imageAnuncio2:
                            useImage2 = true;
                            break;
                        case R.id.imageAnuncio3:
                            useImage3 = true;
                            break;
                        case R.id.imageAnuncio4:
                            useImage4 = true;
                            break;
                        case R.id.imageAnuncio5:
                            useImage5 = true;
                            break;
                        case R.id.imageAnuncio6:
                            useImage6 = true;
                            break;
                    }

                    assert thumbnail != null;
                    if (!thumbnail.isRecycled())
                        thumbnail.recycle();
                    System.gc();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_IMAGE_CAPTURE);
            return;
        } else
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSION_IMAGE_CAPTURE);
            return;
        }else
            startCamera();
    }

    public void startCamera() {
        ContentValues mvalues = new ContentValues();
        Date newDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy_hhmmss");
        String nome = format.format(newDate);
        mvalues.put(MediaStore.Images.Media.TITLE, "IMG_" + nome);
        mvalues.put(MediaStore.Images.Media.DESCRIPTION, "Moto Connect");
        imageUri = getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mvalues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    public void publicarAnuncio() throws JSONException {
        mButtonFinalizar.setOnClickListener(null);
        if (!validaFields()) {
            mButtonFinalizar.setText("Publicar");
            mButtonFinalizar.setOnClickListener(clickButtonFinalizar());
            return;
        }
        if (Utils.isOnline(getActivity())) {

            RequestQueue queue = Volley.newRequestQueue(getActivity());

            StringRequest sr = new StringRequest(Request.Method.POST, EndPoints.NEW_ADVERTISEMNT, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("Response", response);

                        JSONObject jsonResp = new JSONObject(response);
                        if (jsonResp.has("success")) {
                            if (jsonResp.getInt("success") == 1) {

                                JSONArray arrayFotos = jsonResp.getJSONArray("fotos");
                                for (int i = 0; i < arrayFotos.length(); i++){
                                    Log.i("foto", arrayFotos.get(i).toString());
                                    uploadFoto(arrayFotos.get(i).toString(), listFotoBase64.get(i));
                                }
                            } else {
                                Toast.makeText(getActivity(), "Falha durante a publicação", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                        mButtonFinalizar.setText("Publicar");
                        mButtonFinalizar.setOnClickListener(clickButtonFinalizar());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error Volley",error.toString());
                    Toast.makeText(getActivity(), "Falha durante a publicação", Toast.LENGTH_SHORT).show();
                    mButtonFinalizar.setText("Publicar");
                    mButtonFinalizar.setOnClickListener(clickButtonFinalizar());
                }
            }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();

                    MyPreferenceManager myPreferenceManager = new MyPreferenceManager(getActivity());
                    params.put("user_id",myPreferenceManager.getUser().getId());
                    params.put("tipo", String.valueOf(selectedType));
                    params.put("preco",mEtPreco.getText().toString());
                    params.put("referencia_fipe",(refFipe)? "1":"0");
                    params.put("preco_fipe", precoFipe);
                    params.put("montadora", selectedMontadora);
                    params.put("modelo", selectedModelo);
                    params.put("ano", selectedAno);
                    params.put("titulo", mEtTitulo.getText().toString());
                    params.put("descricao",mEtDescricao.getText().toString());
                    params.put("quilometragem",mEtQuilometragem.getText().toString());
                    qtdFoto = getCountImage();
                    params.put("qtd_foto", String.valueOf(qtdFoto));
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
            mButtonFinalizar.setText("Publicar");
            mButtonFinalizar.setOnClickListener(clickButtonFinalizar());
        }
    }

    private View.OnClickListener clickButtonFinalizar(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonFinalizar.setText("Publicando...");
                try {
                    publicarAnuncio();
                }catch (JSONException exception){
                    mButtonFinalizar.setText("Publicar");
                }
            }
        };
    }

    private boolean validaFields(){
        boolean result = true;
        if (mEtTitulo.getText().toString().equals("")) {
            mInputTitulo.setError("Preencha o titulo");
            result = false;
        } else {
            mInputTitulo.setErrorEnabled(false);
        }
        if (mEtDescricao.getText().toString().equals("")) {
            mInputDescricao.setError("Preencha a descrição");
            result = false;
        } else {
            mInputDescricao.setErrorEnabled(false);
        }
        if (mEtVeiculo.getText().toString().equals("")) {
            mInputVeiculo.setError("Selecione o veículo");
            result = false;
        } else {
            mInputVeiculo.setErrorEnabled(false);
        }
        if (mEtPreco.getText().toString().equals("")) {
            mInputPreco.setError("Preencha o preço");
            result = false;
        } else {
            mInputPreco.setErrorEnabled(false);
        }
        if (mEtQuilometragem.getText().toString().equals("")) {
            mInputQuilometragem.setError("Preencha a quilometragem");
            result = false;
        } else {
            mInputQuilometragem.setErrorEnabled(false);
        }
        return result;
    }

    private int getCountImage(){
        int count = 0;
        if(useImage1) {
            listFotoBase64.add(convertBase64(mIvFoto1));
            count++;
        }
        if(useImage2) {
            listFotoBase64.add(convertBase64(mIvFoto2));
            count++;
        }
        if(useImage3) {
            listFotoBase64.add(convertBase64(mIvFoto3));
            count++;
        }
        if(useImage4) {
            listFotoBase64.add(convertBase64(mIvFoto4));
            count++;
        }
        if(useImage5) {
            listFotoBase64.add(convertBase64(mIvFoto5));
            count++;
        }
        if(useImage6) {
            listFotoBase64.add(convertBase64(mIvFoto6));
            count++;
        }
        return count;
    }

    private String convertBase64(ImageView imageView){
        Bitmap mBitmap = BitmapSizeHelper.transformBitmap(((BitmapDrawable)imageView.getDrawable()).getBitmap(),400,BitmapSizeHelper.Type.NORMAL);
        return Casting.BitmapToString(mBitmap);
    }

    private void uploadFoto(final String fileName, final String base64){
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest sr = new StringRequest(Request.Method.POST, EndPoints.UPLOAD_FOTO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("Response", response);

                    JSONObject jsonResp = new JSONObject(response);
                    if (jsonResp.has("success")) {
                        if (jsonResp.getInt("success") == 1) {
                            Log.i("FindAUTO", "Foto "+fileName+" salva");

                            qtdFoto--;
                            if (qtdFoto == 0){
                                mButtonFinalizar.setText("Publicar");
                                Toast.makeText(getActivity(), "Anúncio publicado com sucesso =)", Toast.LENGTH_SHORT).show();

                                mEtTitulo.setText("");
                                mEtDescricao.setText("");
                                mEtVeiculo.setText("");
                                mEtPreco.setText("");
                                mEtQuilometragem.setText("");

                                mIvFoto1.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_camera_black_36dp,null));
                                mIvFoto2.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_camera_black_36dp,null));
                                mIvFoto3.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_camera_black_36dp,null));
                                mIvFoto4.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_camera_black_36dp,null));
                                mIvFoto5.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_camera_black_36dp,null));
                                mIvFoto6.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo_camera_black_36dp,null));
                            }
                        } else {
                            Toast.makeText(getActivity(), "Falha durante o upload da imagem.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error Volley",error.toString());
                Toast.makeText(getActivity(), "Falha durante o upaloda da imagem", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("base64", base64);
                params.put("file_name", fileName);
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
    }
}
