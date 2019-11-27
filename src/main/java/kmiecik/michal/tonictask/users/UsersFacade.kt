package kmiecik.michal.tonictask.users

import io.vavr.collection.HashSet
import io.vavr.collection.Set
import io.vavr.control.Either
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.kernel.MonoEither
import kmiecik.michal.tonictask.users.api.UserDataDto
import kmiecik.michal.tonictask.users.api.UserFormDto
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import reactor.core.publisher.Mono

class UsersFacade (private val userRepository: UserRepository) {

    fun addOwner(userFormDto: UserFormDto): MonoEither<UserDataDto> {
        return addUser(userFormDto, HashSet.of(Role.CUSTOMER, Role.OWNER))
    }

    fun addCustomer(userFormDto: UserFormDto): MonoEither<UserDataDto> {
        return addUser(userFormDto, HashSet.of(Role.CUSTOMER))
    }

    fun login(userFormDto: UserFormDto): MonoEither<UserDataDto> {
        return userRepository.findByLogin(userFormDto.login)
                .map {
                    return@map if(it.equalsHashedPass(userFormDto.password)) Either.right<AppError, UserDataDto>(it.toDto())
                    else AppError.UNAUTHORIZED.toEither<UserDataDto>()
                }
                .switchIfEmpty(AppError.UNAUTHORIZED.toMono<UserDataDto>())
    }

    private fun addUser(userFormDto: UserFormDto, roles: Set<Role>): MonoEither<UserDataDto> {
        return userRepository.findByLogin(userFormDto.login).map { user ->
            AppError.LOGIN_EXISTS.toEither<UserDataDto>()
        }.switchIfEmpty(
                createUser(userFormDto, roles)
                        .let { userRepository.save(it) }
                        .map { it.toDto() }
                        .map { Either.right<AppError, UserDataDto>(it) }
        )
    }

    private fun createUser(userFormDto: UserFormDto, roles: Set<Role>): User {
        return User(
                login = userFormDto.login,
                hashedPassword = BCryptPasswordEncoder().encode(userFormDto.password),
                roles = roles
        )
    }

}