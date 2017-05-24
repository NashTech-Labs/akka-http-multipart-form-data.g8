package com.knoldus

import java.io.File

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, Multipart, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FlatSpec, Matchers}

class MultipartFormDataHandlerSpec extends FlatSpec with Matchers with ScalatestRouteTest with MultipartFormDataHandler {
  override def testConfigSource = "akka.loglevel = WARNING"

  val firstName = Multipart.FormData.BodyPart.Strict("FirstName", "Rishi")
  val lastName = Multipart.FormData.BodyPart.Strict("LastName", "Khandelwal")

  "MultiPart Data Handler" should "be able to save multipart data when file has invalid key" in {
    val fileData = Multipart.FormData.BodyPart.Strict("invalid", HttpEntity(ContentTypes.`text/plain(UTF-8)`, "This is test file"), Map("fileName" -> "rishi.txt"))
    val expectedOutput = Map("FirstName" -> "Rishi", "LastName" -> "Khandelwal", "invalid" -> "This is test file")
    val formData = Multipart.FormData(firstName, lastName, fileData)
    Post(s"/process/multipart/data", formData) ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe s"""Data : ${expectedOutput.mkString(", ")} has been successfully saved."""
    }
  }

  it should "be able to save multipart data when filename is not given" in {
    val fileData = Multipart.FormData.BodyPart.Strict("file", HttpEntity(ContentTypes.`text/plain(UTF-8)`, "This is test file"), Map())
    val expectedOutput = Map("FirstName" -> "Rishi", "LastName" -> "Khandelwal", "file Unknown" -> "17 bytes")
    val formData = Multipart.FormData(firstName, lastName, fileData)
    Post(s"/process/multipart/data", formData) ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe s"""Data : ${expectedOutput.mkString(", ")} has been successfully saved."""
    }
  }

  it should "be able to save multipart data when file content is provided" in {
    val fileData = Multipart.FormData.BodyPart.Strict("file", HttpEntity(ContentTypes.`text/plain(UTF-8)`, "This is test file"), Map("fileName" -> "rishi.txt"))
    val expectedOutput = Map("FirstName" -> "Rishi", "LastName" -> "Khandelwal", "file rishi.txt" -> "17 bytes")
    val formData = Multipart.FormData(firstName, lastName, fileData)
    Post(s"/process/multipart/data", formData) ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe s"""Data : ${expectedOutput.mkString(", ")} has been successfully saved."""
    }
  }

  it should "be able to save multipart data when file is read from given path" in {
    val fileData = Multipart.FormData.BodyPart.fromPath("file", ContentTypes.`text/plain(UTF-8)`, new File("./README.md").toPath)
    val expectedOutput = Map("FirstName" -> "Rishi", "LastName" -> "Khandelwal", "file README.md" -> "414 bytes")
    val formData = Multipart.FormData(firstName, lastName, fileData)
    Post(s"/process/multipart/data", formData) ~> routes ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe s"""Data : ${expectedOutput.mkString(", ")} has been successfully saved."""
    }
  }
}
