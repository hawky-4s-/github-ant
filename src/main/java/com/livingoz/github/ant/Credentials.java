package com.livingoz.github.ant;

public class Credentials {

  private final String user;
  private final String password;
  private final String oauthToken;

  public Credentials(final String user, final String password, final String oauthToken) {
    this.user = user;
    this.password = password;
    this.oauthToken = oauthToken;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

  public String getOauthToken() {
    return oauthToken;
  }

  public static Credentials createNewCredentials(final String user, final String password, final String oauthToken) {
    return new Credentials(user, password, oauthToken);
  }
}
