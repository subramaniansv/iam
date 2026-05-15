package com.iam.app.repository;

import java.sql.*;
import java.util.*;
import com.iam.app.config.DBConfig;
import com.iam.app.models.Action;
import com.iam.app.models.Permission;

public class PermissionRepository {
    public Permission create(Permission permission) {
        String sql = "insert into permission (name,description,resource,action) values(?,?,?,?)";
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, permission.getName());
            ps.setString(2, permission.getDescription());
            ps.setString(3, permission.getResource());
            ps.setString(4, permission.getAction().name());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                permission.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            System.out.println("Sql exception at create permission iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  create permission iam " + e);
            e.printStackTrace();
        }
        return permission;
    }

    public Permission getpermission(Long id) {
        String sql = "select * from  permission where permission_id = ?";
        Permission permission = new Permission();
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                permission.setId(rs.getLong("permission_id"));
                permission.setName(rs.getString("name"));
                permission.setDescription(rs.getString("description"));
                permission.setResource(rs.getString("resource"));
                permission.setAction(Action.valueOf(rs.getString("action")));
            }
        } catch (SQLException e) {
            System.out.println("Sql exception at get permission iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  get permission iam " + e);
            e.printStackTrace();
        }

        return permission;
    }

    public List<Permission> getpermissions() {
        String sql = "select * from  permission ";
        List<Permission> permissions = new ArrayList<>();
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Permission permission = new Permission();
                permission.setId(rs.getLong("permission_id"));
                permission.setName(rs.getString("name"));
                permission.setDescription(rs.getString("description"));
                permission.setResource(rs.getString("resource"));
                permission.setAction(Action.valueOf(rs.getString("action")));
                permissions.add(permission);
            }
        } catch (SQLException e) {
            System.out.println("Sql exception at get permission iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  get permission iam " + e);
            e.printStackTrace();
        }
        return permissions;

    }

    public void deletepermission(Long id){
        String sql = "delete from permission where permission_id =?";
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Sql exception at deletepermission iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  deletepermission iam " + e);
            e.printStackTrace();
        }
    }
}
