package kmiecik.michal.tonictask.repertoire

import io.vavr.collection.List
import java.time.LocalTime

data class DisplayHours(val times: List<LocalTime> = List.empty()) {

    fun addTime(time: LocalTime): DisplayHours {
        if(!times.exists{ it.compareTo(time) == 0 }) {
            return DisplayHours(times.append(time))
        }

        return this
    }

    fun removeTime(time: LocalTime): DisplayHours {
        return DisplayHours(times.removeAll { it.compareTo(time) == 0 })
    }

    fun updateTime(from: LocalTime, to: LocalTime): DisplayHours {
        return DisplayHours(times.replace(from, to))
    }

}