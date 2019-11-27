package kmiecik.michal.tonictask.repertoire

import io.vavr.control.Either
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.kernel.TimeUtils

class TimeUpdater {

    fun addTime(repertoire: Repertoire, time: String): Either<AppError, Repertoire> {
        return TimeUtils.toLocalTime(time)
                .map { repertoire.addTime(it) }
    }

    fun removeTime(repertoire: Repertoire, time: String): Either<AppError, Repertoire> {
        return TimeUtils.toLocalTime(time)
                .map { repertoire.removeTime(it) }
    }

    fun updateTime(repertoire: Repertoire, timeFrom: String, timeTo: String): Either<AppError, Repertoire> {
        return TimeUtils.toLocalTime(timeFrom).flatMap { paredTimeFrom ->
            TimeUtils.toLocalTime(timeTo)
                    .map { parsedTimeTo ->
                        repertoire.updateTime(paredTimeFrom, parsedTimeTo)
                    }
        }
    }

}
