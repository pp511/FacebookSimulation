package Server
import scala.collection.mutable
import Array._
import scala.collection.mutable.SynchronizedBuffer
import scala.collection.mutable.SynchronizedMap



class Album(albumId : Int) {
  var picmap = new mutable.HashMap[Int,String]
  var picidlist = new mutable.ArrayBuffer[Int]() with SynchronizedBuffer[Int]
  var lastpicid = 0
  val albumid = albumId
}

class User(userId : Int,loginId : String, passWd : String, firstName : String ,lastName  : String ,Gender : String ,Country : String ,City : String ,Profession : String ,Interestedin : String) {
  var friendlist = new mutable.ArrayBuffer[Friend]() with SynchronizedBuffer[Friend]
  var friendidlist = new mutable.ArrayBuffer[Int]() with SynchronizedBuffer[Int]

  var postlist = new mutable.ArrayBuffer[String]() with SynchronizedBuffer[String]

  var albummap = new mutable.HashMap[Int,Album]

  var albumlist = new mutable.ArrayBuffer[Album]
  var albumidlist = new mutable.ArrayBuffer[Int]

//  var profilepic =  new Array[Byte](50)
  var profilepic = ""
  val userid = userId
  val loginid = loginId
  val passwd = passWd
  val gender = Gender
  val firstname = firstName
  val lastname = lastName
  var country = Country
  var city = City
  var profession = Profession
  var interestedin = Interestedin
  /* Intrinsic values*/
   var lastpostid = 0;
}

