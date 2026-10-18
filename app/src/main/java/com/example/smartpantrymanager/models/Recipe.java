package com.example.smartpantrymanager.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recipe implements Serializable {
    private int id;
    private String name;
    private String preparationTime;
    private String instructions;
    private List<RecipeIngredient> ingredients;

    public Recipe(int id, String name, String preparationTime, String instructions) {
        this.id = id;
        this.name = name;
        this.preparationTime = preparationTime;
        this.instructions = instructions;
        this.ingredients = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPreparationTime() { return preparationTime; }
    public String getInstructions() { return instructions; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredient> ingredients) { this.ingredients = ingredients; }
}