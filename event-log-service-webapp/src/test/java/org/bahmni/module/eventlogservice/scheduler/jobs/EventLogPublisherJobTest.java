/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.eventlogservice.scheduler.jobs;

import org.bahmni.module.eventlogservice.fetcher.EventLogFetcher;
import org.bahmni.module.eventlogservice.model.EventLog;
import org.bahmni.module.eventlogservice.repository.EventLogRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class EventLogPublisherJobTest {

    private Job eventLogPublisherJob;
    @Mock
    private EventLogRepository eventLogRepository;
    @Mock
    private EventLogFetcher eventLogFetcher;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        eventLogPublisherJob = new EventLogPublisherJob(eventLogRepository, eventLogFetcher);
    }

    @Test
    public void shouldCopyEventRecordsToEventLog() throws Exception {
        Date timestamp = new Date();
        EventLog eventLog = new EventLog();
        eventLog.setUuid("uuid");
        eventLog.setParentUuid(eventLog.getUuid());
        eventLog.setCategory("Patient");
        eventLog.setTimestamp(timestamp);
        when(eventLogRepository.findFirstByOrderByIdDesc()).thenReturn(eventLog);


        EventLog eventLogNew = new EventLog();
        eventLogNew.setId(12);
        eventLogNew.setCategory("Patient");
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>();
        eventLogs.add(eventLogNew);
        when(eventLogFetcher.fetchEventLogsAfter(eventLog.getUuid())).thenReturn(eventLogs);
        eventLogPublisherJob.process();

        verify(eventLogRepository, times(1)).findFirstByOrderByIdDesc();
        verify(eventLogFetcher, times(1)).fetchEventLogsAfter(eventLog.getUuid());
        verify(eventLogRepository, times(1)).save(eventLogs);
    }

    @Test
    public void shouldCopyAllEventRecordsForTheFirstTime() throws Exception {
        when(eventLogRepository.findFirstByOrderByTimestampDesc()).thenReturn(null);

        EventLog eventLogNew = new EventLog();
        eventLogNew.setId(12);
        eventLogNew.setCategory("xyz");
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>();
        eventLogs.add(eventLogNew);
        when(eventLogFetcher.fetchEventLogsAfter("")).thenReturn(eventLogs);

        eventLogPublisherJob.process();
        verify(eventLogRepository, times(1)).findFirstByOrderByIdDesc();
        verify(eventLogFetcher, times(1)).fetchEventLogsAfter("");
        verify(eventLogRepository, times(1)).save(eventLogs);

    }

    @Test
    public void shouldThrowErrorWhenNoEventIsPresentInEventRecords() throws Exception {
        Date timestamp = new Date();
        EventLog eventLog = new EventLog();
        eventLog.setUuid("uuid");
        eventLog.setTimestamp(timestamp);
        eventLog.setParentUuid(eventLog.getUuid());
        when(eventLogRepository.findFirstByOrderByIdDesc()).thenReturn(eventLog);
        List<EventLog> eventLogs = new ArrayList<EventLog>();
        when(eventLogFetcher.fetchEventLogsAfter("uuid")).thenReturn(eventLogs);

        eventLogPublisherJob.process();

        verify(eventLogRepository, times(1)).findFirstByOrderByIdDesc();
        verify(eventLogFetcher, times(1)).fetchEventLogsAfter("uuid");
        verify(eventLogRepository, times(1)).save(eventLogs);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowErrorWhenMapperThrowIOException() throws Exception {
        Date timestamp = new Date();
        EventLog eventLog = new EventLog();
        eventLog.setUuid("uuid");
        eventLog.setParentUuid(eventLog.getUuid());
        eventLog.setTimestamp(timestamp);
        when(eventLogRepository.findFirstByOrderByIdDesc()).thenReturn(eventLog);
        when(eventLogFetcher.fetchEventLogsAfter("uuid")).thenThrow(new IOException());

        eventLogPublisherJob.process();
    }

    @Test
    public void shouldCreateEventsForOlderEncountersForPatientsWithFilterChange() throws Exception {

        EventLog eventLogOld = new EventLog("uuid", new Date(), "PatientURL","patient", "oldFilter", "uuid" );
        when(eventLogRepository.findFirstByOrderByIdDesc()).thenReturn(eventLogOld);

        EventLog eventLogNew = new EventLog("uuid1", new Date(), "PatientURL","patient", "newFilter", "uuid1" );
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>(Arrays.asList(eventLogNew));
        when(eventLogFetcher.fetchEventLogsAfter("uuid")).thenReturn(eventLogs);
        when(eventLogRepository.findTop1ByCategoryAndObjectOrderByIdDesc(eventLogNew.getCategory(),eventLogNew.getObject())).thenReturn(eventLogOld);

        Set<String> eventRecordUuids  = new HashSet<String>();
        eventRecordUuids.add("uuid1");

        EventLog eventLog = new EventLog("uuid2", new Date(), "EncounterUrl","Encounter", null, "uuid1" );

        List<EventLog> eventsForFilterChange = new ArrayList<EventLog>();
        eventsForFilterChange.add(eventLog);
        when(eventLogFetcher.fetchEventLogsForFilterChange(eventRecordUuids)).thenReturn(eventsForFilterChange);
        eventLogPublisherJob.process();
        verify(eventLogRepository, times(1)).findFirstByOrderByIdDesc();
        verify(eventLogFetcher, times(1)).fetchEventLogsAfter("uuid");
        verify(eventLogRepository, times(1)).findTop1ByCategoryAndObjectOrderByIdDesc("patient", eventLogNew.getObject());
        verify(eventLogFetcher, times(1)).fetchEventLogsForFilterChange(eventRecordUuids);

        assertEquals(eventLog.getFilter(), eventLogNew.getFilter());

    }

    @Test
    public void shouldCreateEventsAndSetNewFilterIfEventFilterIsNull() throws Exception {

        EventLog eventLogOld = new EventLog("uuid", new Date(), "PatientURL","patient", null, "uuid" );
        when(eventLogRepository.findFirstByOrderByIdDesc()).thenReturn(eventLogOld);

        EventLog eventLogNew = new EventLog("uuid1", new Date(), "PatientURL","patient", "newFilter", "uuid1" );
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>(Arrays.asList(eventLogNew));
        when(eventLogFetcher.fetchEventLogsAfter("uuid")).thenReturn(eventLogs);
        when(eventLogRepository.findTop1ByCategoryAndObjectOrderByIdDesc(eventLogNew.getCategory(),eventLogNew.getObject())).thenReturn(eventLogOld);

        Set<String> eventRecordUuids  = new HashSet<String>();
        eventRecordUuids.add("uuid1");

        EventLog eventLog = new EventLog("uuid2", new Date(), "EncounterUrl","Encounter", null, "uuid1" );
        List<EventLog> eventsForFilterChange = new ArrayList<EventLog>();
        eventsForFilterChange.add(eventLog);
        when(eventLogFetcher.fetchEventLogsForFilterChange(eventRecordUuids)).thenReturn(eventsForFilterChange);
        eventLogPublisherJob.process();
        verify(eventLogRepository, times(1)).findFirstByOrderByIdDesc();
        verify(eventLogFetcher, times(1)).fetchEventLogsAfter("uuid");
        verify(eventLogRepository, times(1)).findTop1ByCategoryAndObjectOrderByIdDesc("patient", eventLogNew.getObject());
        verify(eventLogFetcher, times(1)).fetchEventLogsForFilterChange(eventRecordUuids);
        assertEquals(eventLog.getFilter(), eventLogNew.getFilter());
    }

    @Test
    public void shouldCreateEventsAndSetNewFilterIfRecentEventFilterIsNull() throws Exception {

        EventLog eventLogOld = new EventLog("uuid", new Date(), "PatientURL","patient", "oldFilter", "uuid" );
        when(eventLogRepository.findFirstByOrderByIdDesc()).thenReturn(eventLogOld);

        EventLog eventLogNew = new EventLog("uuid1", new Date(), "PatientURL","patient", null, "uuid1" );
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>(Arrays.asList(eventLogNew));
        when(eventLogFetcher.fetchEventLogsAfter("uuid")).thenReturn(eventLogs);
        when(eventLogRepository.findTop1ByCategoryAndObjectOrderByIdDesc(eventLogNew.getCategory(),eventLogNew.getObject())).thenReturn(eventLogOld);

        Set<String> eventRecordUuids  = new HashSet<String>();
        eventRecordUuids.add("uuid1");

        EventLog eventLog = new EventLog("uuid2", new Date(), "EncounterUrl","Encounter", null, "uuid1" );
        List<EventLog> eventsForFilterChange = new ArrayList<EventLog>();
        eventsForFilterChange.add(eventLog);
        when(eventLogFetcher.fetchEventLogsForFilterChange(eventRecordUuids)).thenReturn(eventsForFilterChange);
        eventLogPublisherJob.process();
        verify(eventLogRepository, times(1)).findFirstByOrderByIdDesc();
        verify(eventLogFetcher, times(1)).fetchEventLogsAfter("uuid");
        verify(eventLogRepository, times(1)).findTop1ByCategoryAndObjectOrderByIdDesc("patient", eventLogNew.getObject());
        verify(eventLogFetcher, times(1)).fetchEventLogsForFilterChange(eventRecordUuids);
        assertEquals(eventLog.getFilter(), eventLogNew.getFilter());
    }

    @Test
    public void shouldNotCreateEventsForOlderEncountersIfThereIsNotChangeInFilter() throws Exception {

        EventLog eventLogOld = new EventLog("uuid", new Date(), "PatientURL","patient", "Filter", "uuid" );
        when(eventLogRepository.findFirstByOrderByIdDesc()).thenReturn(eventLogOld);

        EventLog eventLogNew = new EventLog("uuid1", new Date(), "PatientURL","patient", "Filter", "uuid1" );
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>(Arrays.asList(eventLogNew));
        when(eventLogFetcher.fetchEventLogsAfter("uuid")).thenReturn(eventLogs);
        when(eventLogRepository.findTop1ByCategoryAndObjectOrderByIdDesc(eventLogNew.getCategory(),eventLogNew.getObject())).thenReturn(eventLogOld);

        Set<String> eventRecordUuids  = new HashSet<String>();
        eventRecordUuids.add("uuid1");

        eventLogPublisherJob.process();
        verify(eventLogRepository, times(1)).findFirstByOrderByIdDesc();
        verify(eventLogFetcher, times(1)).fetchEventLogsAfter("uuid");
        verify(eventLogRepository, times(1)).findTop1ByCategoryAndObjectOrderByIdDesc("patient", eventLogNew.getObject());
        verify(eventLogFetcher, never()).fetchEventLogsForFilterChange(Mockito.anySet());

    }

    @Test
    public void shouldNotCallFetchEventLogsForFilterChangeWhenPatientIsSavedForFirstTime() throws Exception {

        EventLog eventLogOld = new EventLog("uuid", new Date(), "PatientURL2","patient", "Filter", "uuid" );
        when(eventLogRepository.findFirstByOrderByIdDesc()).thenReturn(eventLogOld);

        EventLog eventLogNew = new EventLog("uuid1", new Date(), "PatientURL","patient", "Filter", "uuid1" );
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>(Arrays.asList(eventLogNew));
        when(eventLogFetcher.fetchEventLogsAfter("uuid")).thenReturn(eventLogs);
        when(eventLogRepository.findTop1ByCategoryAndObjectOrderByIdDesc(eventLogNew.getCategory(),eventLogNew.getObject())).thenReturn(null);

        Set<String> eventRecordUuids  = new HashSet<String>();
        eventRecordUuids.add("uuid1");

        eventLogPublisherJob.process();
        verify(eventLogRepository, times(1)).findFirstByOrderByIdDesc();
        verify(eventLogFetcher, times(1)).fetchEventLogsAfter("uuid");
        verify(eventLogRepository, times(1)).findTop1ByCategoryAndObjectOrderByIdDesc("patient", eventLogNew.getObject());
        verify(eventLogFetcher,never()).fetchEventLogsForFilterChange(Mockito.anySet());

    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRunTimeException() throws Exception {
        EventLog eventLogOld = new EventLog("uuid", new Date(), "PatientURL","patient", "oldFilter", "uuid" );
        when(eventLogRepository.findFirstByOrderByIdDesc()).thenReturn(eventLogOld);

        EventLog eventLogNew = new EventLog("uuid1", new Date(), "PatientURL","patient", "newFilter", "uuid1" );
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>(Arrays.asList(eventLogNew));
        when(eventLogFetcher.fetchEventLogsAfter("uuid")).thenReturn(eventLogs);
        when(eventLogRepository.findTop1ByCategoryAndObjectOrderByIdDesc(eventLogNew.getCategory(),eventLogNew.getObject())).thenReturn(eventLogOld);

        when(eventLogFetcher.fetchEventLogsForFilterChange(Mockito.anySet())).thenThrow(new IOException());
        eventLogPublisherJob.process();

    }


}