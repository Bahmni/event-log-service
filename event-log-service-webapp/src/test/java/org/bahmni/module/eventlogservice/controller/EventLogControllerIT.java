/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.eventlogservice.controller;

import IT.BaseIntegrationTest;
import org.bahmni.module.eventlogservice.model.EventLog;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EventLogControllerIT extends BaseIntegrationTest {
    @Autowired
    private EventLogController eventLogController;

    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:testDataSet/eventLogController/insertEventLogs.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:testDataSet/truncateTables.sql")
    })
    @Test
    public void shouldGetAllEncounterEventLogsByFilterInAndCategoryNotIn() throws Exception {

        Map<String, Object> response = eventLogController.getEvents("encounter", null, new String[]{"2020", "202020"});

        assertNotNull(response);
        assertEquals(1, ((List) response.get("events")).size());
        assertEquals(1, response.get("pendingEventsCount"));
    }

    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:testDataSet/eventLogController/insertEventLogs.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:testDataSet/truncateTables.sql")
    })
    @Test
    public void shouldGetAllPatientEventLogsByFilterInAndCategoryNotIn() throws Exception {

        Map<String, Object> response = eventLogController.getEvents("patient", null, new String[]{"2020", "202020"});

        assertNotNull(response);
        assertEquals(3, ((List) response.get("events")).size());
        assertEquals(3, response.get("pendingEventsCount"));
    }

    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:testDataSet/eventLogController/insertEventLogs.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:testDataSet/truncateTables.sql")
    })
    @Test
    public void shouldGetAllEncounterEventsAfterUuidAndByFilterInAndCategoryNotIn() throws Exception {
        Map<String, Object> response = eventLogController.getEvents("encounter", "uuid11", new String[]{"2020", "202020"});

        assertNotNull(response);
        List<EventLog> events = (List<EventLog>) response.get("events");

        assertNotNull(events);
        assertEquals(0, events.size());
        assertEquals(0, response.get("pendingEventsCount"));
    }

    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:testDataSet/eventLogController/insertEventLogs.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:testDataSet/truncateTables.sql")
    })
    @Test
    public void shouldGetAllPatientEventsAfterUuidAndByFilterInAndCategoryNotIn() throws Exception {
        Map<String, Object> response = eventLogController.getEvents("patient", "uuid8", new String[]{"2020", "202020"});

        assertNotNull(response);
        List<EventLog> events = (List<EventLog>) response.get("events");

        assertNotNull(events);
        assertEquals(2, events.size());
        assertEquals(2, response.get("pendingEventsCount"));
    }

    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:testDataSet/eventLogController/insertEventLogs.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:testDataSet/truncateTables.sql")
    })
    @Test
    public void shouldGetAllEventLogsByCategoryAndFilterIsNull() throws Exception {
        Map<String, Object> response = eventLogController.getAddressHierarchyEvents(null, null);
        List<EventLog> events = (List<EventLog>) response.get("events");

        assertNotNull(events);
        assertEquals(2, events.size());
        assertEquals(2, response.get("pendingEventsCount"));
    }


    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:testDataSet/eventLogController/insertEventLogs.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:testDataSet/truncateTables.sql")
    })
    @Test
    public void shouldGetAllEventLogsAfterUuidByCategoryAndFilterIsNull() throws Exception {
        Map<String, Object> response = eventLogController.getAddressHierarchyEvents("uuid6", null);
        List<EventLog> events = (List<EventLog>) response.get("events");

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(1, response.get("pendingEventsCount"));
    }


    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:testDataSet/eventLogController/insertEventLogs.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:testDataSet/truncateTables.sql")
    })
    @Test
    public void shouldGetAllEventLogsByCategoryAndFilterStartingWith() throws Exception {
        Map<String, Object> response = eventLogController.getAddressHierarchyEvents(null, new String[]{"2020"});
        List<EventLog> events = (List<EventLog>) response.get("events");
        assertNotNull(events);
        assertEquals(5, events.size());
        assertEquals(5, response.get("pendingEventsCount"));
    }


    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:testDataSet/eventLogController/insertConceptEventLogs.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:testDataSet/truncateTables.sql")
    })
    @Test
    public void shouldGetAllConceptEventLogsByCategory() throws Exception {
        Map<String, Object> response = eventLogController.getConcepts(null);
        List<EventLog> events = (List<EventLog>) response.get("events");
        Integer pendingEventsCount = (Integer) response.get("pendingEventsCount");

        assertNotNull(events);
        assertEquals(5, events.size());
        assertEquals(5, pendingEventsCount.intValue());
    }

    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:testDataSet/eventLogController/insertEventLogs.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:testDataSet/truncateTables.sql")
    })
    @Test
    public void shouldGetAllEventLogsAfterUuidByCategoryAndFilterStartingWith() throws Exception {
        Map<String, Object> response = eventLogController.getAddressHierarchyEvents("uuid3", new String[]{"2020"});
        List<EventLog> events = (List<EventLog>) response.get("events");

        assertNotNull(events);
        assertEquals(2, events.size());
        assertEquals(2, response.get("pendingEventsCount"));
    }

    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:testDataSet/eventLogController/insertConceptEventLogs.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:testDataSet/truncateTables.sql")
    })
    @Test
    public void shouldGetAllConceptEventsAfterUuidAndByCategoryStartingWith() throws Exception {
        Map<String, Object> response = eventLogController.getConcepts("conceptUuid1");
        List<EventLog> events = (List<EventLog>) response.get("events");
        Integer pendingEventsCount = (Integer) response.get("pendingEventsCount");

        assertNotNull(events);
        assertEquals(4, events.size());
        assertEquals(4, pendingEventsCount.intValue());

    }

    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:testDataSet/eventLogController/insertEventLogs.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:testDataSet/truncateTables.sql")
    })
    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenFilterByHasMultipleFilters() throws Exception {
        eventLogController.getAddressHierarchyEvents("uuid3", new String[]{"2020", "202020"});
    }

    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:testDataSet/eventLogController/insertFormEventLogs.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:testDataSet/truncateTables.sql")
    })
    @Test
    public void shouldGetAllFormEventLogs() throws Exception {
        Map<String, Object> response = eventLogController.getForms(null);
        List<EventLog> events = (List<EventLog>) response.get("events");
        Integer pendingEventsCount = (Integer) response.get("pendingEventsCount");

        assertNotNull(events);
        assertEquals(4, events.size());
        assertEquals(4, pendingEventsCount.intValue());
    }

    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:testDataSet/eventLogController/insertFormEventLogs.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:testDataSet/truncateTables.sql")
    })
    @Test
    public void shouldGetRemainingFormEventLogs() throws Exception {
        Map<String, Object> response = eventLogController.getForms("formUuid3");
        List<EventLog> events = (List<EventLog>) response.get("events");
        Integer pendingEventsCount = (Integer) response.get("pendingEventsCount");

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(1, pendingEventsCount.intValue());
        assertEquals("formUuid4", events.get(0).getUuid());
    }


}