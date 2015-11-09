package com.twilio.clicktocall.servlet;

import com.twilio.clicktocall.exceptions.UndefinedEnvironmentVariableException;
import com.twilio.clicktocall.lib.AppSetup;
import com.twilio.sdk.TwilioUtils;
import com.twilio.sdk.verbs.Hangup;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/connect")
public class Connect extends HttpServlet {
  AppSetup appSetup;
  TwilioUtils validator;

  public Connect() {}

  public Connect(AppSetup appSetup, TwilioUtils validator) {
    this.appSetup = appSetup;
    this.validator = validator;
  }

  /**
   * Method that handles /connect request and responds with the TwiML after validating
   * the authenticity of the request
   * @param request incoming servlet request object
   * @param response servlet response object
   * @throws ServletException
   * @throws IOException
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    if (this.appSetup == null) {
      this.appSetup = new AppSetup();
    }
    if (isValidRequest(request)) {
      response.getOutputStream().write(getXMLResponse().getBytes());
      response.setContentType("application/xml");
    } else {
      response.getOutputStream().write("Invalid twilio request".getBytes());
    }
  }

  /**
   * Generates the TwiML with a Say and Hangout verb
   * @return String with the TwiML
   */
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

  /**
   * Uses TwilioUtils to validate that the incoming request comes from Twilio automated services
   * @param request passed servlet request to extract parameters necessary for validation
   * @return boolean determining validity of the request
   */
  private boolean isValidRequest(HttpServletRequest request) {
    if (this.validator == null) {
      try {
        validator = new TwilioUtils(appSetup.getAuthToken());
      } catch (UndefinedEnvironmentVariableException e) {
        e.printStackTrace();
        return false;
      }
    }

    String url = request.getRequestURL().toString();
    Map<String, String> params = new HashMap<>();

    Enumeration<String> names = request.getParameterNames();
    while (names.hasMoreElements()) {
      String currentName = names.nextElement();
      params.put(currentName, request.getParameter(currentName));
    }

    String signature = request.getHeader("X-Twilio-Signature");

    return validator.validateRequest(signature, url, params);
  }
}
