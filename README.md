# BPMT

RiverSoft BPMT legacy Java web application, mirrored into Git from the original SVN codebase.

## Repository layout

- `parent`, `util`, `magic`, `dbtools`, `support`, `tools`, `package`: shared Maven modules and utilities
- `platform`: main web application and Docker image packaging
- `db/kyq.sql.gz`: compressed database dump used for local restore
- `docker-compose.yml`: local web + MariaDB runtime
- `DOCKER.md`: Docker build and startup notes

## Local build

This project currently targets JDK 8.

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
mvn -s settings.xml -pl platform -am -Pdocker-image verify
```

## Local runtime

```bash
BPMT_HTTP_PORT=18080 docker compose up -d
```

Notes:

- MariaDB runtime files are intentionally excluded from Git and live under `db/data/`.
- Uploaded files and Tomcat logs live under `runtime/`.
- The default database used by the Docker runtime is `kyq`.
- Restore the bundled dump with `gunzip -c db/kyq.sql.gz | mysql -u root -p kyq`.

## Source control note

This repository is the GitHub mirror for the former SVN path `https://svn.bpmt.app/svn/riversoft/trunk`.

For the GitHub import, machine-local runtime data and oversized bundled distribution assets are intentionally left out of version control, including:

- `db/data/`
- `runtime/`
- bundled JDK and Ant runtimes under `package/src/main/package/common/`
- prepacked library snapshots under `package/src/main/package/platform/WEB-INF/lib/` and `package/src/main/package/tools/internal/libs/`
- bundled third-party jars under `tools/src/main/tools/internal/libs/`
