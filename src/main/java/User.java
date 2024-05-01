public class User {
    private final String email;
    private final String password;
    private final String name;

    public static User create(String email, String password, String name){
        return new User(email,password, name);
    }
    public User(String email, String password, String name){
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }
    public String getName(){
        return name;
    }

}
