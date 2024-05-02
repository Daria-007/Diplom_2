import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserDataTest extends BaseTest {
    private BurgerServiceUserImpl burgerServiceUser;
    private User testUser;


    @Before
    public void setUp() {
        burgerServiceUser = new BurgerServiceUserImpl(REQUEST_SPECIFICATION, RESPONSE_SPECIFICATION);
        testUser = new User("test2356@burger.com", "Test User", "password");
    }

    @After
    public void cleanUp() {
        if (testUser != null) {
            burgerServiceUser.deleteUser(testUser);
        }
    }

    @Test
    public void testUpdateUserDataWithAuthorization() {
        burgerServiceUser.createUser(testUser);
        String accessToken = burgerServiceUser.login(new Credentials("test2356@burger.com", "password"))
                .extract()
                .path("accessToken");

        User updatedUserData = new User("testuser22@burger.com", "Updated User", "newpassword"); // Изменяем также пароль
        ValidatableResponse response = burgerServiceUser.updateUser(updatedUserData, accessToken);

        response.log().all();

        response.statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo("updated@example.com"))
                .body("user.name", equalTo("Updated User"));
    }

    @Test
    public void testUpdateUserDataWithoutAuthorization() {
        User updatedUserData = new User("updated@example.com", "Updated User", "password"); // Добавляем пароль для пользователя
        ValidatableResponse response = burgerServiceUser.updateUser(updatedUserData, null);

        response.statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    public void testUpdateUserWithExistingEmail() {
        // Создание двух пользователей
        burgerServiceUser.createUser(new User("test1@example.com", "Test User 1", "password1"));
        burgerServiceUser.createUser(new User("test2@example.com", "Test User 2", "password2"));

        // Получение токена авторизации для первого пользователя
        String accessToken = burgerServiceUser.login(new Credentials("test1@example.com", "password1"))
                .extract()
                .path("accessToken");

        // Попытка обновления данных второго пользователя с использованием email первого
        User updatedUserData = new User("test1@example.com", "Updated User", "newpassword"); // Добавляем пароль для пользователя
        ValidatableResponse response = burgerServiceUser.updateUser(updatedUserData, accessToken);

        // Проверка ответа сервера на попытку обновления с использованием существующего email
        response.statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));
    }

}