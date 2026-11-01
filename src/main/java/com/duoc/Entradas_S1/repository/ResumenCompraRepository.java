package com.duoc.Entradas_S1.repository;

import com.duoc.Entradas_S1.model.ResumenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumenCompraRepository extends JpaRepository<ResumenCompra, Long> {
}
