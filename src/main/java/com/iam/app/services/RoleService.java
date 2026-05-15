package com.iam.app.services;

import java.util.List;

import com.iam.app.models.Role;
import com.iam.app.repository.RoleRepository;

public class RoleService {
    private RoleRepository roleRepository = new RoleRepository();


    public Role create(Role role){
        return roleRepository.create(role);
    }

    public Role getRoleById(Long id){
        return roleRepository.getRole(id);
    }

    public List<Role> getAllRoles(){
        return roleRepository.getRoles();
    }

    public void deleteRoleById(Long id){
        roleRepository.deleteRole(id);
    }

}
