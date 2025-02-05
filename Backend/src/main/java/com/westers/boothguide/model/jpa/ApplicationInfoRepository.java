package com.westers.boothguide.model.jpa;

import com.westers.boothguide.model.ApplicationInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ApplicationInfoRepository extends CrudRepository<ApplicationInfo, Long> {
    Optional<ApplicationInfo> findFirstByInfoTypeAndInfoName(String infoType, String infoName);
}
