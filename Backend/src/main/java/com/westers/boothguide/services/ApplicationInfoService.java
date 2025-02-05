package com.westers.boothguide.services;

import com.westers.boothguide.model.ApplicationInfo;
import com.westers.boothguide.services.impl.ApplicationInfoServiceImpl;

import java.util.Optional;

/**
 * Service interface for managing application information.
 */
public interface ApplicationInfoService {
    /**
     * Returns the requested application info of type and name
     *
     * @param infoType Type of the info (e.g. DESIGN)
     * @param infoName Name of the info with type (e.g. default)
     * @return The requested {@link ApplicationInfo} or an option
     */
    Optional<ApplicationInfo> getApplicationInfo(String infoType, String infoName);

    /**
     * Creates and saves a new {@link ApplicationInfo}
     *
     * @param infoType              Type of the info (e.g. DESIGN)
     * @param infoName              Name of the info (e.g. default)
     * @param infoValue             Value to save
     * @param overwriteExistentData If an information with the given type and name already exists, this field allows to override the currently saved information. If set to false, an {@link com.westers.boothguide.services.impl.ApplicationInfoServiceImpl.InfoAlreadyExistsException} will be thrown.
     * @return The saved {@link ApplicationInfo}
     * @throws ApplicationInfoServiceImpl.InfoAlreadyExistsException When the information with the given type and name already exists and <code>overwriteExistentData</code> is set to <code>false</code>
     */
    ApplicationInfo saveApplicationInfo(String infoType, String infoName, String infoValue, boolean overwriteExistentData) throws ApplicationInfoServiceImpl.InfoAlreadyExistsException;

    /**
     * Creates and saves a new {@link ApplicationInfo}
     *
     * @param infoType  Type of the info (e.g. DESIGN)
     * @param infoName  Name of the info (e.g. default)
     * @param infoValue Value to save
     * @return The saved {@link ApplicationInfo}
     * @throws ApplicationInfoServiceImpl.InfoAlreadyExistsException When the information with the given type and name already exists
     */
    ApplicationInfo saveApplicationInfo(String infoType, String infoName, String infoValue) throws ApplicationInfoServiceImpl.InfoAlreadyExistsException;
}
