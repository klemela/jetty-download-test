package fi.csc.chipster.jettylargefile;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.net.URI;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import jdk.net.ExtendedSocketOptions;

public class JettyLargeFileServer {

    private Server httpServer;

    public void startServer(int port, boolean tcpKeepalive) throws Exception {

        Logger logger = LogManager.getLogger();

        ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletHandler.setContextPath("/");

        URI baseUri = new URI("http", null, "0.0.0.0", port, null,
                null, null);

        servletHandler.addServlet(
                new ServletHolder(new JettyLargeFileServlet()),
                "/*");

        httpServer = new Server();

        ServerConnector connector = new ServerConnector(httpServer);
        connector.setPort(baseUri.getPort());
        connector.setHost(baseUri.getHost());

        if (tcpKeepalive) {

            logger.info("configure tcp keepalive");

            Connection.Listener listener = new Connection.Listener() {
                @Override
                public void onOpened(Connection connection) {
                    Object t = connection.getEndPoint().getTransport();
                    if (t instanceof SocketChannel c) {
                        try {
                            c.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                            c.setOption(ExtendedSocketOptions.TCP_KEEPIDLE, 2);
                            // c.setOption(ExtendedSocketOptions.TCP_KEEPCOUNT, 2);
                            c.setOption(ExtendedSocketOptions.TCP_KEEPINTERVAL, 1);
                        } catch (IOException e) {
                            logger.warn("failed to set socket options", e);
                        }

                    }
                }
            };
            connector.addBean(listener);
        }

        httpServer.addConnector(connector);

        httpServer.setHandler(servletHandler);

        httpServer.start();

        logger.info("listening " + baseUri);
    }

    public static void main(String[] args) throws Exception {
        JettyLargeFileServer jlf = new JettyLargeFileServer();
        jlf.startServer(8800, false);
        jlf.startServer(8801, true);
    }
}
