package com.firstbird.emergence.core.vcs.model

final case class Repository(owner: String, name: String) {
    override def toString: String = s"$owner/$name"
}