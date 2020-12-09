/*
 * Copyright 2020 Emergence contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
