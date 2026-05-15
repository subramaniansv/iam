package com.iam.app.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.iam.app.config.DBConfig;
import com.iam.app.models.RefreshToken;

public class AuthRepository {
    public RefreshToken create(RefreshToken refreshToken) {
        String sql = "insert into refresh_token (token_hash,user_id,ip_address,user_agent,expires_at) values(?,?,?,?,?)";
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, refreshToken.getTokenHash());
            ps.setObject(2, refreshToken.getUserId());
            ps.setString(3, refreshToken.getIpAddress());
            ps.setString(4, refreshToken.getUserAgent());
            Timestamp expiresAt = Timestamp.from(Instant.now().plus(7, ChronoUnit.DAYS));
            ps.setTimestamp(5, expiresAt);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                refreshToken.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            System.out.println("Sql exception at auth create iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  auth create iam " + e);
            e.printStackTrace();
        }
        return refreshToken;
    }

    public RefreshToken getRefreshTokenByTokenHash(String tokenHash) {
        String sql = "select * from refresh_token where token_hash =? and isRevoked = false ";
        RefreshToken refreshToken = new RefreshToken();
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, tokenHash);
            // ps.setTimestamp(2, Timestamp.from(Instant.now()));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                refreshToken.setId(rs.getLong("refresh_token_id"));
                refreshToken.setTokenHash(rs.getString("token_hash"));
                refreshToken.setUserId(rs.getObject("user_id", java.util.UUID.class));
                refreshToken.setCreatedAt(rs.getTimestamp("created_at").getTime());
                refreshToken.setIpAddress(rs.getString("ip_address"));
                refreshToken.setUserAgent(rs.getString("user_agent"));
                refreshToken.setexpiredAt(rs.getTimestamp("expires_at").getTime());
            }
        } catch (SQLException e) {
            System.out.println("Sql exception at auth get refresh iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  auth get refresh  iam " + e);
            e.printStackTrace();
        }
        return refreshToken;
    }

    public List<RefreshToken> getAllRefreshTokenByUserId(UUID userId) {
        String sql = "select * from refresh_token where user_id =? and isRevoked = false and expires_at <? ";
        List<RefreshToken> tokens = new ArrayList<>();
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, userId);
            ps.setTimestamp(2, Timestamp.from(Instant.now()));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                RefreshToken refreshToken = new RefreshToken();

                refreshToken.setId(rs.getLong("refresh_token_id"));
                refreshToken.setTokenHash(rs.getString("token_hash"));
                refreshToken.setUserId(rs.getObject("user_id", java.util.UUID.class));
                refreshToken.setCreatedAt(rs.getTimestamp("created_at").getTime());
                refreshToken.setIpAddress(rs.getString("ip_address"));
                refreshToken.setUserAgent(rs.getString("user_agent"));
                refreshToken.setexpiredAt(rs.getTimestamp("expires_at ").getTime());
                tokens.add(refreshToken);
            }

        } catch (SQLException e) {
            System.out.println("Sql exception at auth get refresh iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  auth get refresh  iam " + e);
            e.printStackTrace();
        }
        return tokens;
    }

    public void revokeByTokenHash(String tokenHash,UUID userid){
        String sql ="update refresh_token set isRevoked = true where token_hash =? and user_id = ?";
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, tokenHash);
            ps.setObject(2, userid);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Sql exception at authrevokeByTokenHash iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  auth revokeByTokenHash  iam " + e);
            e.printStackTrace();
        }
    }
    public void revokeByUserId(UUID userId){
        String sql ="update refresh_token set isRevoked = true where user_id =?";
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Sql exception at authrevokeByuserId iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  auth revokeByTokenHash  iam " + e);
            e.printStackTrace();
        }
    }
    public void deleteExpiredTokens(){
        String sql = "delete from refresh_token where expires_at > ? ";
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.from(Instant.now()));
        } catch (SQLException e) {
            System.out.println("Sql exception at deleteExpiredTokens iam " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("unhandled exception at  auth deleteExpiredTokens  iam " + e);
            e.printStackTrace();
        }
    }

}
