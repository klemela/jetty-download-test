package fi.csc.chipster.jettylargefile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JettyLargeFileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long GiB = 1024l * 1024 * 1024;

        InputStream fileStream = new NullInputStream(150 * GiB);

        response.setStatus(HttpServletResponse.SC_OK);

        OutputStream output = response.getOutputStream();

        IOUtils.copyLarge(fileStream, output);

        output.close();
    }
}
