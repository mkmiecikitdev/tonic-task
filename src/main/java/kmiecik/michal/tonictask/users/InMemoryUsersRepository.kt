package kmiecik.michal.tonictask.users

import io.vavr.collection.HashMap
import io.vavr.collection.Map
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicReference

class InMemoryUsersRepository(private var users: AtomicReference<Map<String, User>> = AtomicReference(HashMap.empty())) : UserRepository {

    override fun findByLogin(login: String): Mono<User> {
        return users.get()[login]
                .map { Mono.just(it) }
                .getOrElse { Mono.empty() }
    }

    override fun save(user: User): Mono<User> {
        users.updateAndGet { it.put(user.login, user) }
        return Mono.just(user)
    }
}