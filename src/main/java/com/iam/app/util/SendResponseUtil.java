package com.iam.app.util;

import jakarta.servlet.http.HttpServletResponse;

import com.iam.app.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SendResponseUtil {
    public static void sendResponse(ApiResponse apiResponse ,HttpServletResponse response){


        try {

      ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(apiResponse));
            response.setStatus(apiResponse.getStatusCode());
            
        } catch (Exception e) {

            System.out.println("error ata send response util iam"+e);
        }
    }
}
