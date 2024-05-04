import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
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
    @Step("Test creation of order with authorization and ingredients")
    public void testCreateOrderWithAuthorizationAndIngredients() {
        User testUser = User.create("test333@burger.com", "password123", "Test User");
        burgerServiceUser.createUser(testUser);

        Credentials credentials = Credentials.fromUser(testUser);
        ValidatableResponse loginResponse = burgerServiceUser.login(credentials);
        String accessToken = loginResponse.extract().path("accessToken");

        Order order = new Order(List.of("invalid_hash_1", "invalid_hash_2"));
        burgerServiceUser.createOrder(order);

        burgerServiceUser.createOrder(order)
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @Step("Test creation of order without authorization")
    public void testCreateOrderWithoutAuthorization() {
        User testUser = User.create("test998@example.com", "password123", "Test User");
        burgerServiceUser.createUser(testUser);

        Response ingredientsResponse = given()
                .baseUri("https://stellarburgers.nomoreparties.site")
                .basePath("/api")
                .get("/ingredients");

        ingredientsResponse.then().statusCode(200);
        String firstIngredientId = ingredientsResponse.jsonPath().getString("data[0]._id");

        System.out.println("Отправляем запрос на создание заказа...");
        Response orderResponse = given()
                .baseUri("https://stellarburgers.nomoreparties.site")
                .basePath("/api")
                .body("{\"ingredients\": [\"" + firstIngredientId + "\"]}")
                .post("/orders");

        System.out.println("Тело запроса на создание заказа: " + "{\"ingredients\": [\"" + firstIngredientId + "\"]}");
        System.out.println("Получен ответ на запрос создания заказа: " + orderResponse.body().asString());

        orderResponse.then().statusCode(401);
        String errorMessage = orderResponse.jsonPath().getString("message");
        Assert.assertEquals("You should be authorised", errorMessage);
    }

//    @Test
//    public void testOrderCreationWithoutIngredients() {
//        Order order = new Order("[]");
//
//        Response response = burgerServiceUser.createOrder(order)
//                .statusCode(400)
//                .extract()
//                .response();
//
//
//        String errorMessage = response.getBody().jsonPath().get("message");
//        Assert.assertEquals("One or more ids provided are incorrect", errorMessage);
//    }

    @Test
    @Step("Test creation of order without ingredients")
    public void testOrderCreationWithoutIngredients() {
    testUser = User.create("test@example.com", "password123", "Test User");
        burgerServiceUser.createUser(testUser);
    // Вход в систему для получения accessToken
    ValidatableResponse response = burgerServiceUser.login(new Credentials(testUser.getEmail(), testUser.getPassword()));
    accessToken = response.extract().jsonPath().getString("accessToken");
        Order order = new Order();
        // Создание заказа без ингредиентов
        ValidatableResponse response = burgerServiceUser.createOrder(order);
        response.statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }


    @Test
    @Step("Test creation of order with invalid ingredient hash")
    public void testOrderCreationWithInvalidIngredientHash() {
        Order order = new Order(List.of("invalid_hash_1", "invalid_hash_2"));

        Response response = burgerServiceUser.createOrder(order)
                .statusCode(500)
                .extract()
                .response();
        String contentType = response.getContentType();
        Assert.assertTrue(contentType.toLowerCase().contains("text/html"));

        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("Internal Server Error"));
    }
}