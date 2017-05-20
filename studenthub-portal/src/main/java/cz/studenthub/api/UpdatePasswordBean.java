package cz.studenthub.api;

import java.io.Serializable;

public class UpdatePasswordBean implements Serializable {

  private static final long serialVersionUID = 2226105431252778249L;
  private String oldPwd;
  private String newPwd;

  public UpdatePasswordBean() {
  }
  
  public UpdatePasswordBean(String oldPwd, String newPwd) {
    this.oldPwd = oldPwd;
    this.newPwd = newPwd;
  }

  public String getOldPwd() {
    return oldPwd;
  }

  public void setOldPwd(String oldPwd) {
    this.oldPwd = oldPwd;
  }

  public String getNewPwd() {
    return newPwd;
  }

  public void setNewPwd(String newPwd) {
    this.newPwd = newPwd;
  }
}
