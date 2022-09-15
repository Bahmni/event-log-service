name: Build and Publish OpenELIS
on:
  push:
    branches:
      - master
      - 'release-*'
    tags:
      - '[0-9]+.[0-9]+.[0-9]+'
    paths-ignore:
      - '**.md'

  repository_dispatch:
    types: ['bahmni-embedded-tomcat-trigger', 'default-config-trigger']

jobs:
  build:
    name: Build event log service
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup Java 7
        uses: actions/setup-java@v2
        with:
          distribution: 'zulu'
          java-version: '7'
      - name: Package war
        run: mvn clean install -U -DskipTests
    #   - uses: actions/upload-artifact@v3
    #     with:
    #       name: openelis.war
    #       path: openelis/dist/openelis.war
    #   - uses: actions/upload-artifact@v3
    #     with:
    #       name: openelis.zip
    #       path: OpenElis.zip

#   docker-build-publish: 
#     name: Docker Build Publish
#     runs-on: ubuntu-latest
#     needs: [ build ]
#     steps:
#       - uses: actions/checkout@v2
#       - name: Set artifact version
#         run: |
#           wget -q https://raw.githubusercontent.com/Bahmni/bahmni-infra-utils/main/setArtifactVersion.sh && chmod +x setArtifactVersion.sh
#           ./setArtifactVersion.sh
#           rm setArtifactVersion.sh
#       - uses: actions/download-artifact@v3
#         with:
#           name: openelis.war
#           path: openelis/dist/
#       - uses: actions/download-artifact@v3
#         with:
#           name: openelis.zip
#           path: .
#       - name: Download default_config.zip
#         run: sh .github/download_artifact.sh default-config default_config ${{secrets.BAHMNI_PAT}}
#       - name: Download bahmni-embedded-tomcat.zip
#         run: sh .github/download_artifact.sh bahmni-package bahmni-embedded-tomcat ${{secrets.BAHMNI_PAT}}
#       - name: Login to DockerHub
#         uses: docker/login-action@v1 
#         with:
#           username: ${{ secrets.DOCKER_HUB_USERNAME }}
#           password: ${{ secrets.DOCKER_HUB_TOKEN }}
#       - name: Docker Build and push
#         uses: docker/build-push-action@v2
#         with:
#           context: .
#           file: package/docker/openelis/Dockerfile
#           push: true
#           tags: bahmni/openelis:${{env.ARTIFACT_VERSION}},bahmni/openelis:latest

#   rpm-build-publish:
#     name: RPM Build and Publish
#     runs-on: ubuntu-latest
#     needs: [ build ]
#     steps:
#       - uses: actions/checkout@v2
#       - name: Set artifact version
#         run: |
#           wget -q https://raw.githubusercontent.com/Bahmni/bahmni-infra-utils/main/setArtifactVersion.sh && chmod +x setArtifactVersion.sh
#           ./setArtifactVersion.sh
#           rm setArtifactVersion.sh
#       - uses: actions/download-artifact@v3
#         with:
#           name: openelis.war
#           path: openelis/dist/
#       - uses: actions/download-artifact@v3
#         with:
#           name: openelis.zip
#           path: .
#       - name: Download bahmni-embedded-tomcat.zip
#         run: sh .github/download_artifact.sh bahmni-package bahmni-embedded-tomcat ${{secrets.BAHMNI_PAT}}
#       - name: Download openelis_backup.sql.gz file from emr-functional-tests/dbdump
#         run:  |
#               wget https://raw.githubusercontent.com/Bahmni/emr-functional-tests/master/dbdump/openelis_backup.sql.gz 
#               gunzip -c openelis_backup.sql.gz > package/resources/openelis_backup.sql
#       - name: Copying Artifacts to package/resources
#         run: |
#              cp openelis/dist/openelis.war package/resources/
#              cp OpenElis.zip package/resources/
#       - name: Generate bahmni_openelis_revision.json
#         run: ./package/rpm/test/bahmni_openelis_revision.sh > bahmni_openelis_revision.json
#       - name: Setup Java 8 for Gradle
#         uses: actions/setup-java@v3
#         with:
#           distribution: 'zulu'
#           java-version: '8'      
#       - name: Gradle Build
#         run: |
#             cd package/rpm
#             export version=$(awk -F- '{print $1}' <<< ${{env.ARTIFACT_VERSION}})
#             export release=$(awk -F- '{print $2}' <<< ${{env.ARTIFACT_VERSION}})
#             ./gradlew -Pversion=$version -Prelease=$release clean buildRpm
#       - uses: actions/upload-artifact@v3
#         with:
#           name: bahmni-lab-rpm
#           path: package/rpm/build/distributions/*.rpm
#       - uses: actions/upload-artifact@v3
#         with:
#           name: openelis-revision
#           path: bahmni_openelis_revision.json