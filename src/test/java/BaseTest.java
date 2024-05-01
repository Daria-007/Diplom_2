import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class BaseTest {
    protected static final String BASE_URI = "https://stellarburgers.nomoreparties.site/";

    protected static final RequestSpecification REQUEST_SPECIFICATION =
            new RequestSpecBuilder()
                    .log(LogDetail.ALL)
                    .addHeader("Content-type", "application/json")
                    .setBaseUri(BASE_URI)
                    .build();

    protected static final ResponseSpecification RESPONSE_SPECIFICATION =
            new ResponseSpecBuilder().log(LogDetail.ALL).build();
}
