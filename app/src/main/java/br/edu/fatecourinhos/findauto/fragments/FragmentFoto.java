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
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;

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
import br.edu.fatecourinhos.findauto.adapters.AnuncioAdapter;
import br.edu.fatecourinhos.findauto.app.EndPoints;
import br.edu.fatecourinhos.findauto.model.Anuncio;
import br.edu.fatecourinhos.findauto.utils.Utils;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by Chiptronic on 19/10/2016.
 */
public class FragmentFoto extends Fragment {
    public static FragmentFoto newInstance(String url) {
        Bundle args = new Bundle();
        args.putString("url", url);

        FragmentFoto fragmentFoto = new FragmentFoto();
        fragmentFoto.setArguments(args);
        return fragmentFoto;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_foto, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.fragmentFotoImageView);

        Log.i("foto",EndPoints.BASE_URL + "/" + getArguments().getString("url"));
        Glide.with(getActivity())
                .load(EndPoints.BASE_URL + "/" + getArguments().getString("url"))
                //.error(R.drawable.default_user)
                //.bitmapTransform(new RoundedCornersTransformation(ActivityVisualizarAnuncio.this, 10, 0))
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .into(imageView);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
