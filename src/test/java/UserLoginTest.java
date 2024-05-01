import org.junit.After;
import org.junit.Before;

public class UserLoginTest  extends BaseTest{
    private BurgerServiceUserImpl burgerServiceUser;
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


}
