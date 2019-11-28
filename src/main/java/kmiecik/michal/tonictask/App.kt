package kmiecik.michal.tonictask

import kmiecik.michal.tonictask.films.FilmsFacade
import kmiecik.michal.tonictask.films.FilmsModule
import kmiecik.michal.tonictask.ratings.RatingsFacade
import kmiecik.michal.tonictask.ratings.RatingsModule
import kmiecik.michal.tonictask.repertoire.RepertoireFacade
import kmiecik.michal.tonictask.repertoire.RepertoireModule
import kmiecik.michal.tonictask.users.UsersFacade
import kmiecik.michal.tonictask.users.UsersModule

data class Modules(
        val usersModule: UsersModule = UsersModule(),
        val filmsModule: FilmsModule = FilmsModule(),
        val ratingsModule: RatingsModule = RatingsModule(),
        val repertoireModule: RepertoireModule = RepertoireModule()
)

interface App {
    val usersFacade: UsersFacade
    val filmsFacade: FilmsFacade
    val repertoireFacade: RepertoireFacade
    val ratingsFacade: RatingsFacade
}

class InMemoryApp(
        private val modules: Modules = Modules(),
        override val usersFacade: UsersFacade = modules.usersModule.createInMemoryFacade(),
        override val filmsFacade: FilmsFacade = modules.filmsModule.createInMemoryFacade(),
        override val repertoireFacade: RepertoireFacade = modules.repertoireModule.createInMemoryFacade(filmsFacade),
        override val ratingsFacade: RatingsFacade = modules.ratingsModule.createInMemoryFacade(filmsFacade)
) : App


fun main() {
    Server().start(InMemoryApp())
}
