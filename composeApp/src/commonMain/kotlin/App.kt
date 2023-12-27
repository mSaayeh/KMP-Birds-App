import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun BirdAppTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = MaterialTheme.colors.copy(primary = Color.Black, primaryVariant = Color.Green),
        shapes = MaterialTheme.shapes.copy(
            small = RoundedCornerShape(0.dp),
            medium = RoundedCornerShape(0.dp),
            large = RoundedCornerShape(0.dp),
        ),
        content = content
    )
}

@Composable
fun App() {
    BirdAppTheme {
        val birdsViewModel = getViewModel(Unit, viewModelFactory { MainViewModel() })
        val uiState by birdsViewModel.uiState.collectAsState()

        LaunchedEffect(birdsViewModel) {
            birdsViewModel.updateImages()
        }

        BirdsPage(uiState, birdsViewModel::updateSelectedCategory)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BirdsPage(uiState: BirdsUiState, updateSelectedCategory: (String) -> Unit) {
    var screenWidth: Float by remember { mutableStateOf(-1f) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BoxWithConstraints(
            content = {
                screenWidth = constraints.maxWidth / LocalDensity.current.density
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    for (category in uiState.categories) {
                        Button(
                            onClick = { updateSelectedCategory(category) },
                            modifier = if(screenWidth >= 750) Modifier.height(200.dp).weight(1f) else Modifier.aspectRatio(1.0f).weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (uiState.selectedCategory == category)
                                    MaterialTheme.colors.primaryVariant
                                else MaterialTheme.colors.primary
                            )
                        ) {
                            Text(category)
                        }
                    }
                }
            },
        )
        AnimatedVisibility(uiState.selectedImages.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(180.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 5.dp, vertical = 5.dp)
            ) {
                items(uiState.selectedImages) { image ->
                    BirdImageCell(image)
                }
            }
        }
    }
}

@Composable
fun BirdImageCell(image: BirdImage) {
    KamelImage(
        resource = asyncPainterResource(
            data = "https://sebi.io/demo-image-api/${image.path}"
        ),
        contentDescription = "${image.category} by ${image.author}",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth().aspectRatio(1.0f),
        onLoading = {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    )
}
