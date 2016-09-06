package com.twilio.clicktocall.servlet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConnectServletTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ResponseWriter responseWriter;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void connectValidTest() throws ServletException, IOException {
        // Given
        ConnectServlet servlet = new ConnectServlet(responseWriter);

        // When
        servlet.doPost(request, response);
        String expectedTwiML =
                "<Response><Say>If this were a real click to call implementation, you would be connected to an agent at this point.</Say><Hangup/></Response>";

        // Then
        verify(responseWriter, times(1)).writeIn(response, expectedTwiML);
    }
}
