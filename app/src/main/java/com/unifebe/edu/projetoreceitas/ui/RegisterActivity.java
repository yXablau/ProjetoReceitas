package com.unifebe.edu.projetoreceitas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.unifebe.edu.projetoreceitas.R;
import com.unifebe.edu.projetoreceitas.model.User;
import com.unifebe.edu.projetoreceitas.DAO.UserDAO;

/**
 * Activity responsável pelo registro de novos usuários no aplicativo.
 *
 * Permite que o usuário crie uma conta usando email e senha através do Firebase Authentication.
 * Também salva localmente os dados do usuário para possibilitar login offline.
 * Realiza validação simples nos campos (não vazios e senha com pelo menos 6 caracteres).
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;

    /**
     * Método chamado na criação da Activity.
     * Inicializa as views e configura o listener do botão de registro.
     *
     * @param savedInstanceState Bundle com estado salvo da Activity (se houver)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> registerUser());
    }

    /**
     * Executa o processo de registro do usuário.
     * Verifica se os campos estão preenchidos corretamente,
     * cria o usuário no Firebase e salva os dados localmente no SQLite.
     * Exibe mensagens Toast com o status da operação.
     */
    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validação dos campos
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cria usuário no Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Register", "Registro bem-sucedido");
                        Toast.makeText(this, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show();

                        // Salva localmente para login offline
                        UserDAO userDAO = new UserDAO(this);
                        userDAO.insertUser(new User(email, password));

                        // Redireciona para tela de login
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    } else {
                        Log.e("Register", "Erro ao registrar", task.getException());
                        Toast.makeText(this, "Erro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
