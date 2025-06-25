package com.unifebe.edu.projetoreceitas.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.unifebe.edu.projetoreceitas.R;
import com.unifebe.edu.projetoreceitas.api.MealApiService;
import com.unifebe.edu.projetoreceitas.api.RetrofitClient;
import com.unifebe.edu.projetoreceitas.model.Meal;
import com.unifebe.edu.projetoreceitas.model.MealResponse;

/**
 * Activity para buscar receitas pela API TheMealDB.
 * Permite digitar o nome da receita, buscar na API e exibir ingredientes e modo de preparo.
 */
public class BuscarReceitasActivity extends AppCompatActivity {

    private EditText edtNomeReceita; // Campo para digitar o nome da receita
    private Button btnBuscar, btnVoltar; // Botões para buscar e voltar à tela anterior
    private TextView txtResultado; // TextView para mostrar o resultado da busca

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_receitas);

        // Inicializa as views a partir do layout
        edtNomeReceita = findViewById(R.id.edtNomeReceita);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnVoltar = findViewById(R.id.btnVoltar);
        txtResultado = findViewById(R.id.txtResultado);

        // Define ação do botão Buscar
        btnBuscar.setOnClickListener(v -> {
            String nome = edtNomeReceita.getText().toString().trim();
            if (nome.isEmpty()) {
                txtResultado.setText("Digite o nome de uma receita.");
            } else {
                buscarReceitaPorNome(nome);
            }
        });

        // Define ação do botão Voltar (fecha a Activity atual)
        btnVoltar.setOnClickListener(v -> finish());
    }

    /**
     * Realiza a busca da receita pela API usando Retrofit.
     * @param nome Nome da receita digitado pelo usuário.
     */
    private void buscarReceitaPorNome(String nome) {
        MealApiService api = RetrofitClient.getClient().create(MealApiService.class);
        api.searchMeals(nome).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().meals != null) {
                    // Pega a primeira receita da lista retornada
                    Meal meal = response.body().meals.get(0);
                    // Formata os detalhes da receita para exibição
                    String resultado = formatarReceita(meal);
                    txtResultado.setText(resultado);
                } else {
                    txtResultado.setText("Receita não encontrada.");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                txtResultado.setText("Erro ao buscar: " + t.getMessage());
            }
        });
    }

    /**
     * Formata a receita retornada para string, exibindo ingredientes e modo de preparo.
     * Usa reflexão para iterar pelos campos strIngredient1..20 e strMeasure1..20.
     * @param meal Objeto Meal com dados da receita.
     * @return String formatada com ingredientes e instruções.
     */
    private String formatarReceita(Meal meal) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ingredientes:\n");

        for (int i = 1; i <= 20; i++) {
            try {
                Field ingField = Meal.class.getField("strIngredient" + i);
                Field medField = Meal.class.getField("strMeasure" + i);
                String ing = (String) ingField.get(meal);
                String med = (String) medField.get(meal);

                if (ing != null && !ing.trim().isEmpty()) {
                    sb.append("- ").append(med != null ? med : "").append(" ").append(ing).append("\n");
                }
            } catch (Exception e) {
                // Ignora caso algum campo não exista (proteção via reflexão)
            }
        }

        sb.append("\nModo de preparo:\n");
        sb.append(meal.strInstructions != null ? meal.strInstructions : "Não disponível.");
        return sb.toString();
    }
}
