package kmiecik.michal.tonictask.errors




data class CannotUpdateFilm(val m: String): Throwable(m)