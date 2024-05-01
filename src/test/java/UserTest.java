import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserTest extends BaseTest {
    private BurgerServiceUserImpl burgerServiceUser;
    private User testUser;
    @Before
    public void setUp() {
        burgerServiceUser = new BurgerServiceUserImpl(REQUEST_SPECIFICATION, RESPONSE_SPECIFICATION);
    }

    @After
    public void cleanUp() {
        if (testUser != null) {
            burgerServiceUser.deleteUser(testUser);
        }
    }

    @Test
    @Step("Creating a unique user")
    public void testCreateUniqueUser() {
        User testUser = User.create("test-data@yandex.ru", "password", "Username");
        burgerServiceUser.createUser(testUser)
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @Step("Creating a duplicate user")
    public void testCreateDuplicateUser() {
        // First, let's create a user
        User existingUser = User.create("test-data@yandex.ru", "password", "Username");
        burgerServiceUser.createUser(existingUser)
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @Step("Creating a user with missing field")
    public void testCreateUserWithMissingField() {
        User incompleteUser = User.create("test-data@yandex.ru", "password", null); // Missing name
        burgerServiceUser.createUser(incompleteUser)
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}