package com.example.appclimaproyecto

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var buttonUser: Button
    private lateinit var buttonPost: Button
    private var currentUserIndex = 0 // Índice del usuario actual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.usertext)
        buttonUser = findViewById(R.id.buttonUser)
        buttonPost = findViewById(R.id.buttonPost)

        //Costruimos la instancia retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //Creamos una instacia para hacer las solcitudes HTTP a la pagina web
        val apiService = retrofit.create(ApiService::class.java)

        // Inicialmente, cargar el primer usuario
        loadUser(apiService, currentUserIndex)

        // Configurar el manejador de clics para el botón de usuario
        buttonUser.setOnClickListener {
            // Incrementar el índice del usuario y cargar el siguiente usuario
            currentUserIndex++
            loadUser(apiService, currentUserIndex)
        }

        // Configurar el manejador de clics para el botón de post
        buttonPost.setOnClickListener {
            // Cargar un nuevo post
            loadPost(apiService)
        }
    }

    //Realizamos la solicitud HTTP GET
    private fun loadUser(apiService: ApiService, index: Int) {
        val call = apiService.getUsers() //Se crea una solicitud HTTP GET utilizando el método getUsers()
        call.enqueue(object : Callback<List<User>> { //Manejar las respuestas exitosas y las fallidas con enqueue
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) { //Verifica si la respuesta HTTP fue exitosa
                    val users = response.body() //Obtenmos la lista de usuarios
                    if (users != null && users.isNotEmpty()) {
                        val userIndex = index % users.size // Evitar desbordamiento del índice
                        val user = users[userIndex]  //Le asignamos un indice correcto para mostrar ese usuario
                        textView.text = "ID: ${user.id}\nName: ${user.name}\nEmail: ${user.email}" //actualizamos el text view
                    }
                } else {
                    textView.text = "Error al obtener datos"
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                textView.text = "Error al conectarse a la API"
            }
        })
    }

    private fun loadPost(apiService: ApiService) {
        val call = apiService.getPosts()
        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    val posts = response.body()
                    if (posts != null && posts.isNotEmpty()) {
                        val randomPost = posts.random() // Seleccionar un post aleatorio
                        textView.text = "Post ID: ${randomPost.id}\nTitle: ${randomPost.title}\nBody: ${randomPost.body}"
                    }
                } else {
                    textView.text = "Error al obtener datos del post"
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                textView.text = "Error al conectarse a la API de posts"
            }
        })
    }
}

//Creamos los objetos a partir de los datos JSON
//Estructuramos la informacion que obtenemos de la API web
//Representar al objeto usuario
data class User(
    val id: Int,
    val name: String,
    val email: String
)

//Representar al objeto post
data class Post(
    val id: Int,
    val title: String,
    val body: String
)

//Difinimos los metodos que corresponden a rutas de una API WEB
interface ApiService {
    //Solicitud a la ruta users
    @GET("users")
    fun getUsers(): Call<List<User>> //Devuelve una lista de objetos User

    //Solicitud a la ruta posts
    @GET("posts")
    fun getPosts(): Call<List<Post>> //Devuelve una lista de objetos post
}




