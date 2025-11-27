package com.github.gmentzik.mysqlexcelexport.services;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import com.github.gmentzik.mysqlexcelexport.config.Config;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLToExcelExporter {

    private static final Logger logger = LoggerFactory.getLogger(MySQLToExcelExporter.class);

    private final Config config;

    public MySQLToExcelExporter(Config config) {
        this.config = config;
    }

    public void export () {

        String url = config.getDbUrl();
        String user = config.getDbUsername();
        String password = config.getDbPassword();
        String outputFolder = config.getOutputFolder();


        // ISO 8601 format (e.g., 2025-11-25T14-30-45)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss");
        String isoDatetime = LocalDateTime.now().format(formatter);


        logger.info("[{}] Starting export of db: {}", isoDatetime, config.getDbDatabase());

        // Create directory if it doesn't exist
        File dir = new File(outputFolder);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                logger.info("Created output directory: {}", outputFolder);

            } else {
                logger.error("Failed to create output directory: {}", outputFolder);
            }
        }

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            logger.info("Connected to MySQL");

            Statement stmt = conn.createStatement();
            ResultSet tables = stmt.executeQuery("SHOW TABLES");

            Workbook workbook = new XSSFWorkbook();

            // Keep track of used sheet names
            Set<String> usedSheetNames = new HashSet<>();


            while (tables.next()) {

                String tableName = tables.getString(1);
                logger.info("Exporting table: {}", tableName);

                // Build query dynamically for table name
                String query = "SELECT * FROM " + tableName;

                try (PreparedStatement ps = conn.prepareStatement(query);
                     ResultSet rs = ps.executeQuery()) {

                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // Shorten table name down to 31 chars that excel supports
                    String baseName = tableName.length() > 31 ? tableName.substring(0, 31) : tableName;
                    String sheetName = baseName;
                    int counter = 1;

                    // Ensure uniqueness
                    while (usedSheetNames.contains(sheetName)) {
                        sheetName = (baseName.length() > 28 ? baseName.substring(0, 28) : baseName) + "_" + counter++;
                    }
                    usedSheetNames.add(sheetName);


                    Sheet sheet = workbook.createSheet(sheetName);
                    Row headerRow = sheet.createRow(0);

                    for (int i = 1; i <= columnCount; i++) {
                        headerRow.createCell(i - 1).setCellValue(metaData.getColumnName(i));
                    }

                    int rowIndex = 1;
                    while (rs.next()) {
                        Row row = sheet.createRow(rowIndex++);
                        for (int i = 1; i <= columnCount; i++) {
                            row.createCell(i - 1).setCellValue(rs.getString(i));
                        }
                    }
                }
            }

            String outputFile = outputFolder + "/" + config.getDbDatabase() + "_database_export_" + isoDatetime + ".xlsx";
            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                workbook.write(fileOut);
            }
            workbook.close();
            logger.info("Export completed: {}", outputFile);

        } catch (Exception e) {
            logger.error("An error occurred while executing doSomething()", e);
        }
    }
}

