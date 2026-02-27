package com.weaver.seconddev.hnweaver.integration.archive.ecode.util;

import cn.hutool.core.text.CharSequenceUtil;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.config.ArchiveProperties;
import com.weaver.seconddev.hnweaver.integration.archive.ecode.exception.ArchiveConfigException;
import com.weaver.teams.domain.user.SimpleEmployee;
import com.weaver.teams.security.context.UserContext;
import com.weaver.workflow.common.cfg.org.service.UserService;
import com.weaver.workflow.common.entity.org.WeaUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author 姚礼林
 * @desc 用户工具类
 * @date 2025/9/24
 **/
@Component
@RequiredArgsConstructor
public class UserUtil {
    private final ArchiveProperties properties;
    private final UserService userService;

    public SimpleEmployee getUser() {
        SimpleEmployee currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            String userId = properties.getUserId();
            if (CharSequenceUtil.isBlank(userId)) {
                throw new ArchiveConfigException("配置文件中的用户id未配置");
            }
            return userService.getSimpleEmployeeById(Long.parseLong(userId));
        }
        return currentUser;
    }

    public WeaUser getWeaUserByConfig() {
        String userId = properties.getUserId();
        if (CharSequenceUtil.isBlank(userId)) {
            throw new ArchiveConfigException("配置文件中的用户id未配置");
        }
        return userService.getUserById(Long.parseLong(userId));
    }
}
