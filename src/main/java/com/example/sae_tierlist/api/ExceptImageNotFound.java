package com.example.sae_tierlist.api;

public class ExceptImageNotFound extends Exception {
    public ExceptImageNotFound() {
        super("Aucune image trouvée pour cette recherche.");
    }
}
