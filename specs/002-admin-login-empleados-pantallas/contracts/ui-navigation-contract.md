# UI Navigation Contract

## Screen map
- Route `/login`: Admin login screen.
- Route `/empleados`: Employee CRUD screen (protected).

## Access rules
- Anonymous users can access only `/login`.
- Access to `/empleados` requires in-memory authenticated session.
- Reload/close tab clears session, forcing navigation back to `/login`.

## Screen responsibilities
- Login screen:
  - Capture username/password.
  - Validate via HTTP Basic against protected API call.
  - Route to `/empleados` only on success.
- Employee screen:
  - List active employees by default.
  - Create, edit (with version), and logical delete.
  - Show explicit conflict-recovery flow on edit 409.
  - Provide logout action.

## UX and accessibility acceptance obligations
- Keyboard-only operation for all interactive controls.
- Visible focus indicator for all actionable elements.
- Contrast and semantics aligned with WCAG 2.1 AA.
- Error and status announcements for auth and CRUD actions.
