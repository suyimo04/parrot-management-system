package com.parrot.service;

import com.parrot.common.PageResult;
import com.parrot.dto.ProfileUpdateDTO;
import com.parrot.dto.UserQueryDTO;
import com.parrot.dto.UserSaveDTO;
import com.parrot.vo.UserVO;

/**
 * 用户管理和客户个人资料相关业务。
 */
public interface UserService {

    PageResult<UserVO> page(UserQueryDTO query);

    UserVO add(UserSaveDTO dto);

    UserVO update(Long id, UserSaveDTO dto);

    void disable(Long id);

    void resetPassword(Long id);

    UserVO profile();

    UserVO updateProfile(ProfileUpdateDTO dto);
}
