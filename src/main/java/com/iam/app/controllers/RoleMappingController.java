package com.iam.app.controllers;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.iam.app.models.*;
import com.iam.app.services.MapperService;
import com.iam.app.util.SendResponseUtil;
import java.util.*;
import java.io.IOException;

@WebServlet("/map")
public class RoleMappingController extends HttpServlet {
    MapperService service = new MapperService();
        public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            if(request.getParameter("userId")!=null){
                String userID = request.getParameter("userId");
                Long roleId = null;
                if(request.getParameter("roleId") !=null){
                    roleId = Long.parseLong( request.getParameter("roleId"));
                }

               boolean success =  service.mapRoleAndUser(userID, roleId);
                SendResponseUtil.sendResponse(new ApiResponse(success, "user mapped with the role", null, 200), response);
            }else{
                Long roleId = null;
                if(request.getParameter("roleId") !=null){
                    roleId = Long.parseLong( request.getParameter("roleId"));
                }
                Long permissionId = null;
                if(request.getParameter("permissionId") !=null){
                    permissionId = Long.parseLong( request.getParameter("permissionId"));
                }
                boolean success =  service.mapRoleAndPermission(roleId,permissionId);
                SendResponseUtil.sendResponse(new ApiResponse(success, "permission mapped with the role", null, 200), response);
            }
        
        }

                public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            if(request.getParameter("userId")!=null){
                String userID = request.getParameter("userId");
               List<Role> roles =  service.getRolesByUserId(userID);
                SendResponseUtil.sendResponse(new ApiResponse(true, "user mapped  roles", roles, 200), response);
            }else{
                Long roleId = null;
                if(request.getParameter("roleId") !=null){
                    roleId = Long.parseLong( request.getParameter("roleId"));
                }

                List<Permission> permissions =  service.getPermissionsbyRoleId(roleId);
                SendResponseUtil.sendResponse(new ApiResponse(true, "permission mapped  role", permissions, 200), response);
            }
        
        }


    }
