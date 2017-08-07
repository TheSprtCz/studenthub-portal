package cz.studenthub.validators;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import cz.studenthub.core.User;
import cz.studenthub.db.UserDAO;
import cz.studenthub.validators.annotations.Role;

/**
 * This validator checks if required User has required UserRole
 */
public class RoleValidator implements ConstraintValidator<Role, User> {

  private Role check;

  @Inject
  private UserDAO userDao;

  @Override
  public void initialize(Role check) {
    this.check = check;    
  }

  @Override
  public boolean isValid(User user, ConstraintValidatorContext arg1) {
    // Will not validate null, if field cannot be null it will be annotated with @NotNull
    if (user != null) {
      user = userDao.findById(user.getId());
      if (user == null)
        return false;

      return user.hasRole(check.role());
    }
    return true;
  }

}
