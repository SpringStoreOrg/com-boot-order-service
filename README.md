# Development

```bash
mvn package
docker build . -t fractalwoodstories/order-service:latest
docker push fractalwoodstories/order-service:latest
helm upgrade --install order-service ./helm/order-service
```