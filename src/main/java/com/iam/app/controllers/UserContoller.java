package com.iam.app.controllers;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.iam.app.services.UserService;
import com.iam.app.util.SendResponseUtil;
import com.iam.app.mapper.PassWordResetConverterUtil;
import com.iam.app.models.*;
import com.iam.app.security.AuthContext;

import java.io.*;
@WebServlet("/api/user")
public class UserContoller  extends HttpServlet{
    UserService service = new UserService();

    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        PasswordResetRequest ps = PassWordResetConverterUtil.requestToDto(request);
        ps.setUserId(AuthContext.get().getUserId().toString());
        boolean ok = service.updatePassword(ps);
        SendResponseUtil.sendResponse(new ApiResponse(ok, "password reset", null, 200), response);
    }


    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if(request.getParameter("userId") != null){
            User user = service.getUser(AuthContext.get().getUserId().toString());
            SendResponseUtil.sendResponse(new ApiResponse(true, "user fetched ", user, 200), response);
        }
        else{
             SendResponseUtil.sendResponse(new ApiResponse(true, "user fetched ", service.getAllUsers(), 200), response);
        }
    }

}
