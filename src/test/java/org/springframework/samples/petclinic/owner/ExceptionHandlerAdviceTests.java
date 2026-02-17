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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Tests for the global exception handler — verifies that missing owner/pet requests
 * return HTTP 404 with a friendly error view.
 */
@WebMvcTest(OwnerController.class)
@DisabledInNativeImage
@DisabledInAotMode
class ExceptionHandlerAdviceTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	@Test
	void missingOwnerShowPageReturnsFriendly404() throws Exception {
		given(this.owners.findById(999)).willReturn(Optional.empty());

		mockMvc.perform(get("/owners/{ownerId}", 999))
			.andExpect(status().isNotFound())
			.andExpect(view().name("error"))
			.andExpect(model().attribute("status", 404));
	}

	@Test
	void missingOwnerEditPageReturnsFriendly404() throws Exception {
		given(this.owners.findById(999)).willReturn(Optional.empty());

		mockMvc.perform(get("/owners/{ownerId}/edit", 999))
			.andExpect(status().isNotFound())
			.andExpect(view().name("error"))
			.andExpect(model().attribute("status", 404));
	}

	@Test
	void existingOwnerShowPageReturnsOk() throws Exception {
		Owner owner = new Owner();
		owner.setId(1);
		owner.setFirstName("George");
		owner.setLastName("Franklin");
		owner.setAddress("110 W. Liberty St.");
		owner.setCity("Madison");
		owner.setTelephone("6085551023");
		given(this.owners.findById(1)).willReturn(Optional.of(owner));

		mockMvc.perform(get("/owners/{ownerId}", 1))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownerDetails"));
	}

}
