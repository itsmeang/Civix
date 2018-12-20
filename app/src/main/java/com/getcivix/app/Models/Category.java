package com.getcivix.app.Models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Category {

    public String categoryName;
    public String categoryType;
    public String categoryTypeColor;

    public Category(String categoryName, String categoryType, String categoryTypeColor) {
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

