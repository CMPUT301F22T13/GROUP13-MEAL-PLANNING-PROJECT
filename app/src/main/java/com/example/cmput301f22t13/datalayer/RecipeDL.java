package com.example.cmput301f22t13.datalayer;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cmput301f22t13.domainlayer.item.IngredientItem;
import com.example.cmput301f22t13.domainlayer.item.RecipeItem;
import com.example.cmput301f22t13.uilayer.userlogin.ResultListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/** Singelton class that implements the recipe storage functionality
 * @extends: FirebaseDL
 * */
public class RecipeDL extends FireBaseDL {
    static private RecipeDL recipeDL;

    /** Listens for changes to the arraylist
     * */
    public static ResultListener listener;

    /** Stores recipes
     * */
    public static ArrayList<RecipeItem> recipeStorage = new ArrayList<RecipeItem>();

    private ListenerRegistration registration;


    public void deRegisterListener(){
        registration.remove();
    }

    /** Gets or creates current instance of the recipe DL
     * */
    public static RecipeDL getInstance(){
        if(recipeDL==null){
            recipeDL = new RecipeDL();
        }
        return recipeDL;
    }

    /** Constructor calls populateStorageOnStartup
     *  */
    public RecipeDL() {
        populateStorageOnStartup();
    }

    /** populateRecipesOnStartup - called when first instance of RecipeDL is made
     * listens for db changes and updates the recipe storage accordingly
     * */
    private void populateStorageOnStartup() {
        registration = fstore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .collection("Recipe Storage")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                recipeStorage.clear();
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String hash = doc.getId();
                        String title = doc.getString("Title");
                        int prep = doc.getDouble("Prep Time").intValue();
                        int servings = doc.getDouble("Servings").intValue();
                        String category = doc.getString("Category");
                        String comments = doc.getString("Comments");
                        String photo = doc.getString("Photo");

                        RecipeItem r = new RecipeItem();

                        r.setTitle(title);
                        r.setHashId(hash);
                        r.setPrepTime(prep);
                        r.setServings(servings);
                        r.setCategory(category);
                        r.setComments(comments);
                        r.setPhoto(photo);


                        fstore.collection("Users")
                                .document(auth.getCurrentUser().getUid())
                                .collection("Recipe Storage").document(r.getHashId())
                                .collection("Ingredients").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                            String hash = doc.getId();
                                            String name = doc.getString("Name");
                                            String description = (String) doc.getData().get("Description");
                                            String unit = (String) doc.getData().get("Unit");
                                            String category = (String) doc.getData().get("Category");
                                            String photo = doc.getString("Photo");
                                            Double amount = 0.0;
                                            try {
                                                amount = (Double) doc.getDouble("Amount");
                                            } catch (Exception e) {
                                            }


                                            IngredientItem i = new IngredientItem();
                                            i.setName(name);
                                            i.setDescription(description);
                                            i.setAmount(amount);
                                            i.setUnit(unit);
                                            i.setCategory(category);
                                            i.setHashId(hash);
                                            i.setPhoto(photo);
                                            r.addIngredient(i);
                                        }
                                    }
                                });

                        recipeStorage.add(r);
                    }
                }
                if (listener != null) {
                    listener.onSuccess();
                }
            }
        });
    }

    /** Add/Edit item received from Domain Layer into FireStore Recipe storage collection
     * @param: Recipe item - item to add or edit
     * */
    public void firebaseAddEdit(RecipeItem item) {
        Map<String, Object> recipe = getRecipeMap(item);
        //Storing data in Hashmap to correct location in Firebase using uniqueKey as document reference
        DocumentReference recipeStorage = fstore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .collection("Recipe Storage")
                .document(item.getHashId());

        ArrayList<IngredientItem> ingredientItems = item.getIngredients();

        for (IngredientItem i: ingredientItems) {
            Map<String, Object> ingredient = IngredientDL.getIngredientMap(i);

            DocumentReference ingredientStorage = fstore.collection("Users")
                    .document(auth.getCurrentUser().getUid())
                    .collection("Recipe Storage")
                    .document(item.getHashId())
                    .collection("Ingredients")
                    .document(i.getHashId());

            addToFireBase(ingredient, ingredientStorage);
        }

        addToFireBase(recipe, recipeStorage);
    }

    /** Creates a map for adding data to firebase
     * @param: RecipeItem item - item to  add
     *
     * */
    public static Map<String, Object>  getRecipeMap(RecipeItem item) {
        //Initializing and storing data value from passed in Recipe item
        String recipe_title = item.getTitle();
        int recipe_prepTime = item.getPrepTime();
        int recipe_servings = item.getServings();
        String recipe_category = item.getCategory();
        String recipe_comments = item.getComments();
        String recipe_photo = item.getPhoto();

        //Storing data collected from object in a HashMap
        Map<String, Object> recipe = new HashMap<>();
        recipe.put("Title", recipe_title);
        recipe.put("Prep Time", recipe_prepTime);
        recipe.put("Servings", recipe_servings);
        recipe.put("Category", recipe_category);
        recipe.put("Comments", recipe_comments);
        recipe.put("Photo", recipe_photo);

        return recipe;
    }

    /** Deletes data from Firestore for a particular passed in Recipe item - by doing so the recipe document is deleted from Firestore
     *  @param item - Recipe item to be deleted from Firestore
     * */
    public void firebaseDelete(RecipeItem item){
        //Referencing wanted document from correct location in Firestore database
        DocumentReference deleteIngredient = fstore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .collection("Recipe Storage")
                .document(item.getHashId());

        deleteFromFireBase(deleteIngredient);
    }

    /** Deletes data from Firestore for a particular passed in Recipe item
     *  @param recipeItem - Recipe item to with ingredient to delete
     *  @param ingredientItem - Ingredient item to delete
     * */
    public void deleteIngredient(RecipeItem recipeItem, IngredientItem ingredientItem) {
        DocumentReference deleteIngredient = fstore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .collection("Recipe Storage")
                .document(recipeItem.getHashId())
                .collection("Ingredients")
                .document(ingredientItem.getHashId());

        deleteFromFireBase(deleteIngredient);
    }

    /** Getter for Recipe storage
     * @Returns: ArrayList<RecipeItems> representing recipes in storage
     * */
    public ArrayList<RecipeItem> getStorage() {
        return recipeStorage;
    }

}
