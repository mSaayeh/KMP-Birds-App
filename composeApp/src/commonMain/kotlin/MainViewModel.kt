import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * @author Mohannad El-Sayeh email(eng.mohannadelsayeh@gmail.com)
 * @date 27/12/2023
 */

data class BirdsUiState(
    val images: List<BirdImage> = emptyList(),
    val selectedCategory: String? = null,
) {
    val categories = images.map { it.category }.toSet()
    val selectedImages = images.filter { it.category == selectedCategory }
}

class MainViewModel : ViewModel() {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    private val _uiState = MutableStateFlow(BirdsUiState())
    val uiState = _uiState.asStateFlow()

    fun updateImages() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    images = getImages()
                )
            }
        }
    }

    override fun onCleared() {
        httpClient.close()
    }

    private suspend fun getImages() =
        httpClient.get("https://sebi.io/demo-image-api/pictures.json")
            .body<List<BirdImage>>()

    fun updateSelectedCategory(category: String) {
        var newCategory: String? = category
        if (category == _uiState.value.selectedCategory) {
            newCategory = null
        }
        _uiState.update {
            it.copy(
                selectedCategory = newCategory
            )
        }
    }


}