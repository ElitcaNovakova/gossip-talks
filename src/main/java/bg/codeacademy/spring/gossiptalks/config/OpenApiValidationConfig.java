package bg.codeacademy.spring.gossiptalks.config;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.springmvc.OpenApiValidationFilter;
import com.atlassian.oai.validator.springmvc.OpenApiValidationInterceptor;
import java.io.IOException;
import javax.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// This class will validate, that the OpenAPI specification is properly implemented
@Configuration
@Profile("dev")
public class OpenApiValidationConfig implements WebMvcConfigurer {

  private final OpenApiValidationInterceptor validationInterceptor;

  @Autowired
  public OpenApiValidationConfig() throws IOException {
    this.validationInterceptor = new OpenApiValidationInterceptor(OpenApiInteractionValidator
        .createFor("/static/api.yml").build());
  }

  @Bean
  public Filter validationFilter() {
    return new OpenApiValidationFilter(
        true, // enable request validation
        false  // enable response validation
    );
  }

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry.addInterceptor(validationInterceptor);
  }
}