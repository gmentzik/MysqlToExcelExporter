Description:
=============
Connects to a Mysql DB and exports all tables to Excel file. One sheet per table is created.

Example of file created: my_database_export_2025-11-27T23-13-15.xlsx

Program parameters are at application.properties file but can also be overridden during execution with env variables to support dockerization.

eg. java -Ddb.host=localhost -jar MysqlToExcelExporter.jar

eg. docker run --rm --network=host -e DB_HOST=localhost-v $(pwd)/output:/app/output dbexcelexporter:latest 

How to deploy to docker image:
==============================
Copy Project files to Budibase to host.
Change dir to root folder of project.

Build docker image:
====================
docker build -t dbexcelexporter:latest .

How to run:
=============
Use following command to execute export with backup user. Mount the path where generated export files should be created.

docker run --rm --network=host \
-e DB_HOST=localhost \
-e DB_PORT=3306 \
-e DB_DATABASE=my_database \
-e DB_USERNAME=backupuser \
-e DB_PASSWORD=password \
-e DB_USESSL=false \
-v $(pwd)/output:/app/output \
dbexcelexporter:latest