import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class UpdateUserTest extends BaseTest {
    private BurgerServiceUserImpl burgerServiceUser;
    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        burgerServiceUser = new BurgerServiceUserImpl(REQUEST_SPECIFICATION, RESPONSE_SPECIFICATION);
        testUser = User.create("test029@example.com", "password", "Test User");
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
        accessToken = burgerServiceUser.getAccessToken(testUser);

        User newUser = User.create("updated_test@example.com", "updated_password", "Updated Test User");

        ValidatableResponse response = burgerServiceUser.updateUser(newUser, accessToken);

        response.statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @Step("Updating user data without authorization")
    public void testUpdateUserWithoutAuthorization() {
        testUser.setPassword("updated_password");
        testUser.setName("Updated Test User");
        ValidatableResponse updateUserResponse = burgerServiceUser.updateUser(testUser, null);

        updateUserResponse.statusCode(401);
        Assert.assertEquals("You should be authorised", updateUserResponse.extract().path("message"));
    }
}