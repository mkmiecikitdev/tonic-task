package kmiecik.michal.tonictask.ratings

import io.vavr.collection.List
import io.vavr.control.Either
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.ratings.api.RatingsDataDto

data class Ratings(
        val filmId: String,
        private val ratings: List<Rating>
) {

    fun addRate(userId: String, rating: Int): Either<AppError, Ratings> {
        if (ratings.find { it.userId == userId }.isDefined) {
            return Either.right(this)
        }

        return Rating.of(userId, rating)
                .map { Ratings(filmId, ratings.append(it)) }
    }

    fun toDto(): RatingsDataDto {
        return RatingsDataDto(
                filmId = filmId,
                average = average()
        )
    }

    private fun average(): Double {
        return ratings.map { it.value }
                .average()
                .getOrElse(0.0)
    }

}