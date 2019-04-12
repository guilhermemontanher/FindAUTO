package br.edu.fatecourinhos.findauto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.model.Cidade;

/**
 * Created by Chiptronic on 31/10/2016.
 */
public class AdapterCidades extends BaseAdapter {

    private ArrayList<Cidade> cidades;
    private Context context;

    public AdapterCidades(ArrayList<Cidade> cidades, Context context) {
        this.cidades = cidades;
        this.context = context;
    }

    @Override
    public int getCount() {
        return cidades.size();
    }

    @Override
    public Object getItem(int position) {
        return cidades.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(cidades.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cidade cidade = cidades.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_cidades, null);

        TextView textViewOpcao = (TextView) view.findViewById(R.id.text_view_cidades_nome);
        textViewOpcao.setText(cidade.getNome());

        return view;
    }

    @Override
    public boolean isEnabled (int position) {
        return true;
    }
}
