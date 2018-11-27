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
4. Masuk ke folder cash-model
   ```
   cd cash-model    
   ```
5. Buat virtual environtment untuk proyek cash-model
6. Kemudian Jalankan script migration
   ```
   python3 entity/models.py db init
   python3 entity/models.py db migrate
   python3 entity/models.py db upgrade
   ```

