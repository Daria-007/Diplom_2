import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import static io.restassured.RestAssured.given;

public class BurgerServiceUserImpl implements BurgerServiceUser {
    private static final String CREATE_USER_ENDPOINT = "/api/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String GET_ORDERS_ENDPOINT = "/api/orders";
    private static final String DELETE_USER_ENDPOINT = "api/auth/user";
    private static final String UPDATE_USER_ENDPOINT = "/api/auth/user";
    private static final String GET_USER_ENDPOINT = "/api/auth/user";
    private final RequestSpecification requestSpecification;
    private final ResponseSpecification responseSpecification;

    public BurgerServiceUserImpl(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        this.requestSpecification = requestSpecification;
        this.responseSpecification = responseSpecification;
    }

    @Override
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(requestSpecification)
                .body(user)
                .post(CREATE_USER_ENDPOINT)
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse login(Credentials credentials) {
        return given()
                .spec(requestSpecification)
                .body(credentials)
                .post(LOGIN_ENDPOINT)
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse createOrder(Order order) {
        return given()
                .spec(requestSpecification)
                .body(order)
                .post(GET_ORDERS_ENDPOINT)
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse deleteUser(User user) {
        return given()
                .spec(requestSpecification)
                .delete(DELETE_USER_ENDPOINT)
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse updateUser(User user, String accessToken) {
        RequestSpecification requestSpecificationWithAuth = requestSpecification;
        if (accessToken != null) {
            requestSpecificationWithAuth = requestSpecificationWithAuth.auth().oauth2(accessToken);
        }

        return given()
                .spec(requestSpecificationWithAuth)
                .body(user)
                .patch(UPDATE_USER_ENDPOINT)
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse getUser(String accessToken) {
        RequestSpecification requestSpecificationWithAuth = requestSpecification;
        if (accessToken != null) {
            requestSpecificationWithAuth = requestSpecificationWithAuth.auth().oauth2(accessToken);
        }

        return given()
                .spec(requestSpecificationWithAuth)
                .get(GET_USER_ENDPOINT)
                .then()
                .spec(responseSpecification);
    }

    public String getAccessToken(User user) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/api/auth/login");

        if (response.getStatusCode() == 200) {
            return response.jsonPath().getString("accessToken");
        } else {
            throw new RuntimeException("Failed to get access token");
        }
    }
}