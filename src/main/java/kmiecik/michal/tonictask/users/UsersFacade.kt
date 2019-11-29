package kmiecik.michal.tonictask.users

import io.vavr.collection.HashSet
import io.vavr.collection.Set
import io.vavr.control.Either
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.kernel.MonoEither
import kmiecik.michal.tonictask.users.api.UserDataDto
import kmiecik.michal.tonictask.users.api.UserFormDto
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UsersFacade(private val userRepository: UserRepository) {

    fun addOwner(userFormDto: UserFormDto): MonoEither<UserDataDto> {
        return addUser(userFormDto, HashSet.of(Role.CUSTOMER, Role.OWNER))
    }

    fun addCustomer(userFormDto: UserFormDto): MonoEither<UserDataDto> {
        return addUser(userFormDto, HashSet.of(Role.CUSTOMER))
    }

    fun login(userFormDto: UserFormDto): MonoEither<UserDataDto> {
        return wrapUserCredentials(userFormDto) { login, pass ->
            userRepository.findByLogin(login)
                    .map {
                        return@map if (it.equalsHashedPass(pass)) Either.right<AppError, UserDataDto>(it.toDto())
                        else AppError.UNAUTHORIZED.toEither<UserDataDto>()
                    }
                    .switchIfEmpty(AppError.UNAUTHORIZED.toMono<UserDataDto>())
        }
    }

    private fun addUser(userFormDto: UserFormDto, roles: Set<Role>): MonoEither<UserDataDto> {
        return wrapUserCredentials(userFormDto) { login, pass ->
            userRepository.findByLogin(login).map { user ->
                AppError.LOGIN_EXISTS.toEither<UserDataDto>()
            }.switchIfEmpty(
                    createUser(login, pass, roles)
                            .let { userRepository.save(it) }
                            .map { it.toDto() }
                            .map { Either.right<AppError, UserDataDto>(it) }
            )
        }
    }

    private fun createUser(login: String, pass: String, roles: Set<Role>): User {
        return User(
                login = login,
                hashedPassword = BCryptPasswordEncoder().encode(pass),
                roles = roles
        )
    }

    private fun wrapUserCredentials(userFormDto: UserFormDto, action: (String, String) -> MonoEither<UserDataDto>): MonoEither<UserDataDto> {
        return userFormDto.login?.let { login ->
            userFormDto.password?.let { pass ->
                action(login, pass)
            }
        } ?: AppError.BAD_CREDENTIALS.toMono()
    }
}
