//package org.smartjava;
package Server
import _root_.Server._
import _root_.Server.addFriend
import _root_.Server.addFriend
import _root_.Server.createPost
import _root_.Server.searchUser
import _root_.Server.viewAlbumPics
import _root_.Server.viewPost
import _root_.Server.viewProfile
import _root_.Server.viewfriendList
import _root_.Server.viewPage
import akka.actor.{ActorSystem, Props}
import akka.actor.Actor
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http
import spray.http._
import spray.routing._
import spray.httpx.SprayJsonSupport._
import scala.concurrent.duration._
import spray.json.DefaultJsonProtocol
import MediaTypes._
import Server._
import akka.actor.ActorSelection
import akka.pattern.AskTimeoutException
import concurrent._
import concurrent.duration._
import java.util.concurrent.Executors
import java.net.URLEncoder
import java.net.URLDecoder
/*
case class Person(name: String, fistName: String, age: Long)

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val personFormat = jsonFormat3(Person)
}
*/

/*
case class Person(name: String, fistName: String)

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val personFormat = jsonFormat2(Person)
}
import MyJsonProtocol._
*/


object WebServerBoot extends App {
  // create our actor system with the name smartjava
  implicit val system = ActorSystem("Server")
  val service = system.actorOf(Props(new WebServer(system)), "Web-Server")
  // IO requires an implicit ActorSystem, and ? requires an implicit timeout
  // Bind HTTP to the specified service.
  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
//  val server = system.actorOf(Props(new FBServer),"FBBackendServer") // Going to use Server as a different entity.
}
// simple actor that handles the routes.
class WebServer(system: ActorSystem) extends Actor with HttpService {

  import system.dispatcher
  def actorRefFactory = context

  implicit val timeout = Timeout(2.seconds)

  val connectTo = "akka.tcp://FBServer@127.0.0.1:8081/user/ServerSystem/workerRouter"
  val server = system.actorSelection(connectTo)

  def receive = runRoute(createuser ~ addfriend ~ searchuser ~  deletefriend ~ createpost ~ viewpost ~ deletepost ~ viewfriendlist ~ viewpage ~ viewprofile ~ addprofilepic ~ viewprofilepic ~ createalbum ~ addpicstoalbum ~ viewalbumpics)
  val createuser = {
    path("createuser") {
      /*      post {
              parameter("userid","loginid","password","firstname", "lastname","gender","country","city","profession","interestedin") { (userid,loginid,password,firstname,lastname,gender,country,city,profession,interestedin) =>
                complete {
                  (server ? createUser(userid.toInt,loginid, password, firstname, lastname, gender,country,city,profession,interestedin)).recover {
                    case ex: AskTimeoutException => {
                      "request failed"
                    }
                  }
                    .mapTo[String]
                    .map(s => s"$s")
                 }
               }
             }*/
            post {
              parameter("createuserparam") {  createuserparam =>
                complete {
                //  println("CreateUser")
                  (server ? createUser(URLDecoder.decode(createuserparam,"UTF-8"))).recover {
                 // (server ? createUser(createuserparam)).recover {
                  case ex: AskTimeoutException => {
                      "request failed"
                    }
                  }
                    .mapTo[String]
              .map(s => s"$s")
          }
        }
      }
     }
    }
  //*******************************************************************************************************************
  val addfriend = {
    path("addfriend"){
    post {
          parameter("userid","friendid") { (userid,friendid) =>
            complete {
              (server ? addFriend(userid.toInt,friendid.toInt)).recover {
                case ex: AskTimeoutException => {
                  "request failed"
                }
              }
                .mapTo[String]
                .map(s => s"$s")

            }
        }
      }
    }
  }
  //*******************************************************************************************************************
  val searchuser = {
    path("searchuser"){
      get {
        parameter("firstname","lastname") { (firstname,lastname) =>
          complete {
            (server ? searchUser(firstname,lastname)).recover {
              case ex: AskTimeoutException => {
                "request failed"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*******************************************************************************************************************
  val deletefriend = { // To DO
    path("deletefriend"){
      post {
        parameter("userid","friendid") { (userid,friendid) =>
          complete {
            (server ? deleteFriend(userid.toInt,friendid.toInt)).recover {
              case ex: AskTimeoutException => {
                "request failed"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*******************************************************************************************************************
  val createpost = {
    path("createpost"){
      post {
        parameter("userid", "post") { (userid,post ) =>
          complete{
            (server ? createPost(userid.toInt,post)).recover {
              case ex: AskTimeoutException => {
                "request failed"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
          }
        }
      }
    }
  //*******************************************************************************************************************
  val viewpost = {
    path("viewpost"){
      get {
        parameter("userid","personid") { (userid,personid) =>
          complete {
            (server ? viewPost(userid.toInt,personid.toInt)).recover {
              case ex: AskTimeoutException => {
                "request failed"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*******************************************************************************************************************
  val deletepost = {// To DO
    path("deletepost"){
      post {
        parameter("userid","postid") { (userid, postid) =>
          complete("delete-post")
        }
      }
    }
  }
  //*******************************************************************************************************************
  val viewfriendlist = {
    path("viewfriendlist"){
      get {
        parameter("userid","personid") { (userid,personid) =>
          complete {
            (server ? viewfriendList(userid.toInt,personid.toInt)).recover {
              case ex: AskTimeoutException => {
                "request failed"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*********************************************************i**********************************************************
  val viewpage = {
    path("viewpage"){
      get {
        parameter("userid","personid") { (userid,personid) =>
          complete {
            (server ? viewPage(userid.toInt,personid.toInt)).recover {
              case ex: AskTimeoutException => {
                "request failed"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*********************************************************i**********************************************************
  val viewprofile= {
    path("viewprofile"){
      get {
        parameter("userid","personid") { (userid,personid) =>
          complete {
            (server ? viewProfile(userid.toInt,personid.toInt)).recover {
              case ex: AskTimeoutException => {
                "request failed"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*********************************************************i**********************************************************
  val addprofilepic = {
    path("addprofilepic"){
      post {
        parameter("userid","pic") { (userid,pic) =>
          complete {
          //  (server ? addProfilePic(userid.toInt,pic.getBytes("UTF-8"))).recover {
            (server ? addProfilePic(userid.toInt,pic)).recover {
              case ex: AskTimeoutException => {
                "request failed"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*********************************************************i**********************************************************
  val viewprofilepic = {
    path("viewprofilepic"){
      get {
        parameter("userid","personid") { (userid,personid) =>
          complete {
            (server ? viewProfilePic(userid.toInt,personid.toInt)).recover {
              case ex: AskTimeoutException => {
                "request failed"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*********************************************************i**********************************************************
  val createalbum = {
    path("createalbum"){
      post {
        parameter("userid","albumid") {(userid,albumid) =>
          complete {
            (server ? createAlbum(userid.toInt,albumid.toInt)).recover {
              case ex: AskTimeoutException => {
                "request failed"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*********************************************************i**********************************************************
  val addpicstoalbum = {
    path("addpicstoalbum"){
      post {
        parameter("userid","albumid","pic") {(userid,albumid,pic) =>
          complete {
            (server ? addPicstoAlbum(userid.toInt,albumid.toInt,pic)).recover {
              case ex: AskTimeoutException => {
                "request failed"
              }
            }
              .mapTo[String]
              .map(s => s"$s")

          }
        }
      }
    }
  }
  //*********************************************************i**********************************************************
  val viewalbumpics = {
    path("viewalbumpics"){
      get {
        parameter("userid","personid","albumid","picid") { (userid,personid, albumid, picid) =>
          complete {
            (server ? viewAlbumPics(userid.toInt,personid.toInt, albumid.toInt,picid.toInt)).recover {
              case ex: AskTimeoutException => {
                "request failed"
              }
            }
              .mapTo[String]
              .map(s => s"$s")
          }
        }
      }
    }
  }
}


