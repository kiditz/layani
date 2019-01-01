docker run -d \
  -p 2181:2181 \
  -p 9092:9092 \
  --name=kafka \
  --env ADVERTISED_HOST=0.0.0.0 \
  --env ADVERTISED_PORT=9092 \
  spotify/kafka
