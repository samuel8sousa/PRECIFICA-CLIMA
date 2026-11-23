package samuel.oliveira.precifica_clima.data.remote.viacep

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://viacep.com.br/")
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()

    val viaCepApi: ViaCepApi =
        retrofit.create(ViaCepApi::class.java)
}

