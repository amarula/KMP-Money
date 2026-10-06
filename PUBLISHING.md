# Publishing to Maven Central

`kmp-money` publishes under the `com.github.amarula` namespace via Sonatype's
Central Portal, using the
[`com.vanniktech.maven.publish`](https://github.com/vanniktech/gradle-maven-publish-plugin)
Gradle plugin (configured in `kmp-money/build.gradle.kts`). This plugin talks
to the Central Portal API directly, so none of this uses the old
`~/.m2/settings.xml` / Nexus staging flow -- Gradle properties (or environment
variables) carry the credentials instead.

Everything below is a **one-time setup step** done by a human with access to
the relevant accounts (Sonatype, GPG keyserver, Jenkins). None of it can be
automated by an agent, since each step hands over a real secret or claims a
real identity.

## 1. Sonatype Central Portal account + namespace

1. Register at https://central.sonatype.com.
2. Claim the `com.github.amarula` namespace: Central Portal verifies this by
   asking you to create a short-lived public GitHub repo (or gist) under the
   `amarula` org containing a verification code it gives you. Needs an
   account with push access to the `amarula` GitHub org.
3. Once verified, go to your account's **View Account** page and generate a
   **User Token** (a username/password pair, *not* your login password).

## 2. GPG signing key

Maven Central requires every published artifact to be GPG-signed.

```bash
# RSA 4096 is comfortably above Sonatype's 2048-bit minimum
gpg --full-generate-key
# -> RSA and RSA, 4096 bits, key does not expire (or a long expiry), real
#    name + an email the team controls (not a personal one that might leave)

gpg --list-secret-keys --keyid-format=long   # note the key ID

# Publish the public key so Central Portal can verify signatures
gpg --keyserver keyserver.ubuntu.com --send-keys <KEY_ID>
gpg --keyserver keys.openpgp.org --send-keys <KEY_ID>

# Export the private key in the ASCII-armored, in-memory format the
# vanniktech plugin expects (this is what goes into Jenkins, never into git)
gpg --export-secret-keys --armor <KEY_ID> > kmp-money-signing-key.asc
```

Keep `kmp-money-signing-key.asc` and the key's passphrase somewhere safe
(password manager) until they're loaded into Jenkins in step 3, then delete
the local file.

## 3. Jenkins credentials

Create a **new Pipeline job** pointed at this repo (using the pipeline script from your central CI repository) on an agent with the **`macos`** label -- the
`iosArm64`/`iosSimulatorArm64` targets only compile on macOS, so this can't
run on a generic Linux agent.

In Jenkins → **Manage Jenkins → Credentials**, add four credentials (scope:
wherever this job can read them from, e.g. the folder or global store):

| Credential ID                     | Type              | Value                                   |
|------------------------------------|-------------------|------------------------------------------|
| `sonatype-central-username`        | Secret text       | User token username from step 1          |
| `sonatype-central-password`        | Secret text       | User token password from step 1          |
| `kmp-money-gpg-private-key`        | Secret text       | Contents of `kmp-money-signing-key.asc`   |
| `kmp-money-gpg-passphrase`         | Secret text       | The GPG key's passphrase                  |

The central Jenkins pipeline binds these to `ORG_GRADLE_PROJECT_*` environment
variables, which Gradle automatically maps to the matching project
properties (`mavenCentralUsername`, `mavenCentralPassword`,
`signingInMemoryKey`, `signingInMemoryKeyPassword`) that the publish plugin
reads -- no `settings.xml` needed.

## 4. Running a release

1. Bump `version` in `kmp-money/build.gradle.kts`.
2. Merge that change through the normal Gerrit review flow.
3. Run the Jenkins job with the `PUBLISH` parameter checked.
4. The pipeline runs `ktlintCheck`, `detekt`, and the test suite first, then
   stages the signed artifacts on Central Portal
   (`publishToMavenCentral`, `automaticRelease = false`).
5. Go to https://central.sonatype.com/publishing, find the staged
   deployment, verify the contents, and click **Publish**. (We're starting
   with the manual review step on purpose, since this can't be undone once
   it's live. Once the process has been trusted for a few releases, flip
   `automaticRelease` to `true` in `kmp-money/build.gradle.kts` to skip this
   click.)
6. It typically takes 15-30 minutes to become visible on Maven Central
   search after that.

## Consuming the published library

Once live, consumers add:

```kotlin
dependencies {
    implementation("com.github.amarula:kmp-money:0.1.0")
}
```

(No extra repository needed -- `mavenCentral()` is already in most
projects' `settings.gradle.kts`.)
