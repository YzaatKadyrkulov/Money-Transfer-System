package bank.moneytransfersystem.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import bank.moneytransfersystem.validation.validator.PhoneNumberValidator;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
public @interface PhoneNumberValidation {

    String message() default "Phone number must be valid Kyrgyzstan or Russian number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
    