package br.edu.fatecourinhos.findauto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.app.EndPoints;
import br.edu.fatecourinhos.findauto.model.Estado;
import br.edu.fatecourinhos.findauto.model.User;

/**
 * Created by Chiptronic on 31/10/2016.
 */
public class AdapterUsers extends BaseAdapter {

    private ArrayList<User> users;
    private Context context;

    public AdapterUsers(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(users.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = users.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_user, null);


        TextView textViewNome = (TextView) view.findViewById(R.id.adapterUserTextViewNome);
        textViewNome.setText(user.getName());

        ImageView ivFoto = (ImageView) view.findViewById(R.id.adapterUserImageView);

        if (!user.getPhotoUrl().equals("NULL")) {
            Glide.with(context)
                    .load(EndPoints.BASE_URL + "/" + user.getPhotoUrl())
                    .error(R.drawable.default_user)
                    //.bitmapTransform(new RoundedCornersTransformation(context, 10, 0))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(false)
                    .into(ivFoto);
        }

        return view;
    }

    @Override
    public boolean isEnabled (int position) {
        return true;
    }
}
