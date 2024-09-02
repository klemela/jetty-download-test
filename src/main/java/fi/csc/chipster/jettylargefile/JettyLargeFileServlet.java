package fi.csc.chipster.jettylargefile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JettyLargeFileServlet extends HttpServlet {

    Logger logger = LogManager.getLogger();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String[] defaultSize = { "1Gi" };
        String[] defaultChunked = { "true" };

        long GiB = 1024l * 1024 * 1024;

        String sizeString = request.getParameterMap().getOrDefault("size", defaultSize)[0];
        String chunkedString = request.getParameterMap().getOrDefault("chunked", defaultChunked)[0];

        logger.info("requested size: " + sizeString);

        long scale = 1;

        if (sizeString.contains("ki")) {
            sizeString = sizeString.replace("ki", "");
            scale = 1024l;
        }

        if (sizeString.contains("Mi")) {
            sizeString = sizeString.replace("Mi", "");
            scale = 1024l * 1024;
        }

        if (sizeString.contains("Gi")) {
            sizeString = sizeString.replace("Gi", "");
            scale = 1024l * 1024 * 1024;
        }

        if (sizeString.contains("Ti")) {
            sizeString = sizeString.replace("Ti", "");
            scale = 1024l * 1024 * 1024 * 1024;
        }

        long size = Long.parseLong(sizeString) * scale;

        boolean chunked = Boolean.parseBoolean(chunkedString);

        logger.info("use chunked encoding: " + chunked);

        InputStream fileStream = new NullInputStream(size);

        response.setStatus(HttpServletResponse.SC_OK);

        logger.info("buffer size: " + response.getBufferSize());

        if (!chunked) {
            response.setContentLengthLong(size);
        }

        OutputStream output = response.getOutputStream();

        IOUtils.copyLarge(fileStream, output);

        output.close();
    }
}
