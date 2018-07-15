package com.example.duelmanagerlib.Builder;

import com.example.duelmanagerlib.Model.Theme;

public class ThemeBuilder {

    private String id;
    private String name;

    public ThemeBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ThemeBuilder name(String name) {
        this.name = name;
        return this;
    }

    public Theme build() {
        return new Theme(id, name);
    }

}
