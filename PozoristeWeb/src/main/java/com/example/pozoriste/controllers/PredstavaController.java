package com.example.pozoriste.controllers;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.pozoriste.repositories.GlumiUIzvodjenjuRepository;
import com.example.pozoriste.repositories.IzvodjenjeRepository;
import com.example.pozoriste.repositories.KartaRepository;
import com.example.pozoriste.repositories.MestoRepository;
import com.example.pozoriste.repositories.PosetilacRepository;
import com.example.pozoriste.repositories.PredstavaRepository;
import com.example.pozoriste.repositories.ReziserRepository;
import com.example.pozoriste.repositories.ScenaRepository;
import com.example.pozoriste.repositories.ZanrPredstaveRepository;
import com.example.pozoriste.repositories.ZanrRepository;

import model.GlumiUIzvodjenju;
import model.Izvodjenje;
import model.Karta;
import model.Mesto;
import model.Predstava;
import model.Reziser;
import model.Scena;
import model.Zanr;
import model.ZanrPredstave;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@RequestMapping(value="/predstave")
public class PredstavaController {
	
	@Autowired
	ZanrRepository zr;
	
	@Autowired
	ReziserRepository rr;
	
	@Autowired
	PredstavaRepository pr;
	
	@Autowired
	ZanrPredstaveRepository zpr;
	
	@Autowired
	IzvodjenjeRepository ir;
	
	@Autowired
	KartaRepository kr;
	
	@Autowired
	MestoRepository mr;
	
	@Autowired
	PosetilacRepository ppr;
	
	@Autowired
	ScenaRepository sr;
	
	@Autowired
	GlumiUIzvodjenjuRepository gluir;
	
	@RequestMapping(value="/getPodaci", method=RequestMethod.GET)
	public String getPodaciZaCB(HttpServletRequest request) {
		List<Zanr> zanrovi = zr.findAll();
		List<Reziser> reziseri = rr.findAll();
		
		request.getSession().setAttribute("reziseri", reziseri);
		request.getSession().setAttribute("zanrovi", zanrovi);
		
		return "unos/UnosPredstave";
	}
	
	@RequestMapping(value="/savePredstava", method=RequestMethod.POST)
	public String savePredstava(String naziv, Integer trajanje, String opis, Integer[] zanrovi, Integer reziser, Model m) {
		
		Reziser r = (rr.findById(reziser)).get();
		
		Predstava p = new Predstava();
		p.setNaziv(naziv);
		p.setTrajanje(trajanje);
		p.setOpis(opis);
		p.setReziser(r);
		
		Predstava predstava = pr.save(p);
		
		for(Integer id: zanrovi) {
			ZanrPredstave zp = new ZanrPredstave();
			Zanr z = zr.findById(id).get();
			zp.setZanr(z);
			zp.setPredstava(predstava);
			zpr.save(zp);
		}
		m.addAttribute("poruka","Predstava je uspesno sacuvana. Id predstave je "+predstava.getIdPredstava());
		
		return "unos/UnosPredstave";
	}
	
	@RequestMapping(value="pretraziPredstave", method=RequestMethod.GET)
	public String pretraziPredstave(String naziv, Model m) {
		Predstava p = pr.findByNaziv(naziv);
		List<Izvodjenje> izvodjenja = ir.findByPredstava(p);
		m.addAttribute("izvodjenja", izvodjenja);
		m.addAttribute("predstava", naziv);
		return "/PretragaPredstava";
	}
	
	
	@RequestMapping(value="Rezervisanje", method=RequestMethod.GET)
	public String dovuciMesta(Integer idIzvodjenja, Model m, HttpServletRequest request) {
		List<Mesto> slobodnaMesta = mr.slobodnaMesta(idIzvodjenja);
		System.out.println("Size of mesta: "+slobodnaMesta.size());
		request.getSession().setAttribute("mesta", slobodnaMesta);
		Izvodjenje i = ir.findById(idIzvodjenja).get();
		request.getSession().setAttribute("izvodjenje", i);
		return "/Rezervacija";
	}
	
	@ModelAttribute("karta")
	public Karta napraviKartu() {
		return new Karta();
	}
	
	
	@RequestMapping(value="sacuvajRezervaciju", method=RequestMethod.POST) 
	public String sacuvajRezervaciju(@ModelAttribute("karta") Karta k, HttpServletRequest request, Model m)	{
		Izvodjenje i = (Izvodjenje)request.getSession().getAttribute("izvodjenje");
		k.setIzvodjenje(i);
		Karta karta = kr.save(k);
		m.addAttribute("karta", karta);
		m.addAttribute("uspesnaRezervacija", true);
		return "/Rezervacija";
	}
	
	@RequestMapping(value="izlistajRezisere", method=RequestMethod.GET) 
	public String izlistajRezisere(HttpServletRequest request) { 
		List<Reziser> reziseri = rr.findAll();
		request.getSession().setAttribute("reziseri", reziseri);
		return "PredstaveRezisera";
	}
	
	@RequestMapping(value="getPredstaveRezisera", method=RequestMethod.GET) 
	public String getPredstaveRezisera(HttpServletRequest request, Integer idReziser) { 
		Reziser r = rr.findById(idReziser).get();
		List<Predstava> predstave = pr.findByReziser(r);
		request.getSession().setAttribute("predstave", predstave);
		return "PredstaveRezisera";
	}
	
	@RequestMapping(value="generisiIzvestaj", method=RequestMethod.GET) 
	public void generisiIzvestaj(HttpServletRequest request, HttpServletResponse response) throws Exception { 
		List<Predstava> predstave = (List<Predstava>)request.getSession().getAttribute("predstave");
	
		response.setContentType("text/html");
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(predstave);
		InputStream inputStream = this.getClass().getResourceAsStream("/jasperreports/PredstaveRezisera.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
		Map<String, Object> params = new HashMap<String, Object>();
		String reziser="";
			reziser=predstave.get(0).getReziser().getIme()+" "+predstave.get(0).getReziser().getPrezime();
		if(predstave!=null && predstave.size()>0)
			params.put("reziser", reziser);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
		inputStream.close();
		
		
		response.setContentType("application/x-download");
		response.addHeader("Content-disposition", "attachment; filename=PredstaveRezisera.pdf");
		OutputStream out = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint,out);
	}
	
	@RequestMapping(value="vratiIzvodjenja", method=RequestMethod.GET) 
	public void vratiIzvodjenja(Date datum, HttpServletResponse response) throws Exception { 
		List<Izvodjenje> izvodjenja = ir.findByDatum(datum);
	
		response.setContentType("text/html");
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(izvodjenja);
		InputStream inputStream = this.getClass().getResourceAsStream("/jasperreports/IzvodjenjaZaDatum.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("datum", datum);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
		inputStream.close();
		
		
		response.setContentType("application/x-download");
		response.addHeader("Content-disposition", "attachment; filename=IzvodjenjaZaDatum.pdf");
		OutputStream out = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint,out);
	}
	
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    sdf.setLenient(true);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
	}
	
	//Priprema za kolokvijum - metode
	
	@RequestMapping(value="izborScene", method=RequestMethod.GET) 
	public String izborScene(HttpServletRequest request) { 
		List<Scena> scene = sr.findAll();
		request.getSession().setAttribute("scene", scene);
		return "IzvodjenjaNaSceni";
	}
	
	@RequestMapping(value="getIzvodjenjaZaScenu", method=RequestMethod.GET) 
	public String getIzvodjenjaZaScenu(Model model, Integer idScena) { 
		Scena s = sr.findById(idScena).get();
		List<Izvodjenje> izvodjenja = ir.findByScena(s);
		model.addAttribute("izvodjenja", izvodjenja);
		return "IzvodjenjaNaSceni";
	}
	
	@RequestMapping(value="getUlogeZaIzvodjenje", method=RequestMethod.GET) 
	public void vratiUloge(Integer idIzvodjenja, HttpServletResponse response) throws Exception { 
		Izvodjenje i = ir.findById(idIzvodjenja).get();
		List<GlumiUIzvodjenju> glumiUIzvs = gluir.findByIzvodjenje(i);
	
		response.setContentType("text/html");
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(glumiUIzvs);
		InputStream inputStream = this.getClass().getResourceAsStream("/jasperreports/UlogeUIzvodjenju.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("naziv", i.getPredstava().getNaziv());
		params.put("trajanje", i.getPredstava().getTrajanje()+" min");
		params.put("opis", i.getPredstava().getOpis());
		params.put("reziser", i.getPredstava().getReziser().getIme()+i.getPredstava().getReziser().getPrezime());
		List<Zanr> zps = zr.findByPredstava(i.getPredstava().getIdPredstava());
		String zanroviString="";
		for(Zanr z: zps) {
			zanroviString+=z.getNaziv()+", ";
		}
		zanroviString = zanroviString.substring(0, zanroviString.length()-2);
		params.put("zanrovi", zanroviString);
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
		inputStream.close();
		
		
		response.setContentType("application/x-download");
		response.addHeader("Content-disposition", "attachment; filename=UlogeUIzvodjenju.pdf");
		OutputStream out = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint,out);
	}
}
