# Serba

HTTP File server written in Micronaut

### minimal compose

```yaml
services:
  backend:
    image: ghcr.io/d4rckh/serba:latest
    container_name: serba-backend
    volumes:
      - /my_data_folder:/data
    ports:
      - "8080:8080"
    environment:
      JDBC_URL: jdbc:postgresql://serba-postgres:5432/serba
      DB_USERNAME: serba
      DB_PASSWORD: serba
    depends_on:
      - serba-postgres

  serba-postgres:
    image: postgres:15
    container_name: serba-postgres
    environment:
      POSTGRES_DB: serba
      POSTGRES_USER: serba
      POSTGRES_PASSWORD: serba
    restart: unless-stopped
```

After accessing the login page, an admin user will be created and it's password will be printed in the container logs, as soon as you login go to /admin and change your admin user's password. There, you will also be able to create a library.