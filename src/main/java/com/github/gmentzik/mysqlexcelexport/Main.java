package com.github.gmentzik.mysqlexcelexport;

import com.github.gmentzik.mysqlexcelexport.config.Config;
import com.github.gmentzik.mysqlexcelexport.services.MySQLToExcelExporter;

public class Main {

    public static void main(String[] args) {
        // Initialize configuration
        Config config = new Config();
        // Initialize exporter
        MySQLToExcelExporter mySQLToExcelExporter = new MySQLToExcelExporter(config);
        // Trigger excel file export
        mySQLToExcelExporter.export();
    }
}
