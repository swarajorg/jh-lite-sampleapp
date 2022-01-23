package tech.jhipster.beer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayNameGeneration(ReplaceCamelCase.class)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { BeerApp.class })
@Target(ElementType.TYPE)
public @interface IntegrationTest {
}
