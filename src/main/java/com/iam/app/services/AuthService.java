package com.iam.app.services;

import java.util.List;
import java.util.UUID;

import com.iam.app.models.RefreshToken;
import com.iam.app.models.Role;
import com.iam.app.models.TokenResponse;
import com.iam.app.models.User;
import com.iam.app.models.UserStatus;
import com.iam.app.repository.AuthRepository;
import com.iam.app.repository.MapperRepository;
import com.iam.app.repository.UserRepository;
import com.iam.app.security.AuthContext;
import com.iam.app.security.AuthUser;
import com.iam.app.util.JwtUtil;
import com.iam.app.util.PasswordUtil;

public class AuthService {
    AuthRepository authRepository = new AuthRepository();
    UserRepository userRepository = new UserRepository();
    MapperRepository mapperRepository = new MapperRepository();
    JwtUtil jwtUtil = new JwtUtil();
    public TokenResponse register(User user,RefreshToken refreshToken){
        user = userRepository.create(user);
        List<Role> roles = mapperRepository.getRolesAndPermissionsByUserId(user.getId());
        TokenResponse tokenResponse = new TokenResponse();
        String accessToken =  jwtUtil.generateAccessToken(user.getId(),user.getEmail(),roles);
        String refreshTokenString = jwtUtil.generateRefreshToken(user.getId());
        refreshToken.setTokenHash(refreshTokenString);
        refreshToken.setUserId(user.getId());
        refreshToken = authRepository.create(refreshToken);
        tokenResponse.setExpiresIn(System.currentTimeMillis() + 86400000L);
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshTokenString);
        tokenResponse.setTokenType("Bearer");
        return tokenResponse;
    }

    public TokenResponse refreshAccessToken(String refreshTokenString) throws RuntimeException{
       RefreshToken refreshToken= authRepository.getRefreshTokenByTokenHash(refreshTokenString);
       if(refreshToken.isRevoked() || refreshToken.getexpiredAt() <System.currentTimeMillis()){
        throw new  RuntimeException("refresh token expired");
       }
       User user = userRepository.getUser(refreshToken.getUserId());
        List<Role> roles = mapperRepository.getRolesAndPermissionsByUserId(user.getId());
        TokenResponse tokenResponse = new TokenResponse();
        String accessToken =  jwtUtil.generateAccessToken(user.getId(),user.getEmail(),roles);
        tokenResponse.setExpiresIn(System.currentTimeMillis() + 86400000L);
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshTokenString);
        tokenResponse.setTokenType("Bearer");
        return tokenResponse;
    }

    public TokenResponse login(User user,RefreshToken refreshToken) throws RuntimeException{
       User userDB = userRepository.getUserWithPassword(user.getEmail());
      if(userDB.getId() == null){
          throw new RuntimeException("user not available");
      }
      if(!userDB.getStatus().equals(UserStatus.ACTIVE)){
        throw new RuntimeException("user status is "+userDB.getStatus().name());
      }
      if(user.getPasswordHash() == null){
        throw new RuntimeException("password is required");
      }
      boolean isPasswordVerified =  PasswordUtil.verify(user.getPasswordHash(), userDB.getPasswordHash());
      if(!isPasswordVerified){
        throw new RuntimeException("invalid credentials");
      }
       List<Role> roles = mapperRepository.getRolesAndPermissionsByUserId(userDB.getId());
         TokenResponse tokenResponse = new TokenResponse();
        String accessToken =  jwtUtil.generateAccessToken(userDB.getId(),userDB.getEmail(),roles);
        String refreshTokenString = jwtUtil.generateRefreshToken(userDB.getId());
        refreshToken.setTokenHash(refreshTokenString);
        refreshToken.setUserId(userDB.getId());
        refreshToken = authRepository.create(refreshToken);
        tokenResponse.setExpiresIn(System.currentTimeMillis() + 86400000L);
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshTokenString);
        tokenResponse.setTokenType("Bearer");
        return tokenResponse;

    }
    public void deleteAll(String uuid){
        UUID userId = UUID.fromString(uuid);
        authRepository.revokeByUserId(userId);
    }

    public void deleteByRefreshId(String refreshToken){
        AuthUser user = AuthContext.get();

        authRepository.revokeByTokenHash(refreshToken,user.getUserId());
    }



}
