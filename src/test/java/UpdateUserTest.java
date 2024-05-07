import com.github.javafaker.Faker;
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
    private final Faker faker = new Faker();


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
    @Step("Updating user data with authorization")
    public void testUpdateUserWithAuthorization() {
        testUser = User.create(Faker.instance().internet().emailAddress(), "password", Faker.instance().name().username());
        burgerServiceUser.createUser(testUser)
                .statusCode(200);
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
        testUser = User.create(Faker.instance().internet().emailAddress(), "password", Faker.instance().name().username());
        burgerServiceUser.createUser(testUser)
                .statusCode(200);
        User userToUpdate = User.create("test047@example.com", "password", "Test User");

        ValidatableResponse updateUserResponse = burgerServiceUser.updateUser(userToUpdate, "");

        updateUserResponse.statusCode(401);
        updateUserResponse.assertThat().body("message", equalTo("You should be authorised"));
    }
}