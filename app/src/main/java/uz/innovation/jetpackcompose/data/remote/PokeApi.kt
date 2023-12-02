package uz.innovation.jetpackcompose.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import uz.innovation.jetpackcompose.data.remote.responses.Pokemon
import uz.innovation.jetpackcompose.data.remote.responses.PokemonList

interface PokeApi {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemonInfo(
        @Path("name") name: String
    ): Pokemon
}