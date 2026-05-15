package com.iam;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import com.iam.app.controllers.*;
import com.iam.app.security.AuthorizationFilter;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {

        int port = Integer.parseInt(
                System.getenv().getOrDefault("PORT", "8080")
        );

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector();

        String docBase = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        Context ctx = tomcat.addContext("", docBase);

        // Register servlets
        Tomcat.addServlet(ctx, "authServlet", new AuthController());
        ctx.addServletMappingDecoded("/auth", "authServlet");

        Tomcat.addServlet(ctx, "roleServlet", new RoleController());
        ctx.addServletMappingDecoded("/api/role", "roleServlet");

        Tomcat.addServlet(ctx, "permissionServlet", new PermissionContoller());
        ctx.addServletMappingDecoded("/api/permission", "permissionServlet");

        Tomcat.addServlet(ctx, "userServlet", new UserContoller());
        ctx.addServletMappingDecoded("/api/user", "userServlet");

        Tomcat.addServlet(ctx, "roleMappingServlet", new RoleMappingController());
        ctx.addServletMappingDecoded("/map", "roleMappingServlet");

        Tomcat.addServlet(ctx, "someServlet", new SomeController());
        ctx.addServletMappingDecoded("/some/", "someServlet");

        // Register filter
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("authorizationFilter");
        filterDef.setFilterClass(AuthorizationFilter.class.getName());
        filterDef.setFilter(new AuthorizationFilter());
        ctx.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("authorizationFilter");
        filterMap.addURLPattern("/api/*");
        ctx.addFilterMap(filterMap);

        tomcat.start();
        System.out.println("Server started on port " + port);
        tomcat.getServer().await();
    }
}