import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.hamcrest.CoreMatchers.*;

public class GetOrdersTest extends BaseTest {
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

    // Метод для генерации фиктивного пользователя
    private User generateTestUser() {
        return User.create(Faker.instance().internet().emailAddress(), "password", Faker.instance().name().username());
    }

    // Метод для генерации фиктивного заказа
    private Order generateTestOrder() {
        return new Order(List.of("60d3b41abdacab0026a733c6", "609646e4dc916e00276b2870"));
    }

    @Test
    @Step("Test to verify the retrieval of orders for an authenticated user")
    public void testGetOrdersAuthenticatedUser() {
        // Создание фиктивного пользователя
        testUser = generateTestUser();
        ValidatableResponse createUserResponse = burgerServiceUser.createUser(testUser);
        createUserResponse.statusCode(200);

        String accessToken = createUserResponse.extract().path("accessToken");

        // Создание фиктивных заказов
        burgerServiceUser.createOrder(generateTestOrder());
        burgerServiceUser.createOrder(generateTestOrder());

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
        // Создание фиктивных заказов
        burgerServiceUser.createOrder(generateTestOrder());
        burgerServiceUser.createOrder(generateTestOrder());

        ValidatableResponse response = burgerServiceUser.getUser(null);
        response.statusCode(401); // Проверка статуса 401 Unauthorized
        response.body("success", equalTo(false)); // Проверка сообщения об ошибке

        response.body("message", equalTo("You should be authorised"));

        response.body("total", equalTo(null));
        response.body("totalToday", equalTo(null));
    }
}