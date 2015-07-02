package br.usp.icmc.supermercado;

import br.usp.icmc.supermercado.aplicacao.Servidor;
import br.usp.icmc.supermercado.aplicacao.Cliente;
import br.usp.icmc.supermercado.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class Main
{
	public static void main(String[] args)
	{
		System.out.println("--------------Supermercado--------------");
		System.out.println("\nQual aplicacao voce deseja carregar?");
		System.out.println("1 - Cliente\n2 - Servidor");

		Scanner op = new Scanner(System.in);
		int opcao = op.nextInt();

		clearScreen();
		
		if(opcao == 1)
		{
			op.nextLine();
			System.out.println("\nDigite 0 se estiver na mesma maquina que o servidor(localhost) ou digite o ip do servidor: ");
			String resp = op.nextLine();
			if(resp.equals("0"))
				cliente("127.0.0.1");	
			else
				cliente(resp);				
		}

		if(opcao == 2)
			servidor();
	}

	static void cliente(String ip)
	{
		try{ new Cliente(ip, 8080).executa();}
		catch(Exception e){ e.printStackTrace();}
	}

	static void servidor()
	{
		try{ new Servidor(8080).executa();}
		catch(Exception e){ e.printStackTrace();}
	}

	static void clearScreen()
	{
		final String ANSI_CLS = "\u001b[2J";
        final String ANSI_HOME = "\u001b[H";
        System.out.print(ANSI_CLS + ANSI_HOME);
        System.out.flush();
	}
}