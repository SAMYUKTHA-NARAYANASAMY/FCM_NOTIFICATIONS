package com.example.demo.service;

import com.example.demo.model.User;
import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.UUID;

@Service
public class UserService {

    private final DatabaseReference databaseReference;

    public UserService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void saveUser(User user) {
        String userId = user.getUserId();
       if (userId == null || userId.isEmpty()) {
            userId = UUID.randomUUID().toString(); // Generate a UUID if userId is not provided
            user.setUserId(userId);
        }

        final String finalUserId = userId; // Declare as final

        DatabaseReference usersRef = databaseReference.child("users").child(finalUserId); // Use finalUserId

        CompletableFuture<Void> future = new CompletableFuture<>();//success or failure

        usersRef.setValue(user, (error, ref) -> {
            if (error == null) {
                future.complete(null);
            } else {
                future.completeExceptionally(error.toException());
            }
        });

        future.whenComplete((result, throwable) -> {
            if (throwable == null) {
                System.out.println("User details saved in Firebase RTDB under user: " + finalUserId);
            } else {
                System.err.println("Failed to save user details: " + throwable.getMessage());
            }
        });
    }

    public void getUserById(String userId, UserFetchCallback callback) {
        DatabaseReference userRef = databaseReference.child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {//checks if the data exists at that location
                    User user = dataSnapshot.getValue(User.class); 
                    callback.onUserFetched(user);//converts the retrieved data into a User object
                } else {
                    callback.onUserFetched(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error fetching user: " + databaseError.getMessage());
                callback.onUserFetched(null);
            }
        });
    }
}
