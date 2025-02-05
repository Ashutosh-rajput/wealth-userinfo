package com.WealthManager.UserInfo.service.implimentation;

import com.WealthManager.UserInfo.model.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {


    @Override
    @TrackExecutiontime
    public UserInfoDTO createUser(UserInfoDTO userInfoDTO) {
        if (userInfoRepo.findByusername(userInfoDTO.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username " + userInfoDTO.getUsername() + " already exists");
        }
        UserInfo userInfo = userInfoMapper.userInfoDTOtouserInfo(userInfoDTO);
        userInfo.setRoles("ROLE_USER");
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        UserInfo savedUserInfo = userInfoRepo.save(userInfo);
//        cacheManager.getCache("userinfo").put(savedUserInfo.getId(), userInfoMapper.userInfotouserInfoDTO(savedUserInfo));
        return userInfoMapper.userInfotouserInfoDTO(savedUserInfo);
    }

    @Override
    @Cacheable(value = "userinfo", key = "#id")
    public UserInfoDTO getuserbyid(Long id) {
        UserInfo userInfo = userInfoRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User with that id doesn't exist: " + id));
        return userInfoMapper.userInfotouserInfoDTO(userInfo);
    }

    @Override
    public List<UserInfoDTO> getallusers() {
        List<UserInfo> users = userInfoRepo.findAll();
        List<UserInfoDTO> userDTOs = users.stream().map(userInfoMapper::userInfotouserInfoDTO).collect(Collectors.toList());
        return userDTOs;
    }

    @Override
    public UserInfoDTO updateuser(UserInfoDTO userInfoDTO, Long id) {
        UserInfo user = userInfoRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
        user.setUsername(userInfoDTO.getUsername());
        user.setEmail(userInfoDTO.getEmail());
        user.setMobile(userInfoDTO.getMobile());
        UserInfo updateduser = userInfoRepo.save(user);
        return userInfoMapper.userInfotouserInfoDTO(updateduser);
    }

    @Override
    public UserInfoDTO deleteuser(Long id) {
        UserInfo user = userInfoRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
        userInfoRepo.delete(user);
        return userInfoMapper.userInfotouserInfoDTO(user);
    }

    @Override
    public UserInfoDTO getUserByUsername(String username) {
        UserInfo user = userInfoRepo.findByusername(username).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
        return userInfoMapper.userInfotouserInfoDTO(user);
    }
}
