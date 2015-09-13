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
                  paymentType: String,
                  date: String,
                  checkoutId: String)

object Trip {
  /**
   * Parser for Trips Model
   */
  val simple = {
    get[String]("trips.email") ~
    get[String]("trips.name") ~
    get[String]("trips.trip_name") ~
    get[String]("trips.payment_type") ~
    get[String]("trips.date") ~
    get[String]("trips.checkout_id")  map{
      case email~name~tripName~paymentType~date~checkoutId => Trip(email, name, tripName, paymentType, date, checkoutId)
    }
  }

  // Queries
  def create(trip: Trip): Trip = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into trips values (
            {email}, {name}, {tripName}, {paymentType}, {date}, {checkoutId}
          )
        """
      ).on(
          'email -> trip.email,
          'name -> trip.name,
          'tripName -> trip.tripName,
          'paymentType -> trip.paymentType,
          'date -> trip.date,
          'checkoutId -> trip.checkoutId
        ).executeUpdate()
      trip
    }
  }
}
