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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 * @author Wick Dynex
 */
@Controller
class VisitController {

	private final OwnerRepository owners;

	public VisitController(OwnerRepository owners) {
		this.owners = owners;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	/**
	 * Called before each and every @RequestMapping annotated method that has
	 * {@code {ownerId}} and {@code {petId}} path variables. Loads the owner and pet into
	 * the model and creates a new transient {@link Visit} for form binding.
	 * <p>
	 * When the path variables are absent (e.g. for {@code /visits/upcoming}) the method
	 * returns {@code null} so that no model attribute is contributed.
	 * </p>
	 * @param ownerId the owner id path variable, or {@code null} if not present
	 * @param petId the pet id path variable, or {@code null} if not present
	 * @param model the current model map
	 * @return a new {@link Visit} bound to the pet, or {@code null} when path variables
	 * are absent
	 */
	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable(value = "ownerId", required = false) Integer ownerId,
			@PathVariable(value = "petId", required = false) Integer petId, Map<String, Object> model) {
		if (ownerId == null || petId == null) {
			return null;
		}

		Optional<Owner> optionalOwner = owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));

		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new IllegalArgumentException(
					"Pet with id " + petId + " not found for owner with id " + ownerId + ".");
		}
		model.put("pet", pet);
		model.put("owner", owner);

		Visit visit = new Visit();
		pet.addVisit(visit);
		return visit;
	}

	// Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is
	// called
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String initNewVisitForm() {
		return "pets/createOrUpdateVisitForm";
	}

	// Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is
	// called
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@ModelAttribute Owner owner, @PathVariable int petId, @Valid Visit visit,
			BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}

		owner.addVisit(petId, visit);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Your visit has been booked");
		return "redirect:/owners/{ownerId}";
	}

	/**
	 * Displays all upcoming visits within the next {@code days} days.
	 * <p>
	 * Visits are fetched via a single repository query that filters on date range. The
	 * result is then flattened into a list of {@link UpcomingVisitEntry} records and
	 * additionally post-filtered in Java to exclude any visits outside the window that
	 * may have been loaded eagerly on returned owners.
	 * </p>
	 * @param days number of days into the future to look (default 7)
	 * @param model the Spring MVC model
	 * @return the logical view name {@code visits/upcoming}
	 */
	@GetMapping("/visits/upcoming")
	public String upcomingVisits(@RequestParam(defaultValue = "7") int days, Model model) {
		LocalDate today = LocalDate.now();
		LocalDate until = today.plusDays(days);

		List<Owner> ownersWithVisits = owners.findOwnersWithUpcomingVisits(today, until);

		List<UpcomingVisitEntry> entries = new ArrayList<>();
		for (Owner owner : ownersWithVisits) {
			for (Pet pet : owner.getPets()) {
				for (Visit visit : pet.getVisits()) {
					if (!visit.getDate().isBefore(today) && !visit.getDate().isAfter(until)) {
						entries.add(new UpcomingVisitEntry(owner, pet, visit));
					}
				}
			}
		}
		entries.sort(java.util.Comparator.comparing(e -> e.visit().getDate()));

		model.addAttribute("visits", entries);
		model.addAttribute("days", days);
		return "visits/upcoming";
	}

}
