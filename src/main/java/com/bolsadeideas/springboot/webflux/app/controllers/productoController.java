package com.bolsadeideas.springboot.webflux.app.controllers;

import java.time.Duration;
import java.util.Date;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import com.bolsadeideas.springboot.webflux.app.models.documents.Categoria;
import com.bolsadeideas.springboot.webflux.app.models.documents.Producto;
import com.bolsadeideas.springboot.webflux.app.models.services.ProductosService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@SessionAttributes("producto")
public class productoController {

	@Autowired
	private ProductosService service;

	private Logger log = LoggerFactory.getLogger(productoController.class);

	@ModelAttribute("categorias")
	public Flux<Categoria> categorias() {
		return service.findAllCategoria();
	}

	@GetMapping({ "/listar", "/" })
	public Mono<String> listar(Model model) {
		Flux<Producto> productos = service.findAllWhitName();

		productos.subscribe(prod -> log.info(prod.getNombre()));

		model.addAttribute("productos", productos);
		model.addAttribute("titulo", "Listado de productos");
		return Mono.just("listar");
	}

	@GetMapping("/form")
	public Mono<String> crear(Model model) {
		model.addAttribute("producto", new Producto());
		model.addAttribute("titulo", "Formulario de producto");
		return Mono.just("form");
	}

	@PostMapping("/form")
	public Mono<String> guardar(@Valid Producto producto, BindingResult result, Model model, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Errores en formulario producto");
			model.addAttribute("boton", "Guardar");

			return Mono.just("form");

		} else {

			status.setComplete();
			System.out.println(producto.getCategoria().getId()+"ID");

			Mono<Categoria> categoria = service.findByIdCategoria(producto.getCategoria().getId());
			System.out.println("1");
		
			
			return categoria.flatMap(c -> {

				System.out.println("2");

				// log.info(c.getId()+"ID");
				if (producto.getCreateAt() == null) {
					producto.setCreateAt(new Date());
				}
				System.out.println("3");

				producto.setCategoria(c);
				System.out.println("4");

				return service.save(producto);
			}).switchIfEmpty(method())
					.doOnNext(p -> {
				log.info("Producto guardado : " + p.getNombre() + " - " + p.getId());
			}).thenReturn("redirect:/listar?success=producti+guardado+exito");
		}
	}
	
	public static Mono<Producto> method() {
		System.out.println("Categoria vacia");
		return Mono.empty();
	}

	@GetMapping("/form/{id}")
	public Mono<String> edit(@PathVariable String id, Model model) {
		Mono<Producto> producto = service.findById(id).doOnNext(p -> {
			log.info("Producto : " + p.getNombre());
		}).defaultIfEmpty(new Producto());
		model.addAttribute("titulo", "Editar producto");
		model.addAttribute("producto", producto);

		return Mono.just("form");

	}

	@GetMapping("/eliminar/{id}")
	public Mono<String> eliminar(@PathVariable String id) {
		return service.findById(id).defaultIfEmpty(new Producto()).flatMap(pr -> {
			if (pr.getId() == null) {
				return Mono.error(new InterruptedException("No existe producto para eliminar"));
			}
			return Mono.just(pr);
		}).flatMap(p -> {
			log.info("Producto eliminar : " + p.getNombre());
			return service.delete(p);
		}).then(Mono.just("redirect:/listar?succes=producto+eliminado+con+exito"))
				.onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto+a+eliminar"));
	}

	@GetMapping({ "/listar-datadriver" })
	private String listarDataDriver(Model model) {
		Flux<Producto> productos = service.findAllWhitName().delayElements(Duration.ofSeconds(1));

		productos.subscribe(prod -> log.info(prod.getNombre()));

		model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos, 1));
		model.addAttribute("titulo", "Listado de productos");
		return "listar";

	}

	@GetMapping({ "/listar-full" })
	private String listarFull(Model model) {
		Flux<Producto> productos = service.findAllUpperRepetat();

		model.addAttribute("productos", productos);
		model.addAttribute("titulo", "Listado de productos");
		return "listar";

	}

	@GetMapping({ "/listar-chunked" })
	private String listarChunk(Model model) {
		Flux<Producto> productos = service.findAllUpperRepetat();

		model.addAttribute("productos", productos);
		model.addAttribute("titulo", "Listado de productos");
		return "listar-chunked";

	}

}
