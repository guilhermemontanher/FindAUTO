package br.edu.fatecourinhos.findauto.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import br.edu.fatecourinhos.findauto.R;
import br.edu.fatecourinhos.findauto.activities.ActivityNotificacoes;
import br.edu.fatecourinhos.findauto.activities.ActivitySobre;
import br.edu.fatecourinhos.findauto.adapters.AdapterConfiguracoes;
import br.edu.fatecourinhos.findauto.model.OpcoesConfiguracao;

public class FragmentConfiguracoes extends Fragment {

    private AdapterConfiguracoes mAdapter;

    public static FragmentConfiguracoes newInstance() {

        FragmentConfiguracoes fragmentConfiguracoes = new FragmentConfiguracoes();
        return fragmentConfiguracoes;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_configuracoes, container, false);

        ListView listOpcoes = (ListView) view.findViewById(R.id.list_opcoes_configuracao);

        ArrayList<OpcoesConfiguracao> mListOpcoes = new ArrayList<>();

        OpcoesConfiguracao op;

        op = new OpcoesConfiguracao();
        op.setNome(getResources().getString(R.string.config_sobre));
        op.setImagem(R.drawable.ic_help_outline_white_24dp);
        mListOpcoes.add(op);

        op = new OpcoesConfiguracao();
        op.setNome(getResources().getString(R.string.config_notificacao));
        op.setImagem(R.drawable.ic_notifications_none_white_24dp);
        mListOpcoes.add(op);

        mAdapter = new AdapterConfiguracoes(mListOpcoes, getActivity());
        listOpcoes.setAdapter(mAdapter);
        listOpcoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(new Intent(getActivity(), ActivitySobre.class));
                        break;
                    case 1:
                        startActivity(new Intent(getActivity(), ActivityNotificacoes.class));
                        break;
                    default:
                        break;
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
