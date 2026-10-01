# Contributing Guidelines

## Conventional Commits
Use the format: `<type>(<scope>): <subject>`

Types: `feat, fix, docs, style, refactor, perf, test, build, ci, chore, revert`
Scopes: `auth, user, student, certificate, import-export, ui, firestore, rules, docs, build`

### Examples
Good:
1. feat(auth): add admin login screen
2. fix(student): resolve crash on sort by GPA
3. docs(uml): add class diagram
4. refactor(ui): update color palette
5. perf(firestore): add index for student search
6. test(user): add cases for employee roles
7. build(gradle): update firebase bom version
8. chore(docs): format project plan
9. style(auth): align text in login button
10. feat(certificate): allow image upload for certificates

Bad:
1. added login screen
2. fix crash
3. updating diagrams
4. refactored some code
5. perf improvement
6. added tests
7. build fix
8. formatting
9. styled stuff
10. feat: certificates

## Branching Strategy
- `main`: Protected, demo-ready
- `develop`: Integration branch
- `feature/<scope>-<short-desc>`: For new features
- `fix/...`: For bug fixes
- `docs/...`: For documentation changes

Tags: `v0.1.0`, etc.
PRs require at least 1 review and squash merge.
