package com.example.extrocontru;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private SignInButton googleSignInButton;
    private MaterialTextView signUpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Inicializar vistas
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        signUpText = findViewById(R.id.signUpText);

        // Configurar el listener para el botón de inicio de sesión
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Por favor, introduce el correo y la contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            signInWithEmail(email, password);
        });

        // Configurar el listener para el botón de inicio de sesión con Google
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        // Configurar el listener para el texto de registro
        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(MainActivity.this, "Fallo al iniciar sesión con Google", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToDatabase(user);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Fallo en la autenticación con Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithEmail(String email, String password) {
        if (email.equals("admin") && password.equals("admin")) {
            // Si las credenciales son "admin admin", redirigir a AdminToolActivity
            Intent intent = new Intent(MainActivity.this, AdminToolActivity.class);
            startActivity(intent);
            finish();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                loadUserProfile(user.getUid());
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Cuenta no registrada", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void saveUserToDatabase(FirebaseUser user) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Guardar datos del usuario en la base de datos
                    User newUser = new User(
                            user.getUid(),
                            user.getDisplayName(),
                            user.getEmail(),
                            "",  // phoneNumber (inicialmente vacío)
                            "",  // shippingAddress1 (inicialmente vacío)
                            ""   // shippingAddress2 (inicialmente vacío)
                    );
                    userRef.setValue(newUser);
                }
                // Cargar el perfil del usuario
                loadUserProfile(user.getUid());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error al guardar los datos del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadUserProfile(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String displayName = snapshot.child("name").getValue(String.class);

                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("displayName", displayName);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Usuario no encontrado en la base de datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error al cargar el perfil del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Clase User para representar la estructura del usuario
    public static class User {
        public String userId;
        public String name;
        public String email;
        public String phoneNumber;
        public String shippingAddress1;
        public String shippingAddress2;

        public User() {
            // Constructor vacío requerido para Firebase
        }
        public User(String userId, String name, String email, String phoneNumber, String shippingAddress1, String shippingAddress2) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.shippingAddress1 = shippingAddress1;
            this.shippingAddress2 = shippingAddress2;
        }
    }
}
