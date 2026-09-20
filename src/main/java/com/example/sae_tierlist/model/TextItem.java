package com.example.sae_tierlist.model;

public class TextItem extends Item {

    private String texte;
    private static final long serialVersionUID = 1L;

    public TextItem(String texte) {
        super();
        this.texte = texte;
    }

    @Override
    public String getDisplayName() {
        return this.texte;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }
}
