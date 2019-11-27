package kmiecik.michal.tonictask.repertoire

import kmiecik.michal.tonictask.films.FilmsFacade

class RepertoireModule {

    fun createInMemoryFacade(filmsFacade: FilmsFacade): RepertoireFacade {
        return RepertoireFacade(InMemoryRepertoireRepository(), filmsFacade, TimeUpdater(), PriceUpdater())
    }

}
