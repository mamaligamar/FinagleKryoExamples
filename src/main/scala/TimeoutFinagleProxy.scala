import com.twitter.finagle.service.TimeoutFilter
import com.twitter.finagle.util.DefaultTimer
import com.twitter.finagle.{Http, Service, http}
import com.twitter.util._

object TimeoutFinagleProxy extends App {


    val service = new Service[http.Request, http.Response] {
      def apply(req: http.Request): Future[http.Response] = Future.value(http.Response(req.version, http.Status.Ok))
    }

    val timeoutFilter = new TimeoutFilter[http.Request, http.Response](Duration.fromMilliseconds(5000L), DefaultTimer.getInstance)
    val serviceWithTimeout: Service[http.Request, http.Response] = timeoutFilter.andThen(service)

    var server = Http.serve(":8080", service);
    Await.ready(server)
}
