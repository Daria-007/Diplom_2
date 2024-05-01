import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserLoginTest extends BaseTest {
    private BurgerServiceUserImpl burgerServiceUser;
    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        burgerServiceUser = new BurgerServiceUserImpl(REQUEST_SPECIFICATION, RESPONSE_SPECIFICATION);
        testUser = User.create("test-data@yandex.ru", "password", "Username");
        ValidatableResponse response = burgerServiceUser.createUser(testUser);
        accessToken = response.extract().path("accessToken");
    }

    @After
    public void cleanUp() {
        if (testUser != null) {
            burgerServiceUser.deleteUser(testUser);
        }
    }

    @Test
    @Step("Logging in with valid credentials")
    public void testLoginWithValidCredentials() {
        Credentials validCredentials = Credentials.fromUser(testUser);
        burgerServiceUser.login(validCredentials)
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @Step("Logging in with invalid credentials")
    public void testLoginWithInvalidCredentials() {
        Credentials invalidCredentials = new Credentials("nonexistent@example.com", "wrongPassword");
        burgerServiceUser.login(invalidCredentials)
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
