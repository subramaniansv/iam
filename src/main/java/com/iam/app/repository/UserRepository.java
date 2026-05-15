package com.iam.app.repository;

import com.iam.app.config.DBConfig;
import com.iam.app.models.User;
import com.iam.app.models.UserStatus;
import com.iam.app.util.PasswordUtil;

import java.util.*;
import java.sql.*;

public class UserRepository {
    public User create(User user) {
        System.out.println("inside user repo");
        String sql = "insert into users (email,password_hash,first_name,last_name,status,is_admin,user_id) values (?,?,?,?,?,?,?)";
        UUID newid = UUID.randomUUID();
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user.getEmail());
            ps.setString(2, PasswordUtil.hash(user.getPasswordHash()));
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getLastName());
            ps.setString(5, UserStatus.ACTIVE.name());
            ps.setBoolean(6, false);
            ps.setObject(7, newid);

            ps.executeUpdate();
            user.setId(newid);
        } catch (SQLException e) {
            System.out.println("Sql exception at create user iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at create user iam " + e);
            e.printStackTrace();
        }

        return user;
    }

    public User getUser(UUID userId) {
        String sql = "select * from users where user_id = ?";
        User user = new User();
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user.setId(rs.getObject("user_id", java.util.UUID.class));
                user.setEmail(rs.getString("email"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setStatus(UserStatus.valueOf(rs.getString("status")));
            }
        } catch (SQLException e) {
            System.out.println("Sql exception at get user iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at get user iam " + e);
            e.printStackTrace();
        }
        return user;
    }

    public List<User> getAllUsers() {
        String sql = "select * from users";
        List<User> users = new ArrayList<>();
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();

                 user.setId(rs.getObject("user_id", java.util.UUID.class));
                user.setEmail(rs.getString("email"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setStatus(UserStatus.valueOf(rs.getString("status")));
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Sql exception at get user iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at get user iam " + e);
            e.printStackTrace();
        }

        return users;
    }

    public User getUserWithPassword(UUID userId) {
        String sql = "select * from users where user_id = ?";
        User user = new User();
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                 user.setId(rs.getObject("user_id", java.util.UUID.class));
                user.setEmail(rs.getString("email"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setStatus(UserStatus.valueOf(rs.getString("status")));
            }
        } catch (SQLException e) {
            System.out.println("Sql exception at get user iam with password" + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at get user iam with password" + e);
            e.printStackTrace();
        }
        return user;
    }

        public User getUserWithPassword(String email) {
        String sql = "select * from users where email = ?";
        User user = new User();
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email );
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                 user.setId(rs.getObject("user_id", java.util.UUID.class));
                user.setEmail(rs.getString("email"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setStatus(UserStatus.valueOf(rs.getString("status")));
            }
        } catch (SQLException e) {
            System.out.println("Sql exception at get user iam with password" + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at get user iam with password" + e);
            e.printStackTrace();
        }
        return user;
    }

    public boolean updatePassword(UUID userId, String oldPassword, String newPassword) {
        String sql = "update users set password_hash = ? where user_id =?";
        User user = getUserWithPassword(userId);
        if (!PasswordUtil.verify(oldPassword,user.getPasswordHash())) {
            return false;
        }
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, PasswordUtil.hash(newPassword));
               ps.setObject(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Sql exception at update  user password iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  update  user password iam " + e);
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateUserStatus(UUID userId,UserStatus status){
         String sql = "update users set status = ? where user_id =?";
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, status.name());
               ps.setObject(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Sql exception at updateUserStatus iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  updateUserStatus iam " + e);
            e.printStackTrace();
        }

        return false;
    }

}
