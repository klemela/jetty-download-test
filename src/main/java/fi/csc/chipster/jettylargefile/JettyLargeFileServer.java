package fi.csc.chipster.jettylargefile;

import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class JettyLargeFileServer {

    private Server httpServer;

    public void startServer() throws Exception {

        Logger logger = LogManager.getLogger();

        ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletHandler.setContextPath("/");

        URI baseUri = URI.create("http://localhost:8800");

        servletHandler.addServlet(
                new ServletHolder(new JettyLargeFileServlet()),
                "/*");

        httpServer = new Server();

        ServerConnector connector = new ServerConnector(httpServer);
        connector.setPort(baseUri.getPort());
        connector.setHost(baseUri.getHost());
        httpServer.addConnector(connector);

        httpServer.setHandler(servletHandler);

        httpServer.start();

        logger.info("listening " + baseUri);

        httpServer.join();
    }

    public static void main(String[] args) throws Exception {
        JettyLargeFileServer jlf = new JettyLargeFileServer();
        jlf.startServer();
    }
}
