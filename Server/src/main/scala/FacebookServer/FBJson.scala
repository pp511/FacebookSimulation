package Server

import spray.json.DefaultJsonProtocol

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val friendFormat = jsonFormat2(Friend)
  implicit val FriendListFormat = jsonFormat1(FriendList)
  implicit val personpageFormat = jsonFormat12(PersonPage)
  implicit val limitedprofileFormat = jsonFormat6(LimitedProfile)
}
case class Friend(firstname: String,lastname: String)
case class FriendList(frenlist : Seq[Friend])
case class PersonPage(
   loginid: String,
   firstname: String,
   lastname: String,
   gender: String,
   country: String,
   city: String,
   profession: String,
   interestedin : String,
   friendidlist: Seq[Friend],
   postlist: Seq[String],
   albumidlist : Seq[Int],
   profilepic : String
)
case class LimitedProfile(
    firstname: String,
    lastname: String,
    gender: String,
    country: String,
    city: String,
    profession : String
)
