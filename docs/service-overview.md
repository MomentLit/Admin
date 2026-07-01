# Service Overview

## Service Name

Admin

## Service Responsibility

Admin-only moderation of spaces: approving or rejecting spaces that are pending review. This service does not own space creation, listing, update, deletion, image, or schedule behavior.

## Technology Stack

- Language: Java 21
- Framework: Spring Boot 4.1.0
- Build Tool: Gradle
- Database: PostgreSQL via Spring Data JPA; H2 test runtime dependency
- Other: Lombok. JWT (`io.jsonwebtoken:jjwt`) and Spring Security (stateless, no session) are used for authentication/authorization.

## Main Package Structure

- Main package: `com.example.admin`
- Controller: `AdminController` handles the two visible `/admin/spaces/{space-id}/approve` and `/admin/spaces/{space-id}/reject` APIs.
- Service: `AdminService` owns transaction boundaries (`@Transactional(readOnly = true)` at class level, `@Transactional` plus `synchronized` on each write method) and use-case orchestration for space approval/rejection.
- Repository: `SpaceRepository` and `UserRepository` extend `JpaRepository` directly with no derived query methods visible. `UserRepository` is defined but not referenced by `AdminService`.
- DTO: `AdminSpaceResponse` (record with `spaceId`) is under `dto.response`. `ApiResponse<T>` (record with `message`, `data`) under `global.dto` wraps responses; it has no HTTP status field, so status is set separately on `ResponseEntity`.
- Entity/domain: `Space` (`entity.Space`, table `spaces`) and `User` (`entity.User`, table `users`) under `entity`, along with `ApprovalStatus`, `SpaceCategory`, and `Role` enums.
- Global: `global.exception` (custom exception hierarchy), `global.security` (`JwtFilter`, `JwtProvider`, `UserPrincipal`), `global.config` (`SecurityConfig`), `global.util` (`ResponseUtil`).

## Main Domains

- `Space`: host-owned space record with `adminStatus` (`ApprovalStatus`), `category` (`SpaceCategory`), pricing, address, and soft-delete fields. Only `adminStatus` is mutated by this service, via `Space.update(ApprovalStatus)`.
- `User`: has a `Role` (`ADMIN`, `USER`); not read or written by `AdminService`, only exposed via `UserRepository`.
- `ApprovalStatus`: `PENDING`, `APPROVED`, `REJECTED`.
- `SpaceCategory`: `PRACTICE_ROOM`, `STUDIO`, `MEETING_ROOM`, `PARTY_ROOM`, `CLASSROOM`, `POPUP_STORE`, `OFFICE`, `HALL`, `CAFE`, `OTHER`.
- `Role`: `ADMIN`, `USER`.

No `SpaceImage` or `SpaceSchedule` entities exist in this service.

## Main Features

- Approve a pending space: only allowed when the authenticated role is `ADMIN` and the space's `adminStatus` is `PENDING`; sets it to `APPROVED`.
- Reject a pending space: same authorization and precondition, sets `adminStatus` to `REJECTED`.
- Both actions are idempotency-guarded: re-approving/rejecting a space that is no longer `PENDING` throws `PreconditionFailedException`.

## Main APIs

- `PATCH /admin/spaces/{space-id}/approve` — approves a pending space, returns `202` with `ApiResponse<AdminSpaceResponse>`.
- `PATCH /admin/spaces/{space-id}/reject` — rejects a pending space, returns `202` with `ApiResponse<AdminSpaceResponse>`.

Full details are in `API_SPEC.yaml`.

## Data Access Structure

- `SpaceRepository extends JpaRepository<Space, Long>` — no derived query methods; only `findById` is used, by `AdminService`.
- `UserRepository extends JpaRepository<User, String>` — no derived query methods, and not currently used by `AdminService`.

## Exception Handling

`AdminService` throws project-specific unchecked exceptions rooted at `AdminException extends RuntimeException`:

- `ForbiddenException` — authenticated role is not `ADMIN`.
- `SpaceNotFoundException` — space id does not exist.
- `PreconditionFailedException` — space `adminStatus` is not `PENDING`.

No `@ControllerAdvice` or global exception response mapper is visible, so HTTP status mapping for these exceptions is Needs confirmation.

## Test Structure

Only `AdminApplicationTests` context load test is visible.

## API Documentation

This service uses `API_SPEC.yaml` as the main API specification.
When API behavior changes, `API_SPEC.yaml` must be updated in the same PR.

## Development Notes

- Preserve the current single-module service structure.
- Follow the existing package and naming conventions.
- Keep controller, service, repository, entity, and DTO responsibilities separate where those layers exist.
- Do not add cross-service behavior (space CRUD, images, schedules) unless it is visible in code or explicitly specified by an Issue.
- If implementation changes API behavior, update `API_SPEC.yaml` in the same PR.

## Needs Confirmation

- `SecurityConfig` only defines request matchers for `/spaces*` paths (public reads plus authenticated mutations); no matcher covers `/admin/spaces/**`, so these endpoints fall through to the trailing `anyRequest().permitAll()` rule. Whether `AdminController` endpoints are intentionally left to be guarded only by the in-service `Role.ADMIN` check (rather than by Spring Security) needs confirmation.
- `AdminService` reads `principal.role()` from `@AuthenticationPrincipal UserPrincipal` without a null check; an unauthenticated request (no/invalid JWT) would hit a `NullPointerException` instead of a controlled 401/403 response. Needs confirmation whether this is acceptable given the matcher behavior above.
- HTTP status codes for `ForbiddenException`, `SpaceNotFoundException`, and `PreconditionFailedException` are Needs confirmation since no exception handler is visible.
- Whether `UserRepository` is intended for future admin features (e.g., role lookup from the database instead of the JWT claim) is Needs confirmation.
- Whether admin approve/reject actions should be audited/logged is not visible in code.
