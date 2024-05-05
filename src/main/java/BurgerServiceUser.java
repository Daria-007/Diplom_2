import io.restassured.response.ValidatableResponse;
public interface BurgerServiceUser {
    ValidatableResponse createUser(User user);
    ValidatableResponse login(Credentials credentials);
    ValidatableResponse createOrder(Order order);
    ValidatableResponse deleteUser(User user);
    ValidatableResponse updateUser(User user, String accessToken);
    ValidatableResponse getUser(String accessToken);
    String getAccessToken(User user);
}