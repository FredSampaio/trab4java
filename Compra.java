package br.usp.icmc.supermercado.compra;

import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import br.usp.icmc.supermercado.product.Product;

public class Compra
{
	private Product produto;
	private int quantidade;
	private Date data;

	public Compra(Product produto, int quantidade, Date data)
	{
		this.produto = produto;
		this.quantidade = quantidade;
		this.data = data;
	}

	public int getMes()
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.data);

		return(cal.get(Calendar.MONTH)+1);//+1 pq Calendar, janeiro eh 0
	}

	public int getDia()
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.data);

		return(cal.get(Calendar.DAY_OF_MONTH));		
	}

	public Product getProduto()
	{
		return(this.produto);
	}

	public int getQuantidade()
	{
		return(this.quantidade);
	}

	public Date getData()
	{
		return(this.data);
	}

}