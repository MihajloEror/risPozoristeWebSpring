@RequestMapping(value="/generisiIzvestaj", method=RequestMethod.GET)
	public void generisiIzvestaj(HttpServletResponse response, Integer idIzvodjenja) throws Exception {
		Izvodjenje i = ir.findById(idIzvodjenja).get();
		List<GlumiUIzvodjenju> glumiU = gluir.findByIzvodjenje(i);
		
		
		response.setContentType("text/html");
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(g);
        InputStream inputStream = this.getClass().getResourceAsStream("/jasperreports/Izvestaj.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        Map<String, Object> params = new HashMap<String, Object>();
        
        
        params.put("naziv", i.getPredstava().getNaziv());
        params.put("trajanje", i.getPredstava().getTrajanje());

        
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
        inputStream.close();
        response.setContentType("application/x-download");
        response.addHeader("Content-disposition", "attachment; filename=Izvestaj.pdf");
        OutputStream os = response.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, os);
	}
