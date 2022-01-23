package tech.jhipster.beer.technical.infrastructure.primary.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import tech.jhipster.beer.IntegrationTest;

/**
 * Integration tests {@link ExceptionTranslator} controller advice.
 */
@IntegrationTest
@AutoConfigureMockMvc
class ExceptionTranslatorIT {

  @Autowired
  ApplicationContext applicationContext;

  @Autowired
  private MockMvc mockMvc;

  ExceptionTranslator exceptionTranslator;

  @BeforeEach
  void setUp() {
    if (applicationContext.containsBean("securityExceptionTranslator")) {
      exceptionTranslator = (ExceptionTranslator) applicationContext.getBean("securityExceptionTranslator");
    } else {
      exceptionTranslator = (ExceptionTranslator) applicationContext.getBean("exceptionTranslator");
    }
  }

  @Test
  void shouldProcessNullEntity() {
    NativeWebRequest request = Mockito.mock(NativeWebRequest.class);
    assertThat(exceptionTranslator.process(null, request)).isNull();
  }

  @Test
  void shouldProcessConstraintViolationProblem() {
    // Given
    NativeWebRequest request = Mockito.mock(NativeWebRequest.class);
    List<Violation> violations = List.of();
    ConstraintViolationProblem problem = new ConstraintViolationProblem(BAD_REQUEST, violations);

    // When
    ResponseEntity<Problem> result = exceptionTranslator.process(ResponseEntity.badRequest().body(problem), request);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(result.getStatusCodeValue()).isEqualTo(400);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getTitle()).isEqualTo("Constraint Violation");
  }

  @Test
  void shouldGetMessageWithDefaultMessage() {
    FieldError f = new FieldError("test", "test", "defaultMessage");
    String result = ReflectionTestUtils.invokeMethod(exceptionTranslator, "getMessage", f);
    assertThat(result).isEqualTo("defaultMessage");
  }

  @Test
  void shouldGetMessageWithCode() {
    FieldError f = new FieldError("test", "test", null, false, new String[] { "400" }, null, null);
    String result = ReflectionTestUtils.invokeMethod(exceptionTranslator, "getMessage", f);
    assertThat(result).isEqualTo("400");
  }

  @Test
  void shouldHandleMethodArgumentNotValid() throws Exception {
    mockMvc
      .perform(post("/api/exception-translator-test/method-argument").content("{}").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.message").value(ErrorConstants.ERR_VALIDATION))
      .andExpect(jsonPath("$.fieldErrors.[0].objectName").value("test"))
      .andExpect(jsonPath("$.fieldErrors.[0].field").value("test"))
      .andExpect(jsonPath("$.fieldErrors.[0].message").value("must not be null"));
  }

  @Test
  void shouldMissingServletRequestPartException() throws Exception {
    mockMvc
      .perform(get("/api/exception-translator-test/missing-servlet-request-part"))
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.message").value("error.http.400"));
  }

  @Test
  void shouldHandleMissingServletRequestParameterException() throws Exception {
    mockMvc
      .perform(get("/api/exception-translator-test/missing-servlet-request-parameter"))
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.message").value("error.http.400"));
  }

  @Test
  void shouldHandleExceptionWithResponseStatus() throws Exception {
    mockMvc
      .perform(get("/api/exception-translator-test/response-status"))
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.message").value("error.http.400"))
      .andExpect(jsonPath("$.title").value("test response status"));
  }

  @Test
  void shouldHandleInternalServerError() throws Exception {
    mockMvc
      .perform(get("/api/exception-translator-test/internal-server-error"))
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.message").value("error.http.500"))
      .andExpect(jsonPath("$.title").value("Internal Server Error"));
  }

  @Test
  void shouldHandleBadRequestError() throws Exception {
    mockMvc
      .perform(get("/api/exception-translator-test/bad-request-error"))
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.message").value("error.http.400"))
      .andExpect(jsonPath("$.title").value("Bad request error"));
  }

  @Test
  void shouldHandleBadRequestErrorWithoutDetails() throws Exception {
    mockMvc
      .perform(get("/api/exception-translator-test/bad-request-error"))
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.message").value("error.http.400"))
      .andExpect(jsonPath("$.title").value("Bad request error"));
  }

  @Test
  void shouldHandleHttpMessageConversionException() throws Exception {
    ReflectionTestUtils.setField(exceptionTranslator, "exceptionWithDetails", false);
    mockMvc
      .perform(get("/api/exception-translator-test/http-message-conversion-exception"))
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.message").value("error.http.500"))
      .andExpect(jsonPath("$.title").value("Internal Server Error"));
  }

  @Test
  void shouldHandleHttpMessageConversionExceptionWithDetails() throws Exception {
    ReflectionTestUtils.setField(exceptionTranslator, "exceptionWithDetails", true);
    mockMvc
      .perform(get("/api/exception-translator-test/http-message-conversion-exception"))
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.message").value("error.http.500"))
      .andExpect(jsonPath("$.title").value("Internal Server Error"));
  }

  @Test
  void shouldHandleExceptionContainsPackageName() throws Exception {
    ReflectionTestUtils.setField(exceptionTranslator, "exceptionWithDetails", false);
    ReflectionTestUtils.setField(exceptionTranslator, "packages", List.of("org.", "java."));
    mockMvc
      .perform(get("/api/exception-translator-test/null-pointer-exception"))
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.message").value("error.http.500"))
      .andExpect(jsonPath("$.title").value("Internal Server Error"));
  }

  @Test
  void shouldHandleExceptionContainsPackageNameWithNullPackages() throws Exception {
    ReflectionTestUtils.setField(exceptionTranslator, "exceptionWithDetails", false);
    ReflectionTestUtils.setField(exceptionTranslator, "packages", null);
    mockMvc
      .perform(get("/api/exception-translator-test/null-pointer-exception"))
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.message").value("error.http.500"))
      .andExpect(jsonPath("$.title").value("Internal Server Error"));
  }

  @Test
  void shouldHandleExceptionContainsPackageNameWithEmptyPackages() throws Exception {
    ReflectionTestUtils.setField(exceptionTranslator, "exceptionWithDetails", false);
    ReflectionTestUtils.setField(exceptionTranslator, "packages", List.of());
    mockMvc
      .perform(get("/api/exception-translator-test/null-pointer-exception"))
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.message").value("error.http.500"))
      .andExpect(jsonPath("$.title").value("Internal Server Error"));
  }
}
