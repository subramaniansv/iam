package com.iam.app.security;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.iam.app.models.ApiResponse;
import com.iam.app.util.SendResponseUtil;

@WebFilter("/api/*")
public class AuthorizationFilter implements Filter {
    public static final String[] public_path = { "/health/", "/auth/", "/map/" };

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("filter activated");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getServletPath();
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            SendResponseUtil.sendResponse(
                    new ApiResponse(false, "missing or invalivaild authorization header", null, 401), response);
            return;
        }
        String token = authHeader.substring(7);
        try {
            AuthUser authUser = AuthUser.getAuthUser(token);
            String servletName = request.getHttpServletMapping().getServletName();
            Class<?> servletClass = Class.forName(servletName);
            AuthContext.set(authUser);
            System.out.println(authUser.getEmail());

            // class
            if (servletClass.isAnnotationPresent(RequiresRole.class)) {
                System.out.println("requires role noted");
                RequiresRole annotation = servletClass.getAnnotation(RequiresRole.class);
                if (!checkRole(authUser, annotation)) {
                    SendResponseUtil.sendResponse(
                            new ApiResponse(false, "access denied for this user in role ", null, 403),
                            response);
                    return;
                }
            }
            if (servletClass.isAnnotationPresent(RequiresPermission.class)) {
                System.out.println("requires permission noted");
                RequiresPermission annotation = servletClass.getAnnotation(RequiresPermission.class);
                if (!authUser.hasPermission(annotation.resource(), annotation.action())) {
                    SendResponseUtil.sendResponse(
                            new ApiResponse(false, "access denied for this user in permissions", null, 403),
                            response);
                    return;
                }
            }

            // method
            String httpMethod = request.getMethod();
            String servletMethod = resolveServletMethod(httpMethod);

            try {
                java.lang.reflect.Method method = servletClass.getMethod(
                        servletMethod,
                        HttpServletRequest.class,
                        HttpServletResponse.class);

                if (method.isAnnotationPresent(RequiresRole.class)) {
                    RequiresRole annotation = method.getAnnotation(RequiresRole.class);
                    if (!checkRole(authUser, annotation)) {
                        SendResponseUtil.sendResponse(
                                new ApiResponse(false, "Access denied insufficient role on method", null, 403),
                                response);
                        return;
                    }
                }

                if (method.isAnnotationPresent(RequiresPermission.class)) {
                    RequiresPermission annotation = method.getAnnotation(RequiresPermission.class);
                    if (!authUser.hasPermission(annotation.resource(), annotation.action())) {
                        SendResponseUtil.sendResponse(
                                new ApiResponse(false, "Access denied insufficient permission on method", null, 403),
                                response);
                        return;
                    }
                }

            } catch (NoSuchMethodException e) {
                System.out.println("No method " + servletMethod + " skipping method level check");
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            SendResponseUtil.sendResponse(
                    new ApiResponse(false, "access denied for this user in exception occured", null, 403),
                    response);
            System.out.println(e);
        }finally{
            AuthContext.clear();
        }

    }

    private boolean checkRole(AuthUser user, RequiresRole annotation) {
        return annotation.matchAll() ? user.hasAllRoles(annotation.value()) : user.hasAnyRoles(annotation.value());
    }

    private boolean isPublicPath(String path) {
        for (String p : public_path) {
            if (path.startsWith(p)) {
                return true;
            }
        }
        return false;
    }

    private String resolveServletMethod(String httpMethod) {
        switch (httpMethod.toUpperCase()) {
            case "GET":
                return "doGet";
            case "POST":
                return "doPost";
            case "PUT":
                return "doPut";
            case "DELETE":
                return "doDelete";
            case "PATCH":
                return "doPatch";
            default:
                return "doGet";
        }
    }

}
