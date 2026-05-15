package com.iam.app.controllers;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.iam.app.mapper.UserConverterUtil;
import com.iam.app.models.*;
import com.iam.app.services.AuthService;
import com.iam.app.util.SendResponseUtil;

import java.io.IOException;

@WebServlet("/auth")
public class AuthController extends HttpServlet {
    AuthService service = new AuthService();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if (request.getParameter("isRefresh") == null) {
            User user = UserConverterUtil.requestToDto(request);
            String userAgent = request.getHeader("User-Agent");
            String ipAddress = request.getRemoteAddr();
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setIpAddress(ipAddress);
            refreshToken.setUserAgent(userAgent);
            boolean isLogin = Boolean.parseBoolean(request.getParameter("isLogin"));
            if (!isLogin) {
                try {
                    TokenResponse tokenResponse = service.register(user, refreshToken);
                    SendResponseUtil.sendResponse(new ApiResponse(true, "user registered ", tokenResponse, 200),
                            response);

                } catch (Exception e) {
                    // TODO: handle exception
                    SendResponseUtil.sendResponse(new ApiResponse(false, "user not registered ", e.getMessage(), 400), response);
                }

            } else {
                try {
                    TokenResponse tokenResponse = service.login(user, refreshToken);
                    SendResponseUtil.sendResponse(new ApiResponse(true, "user logged in  ", tokenResponse, 200),
                            response);
                } catch (Exception e) {
                    // TODO: handle exception
                    SendResponseUtil.sendResponse(new ApiResponse(false, "user not logged in ", e.getMessage(), 400), response);
                }

            }
        } else {
            try {
                TokenResponse tokenResponse = service.refreshAccessToken(request.getParameter("token"));
                SendResponseUtil.sendResponse(new ApiResponse(true, "got refresh token  ", tokenResponse, 200),
                        response);

            } catch (Exception e) {
                // TODO: handle exception
                SendResponseUtil.sendResponse(new ApiResponse(false, "didnt get a refresh token ", e.getMessage(), 400), response);
            }

        }

    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (request.getParameter("all") != null) {
            service.deleteAll(request.getParameter("userId"));
            SendResponseUtil.sendResponse(new ApiResponse(true, "all deleted ", null, 200), response);
        } else {
            service.deleteByRefreshId(request.getParameter("refreshId"));
            SendResponseUtil.sendResponse(new ApiResponse(true, " deleted a refresh token", null, 200), response);

        }

    }

}
