package com.example.cmput301f22t13.datalayer;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cmput301f22t13.domainlayer.item.IngredientItem;
import com.example.cmput301f22t13.uilayer.userlogin.ResultListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class LoginDL extends FireBaseDL {
    static private LoginDL loginDL;

    /** Gets or creates current instance of the firebase DL
     * */
    public static LoginDL getInstance(){
        if(loginDL==null){
            loginDL = new LoginDL();
        }
        return loginDL;
    }

    /** Authenticates user SignIn - runs a check against the data in Firestore to see if User has an account made by validating email and password
     * @param: email - Users email
     * @param  password - Users password
     * @param  listener - Interface that helps check is a particular method called in UI works as expected
     * Current status - Completed, one enhancement that is in progress which is to encrypt passwords in Firestore from plaintext -> encrypted value in order to preserve security
     * */
    public void userSignIn(String email, String password, ResultListener listener){
        //User signs in email and password - adding onComplete to signify valid task completion
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    listener.onSuccess();
                    Log.d("TAG", "User authentication is successful");
                } else {
                    listener.onFailure(new Exception());
                    Log.d("TAG", "User authentication is unsuccessful");
                }
            }
        });
    }

    /** Gives user the option to reset their password if they have forgot it. Uses dialog box to send capture new user email and send a recovery email to this specified email
     * @param  email - User email address to which forgot password procedure can be initialized
     * @param  listener - Interface that helps check if a particular method called in UI works as expected
     * Current status - Completed
     * */
    public void userForgotPassword(String email, ResultListener listener) {
        //user inputs email for password reset prompt to be sent
        auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("TAG", "Sent email for forgot password");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "Error in forget password");
            }
        });
    }

    /** Checks if user logging into the app is a "pre exising user" - meaning they are on the Firestore database. If so it automatically logs them into the app
     * @param  listener - Interface that helps check if a particular method called in UI works as expected
     * Current status - Completed
     * */
    public void userReturning(ResultListener listener) {
        //checks if current user is an already existing user
        if(auth.getCurrentUser()!=null) {
            Log.d("TAG", "This is a returning user");
        }
        else {
            Log.d("TAG", "Not a returning user");
        }
    }


    /** If a User is a new user this method registers them into the system using their name,email and password
     * @param email - User email for registering user
     * @param password - User password for registering user
     * @param name - Users name for registering user
     * @param  listener - Interface that helps check if a particular method called in UI works as expected
     * Current status - Completed, one enhancement that is in progress which is to encrypt passwords in Firestore from plaintext -> encrypted value in order to preserve security
     * */
    public void userRegister(String email, String password, String name, ResultListener listener) {
        //User enters email and password for registration - adding onComplete to signify valid task completion
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("TAG", "User created");
                    //Passing user id to store unique documents for each user in FireStore
                    String userId = auth.getCurrentUser().getUid();
                    DocumentReference documentReference = fstore.collection("Users").document(userId);

                    //Storing user name and email in Firestore
                    Map<String,Object> user = new HashMap<>();
                    user.put("Name" , name);
                    user.put("Email", email);

                    //Pushing changes to Firestore - adding onSuccessListener & onFailureListener to signify valid task completion
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            listener.onSuccess();
                            Log.d("TAG", "onSuccess: user profile is created in firebase with uid " + userId);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "User profile not created" );
                            listener.onFailure(e);
                        }
                    });
                }
            }
        });
    }

    /** Logs the user out of the app onto the Login page
     * @param  listener - Interface that helps check if a particular method called in UI works as expected
     * Current status - Completed, needs to be implemented on the UI layer
     * */
    public void userLogout(ResultListener listener){
        auth.signOut();
    }


}
