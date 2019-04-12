package br.edu.fatecourinhos.findauto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.model.OpcoesMinhaConta;


/**
 * Created by Chiptronic on 12/09/2016.
 */
public class AdapterMinhaContaOpcoes extends BaseAdapter {
    private ArrayList<OpcoesMinhaConta> opcoes;
    private Context context;

    public AdapterMinhaContaOpcoes(ArrayList<OpcoesMinhaConta> opcoes, Context context) {
        this.opcoes = opcoes;
        this.context = context;
    }

    @Override
    public int getCount() {
        return opcoes.size();
    }

    @Override
    public Object getItem(int position) {
        return opcoes.get(position);
    }

    @Override
    public long getItemId(int position) {
        //return position;
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OpcoesMinhaConta opcao = opcoes.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_itens_minha_conta, null);


        TextView textViewOpcao = (TextView) view.findViewById(R.id.text_view_item_opcao_minha_conta);
        textViewOpcao.setText(opcao.getNome());

        TextView textViewQuantidade = (TextView) view.findViewById(R.id.text_view_item_qtd_minha_conta);
        textViewQuantidade.setText(String.valueOf(opcao.getQuantidade()));

        return view;
    }

    @Override
    public boolean isEnabled (int position) {
        return true;
    }
}
