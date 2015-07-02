package br.usp.icmc.supermercado.aplicacao;

import br.usp.icmc.supermercado.user.User;
import br.usp.icmc.supermercado.product.Product;
import br.usp.icmc.supermercado.requisitado.Requisitado;

import java.net.*;
import java.io.*;
import java.util.*;

public class TrataCliente implements Runnable 
{
	private Socket cliente;
	private Servidor servidor;
	private ServerSocket sockServ;

	public TrataCliente(Socket cliente, Servidor servidor)
	{
		this.cliente = cliente;
		this.servidor = servidor;
	}
 
	public void run()
	{
		/*--Recebe mensagem do cliente (comandos)--*/
		try
		{
			Scanner s = new Scanner(this.cliente.getInputStream());
			PrintStream saida = new PrintStream(this.cliente.getOutputStream(), true);
			String str = new String();

			while(s.hasNext())
			{
				str = s.nextLine();
				
				if(str.equals("listarP"))
				{
					servidor.Lista_Produto(saida);
					saida.println("fim");
				}

				else if(str.equals("novoUser"))
				{
					String nome = s.nextLine();
					String end = s.nextLine();
					String tel = s.nextLine();
					String email = s.nextLine();
					int id = s.nextInt();
					s.nextLine();
					String senha = s.nextLine();

					boolean r = this.servidor.adicionaUsuario(new User(nome, end, tel, email, id, senha));
					if(r)
						saida.println("Usuario cadastrado com sucesso!");
					else
						saida.println("ID ja existente!");
				}

				else if(str.equals("login"))
				{
					int id = s.nextInt();
					s.nextLine();
					String strSenha = s.nextLine();
					
					if(!servidor.existeUsuario(id))
						saida.println("Usuario inexistente!");
					else
					{
						if(!servidor.checkSenha(servidor.getUsuario(id), strSenha))
							saida.println("Senha invalida!");
						else
							saida.println(servidor.getUsuario(id).getNome());		
					}
				}

				else if(str.equals("compra"))
				{
					int codigo = s.nextInt();
					int quantidade = s.nextInt();
					s.nextLine();

					int r = this.servidor.efetuaCompra(codigo, quantidade);

					if (r == 1)
						saida.println("Compra realizada com sucesso!");
					else if (r == 2)
						saida.println("Codigo invalido!");
					else
						saida.println("Produto fora de estoque!");
				}

				else if(str.equals("requisitar"))
				{
					int id = s.nextInt();
					int codigo = s.nextInt();
					s.nextLine();

					if(!this.servidor.existeProduto(codigo))
						saida.println("Produto invalido(inexistente)!");
					else
					{
						this.servidor.adicionaRequisitado(new Requisitado(this.servidor.getProduto(codigo), this.servidor.getUsuario(id)));
						saida.println("Voce recebera um email quando o produto estiver disponivel.");
					}
					
				}
				else
				{
					s.close();
					saida.close();
				}
			}
		}
		catch(Exception e) {e.printStackTrace();}
		/*-----------------------------------------*/
	}
}