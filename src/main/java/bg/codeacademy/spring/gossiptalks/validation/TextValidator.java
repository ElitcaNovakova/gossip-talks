package bg.codeacademy.spring.gossiptalks.validation;

import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TextValidator implements ConstraintValidator<ValidText, String> {

  public final static String tagStart =
      "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)\\>";
  public final static String tagEnd =
      "\\</\\w+\\>";
  public final static String tagSelfClosing =
      "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)/\\>";
  public final static String htmlEntity =
      "&[a-zA-Z][a-zA-Z0-9]+;";
  public final static String tagSelfClosingWithAtributs = "<(\\w+)(.+?)/>";
  public final static String tagWithAttributs = "[\\S\\s]*\\<html[\\S\\s]*\\>[\\S\\s]*\\<\\/html[\\S\\s]*\\>[\\S\\s]*";
  public final static Pattern htmlPattern = Pattern.compile(
      "(" + tagStart + ".*" + tagEnd + ")|(" + tagSelfClosing + ")|"
          + "(" + htmlEntity + ")|(" + tagSelfClosingWithAtributs + ")|(" + tagWithAttributs + ")",
      Pattern.DOTALL
  );

  @Override
  public boolean isValid(String text, ConstraintValidatorContext context) {
    if (text != null) {
      if (htmlPattern.matcher(text).find()) {
        return false;
      } else {
        return true;
      }
    } else {
      return false;
    }
  }

}
