import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UpdateUserTest extends BaseTest {
    private BurgerServiceUserImpl burgerServiceUser;
    private User testUser;

    @Before
    public void setUp() {
        burgerServiceUser = new BurgerServiceUserImpl(REQUEST_SPECIFICATION, RESPONSE_SPECIFICATION);
        testUser = User.create("test003@example.com", "password", "Test User");
        ValidatableResponse createUserResponse = burgerServiceUser.createUser(testUser);
    }

    @After
    public void cleanUp() {
        if (testUser != null) {
            burgerServiceUser.deleteUser(testUser);
        }
    }

    @Test
    public void testUpdateUserWithAuthorization() {
        // Входим в систему, чтобы получить accessToken
        Credentials credentials = Credentials.fromUser(testUser);
        ValidatableResponse loginResponse = burgerServiceUser.login(credentials);
        String accessToken = loginResponse.extract().path("accessToken");

        // Обновляем данные пользователя
        testUser = User.create("updated_test@example.com", "updated_password", "Updated Test User");
        ValidatableResponse updateUserResponse = burgerServiceUser.updateUser(testUser, accessToken);
        updateUserResponse.statusCode(200);

        // Проверяем, что данные пользователя обновлены
        ValidatableResponse getUserResponse = burgerServiceUser.getUser(accessToken);
        getUserResponse.statusCode(200);
        Assert.assertEquals("updated_test@example.com", getUserResponse.extract().path("user.email"));
        Assert.assertEquals("Updated Test User", getUserResponse.extract().path("user.name"));
    }

    @Test
    public void testUpdateUserWithoutAuthorization() {
        testUser.setPassword("updated_password");
        testUser.setName("Updated Test User");
        ValidatableResponse updateUserResponse = burgerServiceUser.updateUser(testUser, null);

        updateUserResponse.statusCode(401);
        Assert.assertEquals("You should be authorised", updateUserResponse.extract().path("message"));
    }
}