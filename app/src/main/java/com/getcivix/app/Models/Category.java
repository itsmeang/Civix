package com.getcivix.app.Models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Category {

    public String categoryName;
    public Integer categoryType;
    public String categoryTypeColor;

    public Category(String categoryName, Integer categoryType, String categoryTypeColor) {
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.categoryTypeColor = categoryTypeColor;
    }

    public Category() {
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryName='" + categoryName + '\'' +
                ", categoryType=" + categoryType +
                ", categoryTypeColor='" + categoryTypeColor + '\'' +
                '}';
    }
}

