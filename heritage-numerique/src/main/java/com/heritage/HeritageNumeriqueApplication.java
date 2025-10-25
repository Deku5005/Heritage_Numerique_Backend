package com.heritage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.heritage.securite.JwtProperties;

/**
 * Classe principale de l'application Spring Boot.
 * 
 * @EnableScheduling : Active le scheduling pour l'expiration automatique des invitations
 * @EnableConfigurationProperties : Active les propriétés de configuration personnalisées (JwtProperties)
 */
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(JwtProperties.class)
public class HeritageNumeriqueApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeritageNumeriqueApplication.class, args);
        
        System.out.println("\n" +
            "========================================\n" +
            "  HERITAGE NUMERIQUE API STARTED\n" +
            "  http://localhost:8080\n" +
            "========================================\n"
        );
    }
}

