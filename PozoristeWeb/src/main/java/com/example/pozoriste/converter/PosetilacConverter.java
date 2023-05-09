package com.example.pozoriste.converter;

import org.springframework.core.convert.converter.Converter;

import com.example.pozoriste.repositories.PosetilacRepository;

import model.Posetilac;

public class PosetilacConverter implements Converter<String, Posetilac> {
	
	PosetilacRepository pr;
	
	public PosetilacConverter(PosetilacRepository pr) {
		this.pr = pr;
	}
	@Override
	public Posetilac convert(String source) {
		int posetilacid = -1;
		try {
			posetilacid = Integer.parseInt(source);
		} catch (NumberFormatException nfe) {
			return null;
		}
		Posetilac posetilac = pr.findById(posetilacid).get();
		return posetilac;
	}
	
}