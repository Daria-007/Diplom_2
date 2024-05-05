import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class BaseTest {
    public static final RequestSpecification REQUEST_SPECIFICATION;
    public static final ResponseSpecification RESPONSE_SPECIFICATION;

    protected static final String BASE_URI = "https://stellarburgers.nomoreparties.site/";

    static {
        REQUEST_SPECIFICATION = new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setContentType(ContentType.JSON)
                .build();
        RESPONSE_SPECIFICATION = new ResponseSpecBuilder().log(LogDetail.ALL).build();
    }

}