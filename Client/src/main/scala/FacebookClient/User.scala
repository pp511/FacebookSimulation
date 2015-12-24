package Client
import akka.actor.ActorSystem
import spray.http.BasicHttpCredentials
import spray.client.pipelining._
import akka.actor.Actor
import akka.actor.Cancellable
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
//import org.apache.commons.httpclient.util.URIUtil
import scala.util.Random
import spray.http.HttpRequest
import java.io.RandomAccessFile
import java.util.Date
import java.io.File
import akka.actor.Props
import spray.http.HttpResponse
import Client._
import java.net.URLEncoder
import spray.json.DefaultJsonProtocol

import MyJsonProtocol._
import spray.json._

case class UserStart()
case class UserStop()
case class notification(createdby:Int)
class User(userid: Int, maxUser: Int) extends Actor {
  implicit val system = context.system
  import system.dispatcher
  Global.currentUserCount+=1

  val pipeline = sendReceive

  var addfriend : Cancellable =null;
  var createpost : Cancellable = null;
  var viewfriendlist : Cancellable = null;
  var viewpost: Cancellable = null;
  var viewpage: Cancellable = null;
  var searchuser : Cancellable = null;
  var deletefriend : Cancellable = null;
  var viewprofile : Cancellable = null;
  //Album Related API
  var addprofilepic : Cancellable =null;
  var viewprofilepic : Cancellable =null;
  var createalbum : Cancellable =null;
  var addpicstoalbum : Cancellable =null;
  var viewalbumpics : Cancellable =null;


  var randhighselect = Random.nextInt(20)
  var randlowselect = Random.nextInt(10)
  var select = userid
  if(maxUser > 1000)
     select = Random.nextInt(1000)

  val Mypost = 1 + Random.nextInt(maxUser);
  val Myfriend = 1 + Random.nextInt(maxUser);
  val Mypageview = 1+ Random.nextInt(maxUser);
  val Myalbum = 1+ Random.nextInt(10) //Max 10 albums of each user

  var countpost  = 0
  var countfriend = 0
  var countpageview = 0
  var countalbum = 0
  val usertype =  userid % 6   // Simjulate user behaviour 0,1,2 Most active 3,4, moderately active and 5 rearely active

  var friendset = scala.collection.mutable.Set[Int]()
 // println(self.path)


/*
   pipeline(Post(Global.CreateUser + userid + "&loginid=" + Global.loginidlist(select) + "&password=" + Global.passlist(select) + "&firstname=" + Global.namelist(select) + "&lastname=" + Global.lnamelist(select) + "&gender=" + Global.genderlist(randlowselect) + "&country=" + Global.countrylist(randlowselect) + "&city=" + Global.citylist(randlowselect) + "&profession=" + Global.professionlist(randhighselect) + "&interestedin=" + Global.interestlist(randhighselect)))
*/
  var requestParam =  new CreateUserParam(userid,
                          Global.loginidlist(select),
                          Global.passlist(select),
                          Global.namelist(select),
                          Global.lnamelist(select),
                          Global.genderlist(randlowselect),
                          Global.countrylist(randlowselect),
                          Global.citylist(randlowselect),
                          Global.professionlist(randhighselect),
                          Global.interestlist(randhighselect)
                          )
  var encodedString = URLEncoder.encode(requestParam.toJson+"","UTF-8")
  //var encodedString = "createuserparam=Hey"
  val result = pipeline(Post(Global.CreateUser+encodedString))

  displayResult(result)



  def receive = {
    case UserStart =>
    usertype match{
      case 0 | 1| 2=>
    addfriend = context.system.scheduler.schedule(Duration.create(200, TimeUnit.MILLISECONDS),
          Duration.create(2000, TimeUnit.MILLISECONDS))(addFriendfun)
    viewpage = context.system.scheduler.schedule(Duration.create(300, TimeUnit.MILLISECONDS),
          Duration.create(1000, TimeUnit.MILLISECONDS))(viewpageFun)
    createpost =context.system.scheduler.schedule(Duration.create(400, TimeUnit.MILLISECONDS),
          Duration.create(1500, TimeUnit.MILLISECONDS))(createpostFun)
    viewpost = context.system.scheduler.schedule(Duration.create(500, TimeUnit.MILLISECONDS),
          Duration.create(500, TimeUnit.MILLISECONDS))(viewpostFun)
   viewfriendlist=context.system.scheduler.schedule(Duration.create(600, TimeUnit.MILLISECONDS),
          Duration.create(1800, TimeUnit.MILLISECONDS))(viewfriendlistFun)
    searchuser=context.system.scheduler.schedule(Duration.create(800, TimeUnit.MILLISECONDS),
          Duration.create(1200, TimeUnit.MILLISECONDS))(searchuserFun)
   deletefriend=context.system.scheduler.schedule(Duration.create(900, TimeUnit.MILLISECONDS),
          Duration.create(10000, TimeUnit.MILLISECONDS))(deletefriendFun)
   addprofilepic = context.system.scheduler.schedule(Duration.create(1000, TimeUnit.MILLISECONDS),
          Duration.create(3000, TimeUnit.MILLISECONDS))(addprofilepicFun)
   viewprofilepic = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(800, TimeUnit.MILLISECONDS))(viewprofilepicFun)
   viewprofile= context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(1400, TimeUnit.MILLISECONDS))(viewprofileFun)
   createalbum = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(1600, TimeUnit.MILLISECONDS))(createalbumFun)
   addpicstoalbum = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(1700, TimeUnit.MILLISECONDS))(addpicstoalbumFun)
   viewalbumpics = context.system.scheduler.schedule(Duration.create(1500, TimeUnit.MILLISECONDS),
          Duration.create(2200, TimeUnit.MILLISECONDS))(viewalbumpicsFun)
    case 3 | 4=>
     addfriend = context.system.scheduler.schedule(Duration.create(200, TimeUnit.MILLISECONDS),
          Duration.create(4000, TimeUnit.MILLISECONDS))(addFriendfun)
    viewpage = context.system.scheduler.schedule(Duration.create(300, TimeUnit.MILLISECONDS),
          Duration.create(2000, TimeUnit.MILLISECONDS))(viewpageFun)
    createpost =context.system.scheduler.schedule(Duration.create(400, TimeUnit.MILLISECONDS),
          Duration.create(3000, TimeUnit.MILLISECONDS))(createpostFun)
    viewpost = context.system.scheduler.schedule(Duration.create(500, TimeUnit.MILLISECONDS),
          Duration.create(1000, TimeUnit.MILLISECONDS))(viewpostFun)
   viewfriendlist=context.system.scheduler.schedule(Duration.create(600, TimeUnit.MILLISECONDS),
          Duration.create(3600, TimeUnit.MILLISECONDS))(viewfriendlistFun)
    searchuser=context.system.scheduler.schedule(Duration.create(800, TimeUnit.MILLISECONDS),
          Duration.create(2400, TimeUnit.MILLISECONDS))(searchuserFun)
   deletefriend=context.system.scheduler.schedule(Duration.create(900, TimeUnit.MILLISECONDS),
          Duration.create(10000, TimeUnit.MILLISECONDS))(deletefriendFun)
   addprofilepic = context.system.scheduler.schedule(Duration.create(1000, TimeUnit.MILLISECONDS),
          Duration.create(6000, TimeUnit.MILLISECONDS))(addprofilepicFun)
   viewprofilepic = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(1600, TimeUnit.MILLISECONDS))(viewprofilepicFun)
   viewprofile= context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(2800, TimeUnit.MILLISECONDS))(viewprofileFun)
   createalbum = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(3200, TimeUnit.MILLISECONDS))(createalbumFun)
   addpicstoalbum = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(3400, TimeUnit.MILLISECONDS))(addpicstoalbumFun)
   viewalbumpics = context.system.scheduler.schedule(Duration.create(1500, TimeUnit.MILLISECONDS),
          Duration.create(4400, TimeUnit.MILLISECONDS))(viewalbumpicsFun)
    case 5=>
     addfriend = context.system.scheduler.schedule(Duration.create(200, TimeUnit.MILLISECONDS),
          Duration.create(8000, TimeUnit.MILLISECONDS))(addFriendfun)
    viewpage = context.system.scheduler.schedule(Duration.create(300, TimeUnit.MILLISECONDS),
          Duration.create(4000, TimeUnit.MILLISECONDS))(viewpageFun)
    createpost =context.system.scheduler.schedule(Duration.create(400, TimeUnit.MILLISECONDS),
          Duration.create(6000, TimeUnit.MILLISECONDS))(createpostFun)
    viewpost = context.system.scheduler.schedule(Duration.create(500, TimeUnit.MILLISECONDS),
          Duration.create(2000, TimeUnit.MILLISECONDS))(viewpostFun)
   viewfriendlist=context.system.scheduler.schedule(Duration.create(600, TimeUnit.MILLISECONDS),
          Duration.create(7200, TimeUnit.MILLISECONDS))(viewfriendlistFun)
    searchuser=context.system.scheduler.schedule(Duration.create(800, TimeUnit.MILLISECONDS),
          Duration.create(4800, TimeUnit.MILLISECONDS))(searchuserFun)
   deletefriend=context.system.scheduler.schedule(Duration.create(900, TimeUnit.MILLISECONDS),
          Duration.create(10000, TimeUnit.MILLISECONDS))(deletefriendFun)
   addprofilepic = context.system.scheduler.schedule(Duration.create(1000, TimeUnit.MILLISECONDS),
          Duration.create(12000, TimeUnit.MILLISECONDS))(addprofilepicFun)
   viewprofilepic = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(3200, TimeUnit.MILLISECONDS))(viewprofilepicFun)
   viewprofile= context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(5600, TimeUnit.MILLISECONDS))(viewprofileFun)
   createalbum = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(6400, TimeUnit.MILLISECONDS))(createalbumFun)
   addpicstoalbum = context.system.scheduler.schedule(Duration.create(1200, TimeUnit.MILLISECONDS),
          Duration.create(6800, TimeUnit.MILLISECONDS))(addpicstoalbumFun)
   viewalbumpics = context.system.scheduler.schedule(Duration.create(1500, TimeUnit.MILLISECONDS),
          Duration.create(8800, TimeUnit.MILLISECONDS))(viewalbumpicsFun)
        }
    case notification(createdby :Int) =>
    //  println("From notification")
     // val result = pipeline(Get(Global.ViewPost + userid+"&personid="+createdby ))
     // displayResult(result)
    
    case UserStop =>
      println("User Stopped ")
     addfriend.cancel
     viewpage.cancel
     createpost.cancel
     viewpost.cancel
     viewfriendlist.cancel
      searchuser.cancel
     deletefriend.cancel
     addprofilepic.cancel
     viewprofile.cancel
     createalbum.cancel
     viewprofilepic.cancel
     viewalbumpics.cancel
  }


  def addFriendfun() {
    if (countfriend < Myfriend) {
         countfriend = countfriend+1
      //println("Add Friend Request")
      val toadd = Random.nextInt(maxUser);
      if(toadd != userid && (friendset add toadd)){
      val result = pipeline(Post(Global.AddFriend + userid + "&friendid=" + toadd))
     // displayResult(result)
     }
    } else {
     addfriend.cancel
    }
  }
  def createpostFun() {
    if (countpost < Mypost) {
         countpost = countpost+1
     // println("createpostFun ")
     val topost = Global.postlist(Random.nextInt(Global.postlist.length))
     val res=topost.replace(' ','+')
       val result = pipeline(Post(Global.CreatePost + userid +"&post=" + res))
       var getid = 1
       if(friendset.size > 0)
       {
        getid = friendset.toList(Random.nextInt(friendset.size))
        val notifyTo = "akka://default/user/"+getid
      //  println(notifyTo)
      //val server = system.actorSelection(connectTo)
      context.actorSelection(notifyTo) ! notification(userid)
      }
     //displayResult(result)
    } else {
     createpost.cancel
    }
  }


 def viewprofileFun() {
    //  println("viewpostFun ")
      val viewid = Random.nextInt(maxUser)
      val result = pipeline(Get(Global.ViewProfile + userid+"&personid="+viewid ))
   //   displayResult(result)
  }
def searchuserFun() { 
     // println("searchuserFun ")
      val searched = Random.nextInt(Global.namelist.length);
      val searchstring = "&firstname="+Global.namelist(searched) + "&lastname=" + Global.lnamelist(searched)
      val result = pipeline(Get(Global.SearchUser + userid +searchstring))
      //displayResult(result)
  }
  def deletefriendFun() {
      //println("deletefriendFun ")
      /*val todel = friendset.toList(Random.nextInt(friendset.size))
      val result = pipeline(Post(DeleteFriend + userid +"&friendid" + todel))
     friendset -= todel*/
    // displayResult(result)
  }
  def viewpostFun() {
      //println("viewpostFun ")
      val viewid = Random.nextInt(maxUser)
      val result = pipeline(Get(Global.ViewPost + userid+"&personid="+viewid ))
    // displayResult(result)
  }
  def viewfriendlistFun() {
      //println("viewfriendlistFun ")
       val viewid = Random.nextInt(maxUser)
      val result = pipeline(Get(Global.ViewFriendlist + userid +"&personid="+viewid))
     //displayResult(result)
  }
  def viewpageFun() {
       //println("View Page Request")
      val searchid = Random.nextInt(maxUser)
      val result = pipeline(Get(Global.ViewPage + userid+"&personid="+ searchid))
    if(userid == 1)
      displayResult(result)
    }

    //Image   Handling

  def addprofilepicFun() {
    //   println("addprofilepicFunc")
       val bytes = new Array[Byte](10)
       Random.nextBytes(bytes)
      val res = bytes.mkString(" ")
      val repl  = res.replace(' ','+')
      val result = pipeline(Post(Global.AddProfilePic + userid+"&pic="+ repl))
  //    displayResult(result)
    }

   def viewprofilepicFun() { 
     // println("searchuserFun ")
      val searched = Random.nextInt(maxUser);
      val result = pipeline(Get(Global.ViewProfilePic + userid +"&personid="+userid))
     //displayResult(result)
  }

    def createalbumFun() {
    if (countalbum < Myalbum) {
         countalbum = countalbum+1
    //  println("createalbum")
      val toadd = Random.nextInt(Myalbum);
      val result = pipeline(Post(Global.CreateAlbum + userid + "&albumid=" + toadd))
   //   displayResult(result)
     }
     else
       createalbum.cancel
  }
def addpicstoalbumFun(){
  //   println("addpicstoalbumFun")
     val albumid = Random.nextInt(Myalbum)
     val bytes = new Array[Byte](10)
       Random.nextBytes(bytes)
      val res = bytes.mkString(" ")
      val repl  = res.replace(' ','+')
      val result = pipeline(Post(Global.AddPicstoAlbum + userid + "&albumid=" + albumid+"&pic="+repl))
    //  displayResult(result)
}
  def viewalbumpicsFun() {
   //   println("viewalbumpicsFun ")
      val albumid = Random.nextInt(Myalbum)
      val picid = Random.nextInt(20)
      val personid = Random.nextInt(maxUser)
      val result = pipeline(Get(Global.ViewAlbumPics + userid+"&personid="+personid+"&albumid="+albumid+"&picid="+picid ))
   //  displayResult(result)
  }
  def displayResult(result: scala.concurrent.Future[spray.http.HttpResponse]) {
    result.foreach {
      response =>
      //  println(Global.PageViewCount)
      println(s"Request completed with status ${response.status} and content:\n${response.entity.asString}")
    }
  }
}
