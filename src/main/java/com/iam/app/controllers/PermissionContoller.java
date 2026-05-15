package com.iam.app.controllers;

import jakarta.servlet.http.*;

import com.iam.app.mapper.PermissionConverterUtil;
import com.iam.app.models.ApiResponse;
import com.iam.app.models.Permission;
import com.iam.app.services.PermissionService;
import com.iam.app.util.SendResponseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
@WebServlet("/api/permission")
public class PermissionContoller extends HttpServlet{
    PermissionService service = new PermissionService();
    public void doPost(HttpServletRequest request ,HttpServletResponse response) throws IOException,ServletException {
        Permission permission = PermissionConverterUtil.requestToDto(request);
       permission =  service.create(permission);
       if(permission.getId() !=null){
        SendResponseUtil.sendResponse(new ApiResponse(true, "permission created", permission, 200), response);
       }else{
        SendResponseUtil.sendResponse(new ApiResponse(false, "permission not created", permission, 400), response);
       }
    }

    public void doGet(HttpServletRequest request ,HttpServletResponse response) throws IOException,ServletException{
        Long permissionID = null;
        if(request.getParameter("permissionId")!=null){
            permissionID = Long.parseLong(request.getParameter("permissionId"));
        }
        if(permissionID !=null){
           Permission role =  service.getPermissionById(permissionID);
           SendResponseUtil.sendResponse(new ApiResponse(true, "permission fetched", role, 200), response);
        }else{
            SendResponseUtil.sendResponse(new ApiResponse(true, "all permission fetched", service.getPermissions(), 200), response);
        }
    }

 public void doDelete(HttpServletRequest request ,HttpServletResponse response) throws IOException,ServletException{
        Long permissionId = null;
        if(request.getParameter("permissionId")!=null){
            permissionId = Long.parseLong(request.getParameter("permissionId"));
        }else{
             SendResponseUtil.sendResponse(new ApiResponse(false, "id is required", null, 200), response);
        }
        if(permissionId !=null){
            service.deletePermission(permissionId);
            SendResponseUtil.sendResponse(new ApiResponse(true, "permission deleted", null, 200), response);
        }
    }

}
