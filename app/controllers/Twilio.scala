package controllers

import com.twilio.sdk.verbs.Say
import com.twilio.sdk.verbs.TwiMLException
import com.twilio.sdk.verbs.TwiMLResponse

import play.api._
import play.api.mvc._

object Twilio extends Controller with Secured {
  def recordVoiceMessage = IsAuthenticated { _ =>
    _ =>
      val twiml = new TwiMLResponse();
      val say = new Say("Hello There");
      try {
        twiml.append(say);
      } catch {
        case e: TwiMLException => Logger.error("Unable to generate response", e)
      }
      
      Ok(scala.xml.XML.loadString(twiml.toXML()))
  }
}