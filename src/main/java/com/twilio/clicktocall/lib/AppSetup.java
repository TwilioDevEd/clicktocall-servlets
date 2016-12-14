package com.twilio.clicktocall.lib;

import com.twilio.clicktocall.exceptions.UndefinedEnvironmentVariableException;

import java.util.Map;

/**
 * Class that holds methods to obtain configuration parameters from the environment.
 */
public class AppSetup {
  private Map<String, String> env;

  public AppSetup() {
    this.env = System.getenv();
  }

  public String getAccountSid() throws UndefinedEnvironmentVariableException {
    return getEnvironmentVariable("TWILIO_ACCOUNT_SID");
  }

  public String getAuthToken() throws UndefinedEnvironmentVariableException {
    return getEnvironmentVariable("TWILIO_AUTH_TOKEN");
  }

  public String getTwilioNumber() throws UndefinedEnvironmentVariableException {
    return getEnvironmentVariable("TWILIO_NUMBER");
  }

  private String getEnvironmentVariable(String variableName) throws UndefinedEnvironmentVariableException {
    String phoneNumber = env.get(variableName);
    if (phoneNumber == null) {
      throw new UndefinedEnvironmentVariableException(variableName + " is not set");
    } else {
      return phoneNumber;
    }
  }
}
