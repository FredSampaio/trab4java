package br.usp.icmc.supermercado.aplicacao;

import br.usp.icmc.supermercado.user.User;
import br.usp.icmc.supermercado.product.Product;
import br.usp.icmc.supermercado.sendmail.SendMail;
import br.usp.icmc.supermercado.compra.Compra;
import br.usp.icmc.supermercado.requisitado.Requisitado;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.mail.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class Servidor
{
	private int porta;						//porta do servidor
	private ServerSocket servidor;			//servidor
	ArrayList<User> usuarios;				//lista de usuarios
	ArrayList<Product> produtos;			//lista de produtos
	ArrayList<Requisitado> requisitados;	//lista de produtos requisitados
	ArrayList<Compra> compras;				//lista de compras efetuadas
	private Date dataAtual;					//data atual que o sistema foi aberto

	public Servidor(int porta)
	{
		this.porta = porta;
		this.usuarios = new ArrayList<User>();
		this.produtos = new ArrayList<Product>();
		this.requisitados = new ArrayList<Requisitado>();
		this.compras = new ArrayList<Compra>();
		this.dataAtual = new Date();
	}

	public void executa() throws UnknownHostException, IOException
	{
		carregaDados();			//carrega dados dos csv

		Runnable wait = () ->
		{
            try
			{
				servidor = new ServerSocket(this.porta);
				while (true) 
				{
					//aceita um cliente
					Socket cliente = servidor.accept();

					//cria tratador de cliente numa nova thread
					TrataCliente tc = new TrataCliente(cliente, this);
					new Thread(tc).start();
				}
			}
			catch(Exception e) {}
        };
        new Thread(wait).start();

        Menu_Servidor();	//menu principal
	}

	//adiciona um usuario
	public boolean adicionaUsuario(User usuario)
	{
		if(!existeUsuario(usuario.getId()))
		{
			this.usuarios.add(usuario);
			return(true);
		}
		return(false);
	}

	//adiciona um produto(caso exita, apenas atualiza estoque)
	public void adicionaProdutos(Product produto)
	{
		if(!existeProduto(produto.getCodigo()))		//caso seja um novo produto
			this.produtos.add(produto);
		
		else										//caso ja exista
		{	
			getProduto(produto.getCodigo()).setQuantidade(										//atualzia quantidade
				getProduto(produto.getCodigo()).getQuantidade() + produto.getQuantidade());
			getProduto(produto.getCodigo()).setValidade(produto.getValidade());					//atualiza validade
			if(produto.getValidade().after(dataAtual))
				getProduto(produto.getCodigo()).setDisponivel(false);
			else
				getProduto(produto.getCodigo()).setDisponivel(true);							//atualiza disponibiliade
		}
	}

	//adiciona um produto requisitado
	public void adicionaRequisitado(Requisitado requisitado)
	{
		this.requisitados.add(requisitado);
	}

	//returna lista de produtos
	public ArrayList<Product> getProdutos()
	{
		return(this.produtos);
	}

	//retorna produto pelo codigo
	public Product getProduto(int codigo)
	{
		for(Product p : this.produtos)
		{
			if(p.getCodigo() == codigo)
				return(p);
		}
		return(null);		
	}

	//Menu principal
	public void Menu_Servidor()
	{
		Scanner s = new Scanner(System. in );
		int opcao = -1;

		while (opcao != 0)
		{
			System.out.print(
        		"\n\n\n .: Menu Servidor:.\n\n" +
		        "1 - Cadastrar novo produto\n" +
        		"2 - Listar Produtos\n" +
        		"3 - Atualizar Estoque\n" +
        		"4 - Gerar Relatorio\n" +
        		"0 - Sair do programa\n\n" +
		        "Opção: ");
      		opcao = s.nextInt();

      		switch (opcao) 
      		{
	        	case 1:
	        		Cadastrar_Produto();
	          		break;
	        	case 2:
	        		Lista_Produto(System.out);
	          		break;
	          	case 3:
	          		if(Atualizar_Produtos())
	          		{
	          			clearScreen();
	          			System.out.println("Produto Atualizado com sucesso!");	          			
	          		}

	          		else
	          		{
	          			clearScreen();
	          			System.out.println("Produto inexistente!");
	          		}
	          		break;
	          		case 4:
	          			Menu_Relatorio();
	          			break;
	        	case 0:
	        		salvaDados();
	        		
	        		try{servidor.close();}
	        		catch(Exception e){}
	        		System.out.println("Aguardando fechamento dos clientes....");
	        		break;
        	}
		}
	}

	public boolean Cadastrar_Produto()
	{
		int i;
		Scanner s = new Scanner(System. in );
    	System.out.print("Digite o nome do produto: ");
    	String stringNome = s.nextLine();
    	System.out.print("Digite o preco do produto: ");
    	float preco = s.nextFloat();
    	s.nextLine();
    	System.out.print("Digite a validade: ");
    	String val = s.nextLine();
    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    	Date validade = new Date();
    	try {validade = df.parse(val);}
    	catch(Exception e) {e.printStackTrace();}
    	System.out.print("Digite o fornecedor do produto: ");
    	String stringFornecedor = s.nextLine();
    	System.out.print("Digite a quantidade do produto: ");
    	int quantidade = s.nextInt();
    	System.out.print("Digite o codigo do produto: ");
    	int codigo = s.nextInt();

    	Product p = new Product(stringNome, preco, validade, stringFornecedor, quantidade, codigo, true);

    	adicionaProdutos(p);
    	if(vencido(p))
    		p.setDisponivel(false);
    	
    	checkRequisitado(p);	//checa de alguem aguardava por este produto
    	return(true);

	}

	//lista todos os produtos
	public void Lista_Produto(PrintStream saida)
	{
		int i;

		if(produtos.size() > 0)
		{
			saida.println("\n=============================================" +
			 "Produtos============================================");
			saida.println("-------------------------------------------" +
				"------------------------------------------------------");
			saida.format(
				"|%-25s |%-9s |%-10s |%-15s |%-4s |%-10s |%-11s",
				"Produto",
				"Preco",
				"Validade",
				"Fornecedor",
				"Qtde",
				"Codigo",
				"Status"
			);
			saida.println("\n------------------------------------------" +
				"-------------------------------------------------------");

			String status;
			for (Product p : produtos) 
			{
				if (p.getQuantidade() == 0) 
					status = "SEM ESTOQUE";
				else if (vencido(p))
					status = "VENCIDO";
				else
					status = "DISPONIVEL";

				saida.format(
						"|%-25s |R$ %-6s |%-10s |%-15s |%-4s |%-10s |%-11s%n",
						p.getNome(),
						p.getPreco(),
						new SimpleDateFormat("dd/MM/yyyy").format(p.getValidade()),
						p.getFornecedor(),
						p.getQuantidade(),
						p.getCodigo(),
						status
					);
			}
		}
		else
			saida.println("Nao ha produtos cadastrados.");	
	}

	//atualiza estoque de produto
	public boolean Atualizar_Produtos()
	{
		int i;
		Scanner s = new Scanner(System.in);
		System.out.print("Digite o codigo do produto: ");
    	int codigo = s.nextInt();

    	if(!existeProduto(codigo))
    		return(false);
		
		System.out.print("Digite a nova validade: ");
    	s.nextLine();
    	String val = s.nextLine();
    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    	Date validade = new Date();
    	try {validade = df.parse(val);}
    	catch(Exception e) {e.printStackTrace();}
    	
    	System.out.print("Digite a quantidade do produto a ser estocada: ");
    	int quantidade = s.nextInt();

    	for (Product p : produtos)
    	{
    		if (p.getCodigo() == codigo)
    		{
    			p.setQuantidade(p.getQuantidade() + quantidade);
    			p.setValidade(validade);
    			if(p.getValidade().after(dataAtual))
    				p.setDisponivel(true);
    			else
    				p.setDisponivel(false);

    			checkRequisitado(p);
    			return(true);
    		}
    	}
    	return false;
	}

	//menu de gerar relatorios
	public void Menu_Relatorio()
	{
		Scanner s = new Scanner(System.in);
		int opcao2 = -1;

		System.out.print(
    		"\n\n .: Relatorio:.\n\n" +
	        "Digite 0 para relatorio diario ou o numero do mes para relatorio mensal: ");
  		opcao2 = s.nextInt();
  		while(opcao2 > 12 || opcao2 < 0)
  		{
  			System.out.println("\nDigite uma opcao valida!");
  			System.out.print("\nDigite 0 para relatorio diario ou o numero do mes para relatorio mensal: ");
  			opcao2 = s.nextInt();
  		}

  		if(opcao2 == 0)
  			geraRelatorio("diario",-1);		//relatorio diario
  		else
  			geraRelatorio("mensal",opcao2);	//relatorio mensa

	}

	public boolean vencido(Product p)
	{
		return(dataAtual.after(p.getValidade()));
	}

	public boolean existeUsuario(int id)
	{
		for(User u : this.usuarios)
		{
			if(u.getId() == id)
				return(true);
		}
		return(false);
	}

	public boolean existeProduto(int codigo)
	{
		for(Product p : this.produtos)
		{
			if(p.getCodigo() == codigo)
				return(true);
		}
		return(false);
	}

	public User getUsuario(int id)
	{
		for(User u : this.usuarios)
		{
			if(u.getId() == id)
				return(u);
		}
		return(null);
	}

	public boolean checkSenha(User u, String senha)
	{
		if(u.getSenha().equals(senha))
			return(true);
		return(false);
	}

	public int efetuaCompra(int codigo, int quantidade)
	{
		int i;
		//percorre a Arraylist inteira de produtos
		for (i = 0; i < produtos.size(); i++) 
		{
			//se o codigo for igual ao digitado pelo usuario
			if (produtos.get(i).getCodigo() == codigo) 
			{
				//e se a quantidade for maio que a digitada pelo usuario
				if (produtos.get(i).getQuantidade() >= quantidade) 
				{
					//seto a a nova quantidade do produto e retorno
					produtos.get(i).setQuantidade(produtos.get(i).getQuantidade() - quantidade);
					//adiciona as compras
					compras.add(new Compra(getProduto(codigo), quantidade, dataAtual));
					
					if(produtos.get(i).getQuantidade() == 0)
						produtos.get(i).setDisponivel(false);
					return 1;
				}
				else
					return 3;
			}
		}
		return 2;
	}

	public void checkRequisitado(Product p)
	{
		for(int i=0; i<requisitados.size(); i++)
		{
			if(requisitados.get(i).getProduto() == p && p.getDisponivel())
			{
				enviaEmail(requisitados.get(i));
				requisitados.remove(requisitados.get(i));
			}
		}
	}

	public void enviaEmail(Requisitado r)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				new SendMail(r).sendTo();
			}
		})
		.start();
	}

	public void carregaDados()
	{
		try
		{
			String strU, strP, strR, strC;

			FileReader fileReaderU = new FileReader("csv/usuarios.csv");
			BufferedReader usuarios_csv = new BufferedReader(fileReaderU);
			while ((strU = usuarios_csv.readLine()) != null)
			{
				String[] values = strU.split(",");
				adicionaUsuario
				(
					new User(values[0], 
					values[1], 
					values[2], 
					values[3], 
					Integer.parseInt(values[4]), 
					values[5])
				);
			}
			fileReaderU.close();

			FileReader fileReaderP = new FileReader("csv/produtos.csv");
			BufferedReader produtos_csv = new BufferedReader(fileReaderP);
			while ((strP = produtos_csv.readLine()) != null)
			{
				String[] values = strP.split(",");
				adicionaProdutos
				(
					new Product(values[0], 
					Float.parseFloat(values[1]), 
					new SimpleDateFormat("dd/MM/yyyy").parse(values[2]), 
					values[3], 
					Integer.parseInt(values[4]),
					Integer.parseInt(values[5]),
					Boolean.parseBoolean(values[6]))
				);
			}
			fileReaderP.close();	

			FileReader fileReaderR = new FileReader("csv/requisitados.csv");
			BufferedReader requisitados_csv = new BufferedReader(fileReaderR);			
			while ((strR = requisitados_csv.readLine()) != null)
			{
				String[] values = strR.split(",");
				adicionaRequisitado(new Requisitado(getProduto(Integer.parseInt(values[0])), getUsuario(Integer.parseInt(values[1]))));
			}
			fileReaderR.close();

			FileReader fileReaderC = new FileReader("csv/compras.csv");
			BufferedReader compras_csv = new BufferedReader(fileReaderC);
			while ((strC = compras_csv.readLine()) != null)
			{
				String[] values = strC.split(",");
				compras.add(new Compra(getProduto(Integer.parseInt(values[0])),
					Integer.parseInt(values[1]), 
					new SimpleDateFormat("dd/MM/yyyy").parse(values[2])));
			}
			fileReaderC.close();

		}
		catch(FileNotFoundException e){}
		catch(IOException e){}
		catch(ParseException e) {}
	}

	public void salvaDados()
	{
		try
		{
			FileWriter writerU = new FileWriter("csv/usuarios.csv");
			for(User u : usuarios)
			{
				writerU.append(u.getNome());
				writerU.append(",");
				writerU.append(u.getEndereco());
				writerU.append(",");
				writerU.append(u.getTelefone());
				writerU.append(",");
				writerU.append(u.getEmail());
				writerU.append(",");			
				writerU.append(String.valueOf(u.getId()));
				writerU.append(",");
				writerU.append(u.getSenha());
				writerU.append("\n");
			}
			writerU.close();

			FileWriter writerP = new FileWriter("csv/produtos.csv");			
			for(Product p : produtos)
			{
				writerP.append(p.getNome());
				writerP.append(",");
				writerP.append(String.valueOf(p.getPreco()));
				writerP.append(",");
				writerP.append(new SimpleDateFormat("dd/MM/yyyy").format(p.getValidade()));
				writerP.append(",");
				writerP.append(p.getFornecedor());
				writerP.append(",");
				writerP.append(String.valueOf(p.getQuantidade()));
				writerP.append(",");
				writerP.append(String.valueOf(p.getCodigo()));
				writerP.append(",");
				writerP.append(String.valueOf(p.getDisponivel()));
				writerP.append("\n");
			}
			writerP.close();

			FileWriter writerR = new FileWriter("csv/requisitados.csv");			
			for(Requisitado r : requisitados)
			{
				writerR.append(String.valueOf(r.getProduto().getCodigo()));
				writerR.append(",");
				writerR.append(String.valueOf(r.getUsuario().getId()));
				writerR.append("\n");
			}
			writerR.close();

			FileWriter writerC = new FileWriter("csv/compras.csv");			
			for(Compra c : compras)
			{
				writerC.append(String.valueOf(c.getProduto().getCodigo()));
				writerC.append(",");
				writerC.append(String.valueOf(c.getQuantidade()));
				writerC.append(",");
				writerC.append(String.valueOf(new SimpleDateFormat("dd/MM/yyyy").format(c.getData())));
				writerC.append("\n");
			}
			writerC.close();
		}
		catch(Exception e) {}
	}

	public void geraRelatorio(String flag, int mes)
	{
		try
		{
			Document document = new Document();
			if(flag.equals("diario"))
			{
				PdfWriter.getInstance(document, new FileOutputStream("relatorios/relatorio_diario.pdf"));
				document.open();

				Paragraph titulo = new Paragraph();
			    titulo.add(new Paragraph(" "));
			    titulo.add(new Paragraph("Relatorio Diario", 
			    	new Font(Font.FontFamily.TIMES_ROMAN, 25, Font.BOLD)));

				titulo.add(new Paragraph(" "));
				titulo.add(new Paragraph(" "));

				document.add(titulo);

				PdfPTable table = new PdfPTable(4);

				PdfPCell c1 = new PdfPCell(new Phrase("Produto"));
	    		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    		table.addCell(c1);
				
	    		PdfPCell c2 = new PdfPCell(new Phrase("Quantidade"));
			    c2.setHorizontalAlignment(Element.ALIGN_CENTER);
			    table.addCell(c2);

			    PdfPCell c3 = new PdfPCell(new Phrase("Valor unit."));
			    c3.setHorizontalAlignment(Element.ALIGN_CENTER);
			    table.addCell(c3);

			    PdfPCell c4 = new PdfPCell(new Phrase("Valor total"));
			    c4.setHorizontalAlignment(Element.ALIGN_CENTER);
			    table.addCell(c4);
			    float total = 0;
			    for(Compra c : compras)
			    {
			    	table.addCell(c.getProduto().getNome());
			    	table.addCell(String.valueOf(c.getQuantidade()));
			    	table.addCell(String.valueOf("R$ " + c.getProduto().getPreco()));
			    	table.addCell(String.valueOf("R$ " + c.getProduto().getPreco()*(float)c.getQuantidade()));
			    	
			    	total = total + c.getProduto().getPreco()*c.getQuantidade();
			    }
			    table.addCell("TOTAL");
			    table.addCell("-----");
			    table.addCell("-----");
			    table.addCell(String.valueOf("R$ " + total));

			    document.add(table);
			}

			else if(flag.equals("mensal"))
			{
				float[] dias = new float[31];
				for(int i=0; i<31; i++)
					dias[i] = 0;

				PdfWriter.getInstance(document, new FileOutputStream("relatorios/relatorio_mes_0"+mes+".pdf"));
				document.open();
				
				Paragraph titulo = new Paragraph();
			    titulo.add(new Paragraph(" "));
			    titulo.add(new Paragraph("Relatorio Mensal. Mes: " + mes, 
			    	new Font(Font.FontFamily.TIMES_ROMAN, 25, Font.BOLD)));

				titulo.add(new Paragraph(" "));
				titulo.add(new Paragraph(" "));

				document.add(titulo);

				PdfPTable table = new PdfPTable(2);

				PdfPCell c1 = new PdfPCell(new Phrase("Dia"));
	    		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    		table.addCell(c1);
				
	    		PdfPCell c2 = new PdfPCell(new Phrase("Valor"));
			    c2.setHorizontalAlignment(Element.ALIGN_CENTER);
			    table.addCell(c2);

			    float totalMes = 0;
			    for(Compra c : compras)
			    	if(c.getMes() == mes)
			    		dias[c.getDia()-1] = dias[c.getDia()-1] + (c.getProduto().getPreco()*(float)c.getQuantidade());
			    
			    for(int i=0; i<31; i++)
			    {
			    	table.addCell(String.valueOf(i+1));
			    	table.addCell(String.valueOf("R$ " + dias[i]));

			    	totalMes = totalMes + dias[i];
			    }

			    table.addCell("TOTAL");
			    table.addCell(String.valueOf("R$ " + totalMes));

			    document.add(table);			
			}			
		
			document.close();
		}
		catch(Exception e){}
	}

	static void clearScreen()
	{
		final String ANSI_CLS = "\u001b[2J";
        final String ANSI_HOME = "\u001b[H";
        System.out.print(ANSI_CLS + ANSI_HOME);
        System.out.flush();
	}

}
