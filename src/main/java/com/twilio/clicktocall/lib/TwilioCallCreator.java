package com.twilio.clicktocall.lib;

import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.CallCreator;
import com.twilio.type.PhoneNumber;

import java.net.URI;

public class TwilioCallCreator {
    private final TwilioRestClient client;

    public TwilioCallCreator(TwilioRestClient client) {
        this.client = client;
    }

    public Call create(String from, String to, URI uri) {
        return new CallCreator(
                new PhoneNumber(to),
                new PhoneNumber(from),
                uri).create(this.client);
    }
}
