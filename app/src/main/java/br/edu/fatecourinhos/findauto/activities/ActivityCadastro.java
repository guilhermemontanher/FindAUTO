package br.edu.fatecourinhos.findauto.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.edu.fatecourinhos.findauto.MainActivity;
import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.app.EndPoints;
import br.edu.fatecourinhos.findauto.helper.MyPreferenceManager;
import br.edu.fatecourinhos.findauto.model.User;
import br.edu.fatecourinhos.findauto.utils.BitmapSizeHelper;
import br.edu.fatecourinhos.findauto.utils.Casting;
import br.edu.fatecourinhos.findauto.utils.Utils;

public class ActivityCadastro extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progress;

    private ImageView mIvPerfil;
    private EditText mEtNome;
    private EditText mEtSobrenome;
    private EditText mEtTelefone;
    private EditText mEtCidade;
    private EditText mEtEmail;
    private EditText mEtSenha;

    private TextInputLayout mInputNome;
    private TextInputLayout mInputSobrenome;
    private TextInputLayout mInputTelefone;
    private TextInputLayout mInputCidade;
    private TextInputLayout mInputEmail;
    private TextInputLayout mInputSenha;

    private FloatingActionButton fabPhoto;

    private String cidadeId = "";
    private String userId = "";
    private final int REQUEST_CIDADE = 5096;

    private static final int REQUEST_PERMISSION_IMAGE_CAPTURE = 351;
    private static final int REQUEST_LOAD_IMAGE = 98;
    private static final int REQUEST_IMAGE_CAPTURE = 99;
    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 521;
    private Uri imageUri;
    Bitmap bm;

    private boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent().getStringExtra("operation") != null)
            isUpdate = true;

        mIvPerfil = (ImageView) findViewById(R.id.foto_perfil_minha_conta);
        mEtNome = (EditText) findViewById(R.id.editTextNome);
        mEtSobrenome = (EditText) findViewById(R.id.editTextSobrenome);
        mEtTelefone = (EditText) findViewById(R.id.editTextTelefone);
        mEtCidade = (EditText) findViewById(R.id.editTextCidade);
        mEtEmail = (EditText) findViewById(R.id.editTextEmail);
        mEtSenha = (EditText) findViewById(R.id.editTextSenha);

        mInputNome = (TextInputLayout) findViewById(R.id.inputNome);
        mInputSobrenome = (TextInputLayout) findViewById(R.id.inputSobrenome);
        mInputTelefone = (TextInputLayout) findViewById(R.id.inputTelefone);
        mInputCidade = (TextInputLayout) findViewById(R.id.inputCidade);
        mInputEmail = (TextInputLayout) findViewById(R.id.inputEmail);
        mInputSenha = (TextInputLayout) findViewById(R.id.inputSenha);

        progress = (ProgressBar) findViewById(R.id.progressBarCadastro);

        findViewById(R.id.fab_select_photo).setOnClickListener(this);
        mIvPerfil.setOnClickListener(this);

        mEtCidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ActivityCadastro.this, ListaEstados.class),REQUEST_CIDADE);
            }
        });

        if(isUpdate){
            getSupportActionBar().setTitle("Alterar cadastro");

            MyPreferenceManager myPreferenceManager = new MyPreferenceManager(this);
            User user = myPreferenceManager.getUser();
            mEtNome.setText(user.getName());
            mEtSobrenome.setText(user.getLastname());
            mEtTelefone.setText(user.getPhone());
            mEtCidade.setText(user.getCidadeName());
            mEtEmail.setText(user.getEmail());
            cidadeId = user.getCidadeId();
            userId = user.getId();

            try {
                String foto = user.getPhotoBase64();
                if (foto != null){
                    mIvPerfil.setImageBitmap(Casting.StringToBitmap(foto));
                } else {
                    Glide.with(this)
                            .load(EndPoints.BASE_URL + "/" + user.getPhotoUrl())
                            .error(R.drawable.default_user)
                            //.bitmapTransform(new RoundedCornersTransformation(context, 10, 0))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(false)
                            .into(mIvPerfil);
                }
            } catch (Exception e){
                e.printStackTrace();
                Glide.with(this)
                        .load(EndPoints.BASE_URL + "/" + user.getPhotoUrl())
                        .error(R.drawable.default_user)
                        //.bitmapTransform(new RoundedCornersTransformation(context, 10, 0))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(false)
                        .into(mIvPerfil);
            }
        }
    }

    private boolean validaFields(){
        boolean result = true;
        if (mEtNome.getText().toString().equals("")) {
            mInputNome.setError("Preencha o nome");
            result = false;
        } else {
            mInputNome.setErrorEnabled(false);
        }
        if (mEtSobrenome.getText().toString().equals("")) {
            mInputSobrenome.setError("Preencha o sobrenome");
            result = false;
        } else {
            mInputSobrenome.setErrorEnabled(false);
        }
        if (mEtTelefone.getText().toString().equals("")) {
            mInputTelefone.setError("Preencha o telefone");
            result = false;
        } else {
            mInputTelefone.setErrorEnabled(false);
        }
        if (mEtCidade.getText().toString().equals("")) {
            mInputCidade.setError("Selecione a cidade");
            result = false;
        } else {
            mInputCidade.setErrorEnabled(false);
        }
        if (mEtEmail.getText().toString().equals("")) {
            mInputEmail.setError("Preencha o email");
            result = false;
        } else {
            mInputEmail.setErrorEnabled(false);
        }
        if (mEtSenha.getText().toString().equals("")) {
            mInputSenha.setError("Preencha a senha");
            result = false;
        } else {
            mInputSenha.setErrorEnabled(false);
        }
        return result;
    }

    public void cadastrar(final View v) throws JSONException {
        if (!validaFields())
            return;
        if (Utils.isOnline(this)) {
            v.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);

            Bitmap mBitmap = BitmapSizeHelper.transformBitmap(((BitmapDrawable)mIvPerfil.getDrawable()).getBitmap(),100,BitmapSizeHelper.Type.NORMAL);
            final String image = Casting.BitmapToString(mBitmap);
            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest sr = new StringRequest(Request.Method.POST, isUpdate? EndPoints.UPDATE_USER:EndPoints.REGISTER_USER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    v.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    try {
                        Log.d("Response", response);

                        JSONObject jsonResp = new JSONObject(response);
                        if(!isUpdate) {
                            if (jsonResp.has("success")) {
                                if (jsonResp.getInt("success") == 1) {
                                    Toast.makeText(ActivityCadastro.this, "Cadastro efetuado", Toast.LENGTH_SHORT).show();
                                    User user = new User();
                                    user.setId(String.valueOf(jsonResp.getInt("id")));
                                    user.setName(mEtNome.getText().toString());
                                    user.setLastname(mEtSobrenome.getText().toString());
                                    user.setEmail(mEtEmail.getText().toString());
                                    user.setCidadeName(mEtCidade.getText().toString());
                                    user.setPhone(mEtTelefone.getText().toString());
                                    user.setPhotoUrl("imagens/user" + String.valueOf(jsonResp.getInt("id")) + ".png");
                                    user.setCidadeId(cidadeId);
                                    user.setPhotoBase64(image);

                                    new MyPreferenceManager(ActivityCadastro.this).storeUser(user);

                                    startActivity(new Intent(ActivityCadastro.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(ActivityCadastro.this, "Falha no cadastro", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            if (jsonResp.has("success")) {
                                if (jsonResp.getInt("success") == 1) {
                                    User user = new User();
                                    user.setId(userId);
                                    user.setName(mEtNome.getText().toString());
                                    user.setLastname(mEtSobrenome.getText().toString());
                                    user.setEmail(mEtEmail.getText().toString());
                                    user.setCidadeName(mEtCidade.getText().toString());
                                    user.setPhone(mEtTelefone.getText().toString());
                                    user.setPhotoUrl("imagens/user" + userId + ".png");
                                    user.setCidadeId(cidadeId);
                                    user.setPhotoBase64(image);

                                    new MyPreferenceManager(ActivityCadastro.this).storeUser(user);
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }  else {
                                Toast.makeText(ActivityCadastro.this, "Falha no alteração", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ActivityCadastro.this, "Falha durante o cadastro", Toast.LENGTH_SHORT).show();
                    v.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                }
            }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    if (isUpdate)
                        params.put("user_id",userId);
                    params.put("name",mEtNome.getText().toString());
                    params.put("lastname",mEtSobrenome.getText().toString());
                    params.put("email", mEtEmail.getText().toString());
                    params.put("phone", mEtTelefone.getText().toString());
                    params.put("password",mEtSenha.getText().toString());
                    params.put("cidade_id",cidadeId);
                    params.put("photo",image);

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

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_IMAGE_CAPTURE);
            return;
        } else
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
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
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mvalues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if (requestCode == REQUEST_CIDADE){
                cidadeId = data.getStringExtra("cidade_id");
                mEtCidade.setText(data.getStringExtra("cidade_nome"));

            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                try {
                    Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                           getContentResolver(), imageUri);
                    bm = BitmapSizeHelper.transformBitmap(thumbnail, 400, BitmapSizeHelper.Type.NORMAL);
                    mIvPerfil.setImageBitmap(bm);

                    if (!thumbnail.isRecycled())
                        thumbnail.recycle();

                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_LOAD_IMAGE) {
                try {
                    Uri selectedImage = data.getData();
                    imageUri = data.getData();

                    Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), selectedImage);
                    bm = BitmapSizeHelper.transformBitmap(thumbnail, 400, BitmapSizeHelper.Type.NORMAL);
                    mIvPerfil.setImageBitmap(bm);

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

    @Override
    public void onClick(View view) {
        PopupMenu popup = new PopupMenu(ActivityCadastro.this, view);
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
}
