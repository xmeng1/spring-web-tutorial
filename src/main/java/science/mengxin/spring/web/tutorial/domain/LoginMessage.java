package science.mengxin.spring.web.tutorial.domain;

public class LoginMessage {

    private String loginName;
    private LoginMessageType loginMessageType;
    private String result;
    private String token;

    public LoginMessage(String loginName, LoginMessageType loginMessageType, String result, String token) {
        this.loginName = loginName;
        this.loginMessageType = loginMessageType;
        this.result = result;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public LoginMessageType getLoginMessageType() {
        return loginMessageType;
    }

    public void setLoginMessageType(LoginMessageType loginMessageType) {
        this.loginMessageType = loginMessageType;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
