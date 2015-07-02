all:
	javac -cp dependencies/\* -d . *.java
run:
	java -cp ":dependencies/mail.jar:dependencies/activation.jar:dependencies/itextpdf-5.5.6.jar" br.usp.icmc.supermercado.Main
