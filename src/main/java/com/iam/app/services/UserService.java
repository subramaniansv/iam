package com.iam.app.services;

import java.util.List;
import java.util.UUID;

import com.iam.app.models.PasswordResetRequest;
import com.iam.app.models.User;
import com.iam.app.models.UserStatus;
import com.iam.app.repository.UserRepository;

public class UserService {
    UserRepository repository = new UserRepository();
    public User getUser(String uuid){
        UUID userId = UUID.fromString(uuid);
      return  repository.getUser(userId);
    }
    public List<User> getAllUsers(){
        return repository.getAllUsers();
    }
    public boolean updatePassword(PasswordResetRequest req){
        UUID userID = UUID.fromString(req.getUserId());
        return repository.updatePassword(userID, req.getOldPassword(), req.getNewPassword());
    }
    public Boolean updateStatus(String uuid,String status){
          UUID userID = UUID.fromString(uuid);
          UserStatus userStatus = UserStatus.valueOf( status);

          return repository.updateUserStatus(userID, userStatus);
    }

}
