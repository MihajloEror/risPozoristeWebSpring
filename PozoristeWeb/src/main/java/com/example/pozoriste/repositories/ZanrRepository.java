package com.example.pozoriste.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import model.Zanr;

public interface ZanrRepository extends JpaRepository<Zanr, Integer> {

	@Query("select z from Zanr z inner join z.zanrPredstaves zp where zp.predstava.idPredstava = :idP")
	List<Zanr> findByPredstava(@Param("idP")Integer idP);
}
