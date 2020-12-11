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

import cats.effect.Sync
import cats.syntax.all._
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.typesafe.config.{Config, ConfigFactory}

package object configuration {

  def configFromYaml[F[_]](file: String)(implicit F: Sync[F]): F[Config] = {
    for {
      yml    <- parseYaml[F](file)
      json   <- readJsonFromYaml[F](yml)
      config <- F.delay(ConfigFactory.parseString(json))
    } yield config
  }

  private def parseYaml[F[_]](file: String)(implicit F: Sync[F]): F[Object] = {
    val yamllReader = new ObjectMapper(new YAMLFactory)
    F.delay(yamllReader.readValue(file, classOf[Object]))
  }

  private def readJsonFromYaml[F[_]](yml: Object)(implicit F: Sync[F]): F[String] = {
    val jsonWriter = new ObjectMapper
    F.delay(jsonWriter.writeValueAsString(yml))
  }

}
