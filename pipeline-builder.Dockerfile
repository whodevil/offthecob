FROM ubuntu:latest

RUN apt-get update && \
    apt-get -y install skopeo python3 python3-pip npm yarn && \
    pip install awscli
