package com.example.pozoriste.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import model.GlumiUIzvodjenju;
import model.Izvodjenje;

public interface GlumiUIzvodjenjuRepository extends JpaRepository<GlumiUIzvodjenju, Integer>{
	
	List<GlumiUIzvodjenju> findByIzvodjenje(Izvodjenje i);

}
