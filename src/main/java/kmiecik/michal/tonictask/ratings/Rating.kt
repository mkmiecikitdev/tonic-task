package kmiecik.michal.tonictask.ratings

import io.vavr.control.Either
import kmiecik.michal.tonictask.errors.AppError

data class Rating private constructor(
        val userId: String,
        val value: Int
) {

    companion object {
        fun of(userId: String, value: Int): Either<AppError, Rating> {
            return if(value < 1 || value > 5) {
                AppError.INVALID_RATING.toEither()
            } else {
                Either.right(Rating(userId, value))
            }
        }
    }
}