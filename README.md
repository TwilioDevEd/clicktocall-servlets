<a href="https://www.twilio.com">
  <img src="https://static0.twilio.com/marketing/bundles/marketing/img/logos/wordmark-red.svg" alt="Twilio" width="250" />
</a>

# Click to Call Servlets
[![Java Servlet CI](https://github.com/TwilioDevEd/clicktocall-servlets/actions/workflows/gradle.yml/badge.svg)](https://github.com/TwilioDevEd/clicktocall-servlets/actions/workflows/gradle.yml)

An example application implementing Click to Call using Twilio.

[Read the full tutorial here](https://www.twilio.com/docs/tutorials/walkthrough/click-to-call/java/servlets)!

## Set up

### Requirements

- [Java Development Kit](https://adoptopenjdk.net/) version 8
- [ngrok](https://ngrok.com)
- A Twilio account - [sign up](https://www.twilio.com/try-twilio)

### Twilio Account Settings

This application should give you a ready-made starting point for writing your
own appointment reminder application. Before we begin, we need to collect
all the config values we need to run the application:

| Config Value | Description                                                                                                                                                  |
| :---------------- | :----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Account Sid  | Your primary Twilio account identifier - find this [in the Console](https://www.twilio.com/console).                                                         |
| Auth Token   | Used to authenticate - [just like the above, you'll find this here](https://www.twilio.com/console).                                                         |
| Phone number | A Twilio phone number in [E.164 format](https://en.wikipedia.org/wiki/E.164) - you can [get one here](https://www.twilio.com/console/phone-numbers/incoming) |

### Local development

1. Clone the repository and `cd` into it.

1. The application uses Maven to manage dependencies.

1. Set your environment variables

    ```bash
    cp .env.example .env
    ```
   See [Twilio Account Settings](#twilio-account-settings) to locate the necessary environment variables.

1. Configure Twilio to call your webhooks.

   You will also need to configure Twilio to call your application when calls are received.

   You will need to provision at least one Twilio number with voice capabilities
   so the application's users can trigger phone calls. You can buy a number [right
   here](//www.twilio.com/user/account/phone-numbers/search). Once you have
   a number you need to configure your number to work with your application. Open
   [the number management page](//www.twilio.com/user/account/phone-numbers/incoming)
   and open a number's configuration by clicking on it.

   Remember that the number where you change the voice webhooks must be the same one you set on
   the `TWILIO_NUMBER` environment variable.

   ![Configure Voice](http://howtodocs.s3.amazonaws.com/twilio-number-config-all-med.gif)

1. Run the application using Maven.

   ```bash
   mvn compile && mvn jetty:run
   ```

   This will run the embedded Jetty application server that uses port 8080. You can change this value
   on the [app's main file](//github.com/TwilioDevEd/clicktocall-servlets/blob/master/src/main/java/com/twilio/clicktocall/App.java)

1. Expose the application to the wider Internet using [ngrok](https://ngrok.com/)

   ```bash
   ngrok 8080
   ```

   Once you have started ngrok, update your Twilio's number voice URL
   setting to use your ngrok hostname, so it will look something like
   this:

   ```
   http://<your-ngrok-subdomain>.ngrok.io/connect
   ```

### Dependencies

This application uses this Twilio helper library.

* [twilio-java](//github.com/twilio/twilio-java)

### Run the tests

1. Run at the top-level directory.

   ```bash
   mvn compile test
   ```

## Meta

* No warranty expressed or implied. Software is as is. Diggity.
* [MIT License](http://www.opensource.org/licenses/mit-license.html)
* Lovingly crafted by Twilio Developer Education.
