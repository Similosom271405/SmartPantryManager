package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PANTRY = "pantry";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    public static final String COLUMN_ID = "id";

    // Pantry Table Columns
    public static final String COLUMN_PANTRY_NAME = "name";
    public static final String COLUMN_PANTRY_QTY = "quantity";
    public static final String COLUMN_PANTRY_UNIT = "unit";
    public static final String COLUMN_PANTRY_EXPIRY = "expiry_date";

    // Recipe Table Columns
    public static final String COLUMN_RECIPE_NAME = "name";
    public static final String COLUMN_RECIPE_TIME = "prep_time";
    public static final String COLUMN_RECIPE_STEPS = "instructions";

    // Recipe Ingredient Columns
    public static final String COLUMN_RI_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RI_NAME = "ingredient_name";
    public static final String COLUMN_RI_QTY = "required_qty";
    public static final String COLUMN_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PANTRY_TABLE = "CREATE TABLE " + TABLE_PANTRY + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PANTRY_NAME + " TEXT NOT NULL, "
                + COLUMN_PANTRY_QTY + " REAL NOT NULL, "
                + COLUMN_PANTRY_UNIT + " TEXT NOT NULL, "
                + COLUMN_PANTRY_EXPIRY + " TEXT"
                + ");";

        String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_RECIPE_NAME + " TEXT NOT NULL, "
                + COLUMN_RECIPE_TIME + " TEXT NOT NULL, "
                + COLUMN_RECIPE_STEPS + " TEXT NOT NULL"
                + ");";

        String CREATE_RI_TABLE = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_RI_RECIPE_ID + " INTEGER NOT NULL, "
                + COLUMN_RI_NAME + " TEXT NOT NULL, "
                + COLUMN_RI_QTY + " REAL NOT NULL, "
                + COLUMN_RI_UNIT + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_ID + ") ON DELETE CASCADE"
                + ");";

        db.execSQL(CREATE_PANTRY_TABLE);
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_RI_TABLE);

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // --- Pantry CRUD Operations ---

    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PANTRY_NAME, item.getName().trim());
        cv.put(COLUMN_PANTRY_QTY, item.getQuantity());
        cv.put(COLUMN_PANTRY_UNIT, item.getUnit().trim());
        cv.put(COLUMN_PANTRY_EXPIRY, item.getExpiryDate() != null ? item.getExpiryDate().trim() : "");
        long result = db.insert(TABLE_PANTRY, null, cv);
        db.close();
        return result;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PANTRY + " ORDER BY " + COLUMN_PANTRY_NAME + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                PantryItem item = new PantryItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_QTY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_UNIT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_EXPIRY))
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PANTRY_NAME, item.getName().trim());
        cv.put(COLUMN_PANTRY_QTY, item.getQuantity());
        cv.put(COLUMN_PANTRY_UNIT, item.getUnit().trim());
        cv.put(COLUMN_PANTRY_EXPIRY, item.getExpiryDate() != null ? item.getExpiryDate().trim() : "");

        int rows = db.update(TABLE_PANTRY, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(item.getId())});
        db.close();
        return rows;
    }

    public void deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void clearAllPantryItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, null, null);
        db.close();
    }

    // --- Recipe Queries & Strict-Matching Engine ---

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPES, null);

        if (cursor.moveToFirst()) {
            do {
                int recipeId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                Recipe recipe = new Recipe(
                        recipeId,
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_STEPS))
                );
                recipe.setIngredients(getIngredientsForRecipe(db, recipeId));
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recipes;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, int recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPE_INGREDIENTS + " WHERE " + COLUMN_RI_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)});

        if (cursor.moveToFirst()) {
            do {
                RecipeIngredient ingredient = new RecipeIngredient(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RI_RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RI_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RI_QTY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RI_UNIT))
                );
                ingredients.add(ingredient);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ingredients;
    }

    public List<Recipe> getStrictSuggestedRecipes() {
        List<Recipe> suggestedRecipes = new ArrayList<>();
        List<PantryItem> pantryList = getAllPantryItems();

        Map<String, Double> pantryMap = new HashMap<>();
        for (PantryItem item : pantryList) {
            String normalizedKey = normalizeIngredientName(item.getName());
            double currentQty = pantryMap.getOrDefault(normalizedKey, 0.0);
            pantryMap.put(normalizedKey, currentQty + item.getQuantity());
        }

        List<Recipe> allRecipes = getAllRecipes();

        for (Recipe recipe : allRecipes) {
            boolean canMakeRecipe = true;

            if (recipe.getIngredients().isEmpty()) {
                continue;
            }

            for (RecipeIngredient req : recipe.getIngredients()) {
                String reqKey = normalizeIngredientName(req.getIngredientName());

                if (!pantryMap.containsKey(reqKey)) {
                    canMakeRecipe = false;
                    break;
                }

                double availableQty = pantryMap.get(reqKey);
                if (availableQty < req.getRequiredQuantity()) {
                    canMakeRecipe = false;
                    break;
                }
            }

            if (canMakeRecipe) {
                suggestedRecipes.add(recipe);
            }
        }

        return suggestedRecipes;
    }

    public static String normalizeIngredientName(String name) {
        if (name == null) return "";
        String clean = name.trim().toLowerCase();
        if (clean.endsWith("es") && clean.length() > 4) {
            clean = clean.substring(0, clean.length() - 2);
        } else if (clean.endsWith("s") && clean.length() > 3) {
            clean = clean.substring(0, clean.length() - 1);
        }
        return clean;
    }

    // --- Seed 18 Default Recipes ---

    private void seedRecipes(SQLiteDatabase db) {
        insertRecipeWithIngredients(db, "Scrambled Eggs on Toast", "10 mins",
                "1. Whisk eggs in a small bowl with a pinch of salt.\n2. Melt butter in a skillet over medium-low heat.\n3. Pour in eggs and gently stir until soft curds form.\n4. Toast bread slices and serve scrambled eggs immediately on top.",
                new String[][]{{"Egg", "2", "pcs"}, {"Bread", "2", "slices"}, {"Butter", "10", "g"}, {"Salt", "1", "pinch"}});

        insertRecipeWithIngredients(db, "Classic French Toast", "15 mins",
                "1. Whisk eggs, milk, sugar, and cinnamon together in a shallow dish.\n2. Dip bread slices into mixture until well soaked.\n3. Melt butter in a frying pan and cook bread for 3 minutes per side until golden brown.",
                new String[][]{{"Egg", "2", "pcs"}, {"Milk", "100", "ml"}, {"Bread", "4", "slices"}, {"Butter", "15", "g"}, {"Sugar", "10", "g"}});

        insertRecipeWithIngredients(db, "Tomato Garlic Pasta", "20 mins",
                "1. Boil pasta in salted water until al dente.\n2. Sauté minced garlic in olive oil in a pan until fragrant.\n3. Add diced tomatoes and simmer for 10 minutes.\n4. Toss drained pasta into sauce and season with salt.",
                new String[][]{{"Pasta", "200", "g"}, {"Tomato", "2", "pcs"}, {"Garlic", "2", "cloves"}, {"Olive Oil", "15", "ml"}, {"Salt", "1", "tsp"}});

        insertRecipeWithIngredients(db, "Simple Grilled Cheese", "10 mins",
                "1. Butter one side of each slice of bread.\n2. Place cheese between non-buttered sides.\n3. Toast in a pan on medium heat until both sides are golden and cheese melts completely.",
                new String[][]{{"Bread", "2", "slices"}, {"Cheese", "50", "g"}, {"Butter", "15", "g"}});

        insertRecipeWithIngredients(db, "Garlic Butter Rice", "20 mins",
                "1. Cook white rice in water until fluffy.\n2. In a saucepan, melt butter and sauté minced garlic until light golden.\n3. Fold garlic butter through hot rice and season with salt.",
                new String[][]{{"Rice", "200", "g"}, {"Butter", "30", "g"}, {"Garlic", "3", "cloves"}, {"Salt", "1", "tsp"}});

        insertRecipeWithIngredients(db, "Cheesy Scrambled Omelette", "10 mins",
                "1. Whisk eggs with salt and black pepper.\n2. Pour into melted butter in hot pan.\n3. Sprinkle shredded cheese over top as it firms up, fold in half, and serve hot.",
                new String[][]{{"Egg", "3", "pcs"}, {"Cheese", "40", "g"}, {"Butter", "10", "g"}, {"Salt", "1", "pinch"}});

        insertRecipeWithIngredients(db, "Creamy Mashed Potatoes", "25 mins",
                "1. Peel and cube potatoes. Boil in water until fork-tender.\n2. Drain water completely.\n3. Mash thoroughly with warm milk, butter, and salt until smooth and creamy.",
                new String[][]{{"Potato", "3", "pcs"}, {"Milk", "60", "ml"}, {"Butter", "25", "g"}, {"Salt", "1", "tsp"}});

        insertRecipeWithIngredients(db, "Tomato Egg Stir-Fry", "15 mins",
                "1. Scramble eggs lightly in oil, then set aside.\n2. Add diced tomatoes to pan and stir-fry until juicy.\n3. Fold scrambled eggs back in with sugar and salt; cook 2 minutes.",
                new String[][]{{"Egg", "3", "pcs"}, {"Tomato", "2", "pcs"}, {"Cooking Oil", "15", "ml"}, {"Sugar", "5", "g"}, {"Salt", "1", "pinch"}});

        insertRecipeWithIngredients(db, "Quick Chicken Stir-Fry", "20 mins",
                "1. Slice chicken breast into thin strips.\n2. Heat cooking oil in a pan and sauté chicken with garlic until seared.\n3. Add diced onion and soy sauce; cook until tender. Serve over boiled rice.",
                new String[][]{{"Chicken Breast", "250", "g"}, {"Garlic", "2", "cloves"}, {"Onion", "1", "pcs"}, {"Soy Sauce", "20", "ml"}, {"Rice", "150", "g"}});

        insertRecipeWithIngredients(db, "Classic Tuna Salad", "10 mins",
                "1. Drain canned tuna into a bowl.\n2. Finely dice onion.\n3. Combine tuna, onion, and mayonnaise. Season with salt and pepper. Serve cold.",
                new String[][]{{"Canned Tuna", "1", "can"}, {"Mayonnaise", "30", "g"}, {"Onion", "1", "pcs"}, {"Salt", "1", "pinch"}});

        insertRecipeWithIngredients(db, "Simple Golden Pancakes", "20 mins",
                "1. Mix flour, sugar, and baking powder.\n2. Whisk in egg and milk to form smooth batter.\n3. Cook ladlefuls in melted butter on griddle until bubbles form, flip, and cook until golden.",
                new String[][]{{"Flour", "150", "g"}, {"Milk", "200", "ml"}, {"Egg", "1", "pcs"}, {"Sugar", "25", "g"}, {"Butter", "20", "g"}});

        insertRecipeWithIngredients(db, "Vegetable Fried Rice", "20 mins",
                "1. Heat oil in a wok. Scramble egg and break into bits.\n2. Add diced onion and garlic, stirring rapidly.\n3. Add cooked rice and soy sauce; toss everything on high heat for 3 minutes.",
                new String[][]{{"Rice", "200", "g"}, {"Egg", "2", "pcs"}, {"Onion", "1", "pcs"}, {"Garlic", "1", "cloves"}, {"Soy Sauce", "15", "ml"}, {"Cooking Oil", "15", "ml"}});

        insertRecipeWithIngredients(db, "Tomato Soup Comfort", "25 mins",
                "1. Sauté chopped onion and garlic in butter until soft.\n2. Add fresh tomatoes and 150ml water; simmer for 15 minutes.\n3. Blend until silky smooth, season with salt, and serve.",
                new String[][]{{"Tomato", "4", "pcs"}, {"Onion", "1", "pcs"}, {"Garlic", "2", "cloves"}, {"Butter", "20", "g"}, {"Salt", "1", "tsp"}});

        insertRecipeWithIngredients(db, "Crispy Fried Potatoes", "20 mins",
                "1. Slice potatoes into thin rounds.\n2. Heat cooking oil in large skillet.\n3. Fry potato rounds until crisp and golden brown on both sides. Season with salt.",
                new String[][]{{"Potato", "3", "pcs"}, {"Cooking Oil", "30", "ml"}, {"Salt", "1", "tsp"}});

        insertRecipeWithIngredients(db, "Cheesy Toast Delight", "10 mins",
                "1. Toast bread slices lightly.\n2. Spread butter evenly on warm bread.\n3. Layer shredded cheese and broil or pan-toast covered until cheese bubbles.",
                new String[][]{{"Bread", "2", "slices"}, {"Cheese", "40", "g"}, {"Butter", "10", "g"}});

        insertRecipeWithIngredients(db, "Honey Soy Glazed Chicken", "25 mins",
                "1. Sear chicken breast cubes in oil until lightly browned.\n2. Add minced garlic, soy sauce, and honey to the pan.\n3. Reduce glaze on low heat until sticky and chicken is cooked through.",
                new String[][]{{"Chicken Breast", "300", "g"}, {"Soy Sauce", "25", "ml"}, {"Honey", "20", "ml"}, {"Garlic", "2", "cloves"}});

        insertRecipeWithIngredients(db, "Sautéed Garlic Onion Rice", "15 mins",
                "1. Sauté diced onion and minced garlic in cooking oil until fragrant.\n2. Add cooked rice and salt.\n3. Toss over medium heat for 4 minutes until rice absorbs all aroma.",
                new String[][]{{"Rice", "200", "g"}, {"Garlic", "2", "cloves"}, {"Onion", "1", "pcs"}, {"Cooking Oil", "15", "ml"}, {"Salt", "1", "pinch"}});

        insertRecipeWithIngredients(db, "Avocado Toast with Egg", "10 mins",
                "1. Toast bread slices until golden and crisp.\n2. Mash avocado with salt and spread across toast.\n3. Fry egg in butter sunny-side up and place directly on avocado spread.",
                new String[][]{{"Bread", "2", "slices"}, {"Avocado", "1", "pcs"}, {"Egg", "1", "pcs"}, {"Butter", "10", "g"}, {"Salt", "1", "pinch"}});
    }

    private void insertRecipeWithIngredients(SQLiteDatabase db, String name, String time, String steps, String[][] ingredients) {
        ContentValues rCv = new ContentValues();
        rCv.put(COLUMN_RECIPE_NAME, name);
        rCv.put(COLUMN_RECIPE_TIME, time);
        rCv.put(COLUMN_RECIPE_STEPS, steps);
        long recipeId = db.insert(TABLE_RECIPES, null, rCv);

        for (String[] ing : ingredients) {
            ContentValues iCv = new ContentValues();
            iCv.put(COLUMN_RI_RECIPE_ID, recipeId);
            iCv.put(COLUMN_RI_NAME, ing[0]);
            iCv.put(COLUMN_RI_QTY, Double.parseDouble(ing[1]));
            iCv.put(COLUMN_RI_UNIT, ing[2]);
            db.insert(TABLE_RECIPE_INGREDIENTS, null, iCv);
        }
    }
}