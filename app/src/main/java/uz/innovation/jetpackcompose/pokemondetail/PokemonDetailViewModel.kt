package uz.innovation.jetpackcompose.pokemondetail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.innovation.jetpackcompose.data.remote.responses.Pokemon
import uz.innovation.jetpackcompose.repository.PokemonRepository
import uz.innovation.jetpackcompose.util.Resource
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
): ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> =
        repository.getPokemonInfo(pokemonName)
}