package com.base22.harvestmonthlyupdate.demo.ErrorHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    private static final Logger logger = LogManager.getLogger();
    @Override
    public boolean hasError(ClientHttpResponse httpResponse)
            throws IOException {



        return (
                httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
                        || httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public  void handleError(ClientHttpResponse httpResponse)
            throws IOException {

        logger.info(httpResponse.getStatusCode().series());
        logger.info(new Scanner(httpResponse.getBody()).useDelimiter("\\A").next());

        if (httpResponse.getStatusCode()
                .series() == HttpStatus.Series.SERVER_ERROR) {
            throw  new InternalServerErrorException();
        } else if (httpResponse.getStatusCode()
                .series() == HttpStatus.Series.CLIENT_ERROR) {
            throw new IOException();
        }else if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException();
        }

    }

}
