package models

import java.util.Date
import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
/**
 * Created by walterwoodall on 5/3/15.
 */
case class Trip (email: String,
                  name: String,
                  tripName: String,
                  deposit: Int,
                  finalPayment: Int,
                  depositDate: Date,
                  finalDate: Date,
                  depositCheckoutId: String,
                  finalCheckoutId: String)

object Trip {
  /**
   * Parser for Trips Model
   */
  val simple = {
    get[String]("trips.email") ~
    get[String]("trips.name") ~
    get[String]("trips.trip_name") ~
    get[Int]("trips.deposit") ~
    get[Int]("trips.final_payment") ~
    get[Date]("trips.deposit_date") ~
    get[Date]("trips.final_date") ~
    get[String]("trips.deposit_checkout_id") ~
    get[String]("trips.final_checkout_id")  map{
      case email~name~tripName~deposit~finalPayment~depositDate~finalDate~depositCheckoutId~finalCheckoutId => Trip(email, name, tripName, deposit, finalPayment,
        depositDate, finalDate, depositCheckoutId, finalCheckoutId)
    }
  }

  // Queries
  def create(trip: Trip): Trip = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into trips values (
            {email}, {name}, {tripName}, {deposit}, {finalPayment}, {depositDate}, {finalDate},
            {depositCheckoutId}, {finalCheckoutId}
          )
        """
      ).on(
          'email -> trip.email,
          'name -> trip.name,
          'tripName -> trip.tripName,
          'deposit -> trip.deposit,
          'finalPayment -> trip.finalPayment,
          'depositDate -> trip.depositDate,
          'finalDate -> trip.finalDate,
          'depositCheckoutId -> trip.depositCheckoutId,
          'finalCheckoutId -> trip.finalCheckoutId
        ).executeUpdate()
      trip
    }
  }
}
