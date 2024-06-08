import cats.effect.IO.never
import cats.effect.kernel.Ref
import cats.effect.{IO, IOApp, Resource}
import com.comcast.ip4s.{Host, Port}
import fs2.{Chunk, Stream}
import io.circe.derivation._
import io.circe.{Decoder, Encoder}
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder

import scala.concurrent.duration.DurationInt

case class Counter(count: Int)

object Restful {
  type Sessions[F[_]] = Ref[F, Counter]
  implicit val encoderCounter: Encoder[Counter] = deriveEncoder
  implicit val decoderCounter: Decoder[Counter] = deriveDecoder

  def service(sessions: Sessions[IO]): HttpRoutes[IO] = HttpRoutes.of {
    case GET -> Root / "counter" => sessions.getAndUpdate(x => Counter(x.count + 1)).flatMap(c => Ok(c.toString))
    case GET -> Root / "slow" / IntVar(chunck) / IntVar(total) / IntVar(time) =>
      Ok(slow(chunck, total, time))
  }

  def slow(chunck: Int, total: Int, time: Int) = {
    val str = 1 to 10000000
    val str2 = str.toList
    val stream = Stream(str2:_*).delayBy[IO](time.seconds)
      stream.chunkN(chunck).take(total).attempt.compile.fold("")((acc,  x) =>
        acc + x.getOrElse(Chunk.empty[Byte]).map(y => y.toString).foldLeft("")(_ + ", " + _))
  }

  val server = for {
    session <- Resource.eval(Ref.of[IO, Counter](Counter(0)))
    s <- EmberServerBuilder
      .default[IO]
      .withHost(Host.fromString("localhost").get)
      .withPort(Port.fromInt(8080).get)
      .withHttpApp(service(session).orNotFound).build
  } yield s

}

object Rest extends IOApp.Simple {
  override def run: IO[Unit] = Restful.server.use(_ => never)
}
