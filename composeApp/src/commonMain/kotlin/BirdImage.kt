import kotlinx.serialization.Serializable

/**
 * @author Mohannad El-Sayeh email(eng.mohannadelsayeh@gmail.com)
 * @date 27/12/2023
 */

@Serializable
data class BirdImage(
    val category: String,
    val path: String,
    val author: String
)