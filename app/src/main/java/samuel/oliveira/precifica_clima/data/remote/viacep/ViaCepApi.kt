package samuel.oliveira.precifica_clima.data.remote.viacep

import retrofit2.http.GET
import retrofit2.http.Path

interface ViaCepApi {

    @GET("ws/{cep}/json/")
    suspend fun consultarCep(
        @Path("cep") cep: String
    ): ViaCepResponse
}


