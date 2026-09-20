package com.example.sae_tierlist.model;

import java.io.File;


public class ImageItem extends Item {

    private String cheminItem;
    private static final long serialVersionUID = 1L;


    public ImageItem(String cheminItem) {
        super();
        this.cheminItem = cheminItem;
    }

    @Override
    public String getDisplayName() {
        return new File(cheminItem).getName();
    }

    public String getCheminItem() {
        return cheminItem;
    }

    public void setCheminItem(String cheminItem) {
        this.cheminItem = cheminItem;
    }
}
