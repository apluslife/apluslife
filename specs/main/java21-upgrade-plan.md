# Java 21 Upgrade Plan (manual fallback)

This project couldn't use the Copilot Java upgrade tools (account plan restriction). This document lists a manual plan to upgrade the project to Java 21 and verify the build.

Goals

- Upgrade the project to run on Java 21 (latest LTS as of Oct 2025).
- Make minimal, safe changes: use Gradle toolchain and bump source/target compatibility.
- Verify build and tests under Java 21.

Changes already made

- `build.gradle`: switched to Gradle Java toolchain and set `sourceCompatibility`/`targetCompatibility` to `21`.

Checklist

1. Install or make Java 21 available locally

   - Install a JDK 21 (Adoptium / Eclipse Temurin recommended). On Windows, download and install a JDK and add its `bin` to PATH or set `org.gradle.java.home` in `gradle.properties`.

2. Update dependencies that embed JRE classifiers

   - Replace `com.microsoft.sqlserver:mssql-jdbc:12.4.2.jre11` with a JVM-neutral coordinate or a compatible version (for example `com.microsoft.sqlserver:mssql-jdbc:12.4.2` or a `jre17`/`jre21` artifact if provided). Verify the driver's compatibility with Java 21.

3. Build using Gradle and the toolchain

   - From project root run:
     ```powershell
     .\gradlew --version
     .\gradlew -Dorg.gradle.jvmargs=-Xmx1g clean build --no-daemon
     ```
   - If Gradle picks a compatible JDK through the toolchain, the build should run even if system Java is different.

4. If the toolchain cannot find Java 21

   - Add the following to `gradle.properties` (replace the path):
     ```properties
     org.gradle.java.home=C:/Program Files/Eclipse Adoptium/jdk-21.0.0+xx
     ```

5. Run tests

   - `.\gradlew test` and inspect failures. Address API or library compatibility issues.

6. Common compatibility checks

   - Check usage of deprecated/removed APIs (reflection, security manager removal, removed modules).
   - Update any third-party libs that require newer Java (e.g., JDBC driver, native integrations).

7. Finalize
   - Update README, CI configuration to use JDK 21.
   - Bump project metadata if desired.

Notes and rationale

- Gradle 8.5 supports Java toolchains and Java 21. Using the toolchain avoids forcing developers to install a specific JDK globally.
- The MS SQL Server driver previously uses a `jre11` classifier. Using the plain artifact or a newer driver avoids tying to a specific JRE classifier.

If you want, I can:

- Update the `mssql-jdbc` dependency to a neutral or newer coordinate and run a build.
- Try to detect installed JDKs on your machine and (optionally) set `org.gradle.java.home` in `gradle.properties`.

Verified files

- `build.gradle` (toolchain + compatibility set)

End of plan.
