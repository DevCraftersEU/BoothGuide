package com.westers.boothguide.services.impl;

import com.westers.boothguide.model.ApplicationInfo;
import com.westers.boothguide.model.jpa.ApplicationInfoRepository;
import com.westers.boothguide.services.ApplicationInfoService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationInfoServiceImpl implements ApplicationInfoService {

    private final ApplicationInfoRepository infoRepository;

    public ApplicationInfoServiceImpl(ApplicationInfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    @Override
    public Optional<ApplicationInfo> getApplicationInfo(String infoType, String infoName) {
        if (infoType == null || infoName == null) return Optional.empty();
        return infoRepository.findFirstByInfoTypeAndInfoName(infoType, infoName);
    }


    @Override
    public ApplicationInfo saveApplicationInfo(String infoType, String infoName, String infoValue, boolean overwriteExistentData) throws InfoAlreadyExistsException {
        if (infoType == null || infoName == null || infoValue == null)
            throw new IllegalArgumentException("infoType, infoName, and infoValue cannot be null");
        var optionalInfo = getApplicationInfo(infoType, infoName);
        if (optionalInfo.isPresent() && !overwriteExistentData) throw new InfoAlreadyExistsException();
        var info = optionalInfo.orElse(new ApplicationInfo(null, infoName, infoValue, infoType));
        info.setInfoValue(infoValue);
        return infoRepository.save(info);
    }

    @Override
    public ApplicationInfo saveApplicationInfo(String infoType, String infoName, String infoValue) throws InfoAlreadyExistsException {
        return saveApplicationInfo(infoType, infoName, infoValue, false);
    }


    public static class InfoAlreadyExistsException extends RuntimeException {
    }
}
