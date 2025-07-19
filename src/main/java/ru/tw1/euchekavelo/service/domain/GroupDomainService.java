package ru.tw1.euchekavelo.service.domain;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tw1.euchekavelo.exception.GroupNotFoundException;
import ru.tw1.euchekavelo.model.Group;
import ru.tw1.euchekavelo.repository.GroupRepository;

import java.util.UUID;

import static ru.tw1.euchekavelo.exception.enums.ExceptionMessage.GROUP_NOT_FOUND_EXCEPTION_MESSAGE;

@Observed
@Service
@RequiredArgsConstructor
public class GroupDomainService {

    private final GroupRepository groupRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupDomainService.class);

    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    public Group getGroupById(UUID id) {
        return groupRepository.findById(id).orElseThrow(() -> {
            LOGGER.error(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
            return new GroupNotFoundException(GROUP_NOT_FOUND_EXCEPTION_MESSAGE.getExceptionMessage());
        });
    }

    public void deleteGroupById(UUID id) {
        Group group = getGroupById(id);
        groupRepository.delete(group);
    }

    public Group updateGroup(Group group) {
        return createGroup(group);
    }
}
