package bg.codeacademy.spring.gossiptalks.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

  public static final String SPECIAL_CHARACTERS = "!@#$%^&*()-+.,_";

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    //min 8 символа, да включва голяма и малка буква, цифра, специален символ, да няма интервали
    if (value == null || value.length() < 8) {
      context.buildConstraintViolationWithTemplate("The password is too short")
          .addConstraintViolation();
      return false;
    }
    boolean upper = false;
    boolean lower = false;
    boolean digit = false;
    boolean special = false;
    boolean whiteSpace = false;

    for (char ch : value.toCharArray()) {
      if (Character.isUpperCase(ch)) {
        upper = true;
      }
      if (Character.isLowerCase(ch)) {
        lower = true;
      }
      if (Character.isDigit(ch)) {
        digit = true;
      }
      if (SPECIAL_CHARACTERS.indexOf(ch) >= 0) {
        special = true;
      }
      if (Character.isWhitespace(ch)) {
        whiteSpace = true;
      }
    }
    if (!upper) {
      context.buildConstraintViolationWithTemplate("Must contain Upper Case letter")
          .addConstraintViolation();
    }
    if (!lower) {
      context.buildConstraintViolationWithTemplate("Must contain Lower Case letter")
          .addConstraintViolation();
    }
    if (!digit) {
      context.buildConstraintViolationWithTemplate("Must contain at least one digit")
          .addConstraintViolation();
    }
    if (!special) {
      context.buildConstraintViolationWithTemplate("Must contain at least one special character")
          .addConstraintViolation();
    }
    if (whiteSpace) {
      context.buildConstraintViolationWithTemplate("Must not contain whitespaces")
          .addConstraintViolation();
    }
    return upper && lower && digit && special && (!whiteSpace);
  }
}
