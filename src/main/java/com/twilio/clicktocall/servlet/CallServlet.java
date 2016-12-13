package com.twilio.clicktocall.servlet;

import com.twilio.clicktocall.exceptions.UndefinedEnvironmentVariableException;
import com.twilio.clicktocall.lib.AppSetup;
import com.twilio.clicktocall.lib.TwilioCallCreator;
import com.twilio.exception.TwilioException;
import com.twilio.http.TwilioRestClient;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;


@WebServlet("/call")
public class CallServlet extends HttpServlet {
    private final TwilioCallCreator twilioCallCreator;
    private final String twilioNumber;

    @SuppressWarnings("unused")
    public CallServlet()
            throws UndefinedEnvironmentVariableException {
        AppSetup appSetup = new AppSetup();
        TwilioRestClient client = new TwilioRestClient.Builder(
                appSetup.getAccountSid(), appSetup.getAuthToken()).build();

        this.twilioNumber = appSetup.getTwilioNumber();
        this.twilioCallCreator = new TwilioCallCreator(client);
    }

    public CallServlet(AppSetup appSetup, TwilioCallCreator twilioCallCreator)
            throws UndefinedEnvironmentVariableException {
        this.twilioNumber = appSetup.getTwilioNumber();
        this.twilioCallCreator = twilioCallCreator;
    }

    /**
     * Method that triggers a call to the specified number in the request
     * @param request incoming servlet request object
     * @param response servlet response object
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userNumber = request.getParameter("userNumber");
        String salesNumber = request.getParameter("salesNumber");
        if (userNumber == null) {
            response.getOutputStream()
                    .write(getJSONResponse("The phone number field can't be empty").getBytes());
            return;
        }

        // Full URL to the end point that will respond with the call TwiML.
        String encodedSalesNumber = URLEncoder.encode(salesNumber, "UTF-8");
        String url = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        url += "/connect/" + encodedSalesNumber;

        try {
            twilioCallCreator.create(this.twilioNumber, userNumber, new URI(url));
        } catch (TwilioException e) {
            String message =
                    "Twilio rest client error: " + e.getMessage() + "\n" +
                    "Remember not to use localhost to access this app, use your ngrok URL";

            response.getOutputStream()
                    .write(getJSONResponse(message).getBytes());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        response.getOutputStream()
                .write(getJSONResponse("Phone call incoming!").getBytes());
    }

    private String getJSONResponse(String message) {
        JSONObject obj = new JSONObject();
        obj.put("message", message);
        obj.put("status", "ok");

        return obj.toString();
    }
}