public class Credentials {
    private final String email;
    private final String password;

    public static Credentials fromUser(User user) {
        return new Credentials(user.getEmail(), user.getPassword());
    }
    public Credentials(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
