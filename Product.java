package br.usp.icmc.supermercado.product;

import java.util.*;

public class Product
{
	private String nome;
	private float preco;
	private Date validade;
	private String fornecedor;
	private int quantidade;
	private int codigo;
	private boolean disponivel;

	public Product(String nome, float preco, Date validade, String fornecedor, int quantidade, int codigo, boolean disponivel)
	{
		this.nome = nome;
		this.preco = preco;
		this.validade = validade;
		this.fornecedor = fornecedor;
		this.quantidade = quantidade;
		this.codigo = codigo;
		this.disponivel = disponivel;
	}

	public void setNome(String nome)
	{
		this.nome = nome;
	}

	public String getNome()
	{
		return(this.nome);
	}

	public void setPreco (float preco)
	{
		this.preco = preco;
	}

	public float getPreco()
	{
		return(this.preco);
	}

	public void setValidade(Date validade)
	{
		this.validade = validade;
	}

	public Date getValidade()
	{
		return(this.validade);
	}

	public void setFornecedor(String fornecedor)
	{
		this.fornecedor = fornecedor;
	}

	public String getFornecedor()
	{
		return(this.fornecedor);
	}

	public void setQuantidade(int quantidade)
	{
		this.quantidade = quantidade;
	}

	public int getQuantidade()
	{
		return(this.quantidade);
	}

	public void setCodigo(int codigo)
	{
		this.codigo = codigo;
	}

	public int getCodigo()
	{
		return(this.codigo);
	}

	public void setDisponivel(boolean disp)
	{
		this.disponivel = disponivel;
	}

	public boolean getDisponivel()
	{
		return(this.disponivel);
	}
}