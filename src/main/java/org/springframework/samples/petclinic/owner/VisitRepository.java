/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for {@link Visit} entities. Provides a query for fetching upcoming
 * visits with their associated pet and owner data eagerly loaded to avoid N+1 queries.
 */
public interface VisitRepository extends JpaRepository<Visit, Integer> {

	@Query("SELECT v FROM Visit v JOIN FETCH v.pet p JOIN FETCH p.owner o "
			+ "WHERE v.date BETWEEN :startDate AND :endDate ORDER BY v.date ASC")
	List<Visit> findUpcomingWithPetAndOwner(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

}
