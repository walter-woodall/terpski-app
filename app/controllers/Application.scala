package controllers

import java.text.SimpleDateFormat
import java.util.Calendar

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
      "deposit" -> optional(text),
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
        Ok(html.index(user))
      }.getOrElse {
        Ok(html.index(null))
      }
    }.getOrElse {
      Ok(html.index(null))
    }
  }

  def trips(name: String) = Action { implicit request =>
    request.session.get("email").map { email =>
      User.findByEmail(email).map { user =>
        if(name.equals("kill_weekend")){
          Ok(html.trip1(user))
        }else if(name.equals("quebec")){
          Ok(html.trip2(user))
        }else if(name.equals("jackson_hole")){
          Ok(html.trip3(user))
        }else{
          Ok(html.trip4(user))
        }
      }.getOrElse {
        if(name.equals("kill_weekend")){
          Ok(html.trip1(null))
        }else if(name.equals("quebec")){
          Ok(html.trip2(null))
        }else if(name.equals("jackson_hole")){
          Ok(html.trip3(null))
        }else{
          Ok(html.trip4(null))
        }
      }
    }.getOrElse {
      if(name.equals("kill_weekend")){
        Ok(html.trip1(null))
      }else if(name.equals("quebec")){
        Ok(html.trip2(null))
      }else if(name.equals("jackson_hole")){
        Ok(html.trip3(null))
      }else{
        Ok(html.trip4(null))
      }
    }
  }

  def payment(tripName: String, paymentType: String, checkoutId: String) = Action {implicit request =>
    request.session.get("email").map { email =>
      User.findByEmail(email).map { user =>
        val format = new SimpleDateFormat("dd-MM-yyyy")
        val currentDate = format.format(Calendar.getInstance().getTime)
        val trip = new Trip(user.email, user.personalInfo.firstName + " " + user.personalInfo.lastName, tripName, paymentType, currentDate, checkoutId)
        Trip.create(trip)
        Redirect(routes.Application.index).flashing("success" -> "We received your payment").withSession("email" -> user.email)
      }.getOrElse{
        Redirect(routes.Application.index).flashing("error" -> "We could not locate your account.Please contact walter.woodall.iv@gmail.com with the screen shot of your error").withSession("email" -> email)
      }
    }.getOrElse{
      Redirect(routes.Application.index).flashing("error" -> "We could not validate your session. Please contact walter.woodall.iv@gmail.com with the screen shot of your error")
    }
  }

  def deposit(checkoutId: String) = Action {implicit request =>
    request.session.get("email").map { email =>
      User.findByEmail(email).map { user =>
        User.updateDeposit(user, checkoutId)
        Redirect(routes.Application.index).flashing("success" -> "We received your payment").withSession("email" -> user.email)
      }.getOrElse{
        Redirect(routes.Application.index).flashing("error" -> "We could not locate your account.Please contact walter.woodall.iv@gmail.com with the screen shot of your error").withSession("email" -> email)
      }
    }.getOrElse{
      Redirect(routes.Application.index).flashing("error" -> "We could not validate your session. Please contact walter.woodall.iv@gmail.com with the screen shot of your error")
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