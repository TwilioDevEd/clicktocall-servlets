package com.twilio.clicktocall.servlet;

import com.twilio.clicktocall.exceptions.UndefinedEnvironmentVariableException;
import com.twilio.clicktocall.lib.AppSetup;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.TwilioUtils;
import com.twilio.sdk.verbs.Hangup;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class ConnectTest {
  @Test
  public void connectValidTest()
      throws UndefinedEnvironmentVariableException, ServletException, IOException,
      TwilioRestException {
    AppSetup mockAppSetup = mock(AppSetup.class);
    TwilioUtils mockValidator = mock(TwilioUtils.class);
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    ServletOutputStream mockOutputStream = mock(ServletOutputStream.class);
    Enumeration<String> mockEnumeration = mock(Enumeration.class);

    Map<String, String> params = new HashMap<>();
    params.put("From", "incoming_number");
    params.put("To", "outgoing_number");

    Connect connect = new Connect(mockAppSetup, mockValidator);

    when(mockAppSetup.getAuthToken()).thenReturn("your_auth_token");
    when(mockEnumeration.nextElement()).thenReturn("From").thenReturn("To");
    when(mockRequest.getParameter("From")).thenReturn("incoming_number");
    when(mockRequest.getParameter("To")).thenReturn("outgoing_number");
    when(mockRequest.getHeader("X-Twilio-Signature")).thenReturn("twilio_signature");
    when(mockRequest.getParameterNames()).thenReturn(mockEnumeration);
    when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("http://some_url/connect"));
    when(mockResponse.getOutputStream()).thenReturn(mockOutputStream);
    when(mockEnumeration.hasMoreElements()).thenReturn(true, true, false);
    when(mockValidator.validateRequest("twilio_signature", "http://some_url/connect", params))
        .thenReturn(true);

    connect.doPost(mockRequest, mockResponse);



    verify(mockOutputStream).write(getXMLResponse().getBytes());
  }

  @Test
  public void connectInvalidTest()
      throws UndefinedEnvironmentVariableException, ServletException, IOException,
      TwilioRestException {
    AppSetup mockAppSetup = mock(AppSetup.class);
    TwilioUtils mockValidator = mock(TwilioUtils.class);
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    ServletOutputStream mockOutputStream = mock(ServletOutputStream.class);
    Enumeration<String> mockEnumeration = mock(Enumeration.class);

    Map<String, String> params = new HashMap<>();
    params.put("From", "incoming_number");
    params.put("To", "outgoing_number");

    Connect connect = new Connect(mockAppSetup, mockValidator);

    when(mockAppSetup.getAuthToken()).thenReturn("your_auth_token");
    when(mockEnumeration.nextElement()).thenReturn("From").thenReturn("To");
    when(mockRequest.getParameter("From")).thenReturn("incoming_number");
    when(mockRequest.getParameter("To")).thenReturn("outgoing_number");
    when(mockRequest.getHeader("X-Twilio-Signature")).thenReturn("twilio_signature");
    when(mockRequest.getParameterNames()).thenReturn(mockEnumeration);
    when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("http://some_url/connect"));
    when(mockResponse.getOutputStream()).thenReturn(mockOutputStream);
    when(mockEnumeration.hasMoreElements()).thenReturn(true, true, false);
    when(mockValidator.validateRequest("twilio_signature", "http://some_url/connect", params))
        .thenReturn(false);

    connect.doPost(mockRequest, mockResponse);

    verify(mockOutputStream).write("Invalid twilio request".getBytes());
  }

  private String getXMLResponse() {
    TwiMLResponse twimlResponse = new TwiMLResponse();

    Say sayMessage = new Say(
        "If this were a real click to call implementation, you would be connected to an agent at this point.");
    Hangup hangup = new Hangup();

    try {
      twimlResponse.append(sayMessage);
      twimlResponse.append(hangup);
    } catch (TwiMLException e) {
      System.out.println("Twilio's response building error");
    }

    return twimlResponse.toXML();
  }
}
