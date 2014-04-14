package se.callistaenterprise.async;

import akka.actor.ActorSystem;
import akka.actor.Props;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
@WebServlet(urlPatterns = "/AsyncServlet", asyncSupported = true)
public class AsyncServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    ActorSystem actorSystem = null;

    @Override
    public void init() throws ServletException {
        actorSystem = ActorSystem.create("ServletDemo");
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        ServletOutputStream out = response.getOutputStream();
        response.setHeader("Transfer-Encoding", "chunked");

        actorSystem.actorOf(Props.create(AsyncConnectionHandlerActor.class, request.startAsync(), out));
    }
}