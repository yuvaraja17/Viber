package com.example.demo_project.notification;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (user != null) {
            updateToken(refreshedToken);
        }
    }

    private void updateToken(String refreshedToken) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(refreshedToken);
        ref.child(user.getUid()).setValue(token);
    }
}
