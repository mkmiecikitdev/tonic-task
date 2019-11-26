package kmiecik.michal.tonictask.kernel

import io.vavr.control.Either
import io.vavr.control.Try
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.kernel.TimeUtils.PATTERN
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object TimeUtils {

    const val PATTERN: String = "HH:mm"

    fun toLocalTime(time: String): Either<AppError, LocalTime> {
        val formatter = DateTimeFormatter.ofPattern(PATTERN)
        return Try.of { LocalTime.parse(time, formatter) }
                .toEither(AppError.CANNOT_PARSE_DATE)
    }

}

fun LocalTime.parse(): String {
    return DateTimeFormatter.ofPattern(PATTERN)
            .format(this)
}