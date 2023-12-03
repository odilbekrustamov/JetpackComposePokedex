package uz.innovation.jetpackcompose.pokemonlist

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.innovation.jetpackcompose.data.models.PokedexListEntry
import uz.innovation.jetpackcompose.repository.PokemonRepository
import uz.innovation.jetpackcompose.util.Constants.PAGE_SIZE
import uz.innovation.jetpackcompose.util.Resource
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private var curPage = 0

    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    private var cashePolemonList = listOf<PokedexListEntry>()
    private var isSearchSTarting = true
    var isSearching = mutableStateOf(false)

    init {
        loadPokemonPaginated()
    }

    fun searchPokemonList(quary: String) {
        val listToSearch = if (isSearchSTarting) pokemonList.value else cashePolemonList

        viewModelScope.launch(Dispatchers.Default) {
            if (quary.isEmpty()){
                pokemonList.value = cashePolemonList
                isSearching.value = false
                isSearchSTarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.pokemonName.contains(quary.trim(), ignoreCase = true) ||
                        it.number.toString() == quary.trim()
            }
            if (isSearchSTarting){
                cashePolemonList = pokemonList.value
                isSearchSTarting = false
            }
            pokemonList.value = results
            isSearching.value = true
        }
    }

    fun loadPokemonPaginated() {
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.getPokemonList(PAGE_SIZE, PAGE_SIZE * curPage)
            when (result) {
                is Resource.Success -> {
                    endReached.value = curPage * PAGE_SIZE >= result.data!!.count!!
                    val pokedexEntries = result.data.results.mapIndexed { index, entry ->
                        val number = if (entry.url!!.endsWith("/")) {
                            entry.url!!.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url!!.takeLastWhile { it.isDigit() }
                        }
                        val url =
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokedexListEntry(entry.name!!.capitalize(Locale.ROOT), url, number.toInt())
                    }
                    curPage++
                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokedexEntries
                }

                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }

                else -> {}
            }
        }
    }

    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}