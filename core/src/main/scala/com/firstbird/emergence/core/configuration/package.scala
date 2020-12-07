package com.firstbird.emergence.core

import java.io.File

import scala.util.Try

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.typesafe.config.{Config, ConfigFactory}

package object configuration {

  def configFromYaml(file: File): Try[Config] = {
    for {
      yml    <- parseYaml(file)
      json   <- readJsonFromYaml(yml)
      config <- Try(ConfigFactory.parseString(json))
    } yield config
  }

  private def parseYaml(file: File): Try[Object] = {
    val yamllReader = new ObjectMapper(new YAMLFactory)
    Try(yamllReader.readValue(file, classOf[Object]))
  }

  private def readJsonFromYaml(yml: Object): Try[String] = {
    val jsonWriter = new ObjectMapper
    Try(jsonWriter.writeValueAsString(yml))
  }

}
