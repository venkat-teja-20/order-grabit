package com.grabit.bean.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.grabit.enums.RolesList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("role")
    private RolesList role;

    @JsonProperty("role_permissions")
    private List<PermissionDTO> permissions;
}
