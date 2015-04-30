import models.{UserInfo, User}
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

      User.create(User("walter.woodall.iv@gmail.com", "RasinIV4",
        UserInfo("Walter", "Woodall", "3023979589", "3023979589", "M", "L", "Snowboard", "Other", "Advanced",
          "Previous Member", "Nancy Woodall", "3022936495", 110968689, 23, 3, null, null),
        Some(true), Some(true), Some(true)))

      User.create(User("michael.h.shaham@gmail.com", "Betalfa1",
        UserInfo("Michael", "Shaham", "4435427376", "4109975438", "M", "L", "Ski", "Junior", "Advanced",
          "Previous Member", "Karin Helmers", "4437455895", 112880149, 19, 2, null, null),
        Some(true), Some(true), Some(true)))

      //User.create(User("jamesdavidson278@gmail.com", "Calliezoe1", "James", "Davidson", 112441944, "4435530637", "4435530637",
        //"Diane Davidson", "4439451400", 21, 3, "M", "L", "Snowboard", "Senior", "Advanced", "Previous Member", true, true, true))

      val bob = User.findByEmail("walter.woodall.iv@gmail.com")

      bob mustNot be (None)
      bob.get.personalInfo.firstName must be ("Walter")

    }
  }
}
