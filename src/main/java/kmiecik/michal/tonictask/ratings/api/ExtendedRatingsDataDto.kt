package kmiecik.michal.tonictask.ratings.api

data class ExtendedRatingsDataDto(
        val filmId: String,
        val filmName: String,
        val average: Double?
)