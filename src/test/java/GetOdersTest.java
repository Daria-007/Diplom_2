import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.hamcrest.CoreMatchers.*;

public class GetOdersTest extends BaseTest {
    private BurgerServiceUser burgerServiceUser;
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
    @Step("Test to verify the retrieval of orders for an authenticated user")
    public void testGetOrdersAuthenticatedUser() {
        User testUser = User.create("testooo70@example.com", "password123", "Test User");
        ValidatableResponse createUserResponse = burgerServiceUser.createUser(testUser);
        createUserResponse.statusCode(200);

        String accessToken = createUserResponse.log().all().extract().path("accessToken").toString();
        createUserResponse.log().all()
                .assertThat().statusCode(200)
                .and().body("success", is(true))
                .and().body("accessToken", notNullValue())
                .and().body("refreshToken", notNullValue());

        burgerServiceUser.createOrder(new Order(List.of("60d3463f7034a000269f45e9")));
        burgerServiceUser.createOrder(new Order(List.of("60d3463f7034a000269f45e9")));

        // Запрос информации о пользователе с использованием полученного токена доступа
        ValidatableResponse userInfoResponse = burgerServiceUser.getUser(accessToken);

        userInfoResponse
                .statusCode(200)
                .body("orders.size()", equalTo(2))
                .body("total", equalTo(2))
                .body("totalToday", equalTo(2));
    }

    @Test
    @Step("Test to verify the retrieval of orders for an unauthenticated user")
    public void testGetOrdersUnauthenticatedUser() {
        burgerServiceUser.createOrder(new Order(List.of("60d3463f7034a000269f45e9", "60d3463f7034a000269f45e7")));
        burgerServiceUser.createOrder(new Order(List.of("60d3463f7034a000269f45e9")));

        ValidatableResponse response = burgerServiceUser.getUser(null);
        response.statusCode(401); // Проверка статуса 401 Unauthorized
        response.body("success", equalTo(false)); // Проверка сообщения об ошибке

        response.body("message", equalTo("You should be authorised"));

        response.body("total", equalTo(null));
        response.body("totalToday", equalTo(null));
    }
}