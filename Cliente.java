package br.usp.icmc.supermercado.aplicacao;

import br.usp.icmc.supermercado.user.User;
import br.usp.icmc.supermercado.product.Product;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class Cliente
{
	private String host;
	private int porta;
	private Socket servidor;
	private String currentNome = "Visitante";
	private int currentId;

	public Cliente(String host, int porta)
	{
		this.host = host;
		this.porta = porta;
   	}
   
	public void executa()
	{
		try
		{
			servidor = new Socket(this.host, this.porta);
		}
		catch(Exception e)
		{
			System.out.println("Erro! Servidor encontra-se desligado.");
			return;
		}

		Menu_Cliente();
	}

	public void Menu_Cliente()
	{
		Scanner s = new Scanner(System.in);
		int opcao = -1;

		while (opcao != 0)
		{
			System.out.print(
        		"\n\n\n .: Menu Cliente:.\t\t[" + currentNome + "]\n\n" +
		        "1 - Novo Usuario\n" +
        		"2 - Fazer Login\n" +
        		"0 - Sair do programa\n\n" +
		        "Opção: ");
      		opcao = s.nextInt();

      		switch (opcao) 
      		{
	        	case 1:
	        		CadastraUser();
	          		break;
	        	case 2:
	        		Login();
	        		if(!currentNome.equals("Visitante"))
	        			Menu_User();
	          		break;
	        	case 0:
	        		break;
        	}
		}
	}

	public void CadastraUser()
	{
		Scanner s = new Scanner(System. in );
    	System.out.print("Digite seu nome: ");
    	String stringNome = s.nextLine();
    	System.out.print("Digite seu endereco: ");
    	String stringEndereco = s.nextLine();
    	System.out.print("Digite seu telefone: ");
    	String stringTelefone = s.nextLine();
    	System.out.print("Digite seu email: ");
    	String stringEmail = s.nextLine();
    	while(!isValidEmailAddress(stringEmail))
    	{
    		System.out.print("Email invalio!\n");
    		System.out.print("Digite um email valido: ");
    		stringEmail = s.nextLine();
    	}
    	System.out.print("Digite seu ID(apenas numeros): ");
    	int id = s.nextInt();
    	s.nextLine();
    	System.out.print("Digite sua senha: ");
    	String stringSenha = s.nextLine();

    	User u = new User(stringNome, stringEndereco, stringTelefone, stringEmail, id, stringSenha);
		try
		{
			PrintStream saida = new PrintStream(this.servidor.getOutputStream(), true);
			Scanner s2 = new Scanner(this.servidor.getInputStream());
			
			saida.println("novoUser");
			saida.println(stringNome);
			saida.println(stringEndereco);
			saida.println(stringTelefone);
			saida.println(stringEmail);
			saida.println(id);
			saida.println(stringSenha);

			//saida.close();
			String r = s2.nextLine();
			clearScreen();
			System.out.println(r);
		}
		catch(Exception e) {e.printStackTrace();}
	}

	public void Login()
	{
		Scanner s = new Scanner(System.in);

		System.out.print("Digite seu ID: ");
    	int id = s.nextInt();
    	s.nextLine();
    	System.out.print("Digite sua senha: ");
    	String stringSenha = s.nextLine();

		try
		{
			PrintStream saida = new PrintStream(this.servidor.getOutputStream(), true);
			Scanner s2 = new Scanner(this.servidor.getInputStream());
			saida.println("login");
			saida.println(id);
			saida.println(stringSenha);

			String resp = s2.nextLine();
			clearScreen();
			if(!resp.equals("Usuario inexistente!") && !resp.equals("Senha invalida!"))
			{
				System.out.println("Bem vindo " + resp + "!");
				currentNome = (String)resp;
				currentId = id;
			}
			else
				System.out.println(resp);
		}
		catch(Exception e) {e.printStackTrace();}
	}

	public void Menu_User()
	{
		Scanner s = new Scanner(System.in);
		int opcao2 = -1;

		while (opcao2 != 4)
		{
			System.out.print(
        		"\n\n\n .: Menu Usuario:.\t\t[" + currentNome + "]\n\n" +
		        "1 - Listar Produtos\n" +
        		"2 - Comprar Produtos\n" +
        		"3 - Avise-me quando disponivel\n" +
        		"4 - Sair da conta\n" +
		        "Opção: ");
      		opcao2 = s.nextInt();

      		switch (opcao2) 
      		{
	        	case 1:
	        		clearScreen();
	        		Listar_Produtos();
	          		break;
	        	case 2:
	        		Comprar_Produtos();
	          		break;
	          	case 3:
	          		requisitarProduto();
	          		break;
	          	case 4:
	          		logOff();
	          		break;
	          	default:
	          		clearScreen();
	          		break;
        	}
		}
	}

	public void Listar_Produtos()
	{
		/*--Recebe mensagem do cliente (comandos)--*/
		try
		{
			Scanner s = new Scanner(this.servidor.getInputStream());
			PrintStream saida = new PrintStream(this.servidor.getOutputStream());

			saida.println("listarP");

			String str = s.nextLine();
			while(!str.equals("fim") && s.hasNext())
			{
				System.out.println(str);
				str = s.nextLine();
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	public void Comprar_Produtos()
	{
		Scanner s = new Scanner(System.in);
		
		System.out.print("Digite o codigo do produto: ");
    	int codigo = s.nextInt();
    	System.out.print("Digite a quantidade desejada: ");
    	int quantidade = s.nextInt();

    	try
    	{
			Scanner s2 = new Scanner(this.servidor.getInputStream());
			PrintStream saida = new PrintStream(this.servidor.getOutputStream());
    	
	    	saida.println("compra");
	    	saida.println(codigo);
	    	saida.println(quantidade);
	    	
	    	String in = s2.nextLine();
	    	clearScreen();
	    	System.out.println(in);
    	}
    	catch(Exception e) {e.printStackTrace();}
	}

	public void requisitarProduto()
	{
		try
		{
			Scanner s = new Scanner(System.in);
			Scanner s2 = new Scanner(this.servidor.getInputStream());
			PrintStream saida = new PrintStream(this.servidor.getOutputStream());

			System.out.print("Digite o codigo do produto: ");
    		int codigo = s.nextInt();

			saida.println("requisitar");
			saida.println(currentId);
			saida.println(codigo);

	    	String in = s2.nextLine();
	    	System.out.println(in);
		}	
		catch(Exception e) {e.printStackTrace();}	
	}

	static void clearScreen()
	{
		final String ANSI_CLS = "\u001b[2J";
        final String ANSI_HOME = "\u001b[H";
        System.out.print(ANSI_CLS + ANSI_HOME);
        System.out.flush();
	}

	public void logOff()
	{
		currentNome = "Visitante";
		clearScreen();
	}

	public static boolean isValidEmailAddress(String email)
	{
		boolean result = true;
		try
		{
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		}
		catch (AddressException ex)
		{
			result = false;
		}
		return(result);
	}
}