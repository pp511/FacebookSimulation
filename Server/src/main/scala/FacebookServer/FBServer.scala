package Server
import akka.actor._
import akka.actor.{ActorSystem, Props}
import akka.actor.Actor
import collection.mutable.ListBuffer
import akka.routing.RoundRobinRouter
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
//import client._
//import common._
import java.io.FileWriter
import Server._
import akka.actor.{ActorSystem, Props}
import akka.actor.Actor
import akka.routing.RoundRobinRouter
import com.typesafe.config.ConfigFactory
import org.json4s.ShortTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import spray.json.DefaultJsonProtocol
import akka.actor.ActorSystem
import scala.collection.mutable
import scala.collection.mutable.SynchronizedBuffer
import scala.collection.mutable.SynchronizedMap
import play.api.libs.json.Json
import scala.util.parsing.json._





//import common.Messages
case class TBD()



/* Functions of Server Class*/
/*
case class createUser(id: Int,loginid : String, passwd : String, firstname: String, lastname: String, sex : String, country : String,city : String, profession : String, interestedin : String)
*/
case class createUser(createuserparam : String)

case class addFriend(id: Int, idf: Int)
case class searchUser(nameoffriend : String, lnameoffriend : String)
case class deleteFriend(id: Int, idf: Int)  // Not Implemented
case class createPost(id: Int, post: String)
case class viewPost(id: Int, idf : Int)
case class deletePost(id: Int, postId:Int) // Not Implemented
case class viewfriendList(id: Int, idf : Int)
case class viewPage(id: Int, idf : Int)
case class viewProfile(id: Int, idf : Int)


//
case class addProfilePic(id: Int ,profpic:String)
case class viewProfilePic(id: Int, idf : Int)
case class createAlbum(id: Int , albumid : Int)
case class addPicstoAlbum(id: Int , albumid : Int ,pic: String)
case class viewAlbumPics(id: Int ,idf : Int, albumid : Int , picid : Int )



/*
case class Person(userId : Int,loginId : String, passWd : String, firstName : String ,lastName: String)
  object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val personFormat = jsonFormat5(Person)
}
import MyJsonProtocol._


class UserType(userId : Int,loginId : String, passWd : String, firstName : String ,lastName: String){
  var userid = userId
  var logind = loginId
  var passwd = passWd
  var firstname = firstName
  var lastname = lastName
}*/

/*class UserType(Name: String, fistName: String){
  val name= Name
  val firstname = fistName
}*/


class FBServer extends Actor {
  private implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[String])))
  import context.dispatcher
  System.setProperty("java.net.preferIPv4Stack", "true")
  val noOfWorkers = ((Runtime.getRuntime().availableProcessors()) * 1.5).toInt
  val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinRouter(noOfWorkers)), name = "workerRouter")
  var listOfusers = new ListBuffer[ActorRef]
  def receive = {
      case TBD() => {
      }
    }
}
class Worker extends Actor {
  implicit val system = context.system
  import system.dispatcher
  context.system.scheduler.schedule(Duration.create(120000, TimeUnit.MILLISECONDS),
   Duration.create(10000, TimeUnit.MILLISECONDS))(printStatus)

  private implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[String])))
  def receive = {
/*    case createUser(id: Int,loginid : String, passwd: String, firstname: String, lastname: String, gender : String, country : String,city : String, profession : String, interestedin : String) => {
      val currentuser = new User(id,loginid, passwd, firstname,lastname,gender,country,city, profession,interestedin)
      Global.totalUsers+=1
      Global.usermap.put(id, currentuser)

      sender ! "User Account Created"
    }*/
    case createUser(createuserparam : String) => {
/*
      println("Create User Receied")
*/
      val createUserrequest = Json.parse(createuserparam)
      val currentuser = new User(createUserrequest.\("userid").as[Int],
                                  createUserrequest.\("loginid").as[String],
                                  createUserrequest.\("password").as[String],
                                  createUserrequest.\("firstname").as[String],
                                  createUserrequest.\("lastname").as[String],
                                  createUserrequest.\("gender").as[String],
                                  createUserrequest.\("country").as[String],
                                  createUserrequest.\("city").as[String],
                                  createUserrequest.\("profession").as[String],
                                  createUserrequest.\("interestedin").as[String])
      Global.totalUsers+=1
    //  println(createUserrequest.\("userid").as[Int]+ " Created")
      Global.useridlist += createUserrequest.\("userid").as[Int]
      Global.usermap.put(createUserrequest.\("userid").as[Int], currentuser)
      sender ! "User Account Created"
    }
    //*******************************************************************************************************************
/*    case searchUser(nameoffriend : String, lnameoffriend : String) => {
   //   println("findFriend request received")
      var nameList = new ListBuffer[Int]()
      for((key,value) <- Global.usermap)
        if(value.firstname.equals(nameoffriend)&& (value.lastname.equals(lnameoffriend))){
       //   println("Friend Found")
          nameList += value.userid
        }
      Global.searchRequests+=1
      sender ! writePretty(nameList)
    }*/
    case searchUser(nameoffriend : String, lnameoffriend : String) => {
      //   println("findFriend request received")
      var nameList = new ListBuffer[Int]()
      for((key,value) <- Global.usermap)
        if(value.firstname.equals(nameoffriend)&& (value.lastname.equals(lnameoffriend))){
          //   println("Friend Found")
          nameList += value.userid
        }
      Global.searchRequests+=1
      sender ! writePretty(nameList)
    }
    //*******************************************************************************************************************
    case addFriend(id: Int, idf: Int) => {
    //  println("Addfriend request received")

      if(Global.useridlist.exists(_ == idf)) {
        var currentuser = Global.usermap(id)
        var frienduser = Global.usermap(idf)

        var friend = new Friend(frienduser.firstname, frienduser.lastname)
        var current = new Friend(currentuser.firstname, currentuser.lastname)

        currentuser.friendlist += friend
        frienduser.friendlist += current

        currentuser.friendidlist += idf
        frienduser.friendidlist += id

        Global.totalFriendRequestSent += 1
        sender ! "Friend Added"
      }
      else{
        Global.totalRejectedRequests+=1
        sender ! "User Does Not Exist"
      }
    }
    //******************************************************************************************************************
    case deleteFriend(id: Int, idf: Int) => { // To DO
  //    println("-----Exit")

      sender ! "Friend Deleted"

    }
    //******************************************************************************************************************
    case createPost(id: Int, post: String) => {
    //  println("createPost request received")
      var currentuser =  Global.usermap(id)
      currentuser.lastpostid +=1;
      currentuser.postlist +=post
      Global.totalPosts+=1
      sender ! "Post Created"
    }
    //*******************************************************************************************************************
    case viewPost(id: Int, idf : Int) => {
   //   println("viewPost request received")
      if(Global.useridlist.exists(_ == idf)) {
        if (isMutualFriend(id, idf)) {
          sender ! writePretty(Global.usermap(idf).postlist)
          Global.totalPostViews += 1
        }
        else {
          Global.totalRejectedRequests += 1
          sender ! "You Are Not Allowed to Access Posts From This profile"
        }
      } else{
        Global.totalRejectedRequests+=1
        sender ! "User Does Not Exist"
      }
    }
    //********************************************************************************************************************
    case deletePost(id: Int, postId:Int) => { // To DO
      var currentuser =  Global.usermap(id)
      sender ! "Post Deleted"
       }
    //*******************************************************************************************************************
    case viewfriendList(id: Int, idf : Int ) => {
  //    println("viewfriendList request received")
      if(Global.useridlist.exists(_ == idf)) {
        Global.totalviewFriendLists+=1
        /*Ignoring User ID. To be used later in secured implemetation*/
        sender ! writePretty(Global.usermap(idf).friendlist)
      }
      else{
        Global.totalRejectedRequests+=1
        sender ! "User Does Not Exist"
      }

    }
    //*******************************************************************************************************************
    case viewPage(id: Int, idf : Int) => {
  //    println("viewPage request received")
      if(Global.useridlist.exists(_ == idf)) {
        var otheruser = Global.usermap(idf)
        Global.totalPageViews += 1
     //   println("viewPage requested")
        if (isMutualFriend(id, idf)) {
          sender ! writePretty(PersonPage(
            otheruser.loginid,
            otheruser.firstname,
            otheruser.lastname,
            otheruser.gender,
            otheruser.country,
            otheruser.city,
            otheruser.profession,
            otheruser.interestedin,
            otheruser.friendlist,
            otheruser.postlist,
            otheruser.albumidlist,
            otheruser.profilepic
          ))
      //    println("Provide")
        }
        else {
          // Limited access to profile since user is not a friend
          sender ! writePretty(LimitedProfile(
            otheruser.firstname,
            otheruser.lastname,
            otheruser.gender,
            otheruser.country,
            otheruser.city,
            otheruser.profession
          ))

        }
      }
      else{
        Global.totalRejectedRequests+=1
        sender ! "User Does Not Exist"
      }

    }
    //*******************************************************************************************************************
    case viewProfile(id: Int, idf : Int) => {
   //   println("viewProfile request received")
      if(Global.useridlist.exists(_ == idf)) {
        var otheruser = Global.usermap(idf)
        Global.totalProfileViews += 1
        sender ! writePretty(LimitedProfile(
          otheruser.firstname,
          otheruser.lastname,
          otheruser.gender,
          otheruser.country,
          otheruser.city,
          otheruser.profession
        ))
      }
      else{
        Global.totalRejectedRequests+=1
        sender ! "User Does Not Exist"
      }

    }
    //*******************************************************************************************************************
    case addProfilePic(id: Int , profpic:String) => {
  //    println("addProfilePic request received")
      Global.usermap(id).profilepic = profpic
      Global.totalProfilePicsUpdated+=1
      sender ! "Profile Pic Added"
    }
    //*******************************************************************************************************************

    case viewProfilePic(id: Int, idf : Int) => {
      if(Global.useridlist.exists(_ == idf)) {
        var otheruser =  Global.usermap(idf)
        Global.totalProfilePicViews+=1
        sender ! writePretty(otheruser.profilepic)
      }
      else{
        Global.totalRejectedRequests+=1
        sender ! "User Does Not Exist"
      }
    }
    //*******************************************************************************************************************
    case createAlbum(id: Int , albumid : Int) => {
  //    println("createAlbum request received")
      var currentuser =  Global.usermap(id)
      if(currentuser.albumidlist.exists(_ == albumid)) {
        Global.totalRejectedRequests+=1
        sender ! "Album  Already Exists"
      }
      else {
        var newalbum = new Album(albumid)
        currentuser.albummap.put(albumid,newalbum)
        currentuser.albumidlist+=albumid
        Global.totalAlbumsCreated+=1
        sender ! "Album  Created"
        }
    }
    //*******************************************************************************************************************
    case addPicstoAlbum(id: Int , albumid : Int ,pic:String) => {
  //    println("addPicstoAlbum request received")
      var currentuser =  Global.usermap(id)
      if(currentuser.albumidlist.exists(_ == albumid)){
  //      println("Condition Succeeded albumid"+albumid)
        currentuser.albummap(albumid).lastpicid+=1
        currentuser.albummap(albumid).picidlist+= currentuser.albummap(albumid).lastpicid
        currentuser.albummap(albumid).picmap.put(currentuser.albummap(albumid).lastpicid,pic)
        Global.totaPicsAddedtoAlbum+=1
        sender ! "Pic Added in Album"
      }
      else{
        Global.totalRejectedRequests+=1
        sender ! "Album Does Not Exist"
      }

    }
    //*******************************************************************************************************************
    case viewAlbumPics(id: Int , idf: Int, albumid : Int , picid : Int ) => {
      if(Global.useridlist.exists(_ == idf)) {
        //   println("viewAlbumPics request received")
        if(isMutualFriend(id,idf)){
          Global.totalAlbumPicsViewed+=1
          var otheruser =  Global.usermap(idf)
          if(otheruser.albumidlist.exists(_ == albumid)){
            //  println("Returning Desired Pic")
            if(otheruser.albummap(albumid).picidlist.exists(_ == picid)){
              var reqpic =  otheruser.albummap(albumid).picmap(picid) // To be Corrected
              sender ! writePretty(reqpic)
            }
            else{
              Global.totalRejectedRequests+=1
              sender ! "Pic Does Not Exist"
            }

          }
          else{
            //    println("Album Does Not Exist")
            Global.totalRejectedRequests+=1
            sender ! "Album Does Not Exist"
          }

        }
        else{
          Global.totalRejectedRequests+=1
          sender ! "Aunauthorized Access"
        }
      }
      else{
        Global.totalRejectedRequests+=1
        sender ! "Album Does Not Exist"
      }
    }

  }
  def isMutualFriend(id: Int, idf : Int): Boolean ={
    if(id == idf)
        return true
    var currentuser =  Global.usermap(id)
    var seconduser =  Global.usermap(id)
    return currentuser.friendidlist.exists(_ == idf)
  }
  def printStatus()={
    Global.Recent_Requests +=  Global.totalUsers+Global.totalPosts+Global.totalPostViews+Global.totalPageViews+Global.totalProfileViews+Global.searchRequests+Global.totalFriendRequestSent+Global.totalviewFriendLists+Global.totalProfilePicsUpdated+Global.totalProfilePicViews+
                                 +Global.totalAlbumsCreated+Global.totaPicsAddedtoAlbum+Global.totalAlbumPicsViewed+Global.totalRejectedRequests

    Global.Total_Requests_Served+=Global.Recent_Requests



    var  timeElapsed =  (System.currentTimeMillis() - Global.starttime)/1000
    var  avg = Global.Recent_Requests/(timeElapsed+1)
    if(avg < Global.Max_Average_Possible){
      if((avg > Global.Max_Average)){// && (avg < 0X1FFF)){
        Global.Max_Average=avg
      }
    }

    println(
    "******************************************************************************************************"+
/*      "\n User Created = " +Global.totalUsers+
        "\n Posts Count = "+Global.totalPosts+
        "\n Post Views  = "+Global.totalPostViews+
        "\n Page Views = "+Global.totalPageViews+
        "\n Profile Views = "+Global.totalProfileViews+
        "\n Search Requests = "+Global.searchRequests+
        "\n Friend Request Count = "+Global.totalFriendRequestSent+
        "\n View Friend List Count = "+Global.totalviewFriendLists+
        "\n Profile Pics Update Count = "+Global.totalProfilePicsUpdated+
        "\n Profile Pic Views = "+Global.totalProfilePicViews+
        "\n Albums Created = "+Global.totalAlbumsCreated+
        "\n Total Pics Added to Albums = "+Global.totaPicsAddedtoAlbum+
        "\n Total Album Pics Viewed = "+Global.totalAlbumPicsViewed+
        "\n Total Requests Rejected = "+Global.totalRejectedRequests+*/
        "\n **********************************"+
        "\n Total Requests Served so Far = "+Global.Total_Requests_Served+
       // "\n Recent Requests(In Last 10 sec) = "+Global.Recent_Requests+
       // "\n Recent Time Elapsed = "+timeElapsed+" sec"+
       // "\n Current Average(of Last 10 sec) = "+avg+
      "\n Max Average Requests PerSecond(Over period of 10 sec)= ***** "+Global.Max_Average+" ********"+
      "\n **********************************"
    )
    Global.totalUsers=0
    Global.totalPosts=0
    Global.totalPostViews=0
    Global.totalPageViews=0
    Global.totalProfileViews=0
    Global.searchRequests=0
    Global.totalFriendRequestSent=0
    Global.totalviewFriendLists=0
    Global.totalProfilePicsUpdated=0
    Global.totalProfilePicViews=0
    Global.totalAlbumsCreated=0
    Global.totaPicsAddedtoAlbum=0
    Global.totalAlbumPicsViewed=0
    Global.totalRejectedRequests=0
    Global.Recent_Requests=0
    Global.starttime = System.currentTimeMillis()

  }

}
