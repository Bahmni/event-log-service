/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.eventlogservice.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bahmni.module.eventlogservice.model.EventLog;
import org.bahmni.module.eventlogservice.web.helper.OpenMRSWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
public class EventLogFetcher {

    private OpenMRSWebClient openMRSWebClient;

    @Value("${bahmni.eventlog.uri}")
    private String bahmniEventLogURL;

    @Autowired
    public EventLogFetcher(OpenMRSWebClient openMRSWebClient) {
        this.openMRSWebClient = openMRSWebClient;

    }


    public List<EventLog> fetchEventLogsAfter(String lastReadEventUuid) throws IOException {
        URI uri = constructBahmniURLForEventLogs(lastReadEventUuid);
        ObjectMapper objectMapper = new ObjectMapper();
        EventLog[] eventLogs = objectMapper.readValue(openMRSWebClient.get(uri), EventLog[].class);
        return Arrays.asList(eventLogs);
    }

    public List<EventLog> fetchEventLogsForFilterChange(Set<String> eventRecordUuidsWithFilterChange) throws IOException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(bahmniEventLogURL + "getEventsWithNewFilterForPatients");
        for(String uuid : eventRecordUuidsWithFilterChange){
            builder.queryParam("eventRecordUuid", uuid);
        }
        URI uri = builder.build().toUri();
        ObjectMapper objectMapper = new ObjectMapper();
        EventLog[] eventLogs = objectMapper.readValue(openMRSWebClient.get(uri), EventLog[].class);
        return Arrays.asList(eventLogs);
    }

    private URI constructBahmniURLForEventLogs(String lastReadEventUuid) {
        URI uri;
        String url = bahmniEventLogURL + lastReadEventUuid;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid url for event log filter : " + url, e);
        }
        return uri;
    }

    protected void setBahmniEventLogURL(String bahmniEventLogURL) {
        this.bahmniEventLogURL = bahmniEventLogURL;
    }

}
