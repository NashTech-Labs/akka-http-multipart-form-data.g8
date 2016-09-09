package com.knoldus

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Multipart.BodyPart
import akka.http.scaladsl.model.{HttpResponse, Multipart, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.stream.scaladsl.FileIO

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

trait MultipartFormDataHandler {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  val routes = processMultiPartData

  def processMultiPartData: Route = path("process" / "multipart" / "data") {
    (post & entity(as[Multipart.FormData])) { formData =>
    complete {
    val extractedData: Future[Map[String, Any]] = formData.parts.mapAsync[(String, Any)](1) {

      case file: BodyPart if file.name == "file" => val tempFile = File.createTempFile("process", "file")
        file.entity.dataBytes.runWith(FileIO.toPath(tempFile.toPath)).map { ioResult=>
        s"file ${file.filename.fold("Unknown")(identity)}" -> s"${ioResult.count} bytes"}

      case data: BodyPart => data.toStrict(2.seconds).map(strict =>data.name -> strict.entity.data.utf8String)
    }.runFold(Map.empty[String, Any])((map, tuple) => map + tuple)

     extractedData.map { data => HttpResponse(StatusCodes.OK, entity = s"Data : ${data.mkString(", ")} has been successfully saved.")}
     .recover {
      case ex: Exception =>HttpResponse(StatusCodes.InternalServerError,entity = s"Error in processing multipart form data due to ${ex.getMessage}")
     }
   }
    }
  }
}
