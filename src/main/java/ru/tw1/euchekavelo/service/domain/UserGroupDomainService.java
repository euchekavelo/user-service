package ru.tw1.euchekavelo.service.domain;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tw1.euchekavelo.exception.UserGroupNotFoundException;
import ru.tw1.euchekavelo.model.Group;
import ru.tw1.euchekavelo.model.User;
import ru.tw1.euchekavelo.model.UserGroup;
import ru.tw1.euchekavelo.model.UserGroupKey;
import ru.tw1.euchekavelo.repository.UserGroupRepository;

import java.util.UUID;

import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.IMPOSSIBLE_REMOVING_USER_GROUP_EXCEPTION_MESSAGE;

@Observed
@Service
@RequiredArgsConstructor
public class UserGroupDomainService {

    private final UserGroupRepository userGroupRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserGroupDomainService.class);

    public UserGroup createUserGroup(User user, Group group) {
        UserGroupKey userGroupKey = getUserGroupKeyByUserIdAndGroupId(user.getId(), group.getId());

        UserGroup userGroup = new UserGroup();
        userGroup.setUserGroupKey(userGroupKey);
        userGroup.setUser(user);
        userGroup.setGroup(group);

        return userGroupRepository.save(userGroup);
    }

    public UserGroup getUserGroupByUserIdAndGroupId(UUID userId, UUID groupId) {
        UserGroupKey userGroupKey = getUserGroupKeyByUserIdAndGroupId(userId, groupId);

        return userGroupRepository.findById(userGroupKey).orElseThrow(() -> {
            LOGGER.error(IMPOSSIBLE_REMOVING_USER_GROUP_EXCEPTION_MESSAGE.getExceptionMessage());
            return new UserGroupNotFoundException(IMPOSSIBLE_REMOVING_USER_GROUP_EXCEPTION_MESSAGE.getExceptionMessage());
        });
    }

    public void deleteUserGroupByUserIdAndGroupId(UUID userId, UUID groupId) {
        UserGroup userGroup = getUserGroupByUserIdAndGroupId(userId, groupId);
        userGroupRepository.delete(userGroup);
    }

    private UserGroupKey getUserGroupKeyByUserIdAndGroupId(UUID userId, UUID groupId) {
        UserGroupKey userGroupKey = new UserGroupKey();
        userGroupKey.setUserId(userId);
        userGroupKey.setGroupId(groupId);

        return userGroupKey;
    }
}
