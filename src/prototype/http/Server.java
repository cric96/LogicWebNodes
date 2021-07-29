package prototype.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class Server extends AbstractVerticle {
	private final static int HTTP_OK = 200; // http ok code
	
	private final int port;
	
	public Server(final int port) {
		this.port = port;
	}
	
	@Override 
	public void start() {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.get("/api/test").handler(this::handleSimpleRequest);	
		router.route("/static/*").handler(StaticHandler.create("res/client/"));
		vertx
			.createHttpServer()
			.requestHandler(router)
			.listen(port);
	}
	
	private void handleSimpleRequest(RoutingContext routingContext) {
		System.out.println("Hello world");
		HttpServerResponse response = routingContext.response();
		response.setStatusCode(HTTP_OK).end();
	}

}
