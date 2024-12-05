package com.tony.service;

import com.dataaccess.webservicesserver.NumberConversion;
import com.dataaccess.webservicesserver.NumberConversionSoapType;
import com.dataaccess.webservicesserver.NumberToDollarsResponse;
import com.tony.model.NumberToDollarsRequest;
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
import java.math.BigDecimal;

@Service
@Path("/")
public class NumberToDollarsService {
    // initialize logging
    private static final Logger logger = LoggerFactory.getLogger(NumberToDollarsService.class);

    //initialize serviceSoap interface
    private NumberConversionSoapType port;

    // Method to initialize the SOAP service port once the class is instantiated
    @PostConstruct
    public void initiate() {
        NumberConversion numberConversion = new NumberConversion();
        this.port = numberConversion.getNumberConversionSoap();
    }

    @POST
    @Path("/to_dollars")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response getNumberToDollars (NumberToDollarsRequest request){
        try {

            // getting the number entered
            logger.info("Getting numbers from {}", request.getNumberToDollar());

            BigDecimal numberToDollar = new BigDecimal(String.valueOf(request.getNumberToDollar()));

            // Invoke the SOAP service NumberToDollars method and get the response as a string
            String response = port.numberToDollars(String.valueOf(numberToDollar));

            // Create NumberToDollarsResponse object to handle the SOAP response
            NumberToDollarsResponse  numberToDollarsResponse = new NumberToDollarsResponse();
            numberToDollarsResponse.setNumberToDollarsResult(response);

            String result = numberToDollarsResponse.getNumberToDollarsResult();


            // Check if the number's info was retrieved by verifying the result
            if (result != null && !result.isEmpty()) {
                // Log the api response
                logger.info("Getting the api response : {}", result);

                // Return OK response with the result
                return Response.ok().entity(result).build();
            } else {
                // Log failed numbers' information
                logger.info("Getting number's translation failed for {}", request.getNumberToDollar());

                // Return BAD_REQUEST response with a failure message
                return Response.status(Response.Status.BAD_REQUEST).entity("Failed! Please try again!").build();
            }

        } catch (Exception e) {
            logger.error("Error while getting number's translation : {}",e.getMessage());

            // Return INTERNAL_SERVER_ERROR response with the exception message
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal server error: " + e.getMessage())
                    .build();
        }
    }

}
