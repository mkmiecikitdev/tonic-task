package kmiecik.michal.tonictask.films

class FilmsModule {

    fun createInMemoryFacade(): FilmsFacade {
        return FilmsFacade(InMemoryFilmRepository())
    }

}