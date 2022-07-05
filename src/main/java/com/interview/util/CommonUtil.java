package com.interview.util;

import com.interview.model.GenericServiceResponse;
import org.springframework.http.HttpStatus;

public class CommonUtil {

    public static void generatingErrorResponse(GenericServiceResponse response, Object data, String errorMessage, String message) {
        response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        response.setMessage(message);
        response.setErrorMessage(errorMessage);
        response.setData(data);
    }

    public static void generatingSuccessResponse(GenericServiceResponse response, Object data, String message) {
        response.setHttpStatus(HttpStatus.OK);
        response.setMessage(message);
        response.setData(data);
    }

    public static void generatingCustomResponse(GenericServiceResponse response, Object data, String message, HttpStatus status) {
        response.setHttpStatus(status);
        response.setMessage(message);
        response.setData(data);
    }

}
