package testutil

import cats.data.StateT
import io.chrisdavenport.log4cats.Logger

class MockLogger extends Logger[Eff] {
  override def error(t: Throwable)(message: => String): Eff[Unit] =
    impl(Some(t), message)

  override def warn(t: Throwable)(message: => String): Eff[Unit] =
    impl(Some(t), message)

  override def info(t: Throwable)(message: => String): Eff[Unit] =
    impl(Some(t), message)

  override def debug(t: Throwable)(message: => String): Eff[Unit] =
    impl(Some(t), message)

  override def trace(t: Throwable)(message: => String): Eff[Unit] =
    impl(Some(t), message)

  override def error(message: => String): Eff[Unit] =
    impl(None, message)

  override def warn(message: => String): Eff[Unit] =
    impl(None, message)

  override def info(message: => String): Eff[Unit] =
    impl(None, message)

  override def debug(message: => String): Eff[Unit] =
    impl(None, message)

  override def trace(message: => String): Eff[Unit] =
    impl(None, message)

  def impl(maybeThrowable: Option[Throwable], message: String): Eff[Unit] =
    StateT.modify(_.addLog(maybeThrowable, message))
}
