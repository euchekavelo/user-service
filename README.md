# Микросервис ***user-service*** для работы с пользователями.

## Инструкция по развертыванию микросервиса в кластере Kubernetes

### Требования к программному обеспечению
Для корректной работы и тестирования приложения внутри кластера Kubernetes необходимо наличие следующего установленного 
и настроенного ПО 
для ОС Windows:
* Minikube;
* Docker;
* Gradle;
* Helm CLI;
* VirtualBox;
* GitLab Runner (на базе исполняемой оболочки PowerShell хост-машины);
* Локальный сервер Sonarqube.

---
### Запуск локального сервера Sonarqube
Для запуска локального сервера Sonarqube внутри запущенного докера необходимо перейти в корневую директорию проекта и 
ввести команду:
```bash
docker-compose -f sonarqube-server.yml up -d
```

---


### Инструкция по подготовке кластера Kubernetes к работе
Открыть оболочку командной строки с правами администратора и в ней выполнить следующие действия:
1.  Запустить minikube на Windows командой с использование гипервизора VirtualBox: 
```bash
minikube start --vm-driver=virtualbox --no-vtx-check
```

2. Включить входной контроллер NGINX в кластере Kubernetes командой: 
```bash
minikube addons enable ingress
```

3. На подготовленной виртуальной машине Minikube'а создать каталоги для баз данных, используя учетные данные 
суперпользователя **root**, а также следующие команды:
```bash
cd ../
mkdir postgresql-storage-feature
mkdir postgresql-storage-dev
mkdir postgresql-storage-preprod
mkdir postgresql-storage-prod
```

4. На локальной хост-машине ввести следующие команды для создания пространства имен для каждой из сред:
```bash
kubectl create namespace feauture
kubectl create namespace dev
kubectl create namespace preprod
kubectl create namespace prod
```

5. На локальной хост-машине для каждой среды создать **secret**, хранящий настройки подключения к приватному хранилищу 
образов Docker:
```bash
kubectl create secret docker-registry private-docker-registry `
--docker-server=<доменный адрес сервера приватного репозитория> `
--docker-username=<имя пользователя> `
--docker-password=<пароль> `
--docker-email=<адрес почты> `
--namespace=<наименование пространства среды>
```

---


### Деплой приложения в автоматизированном режиме 
**ВАЖНО!:** Данный способ работает лишь при работе с версией репозитория проекта, размещенного на сервисе GitLab.<br> 

Для автоматизированного развертывания собранного приложения достаточно произвести фиксацию изменений в виде коммитов в одной
из веток: **feature**, **dev** или **preprod**.<br>
При выявлении события фиксации будет производиться тестирование, сборка и поставка обновленной версии микросервиса  с 
учетом последних изменений на соответствующий ветке подготовленный стенд.

---

### Деплой приложения в ручном режиме
Если вы хотите выполнить развертывания приложения в ручном режиме в подготовленный кластер Kubernetes, тогда необходимо 
руководствоваться действиями, описанными в указанном подразделе.
1. Указать используемому терминалу об использовании внутреннего демона Docker в кластере с помощью команд:
   - Для оболочки **bash**:
      ```bash
      eval $(minikube docker-env)
      ```
   - Для оболочки **PowerShell**:
      ```bash
      minikube docker-env | Invoke-Expression
      ```
     
2. Перейти в корневую директорию проекта и собрать его при помощи команды:
    ```bash
      ./gradlew build
    ```   

3. Собрать docker-образ микросервиса для интересующего стенда:
    - Для стенда **feature**
      ```bash
      docker build -t euchekavelo/backend-user-service:latest-feature .
      ```
    - Для стенда **dev**
      ```bash
      docker build -t euchekavelo/backend-user-service:latest-dev .
      ```
    - Для стенда **preprod**
      ```bash
      docker build -t euchekavelo/backend-user-service:latest-preprod .
      ```    

4. Внутри корневой папки проекта перейти в директорию **chart** и выполнить ряд команд:
   - Для развертывания chart-файла на **feature**-неймспейсе: 
        ```bash
        helm upgrade --install backend-user-service-feature ./backend-user-service -f ./backend-user-service/values-feature.yml
        ```
   - Для развертывания chart-файла на **dev**-неймспейсе:
        ```bash
        helm upgrade --install backend-user-service-dev ./backend-user-service -f ./backend-user-service/values-dev.yml
        ```
   - Для развертывания chart-файла на **preprod**-неймспейсе:
        ```bash
        helm upgrade --install backend-user-service-preprod ./backend-user-service -f ./backend-user-service/values-preprod.yml
        ```

---


### Общее для обоих способов деплоя    
После развертывания приложения по любому из способов необходимо у себя в системе отредактировать файл hosts, указав имена хостов и соответствующие выделенные внешние 
ip-адреса ingress'ов каждого контура.
<br>Наименования хостов для каждой из сред можно посмотреть в соответствующих файлах: *values-feature.yml*, *values-dev.yml* 
и *values-preprod.yml*.

---
