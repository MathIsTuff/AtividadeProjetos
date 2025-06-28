import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        NewsManager manager = new NewsManager();
        User user = new User();

        System.out.print("Digite seu nome ou apelido: ");
        user.name = scanner.nextLine();
        System.out.println("Bem-vindo, " + user.name + "!");

        manager.loadData();

        boolean running = true;
        while (running) {
            System.out.println("\n--- MENU ---");
            System.out.println("[1] Buscar notícias");
            System.out.println("[2] Ver lista de favoritos");
            System.out.println("[3] Ver lista de lidas");
            System.out.println("[4] Ver lista de para ler depois");
            System.out.println("[0] Sair");
            System.out.print("Escolha: ");
            int option = Integer.parseInt(scanner.nextLine());

            switch (option) {
                case 1 -> manager.searchNews(scanner);
                case 2 -> manager.showList(manager.favorites, scanner, "Favoritas");
                case 3 -> manager.showList(manager.readLater, scanner, "Lidas");
                case 4 -> manager.showList(manager.toReadLater, scanner, "Para Ler Depois");
                case 0 -> {
                    manager.saveData();
                    running = false;
                    System.out.println("Até mais, " + user.name + "!");
                }
                default -> System.out.println("Opção inválida.");
            }
        }

        scanner.close();
    }
}
