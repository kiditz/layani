# Layani
Point Of Sales Project

1. Project [cash-api](https://github.com/kiditz/layani/cash-api)
  Adalah project rest api yang menggunakan flask sebagai library andalannya.
2. Project [cash-gateway-api](https://github.com/kiditz/layani/cash-gateway-api)
  Adalah proyek yang bertugas untuk menghubungkan perangkat android ke berbagai server yang di butuhkan untuk memenuhi kebutuhan
3. Project [cash-oauth-api](https://github.com/kiditz/layani/cash-oauth-api)
  Adalah proyek yang keamanan untuk melindungi data dari pihak yang tidak berkepentingan untuk datang dan menyerang..
  Project ini adalah proyek yang mengamankan api-gateway dengan memanfaatkan spesifikasi keamanan Oauth2
4. Project [cash-oauth-service](https://github.com/kiditz/layani/cash-oauth-service)
  Adalah proyek penghubung antara proyek [cash-oauth-api](https://github.com/kiditz/layani/cash-oauth-api) dengan database.
5. Project [cash-model](https://github.com/kiditz/layani/cash-model)
  Adalah proyek penghubung antara proyek cash-api dengan database
  
# Cara menjalankan aplikasi

1. install docker
  Untuk tahap instalasi docker bisa dilihat di [How to install docker](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-16-04)
2. install docker-compose
   Untuk tahap instalasi docker bisa dilihat di [How to install docker-compose](https://www.digitalocean.com/community/tutorials/how-to-install-docker-compose-on-ubuntu-16-04)
3. Buat database Postgres
   ```
   CREATE DATABASE cash_overflow;
   ```
4. Proyek ini menggunakan service discovery consul, maka sebelum memulai kita perlu menjalankan consul
   ```
   docker pull consul
   docker run -d --net=host  --hostname consul-server --name consul-server --env "SERVICE_IGNORE=true" --volume consul_data:/consul/data --publish 8500:8500 consul:latest consul agent -server -ui -bootstrap -client=0.0.0.0 -advertise=${YOUR_PUBLIC_IP} -data-dir="/consul"   
   ```
   a. Script diatas akan menjadikan consul-server sebagai container
   b. sekarang coba cek container dengan menggunakan 
   ```
   docker ps
   ------------------------------------------------------------------------------------------------------------------
   CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
   85cf0c92307b        consul:latest       "docker-entrypoint.s…"   11 months ago       Up 2 seconds                                 consul-server
   ```
   c. untuk mematikan consul, anda dapat menggunakan script
   ```
   docker stop consul-server
   ```
   d. untuk menjalankan kembali.
   ```
   docker start consul-server
   ```
   e. Masuk ke web admin consul
   ```
   Buka chrome arahkan ke alamat
   http://localhost:8500
   ```
5. Buat virtual environtment untuk proyek cash-model
   ```
   virtualenv -p python3 cash-model
   cd cash-model
   pip3 install -r requirements.txt
   pip3 install psycopg2
   ```
6. Kemudian Jalankan script migration
   ```
   python3 entity/models.py db init
   python3 entity/models.py db migrate
   python3 entity/models.py db upgrade
   ```
7. Setelah database terbentuk, jalankan proyek cash-oauth-api
  ```
  cd cash-oauth-service
  mvn clean install
  cd cash-oauth-api
  mvn clean package
  ```   

