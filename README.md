# Projeto Receitas (Android)

Este é um aplicativo Android simples para cadastro, busca e sincronização de receitas culinárias. O app funciona offline com armazenamento local em SQLite e sincroniza os dados com o Firebase quando estiver online. Também é possível buscar receitas externas pela API [TheMealDB](https://www.themealdb.com/).

## Funcionalidades

- Cadastro de usuário com Firebase Authentication
- Login online (Firebase) e offline (SQLite)
- Adição de receitas com sincronização Firebase + armazenamento local
- Listagem, visualização e exclusão de receitas
- Busca de receitas externas por nome via API TheMealDB
- Integração com SQLite para uso offline

---

## Estrutura do Projeto

### Activities

- **SplashActivity.java**: Tela de splash (inicial). Pode redirecionar para Login ou Main.
- **LoginActivity.java**: Tela de login. Permite login online com Firebase ou offline via SQLite.
- **RegisterActivity.java**: Tela de cadastro de novo usuário com Firebase.
- **MainActivity.java**: Tela principal. Lista receitas, permite excluir e acessar detalhes.
- **AddRecipeActivity.java**: Tela de adição de novas receitas.
- **BuscarReceitasActivity.java**: Busca receitas na API externa TheMealDB.
- **RecipeDetailActivity.java**: Exibe os detalhes completos de uma receita.

### DAO (Data Access Object)

- **UserDAO.java**: Manipula os dados de usuário localmente (SQLite).
- **RecipeDAO.java**: Manipula as receitas localmente (SQLite). Inclui sincronização com Firebase.

### Adapter

- **RecipeAdapter.java**: Adapter do RecyclerView da MainActivity. Permite visualizar e excluir receitas.

### API (Retrofit)

- **MealApiService.java**: Interface Retrofit para consumir a TheMealDB.
- **RetrofitClient.java**: Cliente Retrofit singleton.

### Modelos

- **User.java**: Representa um usuário.
- **Recipe.java**: Representa uma receita local.
- **Meal.java** e **MealResponse.java**: Representam as receitas da API externa.

---

## Tecnologias Utilizadas

- **Java (Android SDK)**
- **SQLite** (armazenamento local)
- **Firebase Authentication** (login)
- **Firebase Realtime Database** (sincronização de receitas)
- **Retrofit2 + Gson** (requisições HTTP para TheMealDB)

---

## Como Executar

1. Clone o repositório e abra no Android Studio
2. Configure um projeto Firebase com Authentication e Realtime Database
3. Ative o login com email/senha no Firebase Console
4. Substitua os valores do `google-services.json`
5. Execute em um emulador ou dispositivo físico Android

---

## Exemplos de Receita para Teste (API externa)

- Arrabiata
- Pizza
- Beef Wellington
- Pad Thai
- Tandoori Chicken

---

## Autor

Pedro Oliveira – UNIFEBE – Curso de Sistemas de Informação  
Rafael Araujo – UNIFEBE – Curso de Sistemas de Informação  
Bruno Boeck – UNIFEBE – Curso de Sistemas de Informação

Este projeto foi desenvolvido com o objetivo de prática e aprendizado na disciplina de desenvolvimento de aplicativos Android, com foco em integração offline/online e APIs REST.

---
