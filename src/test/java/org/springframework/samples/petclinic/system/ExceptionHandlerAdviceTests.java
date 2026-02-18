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

package org.springframework.samples.petclinic.system;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for {@link ExceptionHandlerAdvice}.
 */
@WebMvcTest({ ExceptionHandlerAdvice.class, ExceptionHandlerAdviceTests.TestController.class })
@DisabledInNativeImage
@DisabledInAotMode
class ExceptionHandlerAdviceTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void handleIllegalArgumentReturnsNotFoundStatus() throws Exception {
		mockMvc.perform(get("/test/illegal-argument")).andExpect(status().isNotFound());
	}

	@Test
	void handleIllegalArgumentDoesNotExposeExceptionMessage() throws Exception {
		String responseBody = mockMvc.perform(get("/test/illegal-argument"))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		org.assertj.core.api.Assertions.assertThat(responseBody)
			.doesNotContain("Internal entity details that should not be exposed");
	}

	@Test
	void handleResourceNotFoundExceptionReturnsNotFoundStatus() throws Exception {
		mockMvc.perform(get("/test/resource-not-found")).andExpect(status().isNotFound());
	}

	@Test
	void handleResourceNotFoundExceptionDoesNotExposeExceptionMessage() throws Exception {
		String responseBody = mockMvc.perform(get("/test/resource-not-found"))
			.andExpect(status().isNotFound())
			.andReturn()
			.getResponse()
			.getContentAsString();

		org.assertj.core.api.Assertions.assertThat(responseBody)
			.doesNotContain("Resource details that should not be exposed");
	}

	@RestController
	@RequestMapping("/test")
	static class TestController {

		@GetMapping("/illegal-argument")
		public String throwIllegalArgument() {
			throw new IllegalArgumentException("Internal entity details that should not be exposed");
		}

		@GetMapping("/resource-not-found")
		public String throwResourceNotFound() {
			throw new ResourceNotFoundException("Resource details that should not be exposed");
		}

	}

}
