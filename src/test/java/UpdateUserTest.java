import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserTest extends BaseTest {
    private BurgerServiceUserImpl burgerServiceUser;
    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        burgerServiceUser = new BurgerServiceUserImpl(REQUEST_SPECIFICATION, RESPONSE_SPECIFICATION);
        testUser = User.create("test050@example.com", "password", "Test User");
        burgerServiceUser.createUser(testUser)
                .statusCode(200);
    }

    @After
    public void cleanUp() {
        if (testUser != null) {
            burgerServiceUser.deleteUser(testUser);
        }
    }

    @Test
    @Step("Updating user data with authorization")
    public void testUpdateUserWithAuthorization() {
        Credentials credentials = Credentials.fromUser(testUser);
        ValidatableResponse userLogin = burgerServiceUser.login(credentials);
        userLogin.assertThat()
                .statusCode(200)
                .and().body("success", equalTo(true));

        accessToken = userLogin.extract().path("accessToken");

        testUser.setName("UpdateUserName1");
        testUser.setEmail("UpdateUserEmail11@yandex.ru");
        ValidatableResponse changeUserData = burgerServiceUser.updateUser(testUser, accessToken);
        changeUserData.assertThat()
                .statusCode(200)
                .and().body("success", equalTo(true));
    }


    @Test
    @Step("Updating user data without authorization")
    public void testUpdateUserWithoutAuthorization() {
        // Создание пользователя для обновления данных
        User userToUpdate = User.create("test047@example.com", "password", "Test User");

        // Обновление данных пользователя без передачи токена доступа
        ValidatableResponse updateUserResponse = burgerServiceUser.updateUser(userToUpdate, "");

        // Проверка полученного ответа
        updateUserResponse.statusCode(401);
        updateUserResponse.assertThat().body("message", equalTo("You should be authorised"));
    }
}