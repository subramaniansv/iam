package com.iam.app.repository;

import java.sql.*;
import java.util.*;
import com.iam.app.config.DBConfig;
import com.iam.app.models.Role;

public class RoleRepository {
    public Role create(Role role) {
        String sql = "insert into role (name,description) values(?,?)";
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, role.getName());
            ps.setString(2, role.getDescription());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                role.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            System.out.println("Sql exception at create role iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  create role iam " + e);
            e.printStackTrace();
        }
        return role;
    }

    public Role getRole(Long id) {
        String sql = "select * from  role where role_id = ?";
        Role role = new Role();
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                role.setId(rs.getLong("role_id"));
                role.setName(rs.getString("name"));
                role.setDescription(rs.getString("description"));
            }
        } catch (SQLException e) {
            System.out.println("Sql exception at get role iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  get role iam " + e);
            e.printStackTrace();
        }

        return role;
    }

    public List<Role> getRoles() {
        String sql = "select * from  role ";
        List<Role> roles = new ArrayList<>();
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getLong("role_id"));
                role.setName(rs.getString("name"));
                role.setDescription(rs.getString("description"));
                roles.add(role);
            }
        } catch (SQLException e) {
            System.out.println("Sql exception at get role iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  get role iam " + e);
            e.printStackTrace();
        }
        return roles;

    }

    public void deleteRole(Long id){
        String sql = "delete from role where role_id =?";
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Sql exception at deleteRole iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  deleteRole iam " + e);
            e.printStackTrace();
        }
    }
}
