import models.User
import org.scalatestplus.play._

import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
/**
 * Created by walterwoodall on 4/28/15.
 */

class Test extends PlaySpec with OneAppPerSuite{

  "Create User" should {
    "create and retrieve a User" in {

      User.create(User("walter.woodall.iv@gmail.com", "RasinIV4", "Walter", "Woodall", 110968689, "3023979589", "3023979589",
        "Nancy Woodall", "3022936495", 22, 3, "M", "L", "Snowboard", "Other", "Advanced", "Previous Member", true, false, true))

      val bob = User.findByEmail("walter.woodall.iv@gmail.com")

      bob mustNot be (None)
      bob.get.firstName must be ("Walter")

    }
  }
}
