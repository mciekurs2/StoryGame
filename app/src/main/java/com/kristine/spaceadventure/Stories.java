package com.kristine.spaceadventure;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Stories {
    private String tittle;
    private int position_on_tree;
    private int chapter_size;
    private boolean red;

    public Stories(){}

    public Stories(String tittle, int position_on_tree, int chapter_size, boolean red) {
        this.tittle = tittle;
        this.position_on_tree = position_on_tree;
        this.chapter_size = chapter_size;
        this.red = red;
    }

    public String getTittle() {
        return tittle;
    }

    public int getPosition_on_tree() {
        return position_on_tree;
    }

    public int getChapter_size() {
        return chapter_size;
    }

    public boolean isRed() {
        return red;
    }
}
