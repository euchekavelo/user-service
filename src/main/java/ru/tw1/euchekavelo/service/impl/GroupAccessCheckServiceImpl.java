package ru.tw1.euchekavelo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tw1.euchekavelo.model.Group;
import ru.tw1.euchekavelo.model.User;
import ru.tw1.euchekavelo.service.AuthorizationService;
import ru.tw1.euchekavelo.service.EntityAccessCheckService;

@Service
@RequiredArgsConstructor
public class GroupAccessCheckServiceImpl implements EntityAccessCheckService<Group> {

    private final AuthorizationService authorizationService;

    @Override
    public void checkEntityAccess(Group entity) {
        User ownerGroup = entity.getOwnerUser();
        authorizationService.checkAccess(ownerGroup.getId());
    }
}
