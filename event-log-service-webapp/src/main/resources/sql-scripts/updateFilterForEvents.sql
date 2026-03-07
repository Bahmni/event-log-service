-- This Source Code Form is subject to the terms of the Mozilla Public License,
-- v. 2.0. If a copy of the MPL was not distributed with this file, You can
-- obtain one at https://www.bahmni.org/license/mplv2hd.
--
-- Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
-- graphic logo is a trademark of OpenMRS Inc.

-- update filter for address hierarchy entry events.
UPDATE event_log el
  INNER JOIN address_hierarchy_entry ahe
    ON el.object LIKE concat('%', ahe.uuid, '%') AND el.category = 'addressHierarchy'
SET el.filter = ahe.user_generated_id;

-- update filter for patient events.
UPDATE event_log el
  INNER JOIN person p
    ON substring(el.object,29,36) = p.uuid AND el.category = 'patient'
  INNER JOIN person_attribute pa
    ON pa.person_id = p.person_id
  INNER JOIN person_attribute_type pat
    ON pa.person_attribute_type_id = pat.person_attribute_type_id AND pat.name='addressCode'
SET el.filter = pa.value;

-- update filter for encounter events.
UPDATE event_log el
  INNER JOIN encounter e
    ON substring(el.object,31,36) = e.uuid AND el.category = 'Encounter'
  INNER JOIN person_attribute pa
    ON pa.person_id = e.patient_id
  INNER JOIN person_attribute_type pat
    ON pa.person_attribute_type_id = pat.person_attribute_type_id AND pat.name='addressCode'
SET el.filter = pa.value;