package com.iam.app.controllers;
import jakarta.servlet.http.*;

import com.iam.app.mapper.RoleConverterUtil;
import com.iam.app.models.ApiResponse;
import com.iam.app.models.Role;
import com.iam.app.services.RoleService;
import com.iam.app.util.SendResponseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
@WebServlet("/api/role")
public class RoleController extends HttpServlet{
    RoleService service = new RoleService();
    public void doPost(HttpServletRequest request ,HttpServletResponse response) throws IOException,ServletException {
        Role role = RoleConverterUtil.requestToDto(request);
       role =  service.create(role);
       if(role.getId() !=null){
        SendResponseUtil.sendResponse(new ApiResponse(true, "role created", role, 200), response);
       }else{
        SendResponseUtil.sendResponse(new ApiResponse(false, "role not created", role, 400), response);
       }
    }

    public void doGet(HttpServletRequest request ,HttpServletResponse response) throws IOException,ServletException{
        Long roleID = null;
        System.out.println();
        if(request.getParameter("roleId")!=null){
            roleID = Long.parseLong(request.getParameter("roleId"));
        }
        if(roleID !=null){
           Role role =  service.getRoleById(roleID);
           SendResponseUtil.sendResponse(new ApiResponse(true, "role fetched", role, 200), response);
        }else{
            SendResponseUtil.sendResponse(new ApiResponse(true, "all roles fetched", service.getAllRoles(), 200), response);
        }
    }

 public void doDelete(HttpServletRequest request ,HttpServletResponse response) throws IOException,ServletException{
        Long roleID = null;
        if(request.getParameter("roleId")!=null){
            roleID = Long.parseLong(request.getParameter("roleId"));
        }else{
             SendResponseUtil.sendResponse(new ApiResponse(false, "id is required", null, 200), response);
        }
        if(roleID !=null){
            service.deleteRoleById(roleID);
            SendResponseUtil.sendResponse(new ApiResponse(true, "role deleted", null, 200), response);
        }
    }

}
