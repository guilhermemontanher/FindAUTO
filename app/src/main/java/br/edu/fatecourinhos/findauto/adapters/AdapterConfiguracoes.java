package br.edu.fatecourinhos.findauto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.model.OpcoesConfiguracao;

/**
 * Created by Chiptronic on 12/09/2016.
 */
public class AdapterConfiguracoes extends BaseAdapter {
    private ArrayList<OpcoesConfiguracao> opcoes;
    private Context context;

    public AdapterConfiguracoes(ArrayList<OpcoesConfiguracao> opcoes, Context context) {
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
        OpcoesConfiguracao opcao = opcoes.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_itens_configuracoes, null);


        TextView textViewOpcao = (TextView) view.findViewById(R.id.text_view_item_opcao_configuracoes);
        textViewOpcao.setText(opcao.getNome());

        ImageView img = (ImageView) view.findViewById(R.id.image_view_item_opcao_configuracoes);
        img.setImageResource(opcao.getImagem());

        return view;
    }

    @Override
    public boolean isEnabled (int position) {
        return true;
    }
}
