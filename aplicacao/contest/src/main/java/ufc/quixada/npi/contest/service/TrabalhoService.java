package ufc.quixada.npi.contest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.repository.TrabalhoRepository;

@Service
public class TrabalhoService {
	
	@Autowired
	private TrabalhoRepository trabalhoRepository;
	
	public boolean existeTrabalho(Long idTrabalho){
		return trabalhoRepository.exists(idTrabalho);
	}
	
	public List<Trabalho> getTrabalhosEvento(Evento evento){
		return trabalhoRepository.findByEvento(evento);
	}
	
	public List<Trabalho> getTrabalhosTrilha(Trilha trilha){
		return trabalhoRepository.findByTrilha(trilha);
	}
	
	
	public void adicionarTrabalho(Trabalho trabalho){
		trabalhoRepository.save(trabalho);
	}
}