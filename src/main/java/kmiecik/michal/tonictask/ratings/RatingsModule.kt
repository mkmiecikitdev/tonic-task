package kmiecik.michal.tonictask.ratings

import kmiecik.michal.tonictask.films.FilmsFacade

class RatingsModule {

    fun createInMemoryFacade(filmsFacade: FilmsFacade): RatingsFacade {
        return RatingsFacade(InMemoryRatingsRepository(), filmsFacade)
    }

}