package br.usp.icmc.supermercado.requisitado;

import br.usp.icmc.supermercado.user.User;
import br.usp.icmc.supermercado.product.Product;

import java.util.*;

public class Requisitado
{
	private Product produto;
	private User usuario;

	public Requisitado(Product produto, User usuario)
	{
		this.produto = produto;
		this.usuario = usuario;	
	}

	public User getUsuario()
	{
		return(this.usuario);
	}

	public Product getProduto()
	{
		return(this.produto);
	}
}