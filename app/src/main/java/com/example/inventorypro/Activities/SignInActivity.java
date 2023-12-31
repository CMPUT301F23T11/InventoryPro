package com.example.inventorypro.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inventorypro.ItemList;
import com.example.inventorypro.R;
import com.example.inventorypro.TagList;
import com.example.inventorypro.User;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * The initial activity where the user must login using their Google account to launch the MainActivity.
 */
public class SignInActivity extends AppCompatActivity {
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private Button signInButton;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signInButton = findViewById(R.id.sign_in_button);
        oneTapClient = Identity.getSignInClient(this);
        mAuth = FirebaseAuth.getInstance();

        if (getIntent().hasExtra(getString(R.string.logout_token)) &&
                getIntent().getExtras().getBoolean(getString(R.string.logout_token))) {
            logout();
        }

        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        ActivityResultLauncher<IntentSenderRequest> activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == Activity.RESULT_OK) {
                            try {
                                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(o.getData());
                                String idToken = credential.getGoogleIdToken();
                                if (idToken !=  null) {
                                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                                    mAuth.signInWithCredential(firebaseCredential)
                                            .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        // Sign in success
                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                        login(user);
                                                    } else {
                                                        // If sign in fails, display a message to the user.
                                                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                                                    }
                                                }
                                            });
                                }
                            } catch (ApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneTapClient.beginSignIn(signInRequest)
                        .addOnSuccessListener(SignInActivity.this, new OnSuccessListener<BeginSignInResult>() {
                            @Override
                            public void onSuccess(BeginSignInResult result) {
                                IntentSenderRequest intentSenderRequest =
                                        new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                                activityResultLauncher.launch(intentSenderRequest);
                            }
                        })
                        .addOnFailureListener(SignInActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String warning = "Please sign into a google account on this phone to continue!";
                                Toast.makeText(getApplicationContext(), warning, Toast.LENGTH_LONG).show();
                                Log.d("Google Sign-In", e.getLocalizedMessage());
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // check if already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            login(currentUser);
        }
    }

    /**
     * Called when the user successfully logs in.
     * Initializes UserPreferences and passed to the MainActivity to complete intitialization using the userID.
     * @param user The firebase user.
     */
    private void login(FirebaseUser user) {
        Toast.makeText(this,"Logging in...",Toast.LENGTH_SHORT).show();

        Intent mainActivityIntent = new Intent(getBaseContext(), MainActivity.class);

        // Now construct user.
        User.createInstance(user.getUid(), user.getEmail(), user.getDisplayName());

        startActivity(mainActivityIntent);
    }

    /**
     * Logs the user out.
     */
    private void logout() {
        mAuth.signOut();
        mAuth = FirebaseAuth.getInstance();
    }
}
