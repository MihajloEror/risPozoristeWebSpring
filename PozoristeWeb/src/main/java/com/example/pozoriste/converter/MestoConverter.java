package com.example.pozoriste.converter;

import org.springframework.core.convert.converter.Converter;

import com.example.pozoriste.repositories.MestoRepository;

import model.Mesto;

public class MestoConverter implements Converter<String, Mesto> {

	MestoRepository mr;

	public MestoConverter(MestoRepository mr) {
		this.mr = mr;
	}

	@Override
	public Mesto convert(String source) {
		int mestoid = -1;
		try {
			mestoid = Integer.parseInt(source);
		} catch (NumberFormatException nfe) {
			return null;
		}
		Mesto mesto = mr.findById(mestoid).get();
		return mesto;
	}

}
