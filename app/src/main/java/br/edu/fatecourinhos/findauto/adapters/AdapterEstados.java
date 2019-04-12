package br.edu.fatecourinhos.findauto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.model.Estado;

/**
 * Created by Chiptronic on 31/10/2016.
 */
public class AdapterEstados extends BaseAdapter {

    private ArrayList<Estado> estados;
    private Context context;

    public AdapterEstados(ArrayList<Estado> estados, Context context) {
        this.estados = estados;
        this.context = context;
    }

    @Override
    public int getCount() {
        return estados.size();
    }

    @Override
    public Object getItem(int position) {
        return estados.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(estados.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Estado estado = estados.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_estados, null);


        TextView textViewOpcao = (TextView) view.findViewById(R.id.text_view_estados_nome);
        textViewOpcao.setText(estado.getNome());

        return view;
    }

    @Override
    public boolean isEnabled (int position) {
        return true;
    }
}
