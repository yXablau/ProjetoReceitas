package com.unifebe.edu.projetoreceitas.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Classe responsável por configurar e fornecer uma instância singleton do Retrofit.
 * Utiliza Gson como conversor de JSON.
 */
public class RetrofitClient {

    // Instância única (singleton) do Retrofit
    private static Retrofit retrofit;

    /**
     * Retorna uma instância do Retrofit configurada com a URL base da API e conversor JSON.
     * @return Retrofit configurado para consumir a API do TheMealDB
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    // URL base da API do TheMealDB (deve terminar com "/")
                    .baseUrl("https://www.themealdb.com/api/json/v1/1/")
                    // Adiciona o conversor Gson para transformar JSON em objetos Java
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
