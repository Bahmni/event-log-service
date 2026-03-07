-- This Source Code Form is subject to the terms of the Mozilla Public License,
-- v. 2.0. If a copy of the MPL was not distributed with this file, You can
-- obtain one at https://www.bahmni.org/license/mplv2hd.
--
-- Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
-- graphic logo is a trademark of OpenMRS Inc.

Insert into event_records(uuid,
                          title,
                          timestamp,
                          uri,
                          object,
                          category)
  (select uuid(),
     'addressHierarchy',
     '1970-01-01 00:00:01',
     concat('/openmrs/ws/rest/v1/addressHierarchy/',uuid),
     concat('/openmrs/ws/rest/v1/addressHierarchy/',uuid),
     'addressHierarchy'
   from address_hierarchy_entry);
