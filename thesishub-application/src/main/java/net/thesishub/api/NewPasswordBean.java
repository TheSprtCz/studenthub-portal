package net.thesishub.api;

import java.io.Serializable;

public class NewPasswordBean implements Serializable {

  private static final long serialVersionUID = -6321762850967340888L;
  private String password;

  public NewPasswordBean() {
  }

  public NewPasswordBean(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
