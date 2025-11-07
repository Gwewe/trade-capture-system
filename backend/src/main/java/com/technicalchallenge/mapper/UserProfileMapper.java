package com.technicalchallenge.mapper;

import com.technicalchallenge.dto.UserProfileDTO;
import com.technicalchallenge.model.Role;
import com.technicalchallenge.model.UserProfile;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {
    @Autowired
    private ModelMapper modelMapper;

    public UserProfileDTO toDto(UserProfile entity) {
        UserProfileDTO dto = modelMapper.map(entity, UserProfileDTO.class);
        dto.setUserType(entity.getUserType() != null ? entity.getUserType().name() : null);
        return dto;
    }

    public UserProfile toEntity(UserProfileDTO dto) {
        UserProfile entity = modelMapper.map(dto, UserProfile.class);

        if (org.springframework.util.StringUtils.hasText(dto.getUserType())) {
            Role role = Role.fromString(dto.getUserType());
            if (role == null) {
                throw new IllegalArgumentException("Invalid User Type: " + dto.getUserType());
            }
            entity.setUserType(role);
        }
        return entity;
    }
}

