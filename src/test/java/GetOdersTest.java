import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;

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
    public void testGetOrdersAuthenticatedUser() {
        // Создание тестового пользователя
        User testUser = User.create("testooo4@example.com", "password123", "Test User");
        burgerServiceUser.createUser(testUser).statusCode(200);

        // Авторизация пользователя и получение токена доступа
        Credentials credentials = Credentials.fromUser(testUser);
        String accessToken = burgerServiceUser.login(credentials).extract().path("accessToken");

        // Создание заказов
        burgerServiceUser.createOrder(new Order(List.of("60d3463f7034a000269f45e9", "60d3463f7034a000269f45e7")));
        burgerServiceUser.createOrder(new Order(List.of("60d3463f7034a000269f45e9")));

        // Обновление данных пользователя
        burgerServiceUser.updateUser(testUser, accessToken);

        // Получение информации о пользователе и его заказах
        ValidatableResponse response = burgerServiceUser.getUser(accessToken);
        response.statusCode(200);

        // Проверка информации о заказах пользователя
        response.body("orders.size()", equalTo(2));
        response.body("total", equalTo(2));
        response.body("totalToday", equalTo(2));
    }

    @Test
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