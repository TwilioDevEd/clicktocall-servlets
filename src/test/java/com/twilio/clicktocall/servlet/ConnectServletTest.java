package com.twilio.clicktocall.servlet;

import com.twilio.security.RequestValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConnectServletTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ResponseWriter responseWriter;

    @Mock
    private RequestValidator requestValidator;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldRespondWithSayWhenTheRequestIsValid() throws ServletException, IOException {
        // Given
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
        when(request.getPathInfo()).thenReturn("/+123456");
        when(request.getParameterNames()).thenReturn(Collections.enumeration(Arrays.asList("From")));
        when(requestValidator.validate(anyString(), anyMap(), anyString())).thenReturn(true);
        ConnectServlet servlet = new ConnectServlet(requestValidator, responseWriter);

        // When
        servlet.doPost(request, response);
        String expectedTwiML =
                "<Response><Say>Thanks for contacting our sales department. Our next available representative" +
                        " will take your call.</Say><Dial><Number>+123456</Number></Dial></Response>";

        // Then
        verify(responseWriter, times(1)).writeIn(response, expectedTwiML);
    }

    @Test
    public void shouldRespondWithoutSayWhenTheRequestIsInvalid() throws ServletException, IOException {
        // Given
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
        when(request.getParameterNames()).thenReturn(Collections.enumeration(Arrays.asList("From")));
        when(requestValidator.validate(anyString(), anyMap(), anyString())).thenReturn(false);
        ConnectServlet servlet = new ConnectServlet(requestValidator, responseWriter);

        // When
        servlet.doPost(request, response);
        String expectedTwiML = "Invalid twilio request";

        // Then
        verify(responseWriter, times(1)).writeIn(response, expectedTwiML);
    }
}
