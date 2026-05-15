package com.iam.app.services;

import java.util.List;

import com.iam.app.models.Permission;

import com.iam.app.repository.PermissionRepository;

public class PermissionService {
    private PermissionRepository permissionRepository = new PermissionRepository();

    public Permission create(Permission permission){
        return permissionRepository.create(permission);
    }

    public Permission getPermissionById(Long id){
        return permissionRepository.getpermission(id);
    }

    public List<Permission> getPermissions(){
        return permissionRepository.getpermissions();
    }
    public void deletePermission(Long id){
         permissionRepository.deletepermission(id);
    } 
}
