# Research - 002-admin-login-empleados-pantallas

## Decision 1: Keep authentication with HTTP Basic against protected endpoints
- Decision: Frontend login validates credentials by calling existing protected API endpoints with HTTP Basic.
- Rationale: Matches current backend security configuration and avoids introducing a new auth contract during this iteration.
- Alternatives considered: Dedicated `/login` endpoint with token exchange; mixed Basic + token modes.

## Decision 2: Session state stored in-memory only
- Decision: Store admin session only in frontend runtime memory.
- Rationale: Reduces persistence risk of credentials and aligns with clarified security constraint.
- Alternatives considered: sessionStorage; localStorage; per-request credential prompts.

## Decision 3: Employee delete behavior is logical delete
- Decision: `DELETE` action marks employee as inactive and excludes inactive employees from default listings.
- Rationale: Prevents accidental irreversible loss and supports safer admin operations.
- Alternatives considered: hard delete only; hard delete as optional admin override.

## Decision 4: Concurrency conflicts must be explicit and recoverable
- Decision: On edit conflict, UI must display conflict message, reload latest server data, and allow admin to reapply changes.
- Rationale: Preserves data integrity with optimistic locking while keeping workflow clear to users.
- Alternatives considered: silent overwrite; generic error without recovery guidance.

## Decision 5: Accessibility target is WCAG 2.1 AA for critical screens
- Decision: Login and employee CRUD screens must satisfy WCAG 2.1 AA checks.
- Rationale: Provides a concrete, testable baseline for keyboard, focus, contrast, and semantics.
- Alternatives considered: informal best-effort accessibility; WCAG 2.2 AA full scope.

## Decision 6: Two-screen UI structure with guarded employee route
- Decision: Split frontend into dedicated login screen and employee management screen with route protection.
- Rationale: Enforces access boundaries and keeps user journey predictable.
- Alternatives considered: single combined screen; modal login over CRUD screen.
