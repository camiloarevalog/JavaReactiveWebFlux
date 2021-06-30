package com.bolsadeideas.springboot.webflux.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.bolsadeideas.springboot.webflux.app.models.dao.IProductoDao;
import com.bolsadeideas.springboot.webflux.app.models.documents.Categoria;
import com.bolsadeideas.springboot.webflux.app.models.documents.Producto;
import com.bolsadeideas.springboot.webflux.app.models.services.ProductoServiceImpl;
import com.bolsadeideas.springboot.webflux.app.models.services.ProductosService;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

	@Autowired
	private ProductosService dao;

	@Autowired
	private ReactiveMongoTemplate mongoTemplate;

	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		mongoTemplate.dropCollection("productos").subscribe();
		mongoTemplate.dropCollection("categorias").subscribe();

		Categoria electronico = new Categoria("Electronico");
		Categoria deporte = new Categoria("Deporte");
		Categoria computacion = new Categoria("Computacion");
		Categoria hogar = new Categoria("Hogar");

		Flux.just(electronico, deporte, computacion, hogar).flatMap(dao::saveCategoria).doOnNext(c -> {
			log.info("Categoria creada : " + c.getNombre() + "-" + c.getId());
		}).thenMany(Flux.just(new Producto("Tv Samsung", 2.93, electronico),
				new Producto("Camara Sony", 2.93, electronico), new Producto("Cama la resiste", 2.93, hogar),
				new Producto("Escritorio", 2.93, computacion), new Producto("Equipo de sonido", 2.93, electronico),
				new Producto("HP Notebook", 2.93, computacion), new Producto("Teclado el bueno", 2.93, deporte)

		).flatMap(producto -> {
			producto.setCreateAt(new Date());
			return dao.save(producto);
		})).subscribe(producto -> log.info("Insert s: " + producto.getId() + " Nombre :" + producto.getNombre()));

	}

}
