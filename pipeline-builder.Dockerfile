FROM ubuntu:latest

RUN sudo apt-get update && \
    sudo apt-get -y install skopeo python3 python3-pip npm yarn && \
    sudo pip install awscli