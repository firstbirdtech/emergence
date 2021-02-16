import cats.data.StateT
import cats.effect.IO

package object testutil {

  type Eff[A] = StateT[IO, TestState, A]

}
