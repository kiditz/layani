FROM slerp/layani_python3
MAINTAINER Rifky aditya bastara "kiditzbastara@gmail.com"


EXPOSE 5003
#ENV TZ=Asia/Jakarta
#RUN date



#RUN apk add --no-cache --virtual .pynacl_deps build-base
#RUN python3 -m pip install bcrypt==3.1.4
WORKDIR /app
COPY /requirements.txt /app/requirements.txt
RUN pip3 install -r requirements.txt
ENV TZ=Asia/Jakarta
RUN apk add --no-cache tzdata
COPY ./app /app
COPY ./cash-model-0.0.1.tar.gz /app/cash-model-0.0.1.tar.gz
RUN pip3 install  --no-index --find-links file:///app/cash-model-0.0.1.tar.gz cash-model
#RUN pybabel compile -d translations
CMD [ "uwsgi","--socket", "0.0.0.0:5003", "--processes", "2","--threads", "10", "--plugins", "python3", "--socket-timeout=60", "--protocol=http", "-w", "main:app", "--wsgi-disable-file-wrapper"]
