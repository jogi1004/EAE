package com.example.eaeprojekt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.eaeprojekt.IngredientAmountDTO;
import com.example.eaeprojekt.IngredientDTO;
import com.example.eaeprojekt.RecipeDTO;
import com.example.eaeprojekt.StepDTO;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    // TODO vor Release unbedingt auf private umstellen - dient dem Löschen
    public static final String DATABASE_NAME = "Recipe.db";
    private static final int DATABASE_VERSION = 1;

    // Tabelle für Rezepte
    public static final String TABLE_RECIPES = "Rezepte";
    public static final String COLUMN_RECIPE_ID = "ID";
    public static final String COLUMN_RECIPE_TITLE = "Titel";
    public static final String COLUMN_RECIPE_PORTIONSMENGE = "Portionsmenge";
    public static final String COLUMN_RECIPE_DAUER = "Dauer";
    public static final String COLUMN_IS_FAVORITE = "istFavorit";

    // Tabelle für Zutaten
    public static final String TABLE_INGREDIENTS = "Zutaten";
    public static final String COLUMN_INGREDIENT_ID = "ID";
    public static final String COLUMN_INGREDIENT_NAME = "Bezeichnung";
    public static final String COLUMN_INGREDIENT_UNIT = "Einheit";

    // Tabelle für die Menge der Zutaten
    public static final String TABLE_INGREDIENT_QUANTITY = "ZutatenMenge";
    public static final String COLUMN_INGREDIENT_QUANTITY_ID = "ID";
    public static final String COLUMN_INGREDIENT_QUANTITY_AMOUNT = "Menge";
    public static final String COLUMN_INGREDIENT_QUANTITY_RECIPE_ID = "RezeptID";
    public static final String COLUMN_INGREDIENT_QUANTITY_INGREDIENT_ID = "ZutatID";
    public static final String COLUMN_IS_ON_SHOPPING_LIST = "aufEinkaufsliste";

    // Tabelle für Schritte in Rezepten
    public static final String TABLE_STEPS = "Schritte";
    public static final String COLUMN_STEP_ID = "ID";
    public static final String COLUMN_STEP_TEXT = "Text";
    public static final String COLUMN_STEP_RECIPE_ID = "RezeptID";

    private final SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // CRUD-Operationen für Rezepte

    public long insertRecipe(String title, int portionsmenge, String dauer, int istFavorit) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RECIPE_TITLE, title);
        values.put(COLUMN_RECIPE_PORTIONSMENGE, portionsmenge);
        values.put(COLUMN_RECIPE_DAUER, dauer);
        values.put(COLUMN_IS_FAVORITE, istFavorit);
        return database.insert(TABLE_RECIPES, null, values);
    }

    public List<RecipeDTO> getAllRecipes() {
        List<RecipeDTO> recipes = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_RECIPES, null);

            if (cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(COLUMN_RECIPE_ID);
                    int titleIndex = cursor.getColumnIndex(COLUMN_RECIPE_TITLE);
                    int portionsmengeIndex = cursor.getColumnIndex(COLUMN_RECIPE_PORTIONSMENGE);
                    int dauerIndex = cursor.getColumnIndex(COLUMN_RECIPE_DAUER);
                    int istFavoritIndex = cursor.getColumnIndex(COLUMN_IS_FAVORITE);

                    if (idIndex >= 0 && titleIndex >= 0 && portionsmengeIndex >= 0 && dauerIndex >= 0 && istFavoritIndex >= 0) {
                        int id = cursor.getInt(idIndex);
                        String title = cursor.getString(titleIndex);
                        int portionsmenge = cursor.getInt(portionsmengeIndex);
                        int dauer = cursor.getInt(dauerIndex);
                        int istFavorit = cursor.getInt(istFavoritIndex);

                        RecipeDTO recipeDTO = new RecipeDTO(id, title, portionsmenge, dauer, istFavorit);
                        recipes.add(recipeDTO);
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();

        return recipes;
    }

    public RecipeDTO getRecipeById(int id) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_RECIPES + " WHERE " + COLUMN_RECIPE_ID + " = ?", new String[]{String.valueOf(id)});
        RecipeDTO recipeDTO = null;

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(COLUMN_RECIPE_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_RECIPE_TITLE);
                int portionsmengeIndex = cursor.getColumnIndex(COLUMN_RECIPE_PORTIONSMENGE);
                int dauerIndex = cursor.getColumnIndex(COLUMN_RECIPE_DAUER);
                int istFavoritIndex = cursor.getColumnIndex(COLUMN_IS_FAVORITE);

                if (idIndex >= 0 && titleIndex >= 0 && portionsmengeIndex >= 0 && dauerIndex >= 0 && istFavoritIndex >= 0) {
                    String title = cursor.getString(titleIndex);
                    int portionsmenge = cursor.getInt(portionsmengeIndex);
                    int dauer = cursor.getInt(dauerIndex);
                    int istFavorit = cursor.getInt(istFavoritIndex);

                    recipeDTO = new RecipeDTO(id, title, portionsmenge, dauer, istFavorit);
                }
            }

            cursor.close();

        return recipeDTO;
    }

    public RecipeDTO getRecipeByName(String name) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_RECIPES + " WHERE " + COLUMN_RECIPE_TITLE + " = ?", new String[]{name});
        RecipeDTO recipeDTO = null;

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(COLUMN_RECIPE_ID);
                int portionsmengeIndex = cursor.getColumnIndex(COLUMN_RECIPE_PORTIONSMENGE);
                int dauerIndex = cursor.getColumnIndex(COLUMN_RECIPE_DAUER);
                int istFavoritIndex = cursor.getColumnIndex(COLUMN_IS_FAVORITE);

                if (idIndex >= 0 && portionsmengeIndex >= 0 && dauerIndex >= 0 && istFavoritIndex >= 0) {
                    int id = cursor.getInt(idIndex);
                    int portionsmenge = cursor.getInt(portionsmengeIndex);
                    int dauer = cursor.getInt(dauerIndex);
                    int istFavorit = cursor.getInt(istFavoritIndex);

                    recipeDTO = new RecipeDTO(id, name, portionsmenge, dauer, istFavorit);
                }
            }

            cursor.close();

        return recipeDTO;
    }


    public int updateRecipe(long recipeId, String newTitle, int newPortionsmenge, String newDauer, int newIstFavorit) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RECIPE_TITLE, newTitle);
        values.put(COLUMN_RECIPE_PORTIONSMENGE, newPortionsmenge);
        values.put(COLUMN_RECIPE_DAUER, newDauer);
        values.put(COLUMN_IS_FAVORITE, newIstFavorit);
        return database.update(
                TABLE_RECIPES,
                values,
                COLUMN_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)}
        );
    }

    public int deleteRecipe(long recipeId) {
        return database.delete(
                TABLE_RECIPES,
                COLUMN_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)}
        );
    }

    public List<RecipeDTO> getFavoritenRezepte() {
        List<RecipeDTO> favoriten = new ArrayList<>();
        String[] projection = {
                COLUMN_RECIPE_ID,
                COLUMN_RECIPE_TITLE,
                COLUMN_RECIPE_PORTIONSMENGE,
                COLUMN_RECIPE_DAUER,
                COLUMN_IS_FAVORITE
        };
        String selection = COLUMN_IS_FAVORITE + " = ?";
        String[] selectionArgs = { "1" };

        Cursor cursor = database.query(
                TABLE_RECIPES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_TITLE));
                int portionsmenge = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_PORTIONSMENGE));
                int dauer = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_DAUER));
                int istFavorit = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FAVORITE));

                RecipeDTO recipeDTO = new RecipeDTO(id, title, portionsmenge, dauer, istFavorit);
                favoriten.add(recipeDTO);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return favoriten;
    }


    // CRUD-Operationen für Zutaten

    public long insertIngredient(String name, String unit) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_INGREDIENT_NAME, name);
        values.put(COLUMN_INGREDIENT_UNIT, unit);
        return database.insert(TABLE_INGREDIENTS, null, values);
    }

    public List<IngredientDTO> getAllIngredients() {
        List<IngredientDTO> ingredients = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_INGREDIENTS, null);

            if (cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_ID);
                    int nameIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_NAME);
                    int unitIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_UNIT);

                    if (idIndex >= 0 && nameIndex >= 0 && unitIndex >= 0) {
                        int id = cursor.getInt(idIndex);
                        String name = cursor.getString(nameIndex);
                        String unit = cursor.getString(unitIndex);

                        IngredientDTO ingredientDTO = new IngredientDTO(id, name, unit);
                        ingredients.add(ingredientDTO);
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();

        return ingredients;
    }

    public IngredientDTO getIngredientById(int id) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_INGREDIENTS + " WHERE " + COLUMN_INGREDIENT_ID + " = ?", new String[]{String.valueOf(id)});
        IngredientDTO ingredientDTO = null;
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_ID);
            int nameIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_NAME);
            int unitIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_UNIT);

            if (idIndex >= 0 && nameIndex >= 0 && unitIndex >= 0) {
                String name = cursor.getString(nameIndex);
                String unit = cursor.getString(unitIndex);

                cursor.close();
                ingredientDTO = new IngredientDTO(id, name, unit);
            }
        }

        cursor.close();
        return ingredientDTO;
    }


/*    public List<IngredientDTO> getIngredientsByName(String name) {
        List<IngredientDTO> result = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_INGREDIENTS + " WHERE " + COLUMN_INGREDIENT_NAME + " = ?", new String[]{name});

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_ID);
            int nameIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_NAME);
            int unitIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_UNIT);

            do {
                int ingredientId = cursor.getInt(idIndex);
                String ingredientName = cursor.getString(nameIndex);
                String unit = cursor.getString(unitIndex);

                IngredientDTO ingredientDTO = new IngredientDTO(ingredientId, ingredientName, unit);
                result.add(ingredientDTO);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return result;
    }*/





    public int updateIngredient(long ingredientId, String newName, String newUnit) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_INGREDIENT_NAME, newName);
        values.put(COLUMN_INGREDIENT_UNIT, newUnit);
        return database.update(
                TABLE_INGREDIENTS,
                values,
                COLUMN_INGREDIENT_ID + " = ?",
                new String[]{String.valueOf(ingredientId)}
        );
    }

    public int deleteIngredient(long ingredientId) {
        return database.delete(
                TABLE_INGREDIENTS,
                COLUMN_INGREDIENT_ID + " = ?",
                new String[]{String.valueOf(ingredientId)}
        );
    }

    // CRUD-Operationen für ZutatenMenge

    public long insertIngredientQuantity(long recipeId, long ingredientId, double amount, int isOnShoppingList) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_INGREDIENT_QUANTITY_AMOUNT, amount);
        values.put(COLUMN_INGREDIENT_QUANTITY_RECIPE_ID, recipeId);
        values.put(COLUMN_INGREDIENT_QUANTITY_INGREDIENT_ID, ingredientId);
        values.put(COLUMN_IS_ON_SHOPPING_LIST, isOnShoppingList);
        return database.insert(TABLE_INGREDIENT_QUANTITY, null, values);
    }

    public int updateIngredientQuantity(long ingredientQuantityId, double newAmount, int newIsOnShoppingList) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_INGREDIENT_QUANTITY_AMOUNT, newAmount);
        values.put(COLUMN_IS_ON_SHOPPING_LIST, newIsOnShoppingList);
        return database.update(
                TABLE_INGREDIENT_QUANTITY,
                values,
                COLUMN_INGREDIENT_QUANTITY_ID + " = ?",
                new String[]{String.valueOf(ingredientQuantityId)}
        );
    }

    public int deleteIngredientQuantity(long ingredientQuantityId) {
        return database.delete(
                TABLE_INGREDIENT_QUANTITY,
                COLUMN_INGREDIENT_QUANTITY_ID + " = ?",
                new String[]{String.valueOf(ingredientQuantityId)}
        );
    }

    public List<IngredientAmountDTO> getIngredientsForRecipe(int recipeId) {
        List<IngredientAmountDTO> ingredientAmounts = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_INGREDIENT_QUANTITY + " WHERE " + COLUMN_INGREDIENT_QUANTITY_RECIPE_ID + " = ?", new String[]{String.valueOf(recipeId)});

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_QUANTITY_ID);
                int amountIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_QUANTITY_AMOUNT);
                int ingredientIdIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_QUANTITY_INGREDIENT_ID);
                int isOnShoppingListIndex = cursor.getColumnIndex(COLUMN_IS_ON_SHOPPING_LIST);

                while (!cursor.isAfterLast()) {
                    if (idIndex >= 0 && amountIndex >= 0 && ingredientIdIndex >= 0 && isOnShoppingListIndex >= 0) {
                        int id = cursor.getInt(idIndex);
                        int amount = cursor.getInt(amountIndex);
                        int ingredientId = cursor.getInt(ingredientIdIndex);
                        int isOnShoppingList = cursor.getInt(isOnShoppingListIndex);

                        IngredientAmountDTO ingredientAmountDTO = new IngredientAmountDTO(id, recipeId, ingredientId, amount, isOnShoppingList);
                        ingredientAmounts.add(ingredientAmountDTO);
                    }

                    cursor.moveToNext();
                }

            cursor.close();
        }

        return ingredientAmounts;
    }

    public List<IngredientAmountDTO> getIngredientsOnShoppingList() {
        List<IngredientAmountDTO> ingredientsOnShoppingList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_INGREDIENT_QUANTITY + " WHERE " + COLUMN_IS_ON_SHOPPING_LIST + " = ?", new String[]{"1"});

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_QUANTITY_ID);
            int amountIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_QUANTITY_AMOUNT);
            int recipeIdIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_QUANTITY_RECIPE_ID);
            int ingredientIdIndex = cursor.getColumnIndex(COLUMN_INGREDIENT_QUANTITY_INGREDIENT_ID);
            int isOnShoppingListIndex = cursor.getColumnIndex(COLUMN_IS_ON_SHOPPING_LIST);

            while (!cursor.isAfterLast()) {
                if (idIndex >= 0 && amountIndex >= 0 && recipeIdIndex >= 0 && ingredientIdIndex >= 0 && isOnShoppingListIndex >= 0) {
                    int id = cursor.getInt(idIndex);
                    int recipeId = cursor.getInt(recipeIdIndex);
                    int ingredientId = cursor.getInt(ingredientIdIndex);
                    int amount = cursor.getInt(amountIndex);
                    int isOnShoppingList = cursor.getInt(isOnShoppingListIndex);

                    IngredientAmountDTO ingredientAmountDTO = new IngredientAmountDTO(id, amount, recipeId, ingredientId, isOnShoppingList);
                    ingredientsOnShoppingList.add(ingredientAmountDTO);
                }

                cursor.moveToNext();
            }
        }

        cursor.close();
        return ingredientsOnShoppingList;
    }


    // CRUD-Operationen für Schritte

    public long insertStep(long recipeId, String stepText) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STEP_RECIPE_ID, recipeId);
        values.put(COLUMN_STEP_TEXT, stepText);
        return database.insert(TABLE_STEPS, null, values);
    }

    public List<StepDTO> getAllStepsForRecipe(int recipeId) {
        List<StepDTO> steps = new ArrayList<>();
        String selection = COLUMN_STEP_RECIPE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(recipeId)};
        Cursor cursor = database.query(
                TABLE_STEPS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(COLUMN_STEP_ID);
                int textIndex = cursor.getColumnIndex(COLUMN_STEP_TEXT);

                while (!cursor.isAfterLast()) {
                    if (idIndex >= 0 && textIndex >= 0) {
                        int id = cursor.getInt(idIndex);
                        String text = cursor.getString(textIndex);

                        StepDTO stepDTO = new StepDTO(id, text, recipeId);
                        steps.add(stepDTO);
                    }

                    cursor.moveToNext();
                }
            }

            cursor.close();

        return steps;
    }


    public int updateStep(long stepId, String newStepText) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STEP_TEXT, newStepText);
        return database.update(
                TABLE_STEPS,
                values,
                COLUMN_STEP_ID + " = ?",
                new String[]{String.valueOf(stepId)}
        );
    }

    public int deleteStep(long stepId) {
        return database.delete(
                TABLE_STEPS,
                COLUMN_STEP_ID + " = ?",
                new String[]{String.valueOf(stepId)}
        );
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context cxt) {
            super(cxt, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Tabelle für Rezepte erstellen
            String createRecipeTableQuery = "CREATE TABLE " + TABLE_RECIPES + " (" +
                    COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_RECIPE_TITLE + " TEXT, " +
                    COLUMN_RECIPE_PORTIONSMENGE + " TEXT, " +
                    COLUMN_RECIPE_DAUER + " TEXT, " +
                    COLUMN_IS_FAVORITE + " INTEGER)";
            db.execSQL(createRecipeTableQuery);

            // Tabelle für Zutaten erstellen
            String createIngredientTableQuery = "CREATE TABLE " + TABLE_INGREDIENTS + " (" +
                    COLUMN_INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_INGREDIENT_NAME + " TEXT, " +
                    COLUMN_INGREDIENT_UNIT + " TEXT)";
            db.execSQL(createIngredientTableQuery);

            // Tabelle für die Menge der Zutaten erstellen
            String createIngredientQuantityTableQuery = "CREATE TABLE " + TABLE_INGREDIENT_QUANTITY + " (" +
                    COLUMN_INGREDIENT_QUANTITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_INGREDIENT_QUANTITY_AMOUNT + " REAL, " +
                    COLUMN_INGREDIENT_QUANTITY_RECIPE_ID + " INTEGER, " +
                    COLUMN_INGREDIENT_QUANTITY_INGREDIENT_ID + " INTEGER, " +
                    COLUMN_IS_ON_SHOPPING_LIST + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_INGREDIENT_QUANTITY_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + "), " +
                    "FOREIGN KEY (" + COLUMN_INGREDIENT_QUANTITY_INGREDIENT_ID + ") REFERENCES " + TABLE_INGREDIENTS + "(" + COLUMN_INGREDIENT_ID + ")" +
                    ")";
            db.execSQL(createIngredientQuantityTableQuery);



            // Tabelle für Schritte in Rezepten erstellen
            String createStepsTableQuery = "CREATE TABLE " + TABLE_STEPS + " (" +
                    COLUMN_STEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_STEP_TEXT + " BLOB, " +
                    COLUMN_STEP_RECIPE_ID + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_STEP_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + ")" +
                    ")";
            db.execSQL(createStepsTableQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Sichern der vorhandenen Daten, um Datenverlust zu vermeiden
            String backupRecipeTableQuery = "CREATE TABLE RecipeBackup AS SELECT * FROM " + TABLE_RECIPES;
            db.execSQL(backupRecipeTableQuery);

            String backupIngredientTableQuery = "CREATE TABLE IngredientBackup AS SELECT * FROM " + TABLE_INGREDIENTS;
            db.execSQL(backupIngredientTableQuery);

            String backupIngredientQuantityTableQuery = "CREATE TABLE IngredientQuantityBackup AS SELECT * FROM " + TABLE_INGREDIENT_QUANTITY;
            db.execSQL(backupIngredientQuantityTableQuery);

            String backupStepsTableQuery = "CREATE TABLE StepsBackup AS SELECT * FROM " + TABLE_STEPS;
            db.execSQL(backupStepsTableQuery);

            // Löschen aller Tabellen
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENT_QUANTITY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);

            // Erstellen der neuen Tabellen
            onCreate(db);

            // Wiederherstellen der gesicherten Daten
            String restoreRecipeTableQuery = "INSERT INTO " + TABLE_RECIPES +
                    " SELECT * FROM RecipeBackup";
            db.execSQL(restoreRecipeTableQuery);

            String restoreIngredientTableQuery = "INSERT INTO " + TABLE_INGREDIENTS +
                    " SELECT * FROM IngredientBackup";
            db.execSQL(restoreIngredientTableQuery);

            String restoreIngredientQuantityTableQuery = "INSERT INTO " + TABLE_INGREDIENT_QUANTITY +
                    " SELECT * FROM IngredientQuantityBackup";
            db.execSQL(restoreIngredientQuantityTableQuery);

            String restoreStepsTableQuery = "INSERT INTO " + TABLE_STEPS +
                    " SELECT * FROM StepsBackup";
            db.execSQL(restoreStepsTableQuery);

            // Löschen der temporären Sicherungstabellen
            db.execSQL("DROP TABLE IF EXISTS RecipeBackup");
            db.execSQL("DROP TABLE IF EXISTS IngredientBackup");
            db.execSQL("DROP TABLE IF EXISTS IngredientQuantityBackup");
            db.execSQL("DROP TABLE IF EXISTS StepsBackup");
        }
    }
}
