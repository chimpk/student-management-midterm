package com.example.studentmgmt.data;

import com.example.studentmgmt.model.AppUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Firebase implementation of AuthRepository.
 * Authenticates against Firebase Auth and validates user state against Firestore collection `users/{uid}`
 * using UserDocumentValidator to fail closed on missing or corrupt schema fields.
 */
public class FirebaseAuthRepository implements AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final UsernameAuthResolver authResolver;
    private AppUser currentUser;

    public FirebaseAuthRepository() {
        this(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(), new DefaultUsernameAuthResolver());
    }

    public FirebaseAuthRepository(FirebaseAuth firebaseAuth,
                                  FirebaseFirestore firestore,
                                  UsernameAuthResolver authResolver) {
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;
        this.authResolver = authResolver != null ? authResolver : new DefaultUsernameAuthResolver();
    }

    @Override
    public void signIn(String identifier, String password, AuthCallback callback) {
        if (callback == null) return;

        if (identifier == null || identifier.trim().isEmpty() || password == null || password.isEmpty()) {
            callback.onError(ERROR_INVALID_CREDENTIALS);
            return;
        }

        String resolvedEmail = authResolver.resolveAuthEmail(identifier);
        if (resolvedEmail == null) {
            // Safe failure: do not disclose identifier non-existence or lack of backend alias mapping
            callback.onError(ERROR_INVALID_CREDENTIALS);
            return;
        }

        if (firebaseAuth == null || firestore == null) {
            callback.onError(ERROR_NETWORK);
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(resolvedEmail, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        callback.onError(ERROR_INVALID_CREDENTIALS);
                        return;
                    }
                    String uid = firebaseUser.getUid();
                    fetchAndValidateUserDocument(uid, callback);
                })
                .addOnFailureListener(e -> {
                    // Safe failure: never disclose whether account exists
                    callback.onError(ERROR_INVALID_CREDENTIALS);
                });
    }

    /**
     * Reads user document from Firestore and applies fail-closed validation.
     */
    public void fetchAndValidateUserDocument(String uid, AuthCallback callback) {
        firestore.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    processUserDocument(uid, documentSnapshot, callback);
                })
                .addOnFailureListener(e -> {
                    signOut();
                    callback.onError(ERROR_INVALID_CREDENTIALS);
                });
    }

    /**
     * Processes document snapshot into AppUser using UserDocumentValidator.
     * Enforces fail-closed rules: missing required fields or unknown status/role are rejected.
     */
    public void processUserDocument(String uid, DocumentSnapshot documentSnapshot, AuthCallback callback) {
        if (documentSnapshot == null || !documentSnapshot.exists() || documentSnapshot.getData() == null) {
            signOut();
            callback.onError(ERROR_INVALID_CREDENTIALS);
            return;
        }

        UserDocumentValidator.Result result = UserDocumentValidator.validate(uid, documentSnapshot.getData());

        if (result.isLocked()) {
            signOut();
            callback.onError(ERROR_ACCOUNT_LOCKED);
            return;
        }

        if (!result.isValid() || result.getUser() == null) {
            signOut();
            callback.onError(ERROR_INVALID_CREDENTIALS);
            return;
        }

        AppUser user = result.getUser();
        this.currentUser = user;
        callback.onSuccess(user);
    }

    @Override
    public void signOut() {
        this.currentUser = null;
        if (firebaseAuth != null) {
            try {
                firebaseAuth.signOut();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public AppUser getCurrentUser() {
        return currentUser;
    }

    @Override
    public boolean isSignedIn() {
        return currentUser != null && firebaseAuth != null && firebaseAuth.getCurrentUser() != null;
    }
}
