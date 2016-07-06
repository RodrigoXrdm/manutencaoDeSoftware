 package ufc.quixada.npi.contest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoController;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;

public class ExcluirEventoSteps extends Mockito {

	@InjectMocks
	private EventoController eventoController;

	@Mock
	private EventoService eventoService;

	@Mock
	private ParticipacaoEventoService participacaoEventoService;
	
	@Mock
	private MessageSource messages;

	private static final String ID_EVENTO = "1";
	private static final String TEMPLATE_REMOVER = "/evento/remover/{id}";
	private MockMvc mockMvc;
	private ResultActions action;
	private Evento evento;
	private List<ParticipacaoEvento> listaParticipacaoEvento;

	@cucumber.api.java.Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoController).build();
	}
	
	@Dado("^o administrador deseja excluir um evento$")
	public void administradorDesejaExcluirEventoEmOutroEstado() throws Throwable {

	}
	
	@Quando("^ele selecionar a lista de eventos inativos$")
	public void administradorVaiParaPaginaDeEventosInativos() throws Throwable {
		listaParticipacaoEvento = popularParticipacaoEvento();
		when(participacaoEventoService.getEventosInativos()).thenReturn(listaParticipacaoEvento);
		action = 
				mockMvc.perform(get("/evento/inativos"))
						.andExpect(view().name("evento/admin_lista_inativos"));
	}
	
	@Então("^todos os eventos inativos devem ser exibidos$")
	public void umaListaDeEventosInativosEMostrada() throws Throwable {
		
		
		action
			.andExpect(model().attribute("eventosInativos", listaParticipacaoEvento));
	}
	

	@Quando("^removo um evento com id (.*)$")
	public void removoEventoComIdValido(String idEvento) throws Throwable {
		evento = new Evento();
		evento.setEstado(EstadoEvento.INATIVO);
		evento.setId(Long.valueOf(ID_EVENTO));
		when(eventoService.buscarEventoPorId(Long.valueOf(idEvento))).thenReturn(evento);
		when(messages.getMessage("EVENTO_INATIVO_EXCLUIDO_SUCESSO", null, null)).thenReturn("Evento inativo excluido com sucesso");

		action = mockMvc.perform(get(TEMPLATE_REMOVER, Long.valueOf(ID_EVENTO)));
	}

	@Então("^evento deve ser excluido com sucesso$")
	public void eventoDeveSerExcluidoComSucesso() throws Throwable {
		//evento/ativos e inativos
		action		
			.andExpect(status().isFound()).andExpect(redirectedUrl("/evento/inativos"))
			.andExpect(flash().attributeExists("sucesso"));

		verify(participacaoEventoService).removerParticipacaoEvento(evento);
	}

	@Quando("^tento remover um evento com estado (.*) e id (.*)$")
	public void removoEventoComIdValidoEstadoAtivo(String estado, String idEvento) throws Throwable {
		evento = new Evento();
		evento.setEstado(EstadoEvento.valueOf(estado));
		evento.setId(Long.valueOf(ID_EVENTO));
		when(eventoService.buscarEventoPorId(Long.valueOf(idEvento))).thenReturn(evento);

		action = mockMvc.perform(get(TEMPLATE_REMOVER, Long.valueOf(ID_EVENTO)));
	}

	@Então("^o usuário é informado que não pode excluir esse evento$")
	public void aconteceExcecao() throws Throwable {
		when(messages.getMessage("EVENTO_INATIVO_EXCLUIDO_ERRO", null, null)).thenReturn("Evento não encontrado");
		doThrow(IllegalArgumentException.class).when(participacaoEventoService).removerParticipacaoEvento(evento);

		action.andExpect(status().is3xxRedirection());
	}
	
	public List<ParticipacaoEvento> popularParticipacaoEvento(){
		listaParticipacaoEvento = new ArrayList<>();
		
		for(int i=0; i < 10; i++){
			ParticipacaoEvento participacaoEvento = new ParticipacaoEvento();
			listaParticipacaoEvento.add(participacaoEvento);
		}
		
		return listaParticipacaoEvento;
	}
}