#!/usr/bin/env bash
set -euo pipefail

# Pin and verify the official bootstrap script; it also verifies its binary tarball.
installer="$(mktemp)"
trap 'rm -f "$installer"' EXIT
curl --proto '=https' --tlsv1.2 -fsSL \
  https://releases.nixos.org/nix/nix-2.32.4/install -o "$installer"
echo '8e37f1404678de5d640ec2a96f872bff1f513dac3a8b366c13922f4ab0950d22  '"$installer" | sha256sum --check --status
# The project uses its locked flake; no mutable default channel is needed.
NIX_INSTALLER_YES=1 NIX_INSTALLER_NO_CHANNEL_ADD=1 sh "$installer" --daemon

# Each Actions step starts a new shell, so expose the daemon profile there too.
test -f /nix/var/nix/profiles/default/etc/profile.d/nix-daemon.sh
echo '/nix/var/nix/profiles/default/bin' >> "$GITHUB_PATH"
# The installer creates this profile at runtime.
# shellcheck disable=SC1091
source /nix/var/nix/profiles/default/etc/profile.d/nix-daemon.sh
nix --version
