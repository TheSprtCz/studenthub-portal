package cz.studenthub.validators.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import cz.studenthub.core.UserRole;
import cz.studenthub.validators.RoleValidator;
import cz.studenthub.validators.SetRoleValidator;

/**
 * Annotation for checking required roles
 */
@Constraint(validatedBy = { RoleValidator.class, SetRoleValidator.class })
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER, TYPE_PARAMETER, TYPE, TYPE_USE })
public @interface Role {

  String message() default "doesn't have required role {role}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  UserRole role() default UserRole.ADMIN;
}
