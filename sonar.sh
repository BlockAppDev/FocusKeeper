# 1. Install the m2eclipse plugin for eclipse (Help -> Install new software).
# 2. Install maven on your machine (https://maven.apache.org/), make the mvn command available on your path.
# 3. Run sonarcloud with the following command:
mvn sonar:sonar \
  -Dsonar.projectKey=BlockAppDev_BlockApp \
  -Dsonar.organization=blockappdev \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=6d6c802797c22ac51ebce197f4d03e3e82a1849e
