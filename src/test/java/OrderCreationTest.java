import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;

public class OrderCreationTest extends BaseTest{
    private BurgerServiceUser burgerServiceUser;
    private User testUser;
    private String accessToken;

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
    public void testCreateOrderWithAuthorizationAndIngredients() {
        // Создание пользователя и получение токена доступа
        User testUser = User.create("test333@burger.com", "password123", "Test User");
        burgerServiceUser.createUser(testUser);

        Credentials credentials = Credentials.fromUser(testUser);
        ValidatableResponse loginResponse = burgerServiceUser.login(credentials);
        String accessToken = loginResponse.extract().path("accessToken");

        // Создание заказа с авторизацией и ингредиентами
        Order order = new Order("[\"Говядина\",\"Сыр\"]");
        burgerServiceUser.createOrder(order);

        // Проверка успешности создания заказа
        // В данном случае, можно проверить код ответа, наличие поля "success" со значением true и другие необходимые параметры
        // Например:
        burgerServiceUser.createOrder(order)
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    public void testCreateOrderWithoutAuthorization() {
        Order order = new Order("Говядина,Сыр");
        Response response = burgerServiceUser.createOrder(order)
                .statusCode(400)
                .extract()
                .response();


        String errorMessage = response.getBody().jsonPath().get("message");
        Assert.assertEquals("One or more ids provided are incorrect", errorMessage);
    }

    @Test
    public void testOrderCreationWithoutIngredients() {
        Order order = new Order("[]");

        ValidatableResponse response = burgerServiceUser.createOrder(order)
                .statusCode(400) // Плохой запрос
                .body("success", equalTo(false))
                .body("message", equalTo("Идентификаторы ингредиентов должны быть предоставлены"));
    }

    @Test
    public void testOrderCreationWithInvalidIngredientHash() {
        Order order = new Order("[\"invalid_hash_1\",\"invalid_hash_2\"]"); // Недопустимый хеш ингредиента

        ValidatableResponse response = burgerServiceUser.createOrder(order)
                .statusCode(500);
    }
}