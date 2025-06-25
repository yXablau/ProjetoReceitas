package com.unifebe.edu.projetoreceitas.api;

import com.unifebe.edu.projetoreceitas.model.MealResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface usada pelo Retrofit para definir os endpoints da API do TheMealDB.
 * Este serviço realiza buscas por nome de receita.
 */
public interface MealApiService {

    /**
     * Endpoint que busca receitas pelo nome.
     * Exemplo de chamada real: https://www.themealdb.com/api/json/v1/1/search.php?s=Lasagne
     *
     * @param mealName Nome da receita a ser buscada
     * @return Um objeto MealResponse contendo uma receita (meals)
     */
    @GET("search.php")
    Call<MealResponse> searchMeals(@Query("s") String mealName);
}
