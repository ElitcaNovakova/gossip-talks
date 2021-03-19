package bg.codeacademy.spring.gossiptalks.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TextValidatorTest {
  private ConstraintValidatorContext context;
  private TextValidator textValidator = new TextValidator();

  @BeforeEach
  void setUp() {
    context = Mockito.mock(ConstraintValidatorContext.class);
    Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString()))
        .thenReturn(Mockito.mock(ConstraintViolationBuilder.class));
  }
  @Test
  public void given_null_value_When_validate_Then_returns_false() {
    assertFalse(this.textValidator.isValid(null, context));
  }
  @Test
  public void given_text_containing_single_HTML_tag_returns_false() {
    assertFalse(this.textValidator.isValid("Contains single tag <tag/>", context));
  }

  @Test
  public void given_text_containing_body_HTML_tags_returns_false() {
    assertFalse(
        this.textValidator.isValid("<body id=\"wpdiscuz_5.3.5\">This is a body</body>", context));
  }

  @Test
  public void given_text_containing_single_HTML_tag_returns_false_() {
    assertFalse(this.textValidator
        .isValid("And a single tag <singleTagWithAttr attr=\"alabala\"/> with attribute", context));
  }

  @Test
  public void given_text_containing_two_HTML_tags_returns_false() {
    assertFalse(this.textValidator.isValid("There is <openTag></openTag> inside.", context));
  }

  @Test
  public void given_text_containing_single_closing_tag_returns_false() {
    assertFalse(this.textValidator
        .isValid("And a single tag <singleTagWithAttr/> with attribute", context));
  }

  @Test
  public void given_text_containing_no_HTML_tag_returns_true() {
    assertTrue(this.textValidator.isValid("Contains single tag", context));

  }
}