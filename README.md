# Микросервис ***user-service*** для работы с пользователями.

## Инструкция по первоначальной установке микросервиса и сопутствующей инфраструктуры в кластер Kubernetes на основе Minikube

Открыть оболочку командной строки Bash с правами администратора и в ней выполнить следующие действия:
1.  Запустить minikube на Windows командой: 
```bash
minikube start --vm-driver=hyperv
```

2.  Выполнить команду:
```bash
eval $(minikube docker-env)
```

3. Включить входной контроллер NGINX в кластере командной: 
```bash
minikube addons enable ingress
```

4. Перейти в корневую папку проекта и собрать его командой:
```bash
./gradlew build
```

5. Собрать docker-образ микросервиса командой:
```bash
docker build -t backend-user-service-image .
```

6. Внутри корневой папки проекта перейти в директорию **./chart** и выполнить ряд команд:
   - Для развертывания деплоя на **TEST**-неймспейсе: 
        ```bash
        helm install backend-user-service-test ./backend-user-service -f ./backend-user-service/values-test.yml
        ```
   - Для развертывания деплоя на **DEV**-неймспейсе:
        ```bash
        helm install backend-user-service-dev ./backend-user-service -f ./backend-user-service/values-dev.yml
        ```
   - Для развертывания деплоя на **suslovdev**-неймспейсе:
        ```bash
        helm install backend-user-service-suslovdev ./backend-user-service -f ./backend-user-service/values-suslovdev.yml
        ```
   - Для развертывания деплоя на **suslovpreprod**-неймспейсе:
        ```bash
        helm install backend-user-service-suslovpreprod ./backend-user-service -f ./backend-user-service/values-suslovpreprod.yml
        ```
     
7. У себя в системе отредактировать файл hosts, указав имена хостов и соответствующие выделенные внешние ip-адреса ingress'ов каждого контура.
<br>Наименования хостов для каждой из сред можно посмотреть в соответствующих файлах: *values-test.yml* и *values-dev.yml*.