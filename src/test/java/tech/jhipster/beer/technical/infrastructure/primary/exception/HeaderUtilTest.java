package tech.jhipster.beer.technical.infrastructure.primary.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import tech.jhipster.beer.UnitTest;

@UnitTest
class HeaderUtilTest {

  @Test
  void shouldCreateAlert() {
    String message = "any.message";
    String param = "24";

    HttpHeaders headers = HeaderUtil.createAlert("myApp", message, param);
    assertThat(headers.getFirst("X-myApp-alert")).isEqualTo(message);
    assertThat(headers.getFirst("X-myApp-params")).isEqualTo(param);
  }

  @Test
  void shouldCreateAlertWithException() {
    String message = "any.message";
    String param = "24";

    try (MockedStatic<URLEncoder> urlEncoder = Mockito.mockStatic(URLEncoder.class)) {
      urlEncoder.when(() -> URLEncoder.encode(anyString(), anyString())).thenThrow(new UnsupportedEncodingException());
      HttpHeaders headers = HeaderUtil.createAlert("myApp", message, param);
      assertThat(headers.getFirst("X-myApp-alert")).isEqualTo(message);
    }
  }

  @Test
  void shouldCreateEntityCreationAlertWithTranslation() {
    HttpHeaders headers = HeaderUtil.createEntityCreationAlert("myApp", true, "User", "2");
    assertThat(headers.getFirst("X-myApp-alert")).isEqualTo("myApp.User.created");
    assertThat(headers.getFirst("X-myApp-params")).isEqualTo("2");
  }

  @Test
  void shouldCreateEntityCreationAlertNoTranslation() {
    HttpHeaders headers = HeaderUtil.createEntityCreationAlert("myApp", false, "User", "2");
    assertThat(headers.getFirst("X-myApp-alert")).isEqualTo("A new User is created with identifier 2");
    assertThat(headers.getFirst("X-myApp-params")).isEqualTo("2");
  }

  @Test
  void shouldCreateEntityUpdateAlertWithTranslation() {
    HttpHeaders headers = HeaderUtil.createEntityUpdateAlert("myApp", true, "User", "2");
    assertThat(headers.getFirst("X-myApp-alert")).isEqualTo("myApp.User.updated");
    assertThat(headers.getFirst("X-myApp-params")).isEqualTo("2");
  }

  @Test
  void shouldCreateEntityUpdateAlertNoTranslation() {
    HttpHeaders headers = HeaderUtil.createEntityUpdateAlert("myApp", false, "User", "2");
    assertThat(headers.getFirst("X-myApp-alert")).isEqualTo("A User is updated with identifier 2");
    assertThat(headers.getFirst("X-myApp-params")).isEqualTo("2");
  }

  @Test
  void shouldCreateEntityDeletionAlertWithTranslation() {
    HttpHeaders headers = HeaderUtil.createEntityDeletionAlert("myApp", true, "User", "2");
    assertThat(headers.getFirst("X-myApp-alert")).isEqualTo("myApp.User.deleted");
    assertThat(headers.getFirst("X-myApp-params")).isEqualTo("2");
  }

  @Test
  void shouldCreateEntityDeletionAlertNoTranslation() {
    HttpHeaders headers = HeaderUtil.createEntityDeletionAlert("myApp", false, "User", "2");
    assertThat(headers.getFirst("X-myApp-alert")).isEqualTo("A User is deleted with identifier 2");
    assertThat(headers.getFirst("X-myApp-params")).isEqualTo("2");
  }

  @Test
  void shouldCreateFailureAlertWithTranslation() {
    HttpHeaders headers = HeaderUtil.createFailureAlert("myApp", true, "User", "404", "Failed to find user");
    assertThat(headers.getFirst("X-myApp-error")).isEqualTo("error.404");
    assertThat(headers.getFirst("X-myApp-params")).isEqualTo("User");
  }

  @Test
  void shouldCreateFailureAlertNoTranslation() {
    HttpHeaders headers = HeaderUtil.createFailureAlert("myApp", false, "User", "404", "Failed to find user");
    assertThat(headers.getFirst("X-myApp-error")).isEqualTo("Failed to find user");
    assertThat(headers.getFirst("X-myApp-params")).isEqualTo("User");
  }
}
