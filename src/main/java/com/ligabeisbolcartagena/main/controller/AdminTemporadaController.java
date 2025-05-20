package com.ligabeisbolcartagena.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.ligabeisbolcartagena.main.model.Equipo;
import com.ligabeisbolcartagena.main.model.JugadorBateador;
import com.ligabeisbolcartagena.main.model.Estadio;
import com.ligabeisbolcartagena.main.model.JugadorLanzador;
import com.ligabeisbolcartagena.main.model.Partido;
import com.ligabeisbolcartagena.main.model.Temporada;
import com.ligabeisbolcartagena.main.repository.TemporadaRepository;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminTemporadaController {

    private final TemporadaRepository temporadaRepository;

    public AdminTemporadaController(TemporadaRepository temporadaRepository) {
        this.temporadaRepository = temporadaRepository;
    }
    
    @GetMapping("/crear-temporada")
    public String mostrarFormularioTemporada(Model model) {
        model.addAttribute("temporada", new Temporada());
        return "crear_temporada";
    }

    @PostMapping("/crear-temporada")
    public String guardarTemporada(@ModelAttribute Temporada temporada) {
        temporadaRepository.save(temporada);
        return "redirect:/admin/listar-temporadas";
    }
    
    @GetMapping("/listar-temporadas")
    public String listarTemporadas(Model model) {
        List<Temporada> temporadas = temporadaRepository.findAll();
        model.addAttribute("temporadas", temporadas);
        return "listar_temporadas";
    }
    
    @GetMapping("/editar-temporada/{id}")
    public String mostrarFormularioEditarTemporada(@PathVariable String id, Model model) {
        Temporada temporada = temporadaRepository.findById(id).orElseThrow();
        model.addAttribute("temporada", temporada);
        return "editar_temporada";
    }

    @PostMapping("/editar-temporada/{id}")
    public String guardarEdicionTemporada(@PathVariable String id, @ModelAttribute Temporada temporadaActualizada) {
        Temporada temporada = temporadaRepository.findById(id).orElseThrow();
        temporada.setNombreTemporada(temporadaActualizada.getNombreTemporada());
        temporada.setAnio(temporadaActualizada.getAnio());
        temporadaRepository.save(temporada);
        return "redirect:/admin/listar-temporadas";
    }
    
    @GetMapping("/eliminar-temporada/{id}")
    public String eliminarTemporada(@PathVariable String id) {
        temporadaRepository.deleteById(id);
        return "redirect:/admin/listar-temporadas";
    }


    //EQUIPOS
    
    //listar equipos
    
    @GetMapping("/listar-equipos")
    public String listarEquiposAdmin(Model model) {
        List<Temporada> temporadas = temporadaRepository.findAll();
        model.addAttribute("temporadas", temporadas);
        return "listar_equipos_admin";  
    }
    
    //crear equipos
    
    @GetMapping("/crear-equipo/{idTemporada}")
    public String crearEquipo(@PathVariable String idTemporada, Model model) {
        model.addAttribute("idTemporada", idTemporada);
        model.addAttribute("equipo", new Equipo());
        return "crear_equipo";
    }
    
    @PostMapping("/crear-equipo/{idTemporada}")
    public String guardarEquipo(@PathVariable String idTemporada, @ModelAttribute Equipo equipo) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();

        equipo.setBateadores(new ArrayList<>());
        equipo.setLanzadores(new ArrayList<>());

        if (temporada.getEquiposParticipantes() == null) {
            temporada.setEquiposParticipantes(new ArrayList<>());
        }

        temporada.getEquiposParticipantes().add(equipo);
        temporadaRepository.save(temporada);

        return "redirect:/admin/listar-equipos";
    }
    
    //editar equipos
    
    @GetMapping("/editar-equipo/{idTemporada}/{indexEquipo}")
    public String formEditarEquipo(@PathVariable String idTemporada, @PathVariable int indexEquipo, Model model) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();

        if (indexEquipo < 0 || indexEquipo >= temporada.getEquiposParticipantes().size()) {
            throw new IllegalArgumentException("Índice de equipo fuera de rango");
        }

        Equipo equipo = temporada.getEquiposParticipantes().get(indexEquipo);

        model.addAttribute("temporada", temporada);
        model.addAttribute("equipo", equipo);
        model.addAttribute("indice", indexEquipo);

        return "editar_equipo_admin";
    }


    @PostMapping("/editar-equipo/{idTemporada}/{indexEquipo}")
    public String guardarCambiosEquipo(
            @PathVariable String idTemporada,
            @PathVariable int indexEquipo,
            @ModelAttribute Equipo equipo) {

        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();
        temporada.getEquiposParticipantes().set(indexEquipo, equipo);
        temporadaRepository.save(temporada);
        return "redirect:/admin/listar-equipos";
    }

    
    //eliminar equipos
    
    @GetMapping("/eliminar-equipo/{idTemporada}/{indexEquipo}")
    public String eliminarEquipo(@PathVariable String idTemporada, @PathVariable int indexEquipo) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();
        temporada.getEquiposParticipantes().remove(indexEquipo);
        temporadaRepository.save(temporada);
        return "redirect:/admin/listar-equipos";
    }

    //JUGADORES

    @GetMapping("/jugadores")
    public String jugadores(Model model) {
        model.addAttribute("temporadas", temporadaRepository.findAll());
        return "jugadores_admin";
    }

    @GetMapping("/jugadores/seleccionar")
    public String seleccionarTemporada(@RequestParam String idTemporada, Model model) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();
        model.addAttribute("temporada", temporada);
        return "seleccionar_equipo_jugador";
    }
    
    @GetMapping("/jugadores/{idTemporada}")
    public String verJugadoresPorTemporada(@PathVariable String idTemporada, Model model) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();
        model.addAttribute("temporada", temporada);
        return "ver_jugadores";
    }
    
    @GetMapping("/listar-jugadores-equipo/{idTemporada}/{indexEquipo}")
    public String listarJugadoresEquipo(@PathVariable String idTemporada, @PathVariable int indexEquipo, Model model) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();
        Equipo equipo = temporada.getEquiposParticipantes().get(indexEquipo);

        model.addAttribute("equipo", equipo);
        return "listar_jugadores_equipo";
    }

    
    // bateadores
    
    @GetMapping("/agregar-bateador/{idTemporada}/{indexEquipo}")
    public String formAgregarBateador(@PathVariable String idTemporada, @PathVariable int indexEquipo, Model model) {
        model.addAttribute("idTemporada", idTemporada);
        model.addAttribute("indexEquipo", indexEquipo);
        model.addAttribute("bateador", new JugadorBateador());
        return "agregar_bateador";
    }

    @PostMapping("/agregar-bateador/{idTemporada}/{indexEquipo}")
    public String guardarBateador(@PathVariable String idTemporada, @PathVariable int indexEquipo, @ModelAttribute JugadorBateador bateador) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();
        temporada.getEquiposParticipantes().get(indexEquipo).getBateadores().add(bateador);
        temporadaRepository.save(temporada);
        return "redirect:/admin/agregar-bateador/" + idTemporada + "/" + indexEquipo;
    }

    
    @GetMapping("/perfil-bateador/{idTemporada}/{indexEquipo}/{indexBateador}")
    public String perfilBateador(@PathVariable String idTemporada, @PathVariable int indexEquipo, @PathVariable int indexBateador, Model model) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();
        Equipo equipo = temporada.getEquiposParticipantes().get(indexEquipo);
        JugadorBateador bateador = equipo.getBateadores().get(indexBateador);

        model.addAttribute("bateador", bateador);
        return "perfil_jugador_bateador_admin";
    }
    
    @GetMapping("/perfil-lanzador/{idTemporada}/{indexEquipo}/{indexLanzador}")
    public String perfilLanzador(@PathVariable String idTemporada, @PathVariable int indexEquipo, @PathVariable int indexLanzador, Model model) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();
        Equipo equipo = temporada.getEquiposParticipantes().get(indexEquipo);
        JugadorLanzador lanzador = equipo.getLanzadores().get(indexLanzador);

        model.addAttribute("lanzador", lanzador);
        return "perfil_jugador_lanzador_admin";
    }

    
 // lanzadores

    @GetMapping("/agregar-lanzador/{idTemporada}/{indexEquipo}")
    public String formAgregarLanzador(@PathVariable String idTemporada, @PathVariable int indexEquipo, Model model) {
        model.addAttribute("idTemporada", idTemporada);
        model.addAttribute("indexEquipo", indexEquipo);
        model.addAttribute("lanzador", new JugadorLanzador());
        return "agregar_lanzador";
    }

    @PostMapping("/agregar-lanzador/{idTemporada}/{indexEquipo}")
    public String guardarLanzador(@PathVariable String idTemporada,
                                  @PathVariable int indexEquipo,
                                  @ModelAttribute JugadorLanzador lanzador) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();

        temporada.getEquiposParticipantes()
                 .get(indexEquipo)
                 .getLanzadores()
                 .add(lanzador);

        temporadaRepository.save(temporada);

        return "redirect:/admin/agregar-lanzador/{idTemporada}/{indexEquipo}";
    }
    
    // agentes libres
    
    @GetMapping("/agregar-bateador-libres/{idTemporada}")
    public String formAgregarBateadorLibre(@PathVariable String idTemporada, Model model) {
        model.addAttribute("idTemporada", idTemporada);
        model.addAttribute("bateador", new JugadorBateador());
        return "agregar_bateador_libre";
    }

    @PostMapping("/agregar-bateador-libres/{idTemporada}")
    public String guardarBateadorLibre(@PathVariable String idTemporada, @ModelAttribute JugadorBateador bateador) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();
        Equipo agentesLibres = obtenerCrearAgentesLibres(temporada);
        agentesLibres.getBateadores().add(bateador);
        temporadaRepository.save(temporada);
        return "redirect:/admin/jugadores";
    }

    @GetMapping("/agregar-lanzador-libres/{idTemporada}")
    public String formAgregarLanzadorLibre(@PathVariable String idTemporada, Model model) {
        model.addAttribute("idTemporada", idTemporada);
        model.addAttribute("lanzador", new JugadorLanzador());
        return "agregar_lanzador_libre";
    }

    @PostMapping("/agregar-lanzador-libres/{idTemporada}")
    public String guardarLanzadorLibre(@PathVariable String idTemporada, @ModelAttribute JugadorLanzador lanzador) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();
        Equipo agentesLibres = obtenerCrearAgentesLibres(temporada);
        agentesLibres.getLanzadores().add(lanzador);
        temporadaRepository.save(temporada);
        return "redirect:/admin/jugadores";
    }

    private Equipo obtenerCrearAgentesLibres(Temporada temporada) {
        for (Equipo equipo : temporada.getEquiposParticipantes()) {
            if ("Agentes Libres".equalsIgnoreCase(equipo.getNombre())) {
                return equipo;
            }
        }

        Equipo nuevo = new Equipo();
        nuevo.setNombre("Agentes Libres");
        temporada.getEquiposParticipantes().add(nuevo);
        return nuevo;
    }

    
    //PARTIDOS
    
    @GetMapping("/listar-temporadas-partidos")
    public String listarTemporadasPartidos(Model model) {
        List<Temporada> temporadas = temporadaRepository.findAll();
        model.addAttribute("temporadas", temporadas);
        return "listar_temporadas_partidos";
    }
    
    
    @GetMapping("/crear-partido/{idTemporada}")
    public String formCrearPartido(@PathVariable String idTemporada, Model model) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();

        model.addAttribute("idTemporada", idTemporada);
        model.addAttribute("partido", new Partido());
        model.addAttribute("equipos", temporada.getEquiposParticipantes());
        model.addAttribute("estadios", temporada.getEstadios());  // <-- aquí agregas los estadios

        return "crear_partido";
    }



    @PostMapping("/crear-partido/{idTemporada}")
    public String guardarPartido(@PathVariable String idTemporada, @ModelAttribute Partido partido) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();

        // Buscar el estadio por nombre dentro de la temporada
        Estadio estadioSeleccionado = temporada.getEstadios().stream()
            .filter(e -> e.getNombre().equals(partido.getEstadio().getNombre()))
            .findFirst()
            .orElse(null);

        partido.setEstadio(estadioSeleccionado);

        if (temporada.getPartidosJugados() == null) {
            temporada.setPartidosJugados(new ArrayList<>());
        }
        temporada.getPartidosJugados().add(partido);
        temporadaRepository.save(temporada);

        return "redirect:/admin/crear-partido/{idTemporada}";
    }

    
    @GetMapping("/editar-partido/{idTemporada}/{indexPartido}")
    public String mostrarFormularioEditarPartido(
            @PathVariable String idTemporada,
            @PathVariable int indexPartido,
            Model model) {

        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();

        if (indexPartido < 0 || indexPartido >= temporada.getPartidosJugados().size()) {
            throw new IllegalArgumentException("Índice de partido fuera de rango");
        }

        Partido partido = temporada.getPartidosJugados().get(indexPartido);
        model.addAttribute("idTemporada", idTemporada);
        model.addAttribute("indexPartido", indexPartido);
        model.addAttribute("partido", partido);
        model.addAttribute("estadios", temporada.getEstadios());

        return "editar_partido";
    }
    
    @PostMapping("/editar-partido/{idTemporada}/{indexPartido}")
    public String guardarEdicionPartido(
            @PathVariable String idTemporada,
            @PathVariable int indexPartido,
            @ModelAttribute Partido partidoActualizado) {

        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();

        if (indexPartido < 0 || indexPartido >= temporada.getPartidosJugados().size()) {
            throw new IllegalArgumentException("Índice de partido fuera de rango");
        }

        temporada.getPartidosJugados().set(indexPartido, partidoActualizado);
        temporadaRepository.save(temporada);

        return "redirect:/admin/editar-partido/{idTemporada}/{indexPartido}";
    }

    @GetMapping("/ver-partido/{idTemporada}/{indexPartido}")
    public String verDetallePartido(
            @PathVariable String idTemporada,
            @PathVariable int indexPartido,
            Model model) {

        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();

        if (indexPartido < 0 || indexPartido >= temporada.getPartidosJugados().size()) {
            throw new IllegalArgumentException("Índice de partido fuera de rango");
        }

        Partido partido = temporada.getPartidosJugados().get(indexPartido);
        model.addAttribute("partido", partido);
        model.addAttribute("temporada", temporada);

        return "ver_partido"; // Asegúrate de tener esta vista
    }
    
    @GetMapping("/eliminar-partido/{idTemporada}/{indexPartido}")
    public String eliminarPartido(
            @PathVariable String idTemporada,
            @PathVariable int indexPartido) {

        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();

        if (indexPartido < 0 || indexPartido >= temporada.getPartidosJugados().size()) {
            throw new IllegalArgumentException("Índice de partido fuera de rango");
        }

        temporada.getPartidosJugados().remove(indexPartido);
        temporadaRepository.save(temporada);

        return "redirect:/admin/listar-temporadas-partidos";
    }
    
    //ESTADIOS
    
    @GetMapping("/listar-temporadas-estadios")
    public String seleccionarTemporadaParaEstadios(Model model) {
    	List<Temporada> temporadas = temporadaRepository.findAll();
        model.addAttribute("temporadas", temporadas);
        return "listar_temporadas_estadios";
    }

    
 // Mostrar formulario para crear estadio
    @GetMapping("/crear-estadio/{idTemporada}")
    public String mostrarFormularioEstadio(@PathVariable String idTemporada, Model model) {
        model.addAttribute("idTemporada", idTemporada);
        model.addAttribute("estadio", new Estadio());
        return "crear_estadio";
    }

    @PostMapping("/crear-estadio/{idTemporada}")
    public String guardarEstadio(@PathVariable String idTemporada, @ModelAttribute Estadio estadio) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();

        if (temporada.getEstadios() == null) {
            temporada.setEstadios(new ArrayList<>());
        }

        temporada.getEstadios().add(estadio);
        temporadaRepository.save(temporada);

        // ✅ Redirigir a la lista de estadios de la temporada
        return "redirect:/admin/listar-temporadas-estadios";
    }
    
 // FORMULARIO para editar estadio
    @GetMapping("/editar-estadio/{idTemporada}/{indexEstadio}")
    public String formEditarEstadio(@PathVariable String idTemporada, @PathVariable int indexEstadio, Model model) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();
        
        if (indexEstadio < 0 || indexEstadio >= temporada.getEstadios().size()) {
            throw new IllegalArgumentException("Índice de estadio fuera de rango");
        }
        
        Estadio estadio = temporada.getEstadios().get(indexEstadio);
        
        model.addAttribute("temporada", temporada);
        model.addAttribute("estadio", estadio);
        model.addAttribute("indice", indexEstadio);
        
        return "editar_estadio";
    }

    // GUARDAR cambios estadio
    @PostMapping("/editar-estadio/{idTemporada}/{indexEstadio}")
    public String guardarCambiosEstadio(
            @PathVariable String idTemporada,
            @PathVariable int indexEstadio,
            @ModelAttribute Estadio estadioActualizado) {

        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();

        Estadio estadio = temporada.getEstadios().get(indexEstadio);
        estadio.setNombre(estadioActualizado.getNombre());
        estadio.setUbicacion(estadioActualizado.getUbicacion());
        // Ajusta aquí si tienes más campos

        temporada.getEstadios().set(indexEstadio, estadio);
        temporadaRepository.save(temporada);

        return "redirect:/admin/listar-temporadas-estadios";
    }

    // ELIMINAR estadio
    @GetMapping("/eliminar-estadio/{idTemporada}/{indexEstadio}")
    public String eliminarEstadio(@PathVariable String idTemporada, @PathVariable int indexEstadio) {
        Temporada temporada = temporadaRepository.findById(idTemporada).orElseThrow();
        temporada.getEstadios().remove(indexEstadio);
        temporadaRepository.save(temporada);
        return "redirect:/admin/listar-temporadas-estadios";
    }

}
