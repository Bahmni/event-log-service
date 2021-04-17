package org.bahmni.module.eventlogservice.controller;

import org.bahmni.module.eventlogservice.model.EventLog;
import org.bahmni.module.eventlogservice.repository.EventLogRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class EventLogControllerTest {

    @Mock
    private EventLogRepository eventLogRepository;

    private EventLogController eventLogController;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        eventLogController = new EventLogController(eventLogRepository);
    }

    @Test
    public void shouldGetEncounterEventLogExcludingCategories() throws Exception {
        String uuid = "uuid1";
        String[] filterBy = new String[]{"0303020", "30302001"};
        List<String> filtersList = Arrays.asList(filterBy);

        List<String> categoryList = new ArrayList<String>();
        categoryList.add("addressHierarchy");
        categoryList.add("patient");
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>();
        EventLog lastReadEventLog = new EventLog();
        lastReadEventLog.setId(1000);
        when(eventLogRepository.findTop1ByUuid(uuid)).thenReturn(lastReadEventLog);
        for(String filter: filterBy) {
            when(eventLogRepository.findTop100ByFilterContainsAndIdAfterAndCategoryNotIn(filter, lastReadEventLog.getId(), categoryList)).thenReturn(eventLogs);
        }

//        when(eventLogRepository.countByFilterInAndIdAfterAndCategoryNotIn(filtersList, lastReadEventLog.getId(), categoryList)).thenReturn(1);

        Map<String, Object> response = eventLogController.getEvents("encounter", uuid, filterBy);
        List<EventLog> events = (List<EventLog>) response.get("events");
        Integer pendingEventsCount = (Integer) response.get("pendingEventsCount");

        verify(eventLogRepository, times(1)).findTop1ByUuid(uuid);
        for(String filter: filterBy) {
            verify(eventLogRepository, times(1)).findTop100ByFilterContainsAndIdAfterAndCategoryNotIn(filter, lastReadEventLog.getId(), categoryList);
        }

        verify(eventLogRepository, never()).findTop100ByFilterContainsAndCategoryNotIn(any(String.class), anyList());
//        verify(eventLogRepository, times(1)).countByFilterInAndIdAfterAndCategoryNotIn(filtersList, lastReadEventLog.getId(), categoryList);
        assertNotNull(events);
        assertEquals(events.size(), pendingEventsCount.intValue());
    }

    @Test
    public void shouldGetPatientEventLogExcludingCategories() throws Exception {
        String uuid = "uuid1";
        String[] filterBy = new String[]{"0303020", "30302001"};
        List<String> filtersList = Arrays.asList(filterBy);

        List<String> categoryList = new ArrayList<String>();
        categoryList.add("addressHierarchy");
        categoryList.add("Encounter");
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>();
        EventLog lastReadEventLog = new EventLog();
        lastReadEventLog.setId(1000);
        when(eventLogRepository.findTop1ByUuid(uuid)).thenReturn(lastReadEventLog);
        for(String filter: filterBy) {
            when(eventLogRepository.findTop100ByFilterContainsAndIdAfterAndCategoryNotIn(filter, lastReadEventLog.getId(), categoryList)).thenReturn(eventLogs);
        }

//        when(eventLogRepository.countByFilterInAndIdAfterAndCategoryNotIn(filtersList, lastReadEventLog.getId(), categoryList)).thenReturn(1);

        Map<String, Object> response = eventLogController.getEvents("patient", uuid, filterBy);
        List<EventLog> events = (List<EventLog>) response.get("events");
        Integer pendingEventsCount = (Integer) response.get("pendingEventsCount");

        verify(eventLogRepository, times(1)).findTop1ByUuid(uuid);
        for(String filter: filterBy) {
            verify(eventLogRepository, times(1)).findTop100ByFilterContainsAndIdAfterAndCategoryNotIn(filter, lastReadEventLog.getId(), categoryList);
        }

        verify(eventLogRepository, never()).findTop100ByFilterContainsAndCategoryNotIn(any(String.class), anyList());
//        verify(eventLogRepository, times(1)).countByFilterInAndIdAfterAndCategoryNotIn(filtersList, lastReadEventLog.getId(), categoryList);
        assertNotNull(events);
        assertEquals(events.size(), pendingEventsCount.intValue());
    }

    @Test
    public void shouldGetConceptEventLog() throws Exception {
        String uuid = "uuid1";
        String category = "offline-concepts";
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>();
        EventLog lastReadEventLog = new EventLog();
        lastReadEventLog.setId(1000);
        when(eventLogRepository.findTop1ByUuid(uuid)).thenReturn(lastReadEventLog);
        when(eventLogRepository.findTop100ByCategoryIsAndIdAfter(category, lastReadEventLog.getId())).thenReturn(eventLogs);
        when(eventLogRepository.countByCategoryIsAndIdAfter(category, lastReadEventLog.getId())).thenReturn(10);
        Map<String, Object> response = eventLogController.getConcepts(uuid);
        List<EventLog> concepts = (List<EventLog>) response.get("events");
        Integer pendingEventsCount = (Integer) response.get("pendingEventsCount");


        verify(eventLogRepository, times(1)).findTop1ByUuid(uuid);
        verify(eventLogRepository, times(1)).findTop100ByCategoryIsAndIdAfter(category, lastReadEventLog.getId());
        verify(eventLogRepository, never()).findTop100ByCategoryIs(anyString());
        assertNotNull(concepts);
        verify(eventLogRepository, times(1)).countByCategoryIsAndIdAfter(category, lastReadEventLog.getId());
        assertEquals(10, pendingEventsCount.intValue());
    }

    @Test
    public void shouldGetAllEncounterEventLogExcludingCategoriesForTheFirstTime() throws Exception {
        String[] filterBy = new String[]{"0303020", "30302001"};
//        List<String> filtersList = Arrays.asList(filterBy);
        List<String> categoryList = new ArrayList<String>();
        categoryList.add("addressHierarchy");
        categoryList.add("patient");
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>();
        for(String filter: filterBy) {
            when(eventLogRepository.findTop100ByFilterContainsAndCategoryNotIn(filter, categoryList)).thenReturn(eventLogs);
        }

//        when(eventLogRepository.countByFilterInAndCategoryNotIn(filtersList, categoryList)).thenReturn(1);

        Map<String, Object> response = eventLogController.getEvents("encounter", null, filterBy);
        List<EventLog> events = (List<EventLog>) response.get("events");
        Integer pendingEventsCount = (Integer) response.get("pendingEventsCount");

        for(String filter: filterBy) {
            verify(eventLogRepository, times(1)).findTop100ByFilterContainsAndCategoryNotIn(filter, categoryList);
        }

        verify(eventLogRepository, never()).findTop1ByUuid(anyString());
        verify(eventLogRepository, never()).findTop100ByFilterContainsAndIdAfterAndCategoryNotIn(any(String.class), anyInt(), anyList());
        assertNotNull(events);
        assertEquals(events.size(), pendingEventsCount.intValue());

    }

    @Test
    public void shouldGetAllPatientEventLogExcludingCategoriesForTheFirstTime() throws Exception {
        String[] filterBy = new String[]{"0303020", "30302001"};
        List<String> filtersList = Arrays.asList(filterBy);
        List<String> categoryList = new ArrayList<String>();
        categoryList.add("addressHierarchy");
        categoryList.add("Encounter");
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>();
        for(String filter: filterBy) {
            when(eventLogRepository.findTop100ByFilterContainsAndCategoryNotIn(filter, categoryList)).thenReturn(eventLogs);
        }

//        when(eventLogRepository.countByFilterInAndCategoryNotIn(filtersList, categoryList)).thenReturn(1);

        Map<String, Object> response = eventLogController.getEvents("patient", null, filterBy);
        List<EventLog> events = (List<EventLog>) response.get("events");
        Integer pendingEventsCount = (Integer) response.get("pendingEventsCount");

        for(String filter: filterBy) {
            verify(eventLogRepository, times(1)).findTop100ByFilterContainsAndCategoryNotIn(filter, categoryList);
        }
        verify(eventLogRepository, never()).findTop1ByUuid(anyString());
        verify(eventLogRepository, never()).findTop100ByFilterContainsAndIdAfterAndCategoryNotIn(any(String.class), anyInt(), anyList());
        assertNotNull(events);
        assertEquals(events.size(), pendingEventsCount.intValue());
    }


    @Test
    public void shouldGetEventForSpecificCategory() throws Exception {
        String uuid = "uuid1";
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>();
        EventLog lastReadEventLog = new EventLog();
        lastReadEventLog.setId(1000);
        when(eventLogRepository.findTop1ByUuid(uuid)).thenReturn(lastReadEventLog);
        when(eventLogRepository.findTop100ByCategoryAndIdAfterAndFilterIsNull("addressHierarchy", lastReadEventLog.getId())).thenReturn(eventLogs);
        when(eventLogRepository.countByCategoryAndIdAfterAndFilterIsNull("addressHierarchy", lastReadEventLog.getId())).thenReturn(1);

        Map<String, Object> response = eventLogController.getAddressHierarchyEvents(uuid, null);
        List<EventLog> events = (List<EventLog>) response.get("events");
        Integer pendingEventsCount = (Integer) response.get("pendingEventsCount");

        verify(eventLogRepository, times(1)).findTop1ByUuid(uuid);
        verify(eventLogRepository, times(1)).findTop100ByCategoryAndIdAfterAndFilterIsNull("addressHierarchy", lastReadEventLog.getId());
        verify(eventLogRepository, times(1)).countByCategoryAndIdAfterAndFilterIsNull("addressHierarchy", lastReadEventLog.getId());
        verify(eventLogRepository, never()).findTop100ByCategoryAndFilterIsNull(anyString());
        verify(eventLogRepository, never()).countByCategoryAndFilterIsNull(anyString());
        verify(eventLogRepository, never()).findTop100ByCategoryAndFilterStartingWith(anyString(), anyString());
        verify(eventLogRepository, never()).countByCategoryAndFilterStartingWith(anyString(), anyString());
        verify(eventLogRepository, never()).findTop100ByCategoryAndFilterStartingWithAndIdAfter(anyString(), anyString(), anyInt());
        verify(eventLogRepository, never()).countByCategoryAndFilterStartingWithAndIdAfter(anyString(), anyString(), anyInt());

        assertNotNull(events);
        assertEquals(1, pendingEventsCount.intValue());
    }

    @Test
    public void shouldGetEventForSpecificCategoryForFirstTime() throws Exception {
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>();
        when(eventLogRepository.findTop100ByCategoryAndFilterIsNull("addressHierarchy")).thenReturn(eventLogs);

        Map<String, Object> response = eventLogController.getAddressHierarchyEvents(null, null);
        List<EventLog> events = (List<EventLog>) response.get("events");
        verify(eventLogRepository, times(1)).findTop100ByCategoryAndFilterIsNull("addressHierarchy");
        verify(eventLogRepository, never()).findTop1ByUuid(anyString());
        verify(eventLogRepository, never()).findTop100ByCategoryAndIdAfterAndFilterIsNull(anyString(), anyInt());
        verify(eventLogRepository, never()).findTop100ByCategoryAndFilterStartingWith(anyString(), anyString());
        verify(eventLogRepository, never()).findTop100ByCategoryAndFilterStartingWithAndIdAfter(anyString(), anyString(), anyInt());
        assertNotNull(events);
    }

    @Test
    public void shouldGetEventForSpecificCategoryWithFilterStartingWith() throws Exception {
        String uuid = "uuid1";
        String[] filterBy = new String[]{"0303020"};
        List<String> filtersList = Arrays.asList(filterBy);
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>();
        EventLog lastReadEventLog = new EventLog();
        lastReadEventLog.setId(1000);
        when(eventLogRepository.findTop1ByUuid(uuid)).thenReturn(lastReadEventLog);
        when(eventLogRepository.findTop100ByCategoryAndFilterStartingWithAndIdAfter("addressHierarchy", filterBy[0], lastReadEventLog.getId())).thenReturn(eventLogs);

        Map<String, Object> response = eventLogController.getAddressHierarchyEvents(uuid, filterBy);
        List<EventLog> events = (List<EventLog>) response.get("events");

        verify(eventLogRepository, times(1)).findTop1ByUuid(uuid);
        verify(eventLogRepository, times(1)).findTop100ByCategoryAndFilterStartingWithAndIdAfter("addressHierarchy", filterBy[0], lastReadEventLog.getId());
        verify(eventLogRepository, never()).findTop100ByCategoryAndFilterIsNull(anyString());
        verify(eventLogRepository, never()).findTop100ByCategoryAndIdAfterAndFilterIsNull(anyString(), anyInt());
        verify(eventLogRepository, never()).findTop100ByCategoryAndFilterStartingWith(anyString(), anyString());

        assertNotNull(events);
    }


    @Test
    public void shouldGetEventForSpecificCategoryWithFilterStartingWithForFirstTime() throws Exception {
        String filterBy = "303020";
        String[] filters = new String[]{filterBy};
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>();
        when(eventLogRepository.findTop100ByCategoryAndFilterStartingWith("addressHierarchy", filterBy)).thenReturn(eventLogs);

        Map<String, Object> response = eventLogController.getAddressHierarchyEvents(null, filters);
        List<EventLog> events = (List<EventLog>) response.get("events");

        verify(eventLogRepository, times(1)).findTop100ByCategoryAndFilterStartingWith("addressHierarchy", filterBy);
        verify(eventLogRepository, never()).findTop1ByUuid(anyString());
        verify(eventLogRepository, never()).findTop100ByCategoryAndFilterIsNull(anyString());
        verify(eventLogRepository, never()).findTop100ByCategoryAndIdAfterAndFilterIsNull(anyString(), anyInt());
        verify(eventLogRepository, never()).findTop100ByCategoryAndFilterStartingWithAndIdAfter(anyString(), anyString(), anyInt());

        assertNotNull(events);
    }

    @Test
    public void shouldGetAllConceptEventLogsForTheFirstTime() throws Exception {
        String category = "offline-concepts";
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>();
        when(eventLogRepository.findTop100ByCategoryIs(category)).thenReturn(eventLogs);
        when(eventLogRepository.countByCategoryIs(category)).thenReturn(110);

        Map<String, Object> response = eventLogController.getConcepts(null);
        List<EventLog> events = (List<EventLog>) response.get("events");
        Integer pendingEventsCount = (Integer) response.get("pendingEventsCount");


        verify(eventLogRepository, times(1)).findTop100ByCategoryIs(category);
        verify(eventLogRepository, never()).findTop1ByUuid(anyString());
        verify(eventLogRepository, never()).findTop100ByCategoryIsAndIdAfter(anyString(), anyInt());
        assertNotNull(events);
        verify(eventLogRepository, times(1)).countByCategoryIs(category);
        assertEquals(110, pendingEventsCount.intValue());
    }

    @Test
    public void shouldGetAllFormEventsForTheFirstTime() throws Exception {
        String category = "forms";
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>();
        when(eventLogRepository.findTop100ByCategoryIs(category)).thenReturn(eventLogs);
        when(eventLogRepository.countByCategoryIs(category)).thenReturn(110);

        Map<String, Object> response = eventLogController.getForms(null);
        List<EventLog> events = (List<EventLog>) response.get("events");
        Integer pendingEventsCount = (Integer) response.get("pendingEventsCount");

        verify(eventLogRepository, times(1)).findTop100ByCategoryIs(category);
        verify(eventLogRepository, never()).findTop1ByUuid(anyString());
        verify(eventLogRepository, never()).findTop100ByCategoryIsAndIdAfter(anyString(), anyInt());
        assertNotNull(events);
        verify(eventLogRepository, times(1)).countByCategoryIs(category);
        assertEquals(110, pendingEventsCount.intValue());
    }

    @Test
    public void shouldGetFormEventLog() throws Exception {
        String uuid = "uuid1";
        String category = "forms";
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>();
        EventLog lastReadEventLog = new EventLog();
        lastReadEventLog.setId(1000);
        when(eventLogRepository.findTop1ByUuid(uuid)).thenReturn(lastReadEventLog);
        when(eventLogRepository.findTop100ByCategoryIsAndIdAfter(category, lastReadEventLog.getId())).thenReturn(eventLogs);
        when(eventLogRepository.countByCategoryIsAndIdAfter(category, lastReadEventLog.getId())).thenReturn(10);
        Map<String, Object> response = eventLogController.getForms(uuid);
        List<EventLog> concepts = (List<EventLog>) response.get("events");
        Integer pendingEventsCount = (Integer) response.get("pendingEventsCount");


        verify(eventLogRepository, times(1)).findTop1ByUuid(uuid);
        verify(eventLogRepository, times(1)).findTop100ByCategoryIsAndIdAfter(category, lastReadEventLog.getId());
        verify(eventLogRepository, never()).findTop100ByCategoryIs(anyString());
        assertNotNull(concepts);
        verify(eventLogRepository, times(1)).countByCategoryIsAndIdAfter(category, lastReadEventLog.getId());
        assertEquals(10, pendingEventsCount.intValue());
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenFilterByHasMultipleFilters() throws Exception {
        eventLogController.getAddressHierarchyEvents("uuid3", new String[]{"2020", "202020"});
    }

    @Test
    public void shouldNotGetEncounterEventLogExcludingCategoriesIfUUIDNotPresentInDatabase() throws Exception {
        String uuid = "uuid1";
        String[] filterBy = new String[]{"0303020", "30302001"};

        List<String> categoryList = new ArrayList<String>();
        categoryList.add("addressHierarchy");
        categoryList.add("patient");
        ArrayList<EventLog> eventLogs = new ArrayList<EventLog>();
        when(eventLogRepository.findTop1ByUuid(uuid)).thenReturn(null);

        Map<String, Object> response = eventLogController.getEvents("encounter", uuid, filterBy);
        List<EventLog> events = (List<EventLog>) response.get("events");
        Integer pendingEventsCount = (Integer) response.get("pendingEventsCount");

        
        verify(eventLogRepository, times(1)).findTop1ByUuid(uuid);

        assertNotNull(events);
        assertEquals(events.size(), pendingEventsCount.intValue());
    }
}