package controllers

import play.api._
import play.api.mvc._
import com.twilio.sdk.client.TwilioCapability
import com.twilio.sdk.client.TwilioCapability.DomainException

object Application extends Controller {

  def index = Action {
    val accountSid = Play.current.configuration.getString("twilio.accountsid").get
    val authToken = Play.current.configuration.getString("twilio.authtoken").get
    val applicationSid = Play.current.configuration.getString("twilio.appid").get
    
    val capability = new TwilioCapability(accountSid, authToken);
    capability.allowClientOutgoing(applicationSid);

    var token = ""
    try {
      token = capability.generateToken();
      Logger.info("Retrieved auth token from Twilio: " + token)
    } catch {
      case e: DomainException => {
        Logger.error("Unable to generate auth token with Twilio", e)
        throw new RuntimeException("Unable to generate auth token with Twilio")
      }
    }

    Ok(views.html.index(token))
  }

}