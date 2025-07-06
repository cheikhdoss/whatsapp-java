package com.example.chatapp.demo.entities;


import lombok.Data;


import jakarta.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String contenu;

    private LocalDateTime dateHeure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediteur_id")
    private Membre expediteur;


}
