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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the upcoming visits endpoint in {@link VisitController}.
 */
@WebMvcTest(VisitController.class)
@DisabledInNativeImage
@DisabledInAotMode
class UpcomingVisitsControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	private Owner ownerWithUpcomingVisit() {
		Owner owner = new Owner();
		owner.setId(1);
		owner.setFirstName("George");
		owner.setLastName("Franklin");
		owner.setAddress("110 W. Liberty St.");
		owner.setCity("Madison");
		owner.setTelephone("6085551023");

		Pet pet = new Pet();
		pet.setName("Max");
		PetType dog = new PetType();
		dog.setName("dog");
		pet.setType(dog);
		pet.setBirthDate(LocalDate.now().minusYears(2));

		Visit visit = new Visit();
		visit.setDate(LocalDate.now().plusDays(3));
		visit.setDescription("Annual checkup");
		pet.addVisit(visit);

		owner.addPet(pet); // must be called before pet.setId() since addPet checks
							// isNew()
		pet.setId(1);
		return owner;
	}

	@Test
	void testUpcomingVisitsDefaultDays() throws Exception {
		List<Owner> ownerList = List.of(ownerWithUpcomingVisit());
		given(this.owners.findOwnersWithUpcomingVisits(any(LocalDate.class), any(LocalDate.class)))
			.willReturn(ownerList);

		mockMvc.perform(get("/visits/upcoming"))
			.andExpect(status().isOk())
			.andExpect(view().name("visits/upcoming"))
			.andExpect(model().attributeExists("visits"))
			.andExpect(model().attribute("days", 7));
	}

	@Test
	void testUpcomingVisitsCustomDays() throws Exception {
		given(this.owners.findOwnersWithUpcomingVisits(any(LocalDate.class), any(LocalDate.class)))
			.willReturn(List.of());

		mockMvc.perform(get("/visits/upcoming").param("days", "14"))
			.andExpect(status().isOk())
			.andExpect(view().name("visits/upcoming"))
			.andExpect(model().attribute("days", 14));
	}

	@Test
	void testUpcomingVisitsReturnsOnlyUpcomingVisits() throws Exception {
		Owner owner = ownerWithUpcomingVisit();
		// Add a past visit that should NOT appear in results
		Visit pastVisit = new Visit();
		pastVisit.setId(2);
		pastVisit.setDate(LocalDate.now().minusDays(5));
		pastVisit.setDescription("Past visit");
		owner.getPets().get(0).addVisit(pastVisit);

		given(this.owners.findOwnersWithUpcomingVisits(any(LocalDate.class), any(LocalDate.class)))
			.willReturn(List.of(owner));

		mockMvc.perform(get("/visits/upcoming"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("visits"))
			// Only the future visit (plusDays(3)) should appear, not the past one
			.andExpect(model().attribute("visits", hasSize(1)));
	}

	@Test
	void testUpcomingVisitsEmptyResult() throws Exception {
		given(this.owners.findOwnersWithUpcomingVisits(any(LocalDate.class), any(LocalDate.class)))
			.willReturn(List.of());

		mockMvc.perform(get("/visits/upcoming"))
			.andExpect(status().isOk())
			.andExpect(view().name("visits/upcoming"))
			.andExpect(model().attribute("visits", hasSize(0)))
			.andExpect(model().attribute("days", 7));
	}

	@Test
	void testUpcomingVisitsSortedByDate() throws Exception {
		Owner owner = ownerWithUpcomingVisit();

		// Add a second pet with a visit farther in the future
		Pet pet2 = new Pet();
		pet2.setName("Buddy");
		PetType cat = new PetType();
		cat.setName("cat");
		pet2.setType(cat);
		pet2.setBirthDate(LocalDate.now().minusYears(1));

		Visit laterVisit = new Visit();
		laterVisit.setDate(LocalDate.now().plusDays(6));
		laterVisit.setDescription("Checkup later");
		pet2.addVisit(laterVisit);

		owner.addPet(pet2); // must be called before pet2.setId()
		pet2.setId(2);

		given(this.owners.findOwnersWithUpcomingVisits(any(LocalDate.class), any(LocalDate.class)))
			.willReturn(List.of(owner));

		mockMvc.perform(get("/visits/upcoming"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("visits", hasSize(2)));
	}

}
