package com.twilio.clicktocall.servlet;

import com.twilio.clicktocall.exceptions.UndefinedEnvironmentVariableException;
import com.twilio.clicktocall.lib.AppSetup;
import com.twilio.clicktocall.lib.TwilioCallCreator;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallServletTest {

    @Mock
    private AppSetup appSetup;

    @Mock
    private TwilioCallCreator twilioCallCreator;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletOutputStream outputStream;

    private CallServlet servlet;

    @Before
    public void setUp() throws IOException {
        initMocks(this);
    }

    @Test
    public void callTest() throws
            ServletException,
            IOException,
            UndefinedEnvironmentVariableException,
            URISyntaxException {

        // Given
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));
        when(request.getRequestURI()).thenReturn("/call");
        when(request.getParameter("phone")).thenReturn("to-phone-number");
        when(response.getOutputStream()).thenReturn(outputStream);
        when(appSetup.getTwilioNumber()).thenReturn("twilio-number");

        servlet = new CallServlet(appSetup, twilioCallCreator);

        // When
        servlet.doPost(request, response);

        // Then
        verify(twilioCallCreator, times(1)).create(
                "twilio-number", "to-phone-number", new URI("http://example.com/connect"));

        JSONObject o = new JSONObject();
        o.put("message", "Phone call incoming!");
        o.put("status", "ok");

        verify(outputStream).write(o.toString().getBytes());
    }
}
