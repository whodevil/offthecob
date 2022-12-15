+++
title = "Git Commit Signing"
lastmod = 2022-12-15T12:16:15-08:00
tags = ["git", "github", "gpg", "identity-management", "best-practices"]
draft = false
description = "Why git commit signing matters and how to implement it."
comments = true
+++

## Background {#background}

Git is a distributed repository, when a developer clones a git repository, the developer actually clones the whole
repository and all of it's history. When pushing a branch, the developer is telling the origin repository about
a branch they've made changes to, and pull requests exist for merging that new code into the main branch on origin.
While it is possible to protect the trunk e.g. "main" from having history rewritten, there is nothing stopping a
malicious insider from writing commits as a different user. GPG signing of commits is a mechanism to prove the
git log is not lying about who the author is. After generating a signing key, the user uploads their public key
to GitHub, so that the system can verify the signature. While some would argue the threat might be
minimal, the mantra in the security space is "defense in depth." This article plans to illustrate that implementing
signing is not difficult, and therefore the additional protection against repudiation is an easy win
for security.


## How To Steps {#how-to-steps}

First install the right tools.

```bash
brew install gpg pinentry-mac
```

pinentry-mac is a tool that gives a pop-up for key passphrase entry. This is nice if the developer uses VS Code or Intellij's
commit functionality, otherwise GPG will not know how to prompt for the passphrase. The user is not prompted every
time for the passphrase, because the GPG Agent holds onto authentication for a period of time. To wire pinentry up,
run the following command.

```bash
echo "pinentry-program $(brew --prefix)/bin/pinentry-mac" | tee ~/.gnupg/gpg-agent.conf
```

Next, generate a private key.

```bash
gpg --full-gen-key
# Choose option 10 for ECC Sign Only
# Choose Curve 25519 for the algorithm
# Choose an expiration
# Add your full name ane email address
# Type O for ok, and then enter a secure passphrase
```

Advice on an expiration date, the GitHub documentation says to make the key last forever,
I'd suggest 3 years. This gives a long enough window that the developer doesn't need to regenerate their key
frequently, but if they leave their organization for some reason they can also leave that old key behind with confidence that
eventually the key will expire. This is ultimately up to personal taste.

The next step is to wire the key up to git and GitHub. First, fetch the public key id.

```bash
➜ gpg --list-keys dude@example.com # The email address you used when creating the key
/Users/user/.gnupg/pubring.kbx
----------------------------------
pub   ed25519 2022-12-14 [SC] [expires: 2025-12-13]
      074624E036E80C2094E8545658FF25FD2288E12B
uid           [ultimate] Dude <dude@example.com>
```

Tell git about the signing key.

```bash
git config --global user.signingkey 074624E036E80C2094E8545658FF25FD2288E12B
git config --global commit.gpgsign true
```

Export your public key

```bash
➜ gpg --export --armor dude@example.com
-----BEGIN PGP PUBLIC KEY BLOCK-----

mDMEY5o23hYJKwYBBAHaRw8BAQdAxp5rXGv7lAIgTZsM2zyqA/84Hl3O3+KSno1E
Za7IN/i0F0R1ZGUgPGR1ZGVAZXhhbXBsZS5jb20+iJkEExYKAEEWIQQHRiTgNugM
IJToVFZY/yX9IojhKwUCY5o23gIbAwUJBaOagAULCQgHAgIiAgYVCgkICwIEFgID
AQIeBwIXgAAKCRBY/yX9IojhK5hcAP9MVjFObIbXlGNaMOynnq64jBNsIi3i6GDI
o5zUWsFSowD/ejJO2YQg4NDemQ22wgiDJNzMs5YIdc1FaNbTYvw3Pgg=
=6Gmx
-----END PGP PUBLIC KEY BLOCK-----
```

Finally, login to GitHub, go to settings, then "SSH and GPG Keys" and click "New GPG Key"
and enter the block including the BEGIN and END tags into the GitHub ui.

After creating commits, the developer should see "verified" in the GitHub UI, and when
they run the command "git show --show-signatures" they'll see signing information.


## What's next {#what-s-next}

I plan to explore the following in future articles.

-   Use a U2F key to store the private gpg key.
-   Use gpg for authentication with github.
