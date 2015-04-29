package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
/**
 * Created by walterwoodall on 4/28/15.
 */
case class User ( email: String,
                  password: String,
                  firstName: String,
                  lastName: String,
                  uid: Int,
                  cellPhone: String,
                  homePhone: String,
                  emergencyName: String,
                  emergencyPhone: String,
                  age: Int,
                  memberYears: Int,
                  gender: String,
                  shirtSize: String,
                  sport: String,
                  classYear: String,
                  skillLevel: String,
                  reference: String,
                  //trips: List[String],
                  isAdmin: Boolean,
                  deposit: Boolean,
                  active: Boolean)

object User {

  /**
    Parser for the User
   */
  val simple = {
      get[String]("users.email") ~
      get[String]("users.password") ~
      get[String]("users.first_name") ~
      get[String]("users.last_name") ~
      get[Int]("users.uid") ~
      get[String]("users.cell_phone") ~
      get[String]("users.home_phone") ~
      get[String]("users.emergency_name") ~
      get[String]("users.emergency_phone") ~
      get[Int]("users.age") ~
      get[Int]("users.member_years") ~
      get[String]("users.gender") ~
      get[String]("users.shirt_size") ~
      get[String]("users.sport") ~
      get[String]("users.class_year") ~
      get[String]("users.skill_level") ~
      get[String]("users.reference") ~
      get[Boolean]("users.is_admin") ~
      get[Boolean]("users.deposit") ~
      get[Boolean]("users.active") map {
      case email~password~firstName~lastName~uid~cellPhone~homePhone~emergencyName~emergencyPhone~
        age~memberYears~gender~shirtSize~sport~classYear~skillLevel~reference~admin~deposit~active =>
        User(email, password, firstName, lastName, uid, cellPhone, homePhone, emergencyName, emergencyPhone,
          age, memberYears, gender, shirtSize, sport, classYear, skillLevel, reference, admin, deposit, active)
    }
  }

  // -- Queries

  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users where email = {email}").on(
        'email -> email
      ).as(User.simple.singleOpt)
    }
  }

  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from users").as(User.simple *)
    }
  }

  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from users where
         email = {email} and password = {password}
        """
      ).on(
          'email -> email,
          'password -> password
        ).as(User.simple.singleOpt)
    }
  }

  /**
   * Create a User.
   */
  def create(user: User): User = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into users values (
            {email}, {password}, {firstName}, {lastName}, {uid}, {cellPhone}, {homePhone},
            {emergencyName}, {emergencyPhone}, {age}, {memberYears}, {gender}, {shirtSize}, {sport},
            {classYear}, {skillLevel}, {reference}, {isAdmin}, {deposit}, {active}
          )
        """
      ).on(
          'email -> user.email,
          'password -> user.password,
          'firstName -> user.firstName,
          'lastName -> user.lastName,
          'uid -> user.uid,
          'cellPhone -> user.cellPhone,
          'homePhone -> user.homePhone,
          'emergencyName -> user.emergencyName,
          'emergencyPhone -> user.emergencyPhone,
          'age -> user.age,
          'memberYears -> user.memberYears,
          'gender -> user.gender,
          'shirtSize -> user.shirtSize,
          'sport -> user.sport,
          'classYear -> user.classYear,
          'skillLevel -> user.skillLevel,
          'reference -> user.reference,
          'isAdmin -> user.isAdmin,
          'deposit -> user.deposit,
          'active -> user.active
        ).executeUpdate()
      user
    }
  }
}
