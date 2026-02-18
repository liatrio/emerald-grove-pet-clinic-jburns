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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Test class for {@link UpcomingVisitsController}.
 */
@WebMvcTest(UpcomingVisitsController.class)
@DisabledInNativeImage
@DisabledInAotMode
class UpcomingVisitsControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private VisitRepository visitRepository;

	@Test
	void testUpcomingVisitsDefaultDays() throws Exception {
		given(this.visitRepository.findUpcomingWithPetAndOwner(any(), any())).willReturn(List.of());

		this.mockMvc.perform(get("/visits/upcoming"))
			.andExpect(status().isOk())
			.andExpect(view().name("visits/upcomingVisits"))
			.andExpect(model().attributeExists("visits"))
			.andExpect(model().attribute("days", 7));
	}

	@Test
	void testUpcomingVisitsCustomDays() throws Exception {
		given(this.visitRepository.findUpcomingWithPetAndOwner(any(), any())).willReturn(List.of());

		this.mockMvc.perform(get("/visits/upcoming").param("days", "14"))
			.andExpect(status().isOk())
			.andExpect(view().name("visits/upcomingVisits"))
			.andExpect(model().attributeExists("visits"))
			.andExpect(model().attribute("days", 14));
	}

	@Test
	void testUpcomingVisitsWithVisitData() throws Exception {
		Owner owner = new Owner();
		owner.setFirstName("George");
		owner.setLastName("Franklin");
		owner.setAddress("110 W. Liberty St.");
		owner.setCity("Madison");
		owner.setTelephone("6085551023");

		PetType dog = new PetType();
		dog.setName("dog");

		Pet pet = new Pet();
		pet.setName("Leo");
		pet.setType(dog);
		pet.setBirthDate(LocalDate.of(2010, 9, 7));
		owner.addPet(pet);
		pet.setId(1);
		pet.setOwner(owner);

		Visit visit = new Visit();
		visit.setDate(LocalDate.now().plusDays(2));
		visit.setDescription("Routine checkup");
		visit.setPet(pet);

		given(this.visitRepository.findUpcomingWithPetAndOwner(any(), any())).willReturn(List.of(visit));

		this.mockMvc.perform(get("/visits/upcoming"))
			.andExpect(status().isOk())
			.andExpect(view().name("visits/upcomingVisits"))
			.andExpect(model().attributeExists("visits"));
	}

}
