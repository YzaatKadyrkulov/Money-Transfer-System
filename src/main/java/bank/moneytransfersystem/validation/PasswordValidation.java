package bank.moneytransfersystem.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import bank.moneytransfersystem.validation.validator.PasswordValidator;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
public @interface PasswordValidation {

    String message() default "Password must by length > 6";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
