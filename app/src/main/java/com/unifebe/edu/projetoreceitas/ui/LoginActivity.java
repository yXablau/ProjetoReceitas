package com.unifebe.edu.projetoreceitas.ui;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.unifebe.edu.projetoreceitas.R;
import com.unifebe.edu.projetoreceitas.model.User;
import com.unifebe.edu.projetoreceitas.DAO.UserDAO;

/**
 * Activity de Login, que permite o usuário se autenticar tanto online (Firebase Auth)
 * quanto offline (base SQLite local).
 * Caso o dispositivo não tenha conexão, é verificado o login local.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText; // Campos para email e senha
    private FirebaseAuth mAuth; // Instância do Firebase Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerTextView = findViewById(R.id.registerTextView);

        // Configura evento do botão login para tentar autenticar
        loginButton.setOnClickListener(v -> loginUser());

        // Ao clicar em registrar, abre a tela de registro
        registerTextView.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    /**
     * Tenta realizar o login do usuário.
     * Se estiver online, autentica via Firebase.
     * Caso contrário, tenta autenticar localmente pelo SQLite.
     */
    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Valida se os campos foram preenchidos
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isConnected()) {
            // Login online via Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Login", "Login online bem-sucedido");

                            // Salva credenciais localmente para permitir login offline depois
                            UserDAO userDAO = new UserDAO(this);
                            userDAO.insertUser(new User(email, password));

                            Toast.makeText(this, "Login online com sucesso", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Log.e("Login", "Erro ao logar", task.getException());
                            Toast.makeText(this, "Erro ao logar: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            // Login offline usando dados salvos localmente
            UserDAO userDAO = new UserDAO(this);
            User localUser = userDAO.getUserByEmail(email);

            if (localUser != null && localUser.password.equals(password)) {
                Log.d("Login", "Login offline bem-sucedido");
                Toast.makeText(this, "Login offline realizado", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Credenciais inválidas para login offline", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Verifica se o dispositivo está conectado à internet.
     * @return true se conectado, false caso contrário.
     */
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}
