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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for the read-only upcoming visits page at {@code /visits/upcoming}. Displays
 * visits scheduled within the next N days (default 7).
 */
@Controller
class UpcomingVisitsController {

	private final VisitRepository visits;

	UpcomingVisitsController(VisitRepository visits) {
		this.visits = visits;
	}

	@GetMapping("/visits/upcoming")
	public String upcomingVisits(@RequestParam(defaultValue = "7") int days, Model model) {
		if (days < 1) {
			days = 7;
		}
		else if (days > 365) {
			days = 365;
		}
		LocalDate today = LocalDate.now();
		List<Visit> rawVisits = this.visits.findUpcomingWithPetAndOwner(today, today.plusDays(days));
		List<UpcomingVisitView> viewList = rawVisits.stream()
			.map(v -> new UpcomingVisitView(v.getDate(), v.getDescription(), v.getPet().getName(),
					v.getPet().getOwner().getFirstName(), v.getPet().getOwner().getLastName()))
			.toList();
		model.addAttribute("visits", viewList);
		model.addAttribute("days", days);
		return "visits/upcomingVisits";
	}

}
