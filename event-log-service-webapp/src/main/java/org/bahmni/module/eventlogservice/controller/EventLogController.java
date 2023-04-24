package org.bahmni.module.eventlogservice.controller;

import org.apache.log4j.Logger;
import org.bahmni.module.eventlogservice.model.EventLog;
import org.bahmni.module.eventlogservice.repository.EventLogRepository;
import org.bahmni.module.eventlogservice.scheduler.jobs.EventLogPublisherJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rest/eventlog")
public class EventLogController {
    private EventLogRepository eventLogRepository;
    private static Logger logger = org.apache.log4j.Logger.getLogger(EventLogPublisherJob.class);

    @Autowired
    public EventLogController(EventLogRepository eventLogRepository) {
        this.eventLogRepository = eventLogRepository;
    }

    @RequestMapping(value = "/events/{category}", method = RequestMethod.GET)
    public Map<String, Object> getEvents(@PathVariable String category, @RequestParam(value = "uuid", required = false) String uuid, @RequestParam(value = "filterBy", required = true) String[] filterBy) {
        List<String> categoryList = new ArrayList<String>();
        categoryList.add("addressHierarchy");
        categoryList.add("patient".equals(category) ? "Encounter" : "patient");
        return getEventsExcept(uuid, filterBy, categoryList);
    }

    private Map<String, Object> getEventsExcept(@RequestParam(value = "uuid", required = false) String uuid, @RequestParam(value = "filterBy", required = true) String[] filterBy, List<String> categoryList) {
        Map<String, Object> response = new HashMap();
        if (uuid == null) {
            List<EventLog> events = new ArrayList<EventLog>();
            for(String filter: filterBy) {
                events.addAll(eventLogRepository.findTop100ByFilterContainsAndCategoryNotIn(filter, categoryList));
            }
            response.put("events", events);
            response.put("pendingEventsCount", events.size());
        } else {
            EventLog lastReadEventLog = eventLogRepository.findTop1ByUuid(uuid);
            List<EventLog> events = new ArrayList<EventLog>();
            if (null != lastReadEventLog) {
                for (String filter : filterBy) {
                    events.addAll(eventLogRepository.findTop100ByFilterContainsAndIdAfterAndCategoryNotIn(filter, lastReadEventLog.getId(), categoryList));
                }
            }
            else{
                logger.error("System is Not Able to find passed UUID in DataBase.");
            }

            response.put("events", events);
            response.put("pendingEventsCount", events.size());

        }
        return response;
    }

    @RequestMapping(value = "/getAddressHierarchyEvents", method = RequestMethod.GET)
    public Map<String, Object> getAddressHierarchyEvents(@RequestParam(value = "uuid", required = false) String uuid, @RequestParam(value = "filterBy", required = false) String[] filterBy) {
        if (filterBy != null && filterBy.length > 1) {
            throw new RuntimeException("Address hierarchy events should have only one filter!!");
        }
        Map<String, Object> response = new HashMap();
        if (filterBy == null && uuid == null) {
            response.put("events", eventLogRepository.findTop100ByCategoryAndFilterIsNull("addressHierarchy"));
            response.put("pendingEventsCount", eventLogRepository.countByCategoryAndFilterIsNull("addressHierarchy"));
        } else if (filterBy == null) {
            EventLog lastReadEventLog = eventLogRepository.findTop1ByUuid(uuid);
            response.put("events", eventLogRepository.findTop100ByCategoryAndIdAfterAndFilterIsNull("addressHierarchy", lastReadEventLog.getId()));
            response.put("pendingEventsCount", eventLogRepository.countByCategoryAndIdAfterAndFilterIsNull("addressHierarchy", lastReadEventLog.getId()));
        } else if (uuid == null) {
            response.put("events", eventLogRepository.findTop100ByCategoryAndFilterStartingWith("addressHierarchy", filterBy[0]));
            response.put("pendingEventsCount", eventLogRepository.countByCategoryAndFilterStartingWith("addressHierarchy", filterBy[0]));
        } else {
            EventLog lastReadEventLog = eventLogRepository.findTop1ByUuid(uuid);
            response.put("events", eventLogRepository.findTop100ByCategoryAndFilterStartingWithAndIdAfter("addressHierarchy", filterBy[0], lastReadEventLog.getId()));
            response.put("pendingEventsCount", eventLogRepository.countByCategoryAndFilterStartingWithAndIdAfter("addressHierarchy", filterBy[0], lastReadEventLog.getId()));
        }
        return response;
    }

    @RequestMapping(value = "/concepts", method = RequestMethod.GET)
    public Map<String, Object> getConcepts(@RequestParam(value = "uuid", required = false) String uuid) {
        return getEventsForCategory(uuid, "offline-concepts");
    }

    private Map<String, Object> getEventsForCategory(String uuid, String category) {
        Map<String, Object> response = new HashMap();
        if (uuid == null) {
            response.put("events", eventLogRepository.findTop100ByCategoryIs(category));
            response.put("pendingEventsCount", eventLogRepository.countByCategoryIs(category));
        } else {
            EventLog lastReadEventLog = eventLogRepository.findTop1ByUuid(uuid);
            response.put("events", eventLogRepository.findTop100ByCategoryIsAndIdAfter(category, lastReadEventLog.getId()));
            response.put("pendingEventsCount", eventLogRepository.countByCategoryIsAndIdAfter(category, lastReadEventLog.getId()));
        }
        return response;
    }

    @RequestMapping(value = "/forms", method = RequestMethod.GET)
    public Map<String, Object> getForms(@RequestParam(value = "uuid", required = false) String uuid) {
        return getEventsForCategory(uuid, "forms");
    }

}
