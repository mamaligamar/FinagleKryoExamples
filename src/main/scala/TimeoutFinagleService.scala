import com.twitter.finagle.http._
import com.twitter.finagle.service.TimeoutFilter
import com.twitter.finagle.util.DefaultTimer
import com.twitter.finagle.{Http, Service, SimpleFilter, http}
import com.twitter.util._

object TimeoutFinagleService extends App {


  val service = new Service[Request, Response] {
    def apply(req: http.Request): Future[http.Response] = {
      val response = Response(req.version, http.Status.Ok)
      response.contentString="hello world"
      Future.value(response);
    }
  }

  class ContentFilter extends SimpleFilter[Request, Response] {
      def apply(request: Request, serviceToApply: Service[Request, Response]) = {
        if (request.headerMap.get(Fields.ContentEncoding).get.equals("application/x-kryo")) {
          serviceToApply(request)
        } else {
          Future.exception(new IllegalArgumentException("The encoding type is wrong"))
        }
      }
    }

  class HandleExceptions extends SimpleFilter[Request, Response]{
    def apply(request: Request, service: Service[Request, Response]) = {
      service(request) handle {
        case error =>
          val statusCode = error match{
            case _: IllegalArgumentException => Status.Forbidden
            case _ =>
              Status.InternalServerError
          }
          val errorResponse = Response(Version.Http11, statusCode)
          errorResponse.contentString = error.getStackTrace.mkString("\n")

          errorResponse
      }
    }
  }

  val timeoutFilter = new TimeoutFilter[http.Request, http.Response](Duration.fromMilliseconds(5000L), DefaultTimer.getInstance)
  val serviceWithTimeout: Service[http.Request, http.Response] = new ContentFilter andThen new HandleExceptions andThen service

  var server = Http.serve("localhost:8080", serviceWithTimeout);
  Await.ready(server)
}
