package ru.skillbox.userservice.service;

import org.springframework.stereotype.Service;
import ru.skillbox.userservice.model.Group;
import ru.skillbox.userservice.model.User;
import ru.skillbox.userservice.model.UserGroup;
import ru.skillbox.userservice.model.UserGroupKey;
import ru.skillbox.userservice.model.enums.Sex;
import ru.skillbox.userservice.repository.GroupRepository;
import ru.skillbox.userservice.repository.UserGroupRepository;
import ru.skillbox.userservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TestService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    public TestService(GroupRepository groupRepository, UserRepository userRepository, UserGroupRepository userGroupRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
    }

    public void test(){
        Group group = new Group();
        group.setName("test" + LocalDateTime.now());
        Group savedGroup = groupRepository.save(group);

        User user = new User();
        user.setFullname("test" + LocalDateTime.now());
        user.setSex(Sex.MALE);
        user.setEmail("test" + LocalDateTime.now());
        User savedUser = userRepository.save(user);

        UserGroupKey userGroupKey = new UserGroupKey();
        userGroupKey.setUserId(savedUser.getId());
        userGroupKey.setGroupId(savedGroup.getId());
        UserGroup userGroup = new UserGroup();
        userGroup.setUserGroupKey(userGroupKey);
        userGroup.setGroup(savedGroup);
        userGroup.setUser(savedUser);
        userGroupRepository.save(userGroup);

        //groupRepository.delete(savedGroup);
    }

    public void test1(UUID id) {
        Group savedGroup = groupRepository.findById(id).get();
        System.out.println("test: " + savedGroup.getUserGroupList().size());
        groupRepository.delete(savedGroup);
    }
}
