package models

import java.util.Date
import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
/**
 * Created by walterwoodall on 5/3/15.
 */
case class Trip (id: Int,
                  name: String,
                  image: String,
                  deposit: Int,
                  finalPayment: Int,
                  depositDate: Date,
                  finalDate: Date,
                  details: String)

object Trip {
  /**
   * Parser for Trips Model
   */
  val simple = {
    get[Int]("trips.id") ~
    get[String]("trips.name") ~
    get[String]("trips.image") ~
    get[Int]("trips.deposit") ~
    get[Int]("trips.final_payment") ~
    get[Date]("trips.deposit_date") ~
    get[Date]("trips.final_date") ~
    get[String]("trips.details") map{
      case id~name~image~deposit~finalPayment~depositDate~finalDate~details => Trip(id, name, image, deposit, finalPayment,
        depositDate, finalDate, details)
    }
  }

  // Queries


}
