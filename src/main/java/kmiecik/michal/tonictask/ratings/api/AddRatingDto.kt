package kmiecik.michal.tonictask.ratings.api

data class AddRatingDto (
        val value: Int,
        val filmId: String
)