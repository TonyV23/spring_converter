package com.tony.service;

import com.dataaccess.webservicesserver.NumberConversion;
import com.dataaccess.webservicesserver.NumberConversionSoapType;
import com.dataaccess.webservicesserver.NumberToWordsResponse;
import com.tony.model.NumberToWordsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service
@Path("/")
public class NumberToWordsService {
    // initialize logging
    private static final Logger logger = LoggerFactory.getLogger(NumberToWordsService.class);

    //initialize serviceSoap interface
    private NumberConversionSoapType port;

    // Method to initialize the SOAP service port once the class is instantiated
    @PostConstruct
    public void initiate() {
        NumberConversion numberConversion = new NumberConversion();
        this.port = numberConversion.getNumberConversionSoap();
    }

    @POST
    @Path("/get_number_to_words")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNumberToWords(NumberToWordsRequest request) {
        try {
            // getting the number entered
            logger.info("Getting numbers from {}", request.getUbiNum());

            //format the request parameter
            String requestParam = String.format("ubiNum|%s",request.getUbiNum());
            String response = port.numberToWords(requestParam);

            // Create NumberToDollarsResponse object to handle the SOAP respon
            NumberToWordsResponse numberToWordsResponse = new NumberToWordsResponse();
            numberToWordsResponse.setNumberToWordsResult(response);

            // Getting the result from the soap response
            String result = numberToWordsResponse.getNumberToWordsResult();

            // Check if the number's info was retrieved by verifying the result
            if (result != null && !result.isEmpty()) {
                // Log the api response
                logger.info("Getting the api response : {}", result);

                // Return OK response with the result
                return Response.ok().entity(result).build();
            } else {
                // Log failed numbers' information
                logger.info("Getting number's translation failed for {}", request.getUbiNum());

                // Return BAD_REQUEST response with a failure message
                return Response.status(Response.Status.BAD_REQUEST).entity("Failed! Please try again!").build();
            }

        } catch (Exception e) {
            logger.error("Error while getting number's translation : {}", e.getMessage());

            // Return INTERNAL_SERVER_ERROR response with the exception message
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal server error: " + e.getMessage())
                    .build();
        }
    }

}
