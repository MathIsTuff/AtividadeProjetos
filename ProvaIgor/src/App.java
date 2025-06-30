import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        NewsManager manager = new NewsManager();

        UserManager userManager = new UserManager();
        String username = userManager.getUsername();
        System.out.println("Usuário atual: " + username);


        boolean running = true;

        while (running) {
            System.out.println("\nEscolha uma opção:");
            System.out.println("[1] Buscar notícia por título");
            System.out.println("[2] Buscar notícia por palavra-chave");
            System.out.println("[3] Buscar notícia por data (DD/MM/AAAA)");
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
                    ordenarPrompt(scanner, manager, "favorites");
                }
                case "5" -> {
                    System.out.println("\n--- LER DEPOIS ---");
                    manager.showList("readlater");
                    ordenarPrompt(scanner, manager, "readlater");
                }
                case "6" -> {
                    System.out.println("\n--- LIDAS ---");
                    manager.showList("read");
                    ordenarPrompt(scanner, manager, "read");
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
         private static void ordenarPrompt(Scanner scanner, NewsManager manager, String listName) {
    System.out.println("Deseja ordenar essa lista? [s/n]");
    String resposta = scanner.nextLine();
    if (resposta.equalsIgnoreCase("s")) {
        System.out.println("Escolha o critério:");
        System.out.println("[1] Título (A-Z)");
        System.out.println("[2] Data de publicação");
        System.out.println("[3] Tipo/categoria");
        String criterio = scanner.nextLine();
        String sortBy = switch (criterio) {
            case "1" -> "title";
            case "2" -> "date";
            case "3" -> "type";
            default -> "";
        };
        if (!sortBy.isEmpty()) {
            manager.sortList(listName, sortBy);
        } else {
            System.out.println("Critério inválido.");
        }
    }
}
}
