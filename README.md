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
* Локальный сервер Sonarqube;
* OpenSSL.

---
### Запуск локального сервера Sonarqube
Для запуска локального сервера Sonarqube внутри запущенного докера необходимо перейти в корневую директорию проекта и 
ввести команду:
```bash
docker-compose -f ./docker/docker-compose.yml up -d
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
mkdir -p user-service/postgresql-storage-default
mkdir -p user-service/postgresql-storage-feature
mkdir -p user-service/postgresql-storage-dev
mkdir -p user-service/postgresql-storage-preprod
mkdir -p user-service/postgresql-storage-prod
mkdir -p user-service/postgresql-storage-test
mkdir -p user-service/minio-storage-default
mkdir -p user-service/minio-storage-feature
mkdir -p user-service/minio-storage-dev
mkdir -p user-service/minio-storage-preprod
mkdir -p user-service/minio-storage-prod
mkdir -p user-service/minio-storage-test
```

4. На локальной хост-машине ввести следующие команды для создания пространства имен для каждой из сред:
```bash
kubectl create namespace feauture
kubectl create namespace dev
kubectl create namespace preprod
kubectl create namespace prod
kubectl create namespace test
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
### Создание сертификатов для работы с использованием протокола TLS/SSL
#### Общее
Создаем приватный ключ и корневой сертификат для собственного центра сертификации (CA):
```bash
openssl req -x509 -sha256 -days 365 -newkey rsa:2048 -keyout root_ca.key -out root_ca.crt
```

#### Формирование сертификата для микросервиса
1. Создаем приватный ключ для микросервиса:
```bash
openssl genrsa -out user_service.key 2048
```
2. Создаем запрос на подпись сертификата для приватного ключа микросервиса:
```bash
openssl req -new -key user_service.key -out user_service.csr
```
3. Сформируем файл **user_service.ext** с расширениями сертификата, которые будут добавлены при подписании. В расширениях укажем, что полученный сертификат не является CA-сертификатом и не может использоваться для подписания других сертификатов, а также добавим альтернативные имена.<br>
   Содержимое данного файла должно иметь следующий вид:
```txt
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
subjectAltName=@alt_names
[alt_names]
DNS.1=<DNS имя микросервиса>
IP.1=<IP-адрес микросервиса>
```
4. Выполняем подписание ранее созданного запроса на подпись с использованием сертификата и приватного ключа центра сертификации (CA):
```bash
openssl x509 -req -CA root_ca.crt -CAkey root_ca.key -in user_service.csr -out user_service.crt -days 365 -CAcreateserial -extfile user_service.ext
```
5. Для удобства последующей валидации сформируем цепочку из получившегося сертификата микросервиса и сертификата центра сертификации (CA):
```bash
cat user_service.crt root_ca.crt > user_service_cert.crt
```
6. Создаем хранилище ключей (keystore) для Java-приложения и помещаем в него под соответствующим алиасом финальный сертификат, а также приватный ключ микросервиса:
```bash
   openssl pkcs12 -export -in user_service_cert.crt -inkey user_service.key -name user-service -out keystore.p12
```
7. Получившийся файл **keystore.p12** подкладываем в директорию **~/user-service/certs/** хост-машины.

#### Формирование сертификата для PostgreSQL
1. Создаем приватный ключ для PostgreSQL:
```bash
openssl genrsa -out postgresql_service.key 2048
```
2. Создаем запрос на подпись сертификата для приватного ключа сервиса БД:
```bash
openssl req -new -key postgresql_service.key -out postgresql_service.csr
```
3. Сформируем файл **postgresql_service.ext** с расширениями сертификата, которые будут добавлены при подписании. В расширениях укажем, что полученный сертификат не является CA-сертификатом и не может использоваться для подписания других сертификатов, а также добавим альтернативные имена.<br>
   Содержимое данного файла должно иметь следующий вид:
```txt
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
subjectAltName=@alt_names
[alt_names]
DNS.1=<DNS имя сервиса БД>
IP.1=<IP-адрес сервиса БД>
```
4. Выполняем подписание ранее созданного запроса на подпись с использованием сертификата и приватного ключа центра сертификации (CA):
```bash
openssl x509 -req -CA root_ca.crt -CAkey root_ca.key -in postgresql_service.csr -out postgresql_service.crt -days 365 -CAcreateserial -extfile postgresql_service.ext
```
5. Для файла, содержащего приватный ключ, устанавилваем требуемые права доступа:
```bash
chmod 600 postgresql_service.key
```

#### Формирование сертификата для S3-хранилища
1. Создаем приватный ключ для Minio:
```bash
openssl genrsa -out minio_service.key 2048
```
2. Создаем запрос на подпись сертификата для приватного ключа сервиса хранилища:
```bash
openssl req -new -key minio_service.key -out minio_service.csr
```
3. Сформируем файл **minio_service.ext** с расширениями сертификата, которые будут добавлены при подписании. В расширениях укажем, что полученный сертификат не является CA-сертификатом и не может использоваться для подписания других сертификатов, а также добавим альтернативные имена.<br>
   Содержимое данного файла должно иметь следующий вид:
```txt
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
subjectAltName=@alt_names
[alt_names]
DNS.1=<DNS имя сервиса S3-хранилища>
IP.1=<IP-адрес сервиса S3-хранилища>
```
4. Выполняем подписание ранее созданного запроса на подпись с использованием сертификата и приватного ключа центра сертификации (CA):
```bash
openssl x509 -req -CA root_ca.crt -CAkey root_ca.key -in minio_service.csr -out minio_service.crt -days 365 -extfile minio_service.ext
```
5. Добавим корневой сертификат нашего центра сертификации в хранилище доверенных сертификатов JDK:
```bash
keytool -importcert -file <путь до файла корневого сертификата> -alias <наименование алиаса> -keystore <путь до файла cacerts> -storepass <пароль к хранилищу>
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
    - Для пространства по умолчанию (default)
      ```bash
      docker build -t euchekavelo/backend-user-service:latest-default .
      ```
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
    - Для стенда **prod**
      ```bash
      docker build -t euchekavelo/backend-user-service:latest-prod .
      ```  
    - Для стенда **test**
      ```bash
      docker build -t euchekavelo/backend-user-service:latest-test .
      ```

4. Внутри корневой папки проекта перейти в директорию **chart** и выполнить ряд команд:
   - Для развертывания chart-файла на **default**-неймспейсе:
      ```bash
      helm upgrade --install backend-user-service ./backend-user-service
      ```
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
   - Для развертывания chart-файла на **prod**-неймспейсе:
        ```bash
        helm upgrade --install backend-user-service-prod ./backend-user-service -f ./backend-user-service/values-prod.yml
        ```
   - Для развертывания chart-файла на **test**-неймспейсе:
        ```bash
        helm upgrade --install backend-user-service-test ./backend-user-service -f ./backend-user-service/values-test.yml
        ```
---


### Общее для обоих способов деплоя
После развертывания комплекса приложений по любому из способов необходимо у себя
в системе отредактировать файл **hosts**, указав имена хостов и соответствующие выделенные внешние
ip-адреса ingress'ов каждого контура.
<br>Наименования хостов для каждой из сред можно посмотреть в соответствующих yml-файлах с префиксом ***values***.
Данные файлы расположены в папке **chart/backend-user-service** корневой директории проета.

---


### Настройка подключения к S3-хранилищу для микросервиса
После успешного развертывания комплекса приложений необходимо зайти в MiniO через веб-интерфейс и создать корзину **users**,
сделай ее публичной, а также сгенерировать ключи для пользования API сервера хранилища.
<br>Значения соответствующих ключей необходимо установить в развернутом объекте ConfigMap. После чего потребуется
перезагрузить Pod микросервиса для применения внесенных изменений.

---