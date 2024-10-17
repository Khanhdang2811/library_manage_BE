package com.khanhdang.library_manage.config.db;

import com.khanhdang.library_manage.config.AES256;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration // Đây là annotation để đánh dấu lớp này là một lớp cấu hình trong Spring.
public class DatabaseConfig {

    // Các giá trị này được lấy từ file application.properties.
    @Value("${db.datasource.driver-class-name}")
    String driverClassName;

    // Các giá trị URL, username và password được mã hóa.
    @Value("${db.datasource.url}")
    String urlDb = "UC/5LfaCpbwuhTfElpG2sd7DUNfkSwp2vbBz9lyAZvA60XW8gtK4+H7ioVK1l7TvKFqt3lL1eHNmIZqqntZm9Q==";

    @Value("${db.datasource.username}")
    String username = "64irSBtnVCE0LTBLO8J9+g3mkCfoSNgd4cyJhSrb5zc=";

    @Value("${db.datasource.password}")
    String password = "znNiB6RJomQsp49K4pxpLR/nHhahN3SnttxMfp8NdKg=";

    // Đây là secret key và salt key để sử dụng cho việc giải mã (AES256).
    private final String secretKey = "khanhdang@2811";
    private final String saltKey = "11223344";

    // Bean này sẽ trả về một đối tượng DataSource, chứa các thông tin đã giải mã
    @Bean(name = "dataSource")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        // Giải mã các thông tin URL, username và password.
        String decryptedUrl = AES256.decrypt(urlDb, secretKey, saltKey);
        String decryptedUsername = AES256.decrypt(username, secretKey, saltKey);
        String decryptedPassword = AES256.decrypt(password, secretKey, saltKey);


        dataSource.setUrl(decryptedUrl);
        dataSource.setUsername(decryptedUsername);
        dataSource.setPassword(decryptedPassword);
        dataSource.setDriverClassName("org.postgresql.Driver"); // Driver PostgreSQL

        return dataSource; // Trả về DataSource đã cấu hình
    }
}
