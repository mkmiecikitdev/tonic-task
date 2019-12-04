package kmiecik.michal.tonictask.users

import kmiecik.michal.tonictask.TestUtils.assertMonoEitherLeft
import kmiecik.michal.tonictask.TestUtils.assertMonoEitherRight
import kmiecik.michal.tonictask.errors.AppError
import kmiecik.michal.tonictask.users.api.UserFormDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UsersFacadeTest {

    private lateinit var facade: UsersFacade

    @BeforeEach
    fun setup() {
        facade = UsersModule().createInMemoryFacade()
    }

    @Test
    fun shouldAddCustomer() {
        // given
        val form = UserFormDto(login = "user1", password = "pass")

        // when
        val result = facade.addCustomer(form)

        // then
        assertMonoEitherRight(result) {
            assertEquals("user1", it.login)
            assertTrue { it.roles.contains(Role.CUSTOMER) }
            assertTrue { !it.roles.contains(Role.OWNER) }
        }
    }

    @Test
    fun shouldAddOwner() {
        // given
        val form = UserFormDto(login = "user1", password = "pass")

        // when
        val result = facade.addOwner(form)

        // then
        assertMonoEitherRight(result) {
            assertEquals("user1", it.login)
            assertTrue { it.roles.contains(Role.CUSTOMER) }
            assertTrue { it.roles.contains(Role.OWNER) }
        }
    }

    @Test
    fun shouldReturnUserExistsError() {
        // given
        val form = UserFormDto(login = "user1", password = "pass")
        facade.addCustomer(form).block()

        // when
        val result = facade.addCustomer(form)

        // then
        assertMonoEitherLeft(result) {
            assertEquals(AppError.LOGIN_EXISTS, it)
        }
    }

    @Test
    fun shouldSuccessLogin() {
        // given
        val form = UserFormDto(login = "user1", password = "pass")
        facade.addOwner(form).block()

        // when
        val result = facade.login(form)

        // then
        assertMonoEitherRight(result) {
            assertEquals("user1", it.login)
            assertEquals(2, it.roles.size())
            assertTrue { it.roles.contains(Role.CUSTOMER) }
            assertTrue { it.roles.contains(Role.OWNER) }
        }
    }

    @Test
    fun shouldReturnUnauthorizedIfLoginInvalid() {
        // given
        val form = UserFormDto(login = "user1", password = "pass")
        val invalidLoginForm = UserFormDto(login = "XXX", password = "pass")
        facade.addOwner(form).block()

        // when
        val result = facade.login(invalidLoginForm)

        // then
        assertMonoEitherLeft(result) {
            assertEquals(AppError.UNAUTHORIZED, it)
        }
    }

    @Test
    fun shouldReturnUnauthorizedIfPasswordInvalid() {
        // given
        val form = UserFormDto(login = "user1", password = "pass")
        val invalidLoginForm = UserFormDto(login = "user1", password = "XXX")
        facade.addOwner(form).block()

        // when
        val result = facade.login(invalidLoginForm)

        // then
        assertMonoEitherLeft(result) {
            assertEquals(AppError.UNAUTHORIZED, it)
        }
    }
}