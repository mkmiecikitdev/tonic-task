package kmiecik.michal.tonictask.users

import reactor.core.publisher.Mono

interface UserRepository {

    fun save(user: User): Mono<User>

    fun findByLogin(login: String): Mono<User>

}