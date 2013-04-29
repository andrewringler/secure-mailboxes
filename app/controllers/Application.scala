package controllers

import play.api._
import play.api.mvc._
import com.twilio.sdk.client.TwilioCapability
import com.twilio.sdk.client.TwilioCapability.DomainException
import com.twilio.sdk.verbs.TwiMLResponse

object Application extends Controller with Secured {
  val accountSid = Play.current.configuration.getString("twilio.accountsid").get
  val authToken = Play.current.configuration.getString("twilio.authtoken").get
  val applicationSid = Play.current.configuration.getString("twilio.appid").get

  def index = IsAuthenticated { _ =>
    _ =>
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

  
  import com.twilio.sdk.verbs.Say
  import com.twilio.sdk.verbs.Record
  import com.twilio.sdk.verbs.TwiMLException

  def recordVoiceMessage = IsAuthenticated { _ =>
    _ =>
      val twiml = new TwiMLResponse()
      try {
        val record = new Record()
        record.setAction(controllers.routes.Application.confirmRecordVoiceMessage("").url.toString())
        record.setMethod("GET")
        record.setFinishOnKey("#")
        record.setMaxLength(30)

        twiml.append(new Say("Please record your message at the beep."));
        twiml.append(record)
        twiml.append(new Say("I did not hear a recording.  Goodbye."));
      } catch {
        case e: TwiMLException => Logger.error("Unable to generate response", e)
      }

      Ok(scala.xml.XML.loadString(twiml.toXML()))
  }

  def confirmRecordVoiceMessage(recordingUrl: String) = IsAuthenticated { _ =>
    _ =>
      val twiml = new TwiMLResponse()
      try {
        twiml.append(new Say("Thanks for your recording.  Here is what I heard"));
        twiml.append(new com.twilio.sdk.verbs.Play(recordingUrl))
        twiml.append(new Say("Goodbye"));
      } catch {
        case e: TwiMLException => Logger.error("Unable to generate response", e)
      }

      Ok(scala.xml.XML.loadString(twiml.toXML()))
  }
}