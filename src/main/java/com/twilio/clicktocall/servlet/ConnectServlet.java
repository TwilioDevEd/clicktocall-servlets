package com.twilio.clicktocall.servlet;

import com.twilio.clicktocall.exceptions.UndefinedEnvironmentVariableException;
import com.twilio.clicktocall.lib.AppSetup;
import com.twilio.security.RequestValidator;
import com.twilio.twiml.Hangup;
import com.twilio.twiml.Say;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;

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
public class ConnectServlet extends HttpServlet {
    private RequestValidator requestValidator;
    private ResponseWriter responseWriter;

    @SuppressWarnings("unused")
    public ConnectServlet() throws UndefinedEnvironmentVariableException {
        this.responseWriter = new ResponseWriter();
        this.requestValidator = new RequestValidator(new AppSetup().getAuthToken());
    }

    public ConnectServlet(RequestValidator requestValidator, ResponseWriter responseWriter) {
        this.requestValidator = requestValidator;
        this.responseWriter = responseWriter;
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
        if (isValidRequest(request)) {
            String r = getXMLResponse();
            responseWriter.writeIn(response, r);
        } else {
            responseWriter.writeIn(response, "Invalid twilio request");
        }
    }


    /**
     * Generates the TwiML with a Say and Hangout verb
     * @return String with the TwiML
     */
    private String getXMLResponse() {
        VoiceResponse response =  new VoiceResponse.Builder()
                .say(new Say.Builder(
                        "If this were a real click to call implementation, " +
                        "you would be connected to an agent at this point.").build())
                .hangup(new Hangup())
                .build();

        try {
            return response.toXml();
        } catch (TwiMLException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Uses TwilioUtils to validate that the incoming request comes from Twilio automated services
     * @param request passed servlet request to extract parameters necessary for validation
     * @return boolean determining validity of the request
     */
    private boolean isValidRequest(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        Map<String, String> params = new HashMap<>();

        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String currentName = names.nextElement();
            params.put(currentName, request.getParameter(currentName));
        }

        String signature = request.getHeader("X-Twilio-Signature");

        return requestValidator.validate(url, params, signature);
    }

}
