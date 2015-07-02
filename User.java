package br.usp.icmc.supermercado.user;

import java.io.*;

public class User
{
	private String nome;
	private String endereco;
	private String telefone;
	private String email;
	private int id;
	private String senha;

	public User(String nome, String endereco, String telefone, String email, int id, String senha)
	{
		this.nome = nome;
		this.endereco = endereco;
		this.telefone = telefone;
		this.email = email;
		this.id = id;
		this.senha = senha;
	}

	public void setNome(String nome)
	{
		this.nome = nome;
	}

	public String getNome()
	{
		return(this.nome);
	}

	public void setEndereco(String endereco)
	{
		this.endereco = endereco;
	}

	public String getEndereco()
	{
		return(this.endereco);
	}

	public void setTelefone(String telefone)
	{
		this.telefone = telefone;
	}

	public String getTelefone()
	{
		return(this.telefone);
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getEmail()
	{
		return(this.email);
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return(this.id);
	}

	public void setSenha(String senha)
	{
		this.senha = senha;
	}

	public String getSenha()
	{
		return(this.senha);
	}

}