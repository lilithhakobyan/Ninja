package com.example.ninja;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText textEmail, textPass, confirmPass, textUsername;
    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        textUsername = findViewById(R.id.textUsername);
        textEmail = findViewById(R.id.textEmail);
        textPass = findViewById(R.id.textPass);
        confirmPass = findViewById(R.id.confrimPass);
        Button btnRegister = findViewById(R.id.btnRegister);
        ImageView btnGoogle = findViewById(R.id.btnGoogle);
        ImageView backBtn = findViewById(R.id.backBtn);
        TextView textView = findViewById(R.id.LogNow);
        ImageView showPass1 = findViewById(R.id.showPass1);
        ImageView showPass2 = findViewById(R.id.showPass2);

        backBtn.setOnClickListener(v -> finish());
        btnRegister.setOnClickListener(v -> registerUser());
        setupGoogleSignIn();
        btnGoogle.setOnClickListener(v -> signInWithGoogle());

        setupPasswordToggle(showPass1, textPass);
        setupPasswordToggle(showPass2, confirmPass);

        SpannableString spannable = new SpannableString("Already have an account? Login Now");
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4A5DF3")),
                spannable.toString().indexOf("Login Now"), spannable.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
        textView.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }

    private void setupPasswordToggle(ImageView toggle, EditText passwordField) {
        toggle.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    return true;
            }
            return false;
        });
    }

    private void setupGoogleSignIn() {
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId("YOUR_GOOGLE_C530152070036-iqcni1knrtf917t9pj34b9fsapq6h172.apps.googleusercontent.comLIENT_ID")
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
    }

    private void signInWithGoogle() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        googleSignInLauncher.launch(new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build());
                    } catch (Exception e) {
                        Log.e(TAG, "Google Sign-In Error: " + e.getMessage());
                    }
                })
                .addOnFailureListener(this, e -> Log.e(TAG, "Google Sign-In Failed: " + e.getMessage()));
    }

    private final ActivityResultLauncher<IntentSenderRequest> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    try {
                        SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                        String idToken = credential.getGoogleIdToken();
                        if (idToken != null) {
                            firebaseAuthWithGoogle(idToken);
                        }
                    } catch (ApiException e) {
                        Log.e(TAG, "Google Sign-In Failed: " + e.getMessage());
                    }
                }
            });

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserData(user.getUid(), user.getDisplayName(), user.getEmail());
                        }
                    } else {
                        Log.e(TAG, "Authentication Failed: " + task.getException().getMessage());
                    }
                });
    }

    private void registerUser() {
        String username = textUsername.getText().toString().trim();
        String email = textEmail.getText().toString().trim();
        String password = textPass.getText().toString().trim();
        String confirmPassword = confirmPass.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || !password.equals(confirmPassword) || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || password.length() < 6) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            Toast.makeText(this, "Registration successful. Verify your email!", Toast.LENGTH_LONG).show();
                                            mAuth.signOut();
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            saveUserData(user.getUid(), username, email);
                        }
                    } else {
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserData(String userId, String username, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("email", email);

        db.collection("users").document(userId).set(userMap)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User data successfully saved to Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save user data to Firestore: " + e.getMessage()));
    }
}
