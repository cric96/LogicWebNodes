package prototype.http;

import io.vertx.core.Vertx;

public class MainHTTP {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		HTTPServer server = new HTTPServer(8080);
		vertx.deployVerticle(server);
	}
}
