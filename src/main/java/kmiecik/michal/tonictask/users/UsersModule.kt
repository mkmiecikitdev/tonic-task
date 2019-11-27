package kmiecik.michal.tonictask.users

class UsersModule {

    fun createInMemoryFacade(): UsersFacade {
        return UsersFacade(InMemoryUsersRepository())
    }

}