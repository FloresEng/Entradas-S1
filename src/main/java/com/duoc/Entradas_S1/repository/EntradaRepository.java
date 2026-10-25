package com.duoc.Entradas_S1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.duoc.Entradas_S1.model.Entrada;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {
}
