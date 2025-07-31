package ru.tw1.euchekavelo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tw1.euchekavelo.model.Photo;
import ru.tw1.euchekavelo.model.User;
import ru.tw1.euchekavelo.service.AuthorizationService;
import ru.tw1.euchekavelo.service.EntityAccessCheckService;

@Service
@RequiredArgsConstructor
public class PhotoAccessCheckServiceImpl implements EntityAccessCheckService<Photo> {

    private final AuthorizationService authorizationService;

    @Override
    public void checkEntityAccess(Photo entity) {
        User photoOwner = entity.getUser();
        authorizationService.checkAccess(photoOwner.getId());
    }
}
