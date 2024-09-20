package br.ufg.ceia.gameinsight.gameservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.UUID;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String platform;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String publisher;

    @Column(name = "system_requirements", length = 2000) // SQL não aceita CamelCase
    private String systemRequirements;
}
