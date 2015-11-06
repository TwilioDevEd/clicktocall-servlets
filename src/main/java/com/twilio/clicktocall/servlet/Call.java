package com.twilio.clicktocall.servlet;

import com.twilio.clicktocall.exceptions.UndefinedEnvironmentVariableException;
import com.twilio.clicktocall.lib.AppSetup;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/call")
public class Call extends HttpServlet {
  AppSetup appSetup;
  TwilioRestClient client;

  public Call() {}

  public Call(AppSetup appSetup, TwilioRestClient client) {
    this.appSetup = appSetup;
    this.client = client;
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String phoneNumber = request.getParameter("phone");

    if (phoneNumber != null) {
      if (this.appSetup == null || this.client == null) {
        appSetup = new AppSetup();
        client = null;
        try {
          client = new TwilioRestClient(appSetup.getAccountSid(), appSetup.getAuthToken());
        } catch (UndefinedEnvironmentVariableException e) {
          response.getOutputStream().write(getJSONResponse(e.getMessage()).getBytes());
          return;
        }
      }

      Map<String, String> params = new HashMap<>();
      String twilioNumber = null;
      try {
        twilioNumber = appSetup.getTwilioNumber();
      } catch (UndefinedEnvironmentVariableException e) {
        response.getOutputStream().write(getJSONResponse(e.getMessage()).getBytes());
        return;
      }
      String path = request.getRequestURL().toString().replace(request.getRequestURI(), "") + "/connect";
      params.put("From", twilioNumber);
      params.put("To", phoneNumber);
      params.put("Url", path);

      try {
        client.getAccount().getCallFactory().create(params);
      } catch (TwilioRestException e) {
        String message = "Twilio rest client error: " + e.getErrorMessage() +
            "\nRemember not to use localhost to access this app, use your ngrok URL";
        response.getOutputStream().write(getJSONResponse(message).getBytes());
        return;
      }
      response.getOutputStream().write(getJSONResponse("Phone call incoming!").getBytes());
    }
    else {
      response.getOutputStream().write(getJSONResponse("The phone number field can't be empty").getBytes());
    }
  }

  private String getJSONResponse(String message) {
    JSONObject obj = new JSONObject();
    obj.put("message", message);
    obj.put("status", "ok");

    return obj.toJSONString();
  }
}
