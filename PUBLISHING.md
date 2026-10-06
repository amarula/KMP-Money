# Publishing to Maven Central

`kmp-money` publishes under the `com.amarulasolutions` namespace to Sonatype's Central
Portal. Publishing is done with Gradle's own `maven-publish` and `signing`
plugins (configured in `kmp-money/build.gradle.kts`) -- no third-party
publishing plugin. Gradle stages and signs the artifacts for every target
(`jvm`, `android`, `iosArm64`, `iosSimulatorArm64`, plus the root metadata
publication) into a zip, and the Jenkins pipeline
(`.jenkins/kmpMoneyRelease.jenkinsfile`) uploads that zip to the Central
Portal's [Publisher API](https://central.sonatype.org/publish/publish-portal-api/)
via `curl`.

Everything below is a **one-time setup step** done by a human with access to
the relevant accounts (Sonatype, GPG keyserver, Jenkins). None of it can be
automated by an agent, since each step hands over a real secret or claims a
real identity.

## 1. Sonatype Central Portal account + namespace

1. Register at https://central.sonatype.com.
2. Claim the `com.amarulasolutions` namespace (Central Portal verifies ownership of
   the corresponding domain or org).
3. Once verified, go to your account's **View Account** page and generate a
   **User Token** (a username/password pair, *not* your login password).
4. Base64-encode `username:password` -- this combined string is the Bearer
   token the pipeline sends as `Authorization: Bearer <value>`:
   ```bash
   printf "username:password" | base64
   ```

## 2. GPG signing key

Maven Central requires every published artifact to be GPG-signed.

```bash
gpg --full-generate-key
# -> RSA and RSA, 4096 bits, key does not expire (or a long expiry), real
#    name + an email the team controls (not a personal one that might leave)

gpg --list-secret-keys --keyid-format=long   # note the key ID

# Publish the public key so Central Portal can verify signatures
gpg --keyserver keyserver.ubuntu.com --send-keys <KEY_ID>
gpg --keyserver keys.openpgp.org --send-keys <KEY_ID>

# Export the private key in the ASCII-armored, in-memory format Gradle's
# signing plugin expects (this is what goes into Jenkins, never into git)
gpg --export-secret-keys --armor <KEY_ID> > kmp-money-signing-key.asc
```

Keep `kmp-money-signing-key.asc` and the key's passphrase somewhere safe
(password manager) until they're loaded into Jenkins in step 3, then delete
the local file.

## 3. Jenkins credentials

The `kmpMoneyRelease` job (defined by `.jenkins/kmpMoneyRelease.jenkinsfile`,
registered on the `android-build` node) reads three credentials from
Jenkins -- create them under **Manage Jenkins → Credentials** as Secret text:

| Credential ID                           | Value                                           |
|------------------------------------------|--------------------------------------------------|
| `maven-central-portal-token`             | Base64 token from step 1.4                        |
| `kmp-money-gpg-signing-key`               | Contents of `kmp-money-signing-key.asc`           |
| `kmp-money-gpg-signing-key-password`      | The GPG key's passphrase                          |

These get bound to `ORG_GRADLE_PROJECT_signingInMemoryKey` /
`ORG_GRADLE_PROJECT_signingInMemoryKeyPassword` (which Gradle reads as the
project properties the `signing` block in `kmp-money/build.gradle.kts`
expects) and to `CENTRAL_TOKEN` for the `curl` upload -- no `settings.xml`
needed.

## 4. Running a release

1. Run the `kmpMoneyRelease` Jenkins job with the `RELEASE_VERSION`
   parameter set (e.g. `0.1.0`) and `AUTO_RELEASE` checked or unchecked:
   - **Checked** -- the pipeline uploads with `publishingType=AUTOMATIC`,
     so Central Portal publishes automatically once validation passes.
   - **Unchecked** -- uploads with `publishingType=USER_MANAGED`; the
     pipeline stops once the deployment reaches `VALIDATED`, and someone
     needs to go to https://central.sonatype.com/publishing and click
     **Publish** by hand.
2. The pipeline runs `:kmp-money:zipCentralBundle` (which stages and signs
   every target's publication), uploads the resulting zip, and polls the
   Central Portal status endpoint until it reaches a terminal state,
   failing the build if the deployment is rejected (`FAILED`).
3. It typically takes 15-30 minutes to become visible on Maven Central
   search after that.

## Consuming the published library

Once live, consumers add:

```kotlin
dependencies {
    implementation("com.amarulasolutions:kmp-money:0.1.0")
}
```

(No extra repository needed -- `mavenCentral()` is already in most
projects' `settings.gradle.kts`.)
