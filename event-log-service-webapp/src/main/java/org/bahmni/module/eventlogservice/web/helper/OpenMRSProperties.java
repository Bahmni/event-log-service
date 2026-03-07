/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.eventlogservice.web.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSProperties {

    @Value("${openmrs.auth.uri}")
    private String openmrsAuthURL;

    @Value("${openmrs.user}")
    private String openmrsUser;

    @Value("${openmrs.password}")
    private String openmrsPassword;

    @Value("${bahmni.eventlog.uri}")
    private String bahmniEventLogFilterURL;

    @Value("${openmrs.connectionTimeoutInMilliseconds}")
    private String openMRSconnectionTimeoutInMilliseconds;

    @Value("${openmrs.replyTimeoutInMilliseconds}")
    private String openMRSReplyTimeoutInMilliseconds;

    public String getOpenMRSAuthURL() {
        return openmrsAuthURL;
    }

    public String getOpenMRSUser() {
        return openmrsUser;
    }

    public String getOpenMRSPassword() {
        return openmrsPassword;
    }

    public int getConnectionTimeoutInMilliseconds() {
        return Integer.parseInt(openMRSconnectionTimeoutInMilliseconds);
    }

    public int getReplyTimeoutInMilliseconds() {
        return Integer.parseInt(openMRSReplyTimeoutInMilliseconds);
    }


}
