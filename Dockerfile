FROM gcc:latest
ADD ./chipmunk /udpxy
WORKDIR /udpxy
RUN make && make install
ENV PORT=80
EXPOSE $PORT
CMD udpxy -T -p $PORT
