package com.example.sae_tierlist.api.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {
    private String name;

    @JsonProperty("background_image")
    private String backgroundImage;

    public String getName() {
        return name;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }
}