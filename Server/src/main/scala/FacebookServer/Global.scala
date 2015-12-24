package Server
import scala.collection.mutable
import scala.collection.mutable.SynchronizedBuffer
import scala.collection.mutable.SynchronizedMap

import Server._
object Global {
  var usermap = new mutable.HashMap[Int, User]() with SynchronizedMap[Int,User]
  var useridlist = new mutable.ArrayBuffer[Int]() with SynchronizedBuffer[Int]

  var totalUsers = 0
  var totalPosts = 0
  var totalPostViews = 0
  var totalPageViews = 0
  var totalviewFriendLists = 0
  var totalPicsViewed = 0
  var totalFriendRequestSent = 0
  var totalProfilePicsUpdated = 0
  var totalProfilePicViews =0
  var totalAlbumsCreated =0
  var totaPicsAddedtoAlbum=0
  var totalAlbumPicsViewed=0
  var totalRejectedRequests=0
  var totalProfileViews=0
  var searchRequests=0
  var activityStartTime=0

  var starttime : Long = 0
  var Total_Requests_Served = 0
  var Recent_Requests=0
  var Max_Average : Long=0

  val Max_Average_Possible=7000 // Exceeding it means time time elapsed time somehow gave incorret value. This is just an estimation





}

