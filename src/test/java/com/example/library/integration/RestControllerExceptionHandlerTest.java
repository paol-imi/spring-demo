package com.example.library.integration;

import com.example.library.aspect.RestControllerExceptionHandler;
import com.example.library.lib.SafeRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Tag("integration")
class RestControllerExceptionHandlerTest {

	@InjectMocks
	private RestControllerExceptionHandler exceptionHandler;

	@Mock
	private Environment environment;

	@Mock
	private WebRequest webRequest;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void handleSafeException_shouldReturnBadRequestForExceptionWithoutCause() {
		SafeRuntimeException ex = new SafeRuntimeException("Test exception");
		ResponseEntity<String> response = exceptionHandler.handleSafeException(ex, webRequest);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Test exception", response.getBody());
	}

	@Test
	void handleSafeException_shouldReturnInternalServerErrorForExceptionWithCause() {
		SafeRuntimeException ex = new SafeRuntimeException("Test exception", new RuntimeException());
		ResponseEntity<String> response = exceptionHandler.handleSafeException(ex, webRequest);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Test exception", response.getBody());
	}

	@Test
	void handleGlobalException_shouldReturnGenericMessageInNonTestEnvironment() {
		when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
		Exception ex = new RuntimeException("Actual error message");
		ResponseEntity<?> response = exceptionHandler.handleGlobalException(ex, webRequest);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("An unexpected error occurred", response.getBody());
	}

	@Test
	void handleGlobalException_shouldReturnActualMessageInTestEnvironment() {
		when(environment.getActiveProfiles()).thenReturn(new String[]{"test"});
		Exception ex = new RuntimeException("Actual error message");
		ResponseEntity<?> response = exceptionHandler.handleGlobalException(ex, webRequest);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Actual error message", response.getBody());
	}
}