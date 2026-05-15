package com.iam.app.services;

import java.util.*;

import com.iam.app.repository.MapperRepository;
import com.iam.app.models.*;
public class MapperService {
    MapperRepository mapperRepository = new MapperRepository();

    public boolean mapRoleAndUser(String uuid,Long roleID){
        UUID userID = UUID.fromString(uuid);
        return mapperRepository.mapRoleAndUser(userID, roleID);
    }


    public boolean mapRoleAndPermission(Long roleId,Long permissionId){
        return mapperRepository.mapRoleAndPermission(roleId, permissionId);
    }


    public List<Permission> getPermissionsbyRoleId(Long id){
        return mapperRepository.getPermissionsbyRoleId(id);
    }


    public List<Role> getRolesByUserId(String uuid){
        UUID userID = UUID.fromString(uuid);
        return mapperRepository.getRolesAndPermissionsByUserId(userID);
    }
}
