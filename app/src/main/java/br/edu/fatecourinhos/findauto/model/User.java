package br.edu.fatecourinhos.findauto.model;

import java.io.Serializable;

/**
 * Created by Lincoln on 07/01/16.
 */
public class User implements Serializable {
    private String id, name, lastname, phone,photoUrl, photoBase64, cidadeName, cidadeId, email;

    public User() {
    }

    public User(String id, String name, String lastname, String phone, String photoUrl, String photoBase64, String cidadeName, String cidadeId, String email) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.photoBase64 = photoBase64;
        this.cidadeName = cidadeName;
        this.cidadeId = cidadeId;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }

    public String getCidadeName() {
        return cidadeName;
    }

    public void setCidadeName(String cidadeName) {
        this.cidadeName = cidadeName;
    }

    public String getCidadeId() {
        return cidadeId;
    }

    public void setCidadeId(String cidadeId) {
        this.cidadeId = cidadeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
