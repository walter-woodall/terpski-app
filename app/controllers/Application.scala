package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Application extends Controller {
  val userForm = Form(
    mapping(
      "email" -> email,
      "password" -> text,
      "userInfo" -> mapping(
        "firstName" -> text,
        "lastName" -> text,
        "cellPhone" -> text,
        "homePhone" -> text,
        "gender" -> text,
        "shirtSize" -> text,
        "sport" -> text,
        "classYear" -> text,
        "skillLevel" -> text,
        "reference" -> text,
        "emergencyName" -> text,
        "emergencyPhone" -> text,
        "uid" -> number,
        "age" -> number,
        "memberYears" -> number,
        "roomates" -> list(text)
      )(UserInfo.apply)(UserInfo.unapply),
      "isAdmin" -> optional(boolean),
      "deposit" -> optional(boolean),
      "active" -> optional(boolean)
    )(User.apply)(User.unapply)
  )
  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => User.authenticate(email, password).isDefined
    })
  )

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(html.login(loginForm, null))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors, null)),
      user => Redirect(routes.Application.index).withSession("email" -> user._1)
    )
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  def create = Action { implicit request =>
    Ok(html.create(userForm, null))
  }

  def createUser = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.create(formWithErrors, null)),
      user => {
        val account = User.create(user)
        Redirect(routes.Application.index).withSession("email" -> account.email)
      }
    )
  }

  def index = Action { implicit request =>
    request.session.get("email").map { email =>
      User.findByEmail(email).map { user =>
        Ok(html.index2(user))
      }.getOrElse {
        Ok(html.index2(null))
      }
    }.getOrElse {
      Ok(html.index2(null))
    }
  }

  def trips = Action { implicit request =>
    request.session.get("email").map { email =>
      User.findByEmail(email).map { user =>
        Ok(html.trip(user))
      }.getOrElse {
        Ok(html.trip(null))
      }
    }.getOrElse {
      Ok(html.trip(null))
    }
  }

}/**
 * Provide security features
 */
trait Secured {

  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("email")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)

  // --

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }
  /*
  /**
   * Check if the connected user is a member of this project.
   */
  def IsMemberOf(project: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
    if(Project.isMember(project, user)) {
      f(user)(request)
    } else {
      Results.Forbidden
    }
  }

  /**
   * Check if the connected user is a owner of this task.
   */
  def IsOwnerOf(task: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
    if(Task.isOwner(task, user)) {
      f(user)(request)
    } else {
      Results.Forbidden
    }
  }
  */
}