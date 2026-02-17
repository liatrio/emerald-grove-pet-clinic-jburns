# AI Agent Development Guide

This document provides essential guidance for AI agents working on the Emerald Grove Veterinary Clinic application.

## Context Marker

Always begin your response with all active emoji markers, in the order they were introduced.

Format:  "<marker1><marker2><marker3>\n<response>"

The marker for this instruction is: 🤖

## Critical Requirement: Strict TDD

**MANDATORY**: All feature implementations must follow **Strict Test-Driven Development (TDD)** methodology:

1. **RED Phase**: Write a failing test that defines the desired behavior
2. **GREEN Phase**: Write the minimum code required to make the test pass
3. **REFACTOR Phase**: Improve the code while maintaining test coverage

**Never write production code before a failing test.**

## Documentation Structure

Refer to these comprehensive guides for detailed information:

- @docs/DEVELOPMENT.md — **[Development Guide](docs/DEVELOPMENT.md)** - TDD workflow, setup, and development process
- @docs/TESTING.md — **[Testing Guide](docs/TESTING.md)** - Testing strategies, patterns, and TDD implementation
- @docs/ARCHITECTURE.md — **[Architecture Guide](docs/ARCHITECTURE.md)** - System design and technical decisions

## TDD Standards

### Coverage Requirements

- **Minimum 90% line coverage** for new code
- **100% branch coverage** for critical business logic
- All edge cases must be explicitly tested

### Test Organization

- Follow **Arrange-Act-Assert** pattern
- Use descriptive test method names that document behavior
- Tests must be **fast, isolated, and repeatable**

### Quality Gates

- Tests written before implementation (RED phase)
- All tests pass before commit
- Code coverage meets standards before merge

## Code Standards

### Architecture

- **Layered Architecture**: Presentation → Business → Data layers
- **Spring Boot Best Practices**: Use starters, follow conventions
- **Clean Code**: SOLID principles, DRY, single responsibility

### Database

- **Spring Data JPA** for data access
- **Proper entity relationships** with appropriate cascade settings
- **DTOs** for data transfer between layers

## Parse, Don't Validate

**MANDATORY**: Follow the *Parse, Don't Validate* principle throughout the codebase.

* **Parse at the boundaries.** Convert untrusted input (HTTP, env vars, CLI args, DB rows, JSON) into domain types immediately.
* **Prefer construction over checking.** Don't "validate X" and return `void`/`boolean`; instead **construct a stronger type** (or return a structured error).
* **Encode invariants in types.** If you learn "non-empty", "unique", "positive", "normalized", etc., represent that fact in the resulting type.
* **Keep core logic total.** Business/domain functions should accept only parsed/validated domain types and avoid defensive checks.
* **Make invalid states unrepresentable.** Use types like `NonEmptyList<T>`, `Email`, `PositiveInt`, `NonBlankString`, `Map` (instead of duplicate-prone pairs), etc.
* **Avoid shotgun parsing.** Don't scatter checks across the codebase; parsing should be centralized and composable.
* **Separate phases.** Structure code as: 1) **Parse** (may fail) → 2) **Execute** (assumes invariants).
* **Fail early with good errors.** Prefer returning specific parse errors close to the input source, not deep inside business logic.
* **Don't re-check what types already guarantee.** Once parsed into a refined type, downstream code should treat it as trustworthy.
* **Prefer data structures that enforce constraints.** Use `Map` for unique keys, branded types for IDs, enums for finite sets, etc.

## Development Workflow

1. **Requirements Analysis** → Understand feature and edge cases
2. **Test Design** → Write comprehensive failing tests
3. **TDD Implementation** → Follow Red-Green-Refactor cycle
4. **Integration** → Verify with existing code
5. **Documentation** → Update relevant docs

## Tools and Frameworks

- **Testing**: JUnit 5, Mockito, TestContainers, JaCoCo
- **Build**: Maven or Gradle
- **Quality**: Checkstyle, SpotBugs, SonarQube
- **Version Control**: Git with conventional commits

## Review Checklist

Before committing code:

- [ ] Tests written before implementation
- [ ] All tests pass
- [ ] Code coverage meets requirements (>90%)
- [ ] Follows SOLID principles
- [ ] No code duplication
- [ ] Proper error handling
- [ ] Documentation updated

This guide ensures consistent, high-quality TDD practices for AI contributors to the Emerald Grove Veterinary Clinic application.
