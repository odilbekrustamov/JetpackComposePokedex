package uz.innovation.jetpackcompose.repository

import dagger.hilt.android.scopes.ActivityScoped
import uz.innovation.jetpackcompose.data.remote.PokeApi
import uz.innovation.jetpackcompose.data.remote.responses.Pokemon
import uz.innovation.jetpackcompose.data.remote.responses.PokemonList
import uz.innovation.jetpackcompose.util.Resource
import javax.inject.Inject


@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: PokeApi
) {

    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit, offset)
        } catch(e: Exception) {
            return Resource.Error("An unknown error occured.")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfo(pokemonName)
        } catch(e: Exception) {
            return Resource.Error("An unknown error occured.")
        }
        return Resource.Success(response)
    }
}