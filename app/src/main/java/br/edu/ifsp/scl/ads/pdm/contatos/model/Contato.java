package br.edu.ifsp.scl.ads.pdm.contatos.model;

import java.io.Serializable;

public class Contato implements Serializable {
    private String nomeCompleto;
    private String email;
    private String telefone;
    private String celular;
    private boolean comercial;
    private String site;

    public Contato(String nomeCompleto, String email, String telefone, String celular, boolean comercial, String site) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.telefone = telefone;
        this.celular = celular;
        this.comercial = comercial;
        this.site = site;
    }

    public Contato() {
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public boolean isComercial() {
        return comercial;
    }

    public void setComercial(boolean comercial) {
        this.comercial = comercial;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public String toString() {
        return "contato{" +
                "nomeCompleto='" + nomeCompleto + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", celular='" + celular + '\'' +
                ", comercial=" + comercial +
                ", site='" + site + '\'' +
                '}';
    }
}
