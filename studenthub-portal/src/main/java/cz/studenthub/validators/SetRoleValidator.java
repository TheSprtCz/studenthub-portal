package cz.studenthub.validators;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;

import cz.studenthub.core.User;
import cz.studenthub.db.UserDAO;
import cz.studenthub.validators.annotations.Role;

/**
 * This validator checks if required Set of Users has required UserRole, will not be needed in Hibernate Validator 6
 */
public class SetRoleValidator implements ConstraintValidator<Role, Set<User>> {

  private Role check;

  @Inject
  private UserDAO userDao;

  @Override
  public void initialize(Role check) {
    this.check = check;    
  }

  @Override
  public boolean isValid(@NotNull Set<User> users, ConstraintValidatorContext arg1) {
    // Will not validate null, if field cannot be null it will be annotated with @NotNull
    if (users != null) {
      for (User user : users) {
        user = userDao.findById(user.getId());
        if (user == null)
          return false;

        if (!user.hasRole(check.role()))
          return false;
      }
    }
    return true;
  }  

}
