# CI/CD Pipeline Guide

This repository is configured with automated builds and releases using GitHub Actions.

## 🚀 How to Create a Release

### Automatic Release Process

1. **Commit and push your changes** to the main branch:
   ```bash
   git add .
   git commit -m "feat: your feature description"
   git push
   ```

2. **Create and push a version tag**:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

3. **Automated actions will**:
   - Extract the version number from the tag (e.g., `v1.0.0` → `1.0.0`)
   - Update `pom.xml` with the new version
   - Update `plugin.yml` with the new version
   - Build the plugin JAR with Maven
   - Create a GitHub Release with the JAR attached
   - Generate release notes automatically
   - Commit the version changes back to the repository

### Version Tag Format

Always use the format: `vX.Y.Z`

Examples:
- `v1.0.0` - Major release
- `v1.1.0` - Minor release (new features)
- `v1.0.1` - Patch release (bug fixes)
- `v2.0.0-beta.1` - Pre-release version

### Example Workflow

```bash
# Make your changes
git add .
git commit -m "feat: add piston support for random blocks"
git push

# Tag the release
git tag v1.1.0
git push origin v1.1.0

# Wait a few minutes for GitHub Actions to complete
# Check the Actions tab on GitHub to monitor progress
# Your release will appear in the Releases section
```

## 📋 What Gets Automated

✅ **Version Synchronization**
- `pom.xml` version automatically updated
- `plugin.yml` version automatically updated
- Both files committed back to repository

✅ **Build Process**
- Clean Maven build with Java 21
- Shaded JAR creation
- All dependencies packaged

✅ **GitHub Release**
- Automatic release creation
- JAR file attached to release
- Release notes auto-generated from commits
- Tagged with version number

## 🔍 Monitoring Builds

1. Go to the **Actions** tab on GitHub
2. Click on the latest workflow run
3. View logs for each step
4. Download artifacts if needed

## 🛠️ Manual Building

If you need to build locally:

```bash
# Build the plugin
mvn clean package

# The JAR will be in target/randomblock-VERSION.jar
```

## 📝 Commit Message Convention

For better auto-generated release notes, use conventional commits:

- `feat: description` - New features
- `fix: description` - Bug fixes
- `docs: description` - Documentation changes
- `chore: description` - Maintenance tasks
- `refactor: description` - Code refactoring
- `perf: description` - Performance improvements

## 🔐 Required Repository Settings

The CI/CD pipeline requires:
- GitHub Actions enabled (default)
- Write permissions for `GITHUB_TOKEN` (Settings → Actions → General → Workflow permissions → Read and write)

## 🚨 Troubleshooting

**Build fails?**
- Check the Actions tab for error logs
- Ensure Java 21 compatibility
- Verify pom.xml is valid

**Release not created?**
- Ensure tag starts with `v`
- Check workflow permissions
- Verify tag was pushed (`git push origin v1.0.0`)

**Version not updating?**
- Workflow requires write permissions
- Check if branch is protected (may need to adjust settings)

## 📦 Release Artifacts

Each release includes:
- `randomblock-X.Y.Z.jar` - Ready to use plugin
- Auto-generated release notes
- Source code snapshots (zip & tar.gz)

---

For questions or issues with the CI/CD pipeline, open an issue on GitHub.

