Trabalho 4 – Supermercado Online

-PARA COMPILAR:
  make
  ou
  javac -cp dependencies/\* -d . *.java

-PARA EXECUTAR:
   make run
   ou
   java -cp ":dependencies/mail.jar:dependencies/activation.jar:dependencies/itextpdf-5.5.6.jar" br.usp.icmc.supermercado.Main

OBS: O programa é melhor vizualizado em tela maximizada.(Opcional)

-PONTOS EXTRAS:
  -Relatorios diario e mensal em PDF: iText
  -Envio de e-mails: JavaMail API
  -Design pattern: Observer (class Requisitado). 
	   Descrição: todos os clientes que desejam um produto que não esta mais disponivel(fora de estoque ou vencido), são alertados quando este produto é atualizado no sistema.