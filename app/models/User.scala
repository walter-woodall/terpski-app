package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
/**
 * Created by walterwoodall on 4/28/15.
 */
case class UserInfo (firstName: String,
                      lastName: String,
                      cellPhone: String,
                      homePhone: String,
                      gender: String,
                      shirtSize: String,
                      sport: String,
                      classYear: String,
                      skillLevel: String,
                      reference: String,
                      emergencyName: String,
                      emergencyPhone: String,
                      uid: Int,
                      age: Int,
                      memberYears: Int,
                      roomates: List[String])

case class User ( email: String,
                  password: String,
                  personalInfo: UserInfo,
                  isAdmin: Option[Boolean],
                  deposit: Option[String],
                  active: Option[Boolean])

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
      get[Option[Boolean]]("users.is_admin") ~
      get[Option[String]]("users.deposit") ~
      get[Option[Boolean]]("users.active") map {
      case email~password~firstName~lastName~uid~cellPhone~homePhone~emergencyName~emergencyPhone~
        age~memberYears~gender~shirtSize~sport~classYear~skillLevel~reference~admin~deposit~active =>
        User(email, password, UserInfo(firstName, lastName, cellPhone, homePhone, gender, shirtSize, sport, classYear,
          skillLevel, reference, emergencyName, emergencyPhone, uid, age, memberYears, null),
          admin, deposit, active)
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
  def updateDeposit(user: User, checkoutId: String): Unit ={
    DB.withConnection { implicit connection =>
      SQL(
        """
         update users set deposit = {checkoutId} where
         email = {email}
        """
      ).on(
          'email -> user.email,
          'checkoutId -> checkoutId
        ).executeUpdate()
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
          'firstName -> user.personalInfo.firstName,
          'lastName -> user.personalInfo.lastName,
          'uid -> user.personalInfo.uid,
          'cellPhone -> user.personalInfo.cellPhone,
          'homePhone -> user.personalInfo.homePhone,
          'emergencyName -> user.personalInfo.emergencyName,
          'emergencyPhone -> user.personalInfo.emergencyPhone,
          'age -> user.personalInfo.age,
          'memberYears -> user.personalInfo.memberYears,
          'gender -> user.personalInfo.gender,
          'shirtSize -> user.personalInfo.shirtSize,
          'sport -> user.personalInfo.sport,
          'classYear -> user.personalInfo.classYear,
          'skillLevel -> user.personalInfo.skillLevel,
          'reference -> user.personalInfo.reference,
          'isAdmin -> user.isAdmin,
          'deposit -> user.deposit,
          'active -> user.active
        ).executeUpdate()
      user
    }
  }
}
