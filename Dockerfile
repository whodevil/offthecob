FROM node
RUN mkdir -p /code
ADD . /code
WORKDIR /code/ui
RUN yarn install && yarn build

FROM openjdk:11-jdk
RUN mkdir -p /code
ARG SOURCE_VERSION
ENV SOURCE_VERSION=$SOURCE_VERSION
ARG USERNAME
ENV USERNAME=$USERNAME
ARG GITHUB_TOKEN=$GITHUB_TOKEN
ADD . /code
WORKDIR /code
RUN ./gradlew stage

FROM openjdk:11-jre
RUN mkdir -p /code/ui
WORKDIR /code/ui
COPY --from=0 /code/ui/build .
WORKDIR /code
COPY --from=1 /code/build/install/theagainagain .
COPY --from=1 /code/build/version .
CMD /code/bin/theagainagain