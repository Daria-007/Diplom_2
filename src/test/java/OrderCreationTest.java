import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
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
    @Step("Test creation of order with authorization and ingredients")
    public void testCreateOrderWithAuthorizationAndIngredients() {
        testUser = User.create(Faker.instance().internet().emailAddress(), "password", Faker.instance().name().username());
        burgerServiceUser.createUser(testUser)
                .statusCode(200);
        Credentials credentials = Credentials.fromUser(testUser);
        ValidatableResponse userLogin = burgerServiceUser.login(credentials);
        userLogin.assertThat()
                .statusCode(200)
                .and().body("success", equalTo(true));

        accessToken = userLogin.extract().path("accessToken");

        Order order = new Order(List.of("i60d3463f7034a000269f45e9", "60d3463f7034a000269f45e9"));
        burgerServiceUser.createOrder(order);

        burgerServiceUser.createOrder(order)
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @Step("Test creation of order without authorization")
    public void testCreateOrderWithoutAuthorization() {
        testUser = User.create(Faker.instance().internet().emailAddress(), "password", Faker.instance().name().username());
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



    @Test
    @Step("Test creation of order without ingredients")
    public void testOrderCreationWithoutIngredients() {
        testUser = User.create(Faker.instance().internet().emailAddress(), "password", Faker.instance().name().username());
        burgerServiceUser.createUser(testUser)
                .statusCode(200);
        Credentials credentials = Credentials.fromUser(testUser);
        ValidatableResponse userLogin = burgerServiceUser.login(credentials);
        userLogin.assertThat()
                .statusCode(200)
                .and().body("success", equalTo(true));

        accessToken = userLogin.extract().path("accessToken");
        Order order = new Order(List.of("", ""));
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