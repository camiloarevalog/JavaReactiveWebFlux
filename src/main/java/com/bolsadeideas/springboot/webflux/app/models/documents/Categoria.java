package com.bolsadeideas.springboot.webflux.app.models.documents;

import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;

@Document(collection = "categorias")
public class Categoria {

	@Id
	private String Id;

	private String nombre;

	public Categoria() {

	}

	public Categoria(String nombre) {
		this.nombre = nombre;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public String toString() {
		return "Categoria [Id=" + Id + ", nombre=" + nombre + "]";
	}
	
	

}
