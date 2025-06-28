import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        NewsManager manager = new NewsManager();

        System.out.print("Digite seu nome ou apelido: ");
        String username = scanner.nextLine();
        System.out.println("\nOlá, " + username + "! Bem-vindo ao Blog IBGE.\n");

        boolean running = true;

        while (running) {
            System.out.println("\nEscolha uma opção:");
            System.out.println("[1] Buscar notícia por título");
            System.out.println("[2] Buscar notícia por palavra-chave");
            System.out.println("[3] Buscar notícia por data (AAAA-MM-DD)");
            System.out.println("[4] Ver lista de favoritos");
            System.out.println("[5] Ver lista 'ler depois'");
            System.out.println("[6] Ver lista de lidas");
            System.out.println("[7] Sair");
            System.out.print("Sua escolha: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1", "2", "3" -> {
                    System.out.print("Digite sua busca: ");
                    String query = scanner.nextLine();

                    String mode = input.equals("1") ? "title" :
                                  input.equals("2") ? "keyword" : "date";

                    List<News> results = manager.searchNews(query, mode);

                    if (results.isEmpty()) {
                        System.out.println("Nenhuma notícia encontrada.");
                    } else {
                        for (int i = 0; i < results.size(); i++) {
                            System.out.println("[" + (i + 1) + "] " + results.get(i).title);
                        }

                        System.out.print("Escolha o número de uma notícia para gerenciar ou 0 para voltar: ");
                        int choice = Integer.parseInt(scanner.nextLine());

                        if (choice > 0 && choice <= results.size()) {
                            News selected = results.get(choice - 1);
                            manager.printNews(selected);

                            System.out.println("\nO que deseja fazer?");
                            System.out.println("[1] Adicionar aos favoritos");
                            System.out.println("[2] Marcar como lida");
                            System.out.println("[3] Adicionar na lista 'ler depois'");
                            System.out.println("[0] Voltar");
                            String act = scanner.nextLine();

                            switch (act) {
                                case "1" -> manager.addFavorite(selected);
                                case "2" -> manager.markAsRead(selected);
                                case "3" -> manager.addReadLater(selected);
                                default -> System.out.println("Voltando ao menu.");
                            }
                        }
                    }
                }
                case "4" -> {
                    System.out.println("\n--- FAVORITOS ---");
                    manager.showList("favorites");
                }
                case "5" -> {
                    System.out.println("\n--- LER DEPOIS ---");
                    manager.showList("readlater");
                }
                case "6" -> {
                    System.out.println("\n--- LIDAS ---");
                    manager.showList("read");
                }
                case "7" -> {
                    System.out.println("Saindo... Até logo, " + username + "!");
                    running = false;
                }
                default -> System.out.println("Opção inválida. Tente de novo.");
            }
        }
        scanner.close();
    }
}
