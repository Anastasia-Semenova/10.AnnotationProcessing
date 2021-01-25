
@HtmlForm(method = "post", action = "/users")
public class User {
    @HtmlInput(name = "nickName", placeholder = "Your Nick")
    private String nickName;
    @HtmlInput(name = "email", type = "email",  placeholder = "Your Email")
    private String email;
    @HtmlInput(name = "password", type = "password",  placeholder = "Your Password")
    private String password;
}
