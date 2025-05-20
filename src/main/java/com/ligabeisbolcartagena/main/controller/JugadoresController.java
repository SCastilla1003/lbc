package com.ligabeisbolcartagena.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ligabeisbolcartagena.main.model.Equipo;
import com.ligabeisbolcartagena.main.model.JugadorBateador;
import com.ligabeisbolcartagena.main.model.JugadorLanzador;
import com.ligabeisbolcartagena.main.model.Temporada;
import com.ligabeisbolcartagena.main.repository.TemporadaRepository;

@Controller
public class JugadoresController {

    private final TemporadaRepository temporadaRepository;

    public JugadoresController(TemporadaRepository temporadaRepository) {
        this.temporadaRepository = temporadaRepository;
    }

    @GetMapping("/temporadas-jugadores")
    public String mostrarTemporadasJugadores(Model model) {
        model.addAttribute("temporadas", temporadaRepository.findAll());
        return "temporadas_jugadores";
    }
    
    @GetMapping("/temporadas-jugadores/{idTemporada}")
    public String listarJugadoresPorTemporada(@PathVariable String idTemporada, Model model) {
        Temporada temporada = temporadaRepository.findById(idTemporada)
            .orElseThrow(() -> new IllegalArgumentException("Temporada no encontrada"));

        model.addAttribute("temporada", temporada);
        return "listar_jugadores_inicio";
    }
    
    @GetMapping("/perfil-bateador/{idTemporada}/{indexEquipo}/{indexBateador}")
    public String perfilBateadorUsuario(@PathVariable String idTemporada, @PathVariable int indexEquipo, @PathVariable int indexBateador, Model model) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();
        Equipo equipo = temporada.getEquiposParticipantes().get(indexEquipo);
        JugadorBateador bateador = equipo.getBateadores().get(indexBateador);

        model.addAttribute("bateador", bateador);
        return "perfil_bateador_usuario";
    }
    
    @GetMapping("/perfil-lanzador/{idTemporada}/{indexEquipo}/{indexLanzador}")
    public String perfilLanzadorUsuario(@PathVariable String idTemporada, @PathVariable int indexEquipo, @PathVariable int indexLanzador, Model model) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();
        Equipo equipo = temporada.getEquiposParticipantes().get(indexEquipo);
        JugadorLanzador lanzador = equipo.getLanzadores().get(indexLanzador);

        model.addAttribute("lanzador", lanzador);
        return "perfil_lanzador_usuario";
    }


}

