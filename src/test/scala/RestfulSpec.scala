import cats.effect.IO
import cats.effect.kernel.Ref
import cats.effect.testing.scalatest.AsyncIOSpec
import org.http4s.{Method, Request, Uri}
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatestplus.mockito.MockitoSugar.mock

class RestfulSpec extends AsyncFreeSpec with AsyncIOSpec {
  type Sessions[F[_]] = Ref[F, Counter]
  "slow access" in {
    val request = Request[IO](Method.GET, Uri.fromString("/slow/5/10/2").toOption.get)
    val session = mock[Sessions[IO]]

    Restful.service(session).orNotFound(request).flatMap { response =>
      response.as[String].map { body =>
        assert(response.status.code == 200)
      }
    }
  }

  "counter access" in {
    val request = Request[IO](Method.GET, Uri.fromString("/counter").toOption.get)
    Ref.of[IO, Counter](Counter(0)).flatMap {
      Restful.service(_).orNotFound(request).flatMap { response =>
        response.as[String].map { body =>
          assert(response.status.code == 200)
        }
      }
    }
  }


}
