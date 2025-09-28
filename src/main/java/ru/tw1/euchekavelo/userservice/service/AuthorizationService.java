package ru.tw1.euchekavelo.userservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tw1.euchekavelo.userservice.exception.ResourceAccessDeniedException;
import ru.tw1.euchekavelo.userservice.security.util.UserDetailsContextUtil;

import java.util.UUID;

import static ru.tw1.euchekavelo.userservice.exception.enums.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserDetailsContextUtil userDetailsContextUtil;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationService.class);

    public void checkAccess(UUID resourceOwnerId) {
        if (!isResourceOwner(resourceOwnerId) && !hasAdminRole()) {
            LOGGER.error(RESOURCE_ACCESS_DENIED_EXCEPTION_MESSAGE.getExceptionMessage());
            throw new ResourceAccessDeniedException(RESOURCE_ACCESS_DENIED_EXCEPTION_MESSAGE.getExceptionMessage());
        }
    }

    private boolean isResourceOwner(UUID resourceOwnerId) {
        return userDetailsContextUtil.getUserId().equals(resourceOwnerId);
    }

    private boolean hasAdminRole() {
        return userDetailsContextUtil.getRoles().stream()
                .anyMatch(roleName -> roleName.equalsIgnoreCase("users_admin"));
    }
}
