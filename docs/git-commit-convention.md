# Git Commit & Branching Convention

## 1. Commit Message Format
Use Conventional Commits format:
`<type>(<scope>): <description>`

### Types
- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation changes
- `style`: Formatting, missing semi colons, etc.
- `refactor`: Code change that neither fixes a bug nor adds a feature
- `perf`: Code change that improves performance
- `test`: Adding missing tests or correcting existing tests
- `chore`: Changes to build process or auxiliary tools

### Examples
- `feat(auth): add login screen with validation`
- `fix(student): fix crash when searching by name`
- `docs(srs): update role permissions matrix`

## 2. Branching Strategy
- `main`: Production-ready code
- `develop`: Main development branch
- `feature/<name>`: Feature branches
- `fix/<issue-name>`: Bugfix branches
