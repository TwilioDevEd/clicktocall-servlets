package com.twilio.clicktocall.servlet;

import com.twilio.clicktocall.exceptions.UndefinedEnvironmentVariableException;
import com.twilio.clicktocall.lib.AppSetup;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.CallFactory;
import com.twilio.sdk.resource.instance.Account;
import org.json.simple.JSONObject;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class CallTest {
  @Test
  public void callTest()
      throws UndefinedEnvironmentVariableException, ServletException, IOException,
      TwilioRestException {
    AppSetup mockAppSetup = mock(AppSetup.class);
    TwilioRestClient mockClient = mock(TwilioRestClient.class);
    Account mockAccount = mock(Account.class);
    CallFactory mockCallFactory = mock(CallFactory.class);
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    ServletOutputStream mockOutputStream = mock(ServletOutputStream.class);

    Call call = new Call(mockAppSetup, mockClient);

    when(mockClient.getAccount()).thenReturn(mockAccount);
    when(mockAccount.getCallFactory()).thenReturn(mockCallFactory);
    when(mockAppSetup.getTwilioNumber()).thenReturn("+twilio_number");
    when(mockRequest.getParameter("phone")).thenReturn("+5551234567");
    when(mockRequest.getRequestURL()).thenReturn(new StringBuffer("http://some_url/call"));
    when(mockRequest.getRequestURI()).thenReturn("/call");
    when(mockResponse.getOutputStream()).thenReturn(mockOutputStream);

    call.doPost(mockRequest, mockResponse);

    Map<String, String> params = new HashMap<>();
    params.put("From", "+twilio_number");
    params.put("To", "+5551234567");
    params.put("Url", "http://some_url/connect");

    verify(mockRequest).getParameter("phone");
    verify(mockCallFactory, times(1)).create(params);

    JSONObject obj = new JSONObject();
    obj.put("message", "Phone call incoming!");
    obj.put("status", "ok");

    verify(mockOutputStream).write(obj.toJSONString().getBytes());
  }
}
