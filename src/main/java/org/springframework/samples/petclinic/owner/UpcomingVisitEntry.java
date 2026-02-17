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

/**
 * A read-only projection holding the display tuple for a single upcoming visit: the
 * owning {@link Owner}, the {@link Pet}, and the {@link Visit}.
 *
 * <p>
 * Instances are assembled by {@link VisitController#upcomingVisits} from the flat
 * Owner→Pet→Visit graph returned by {@link OwnerRepository#findOwnersWithUpcomingVisits}.
 */
public record UpcomingVisitEntry(Owner owner, Pet pet, Visit visit) {

}
