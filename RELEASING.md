# Releasing Booth Guide

Booth Guide uses Release Please to keep one product version in sync across the
backend, frontend, Git tags, GitHub releases, and published container images.

## One-time repository setup

Create a GitHub App owned by the organization, for example `boothguide-release`,
and install it only on this repository. Grant the app these repository
permissions:

- Contents: read and write
- Issues: read and write
- Pull requests: read and write

Store its numeric App ID as the Actions repository variable
`RELEASE_BOT_APP_ID`. Generate a private key for the app and store the complete
PEM contents as the Actions repository secret `RELEASE_BOT_PRIVATE_KEY`.

The workflow exchanges these credentials for a short-lived installation token.
Do not use a maintainer's personal token. A separate app token is required
because events created with the built-in `GITHUB_TOKEN` do not start
pull-request or release workflows.

If CLA Assistant does not automatically exempt bot accounts, add the release
bot to its allowlist.

## Release flow

1. Merge changes into `main` using Conventional Commit prefixes.
2. Release Please creates or updates one release pull request.
3. Review the version, changelog, and required checks.
4. Merge the release pull request.
5. Release Please creates the `vX.Y.Z` tag and GitHub release.
6. The existing container publishing workflow builds and publishes the three
   images using the release tag and, for stable releases, `latest`.

Version changes are selected from commit prefixes:

- `fix:` creates a patch release.
- `feat:` creates a minor release.
- `feat!:` or a `BREAKING CHANGE:` footer creates a major release.

Dependency and security updates should use `fix(deps):`, so merging them
prepares a patch release. Critical updates should be reviewed and merged
promptly, but the release pull request remains the final approval gate before
publishing.

## Bootstrap release

The repository files already use version `0.2.2`, while the newest existing tag
is `v0.2.1`. After this automation is merged and the bot token is configured,
publish `v0.2.2` once from commit
`170042398cfc4f83c030f2a606a2768814811b55`. Future versions are managed by
Release Please.
