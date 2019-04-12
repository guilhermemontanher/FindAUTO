package br.edu.fatecourinhos.findauto.model;

/**
 * Created by Chiptronic on 31/10/2016.
 */
public class Estado {

    private String id;
    private String uf;
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
