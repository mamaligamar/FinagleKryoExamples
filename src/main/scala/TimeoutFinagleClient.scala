import com.twitter.finagle.http
import com.twitter.finagle.http.{Fields, Request, Response}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Await, Future}

object TimeoutFinagleClient extends App{
  val client: Service[http.Request, http.Response] = Http.client.newService("localhost:8080")

  val response = makeRequestWithContent(client)


  def makeRequestWithContent(client: Service[Request, Response]) = {
    val request = http.Request(http.Method.Get, "/")

    request.host = "localhost"
    request.headerMap.add(Fields.ContentEncoding, "application/json")

    Await.result(client(request) onSuccess {
      response =>
        println("Result from " + request.host + " " + response.contentString)
      }
    )
  }
}
