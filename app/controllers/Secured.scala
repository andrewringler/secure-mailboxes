package controllers

import play.api._
import play.api.mvc._
import org.apache.commons.codec.binary.Base64.decodeBase64

trait Secured {
  val basicAuthUsername = Play.current.configuration.getString("basic.auth.username").get
  val basicAuthPassword = Play.current.configuration.getString("basic.auth.password").get

  // see https://gist.github.com/guillaumebort/2328236
  // for another cleaner? alternative
  
  private def username(request: RequestHeader): Option[String] = {
    request.headers.get("Authorization").flatMap { authorization =>
      authorization.split(" ").drop(1).headOption.flatMap { encoded =>
        new String(decodeBase64(encoded.getBytes)).split(":").toList match {
          case username :: password :: Nil if (username == basicAuthUsername && password == basicAuthPassword) => Some(username)
          case _ => None
        }
      }
    }
  }

  private def onUnauthorized(request: RequestHeader) = Results.Unauthorized.withHeaders("WWW-Authenticate" -> "Basic realm=\"secure-mailboxes\"")

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }
}