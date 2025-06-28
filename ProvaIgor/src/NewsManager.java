import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class NewsManager {
    List<News> favorites = new ArrayList<>();
    List<News> readLater = new ArrayList<>();
    List<News> toReadLater = new ArrayList<>();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    HttpClient client = HttpClient.newHttpClient();

    public void searchNews(Scanner scanner) {
        try {
            System.out.print("Digite uma palavra-chave para buscar: ");
            String keyword = scanner.nextLine();
            String url = "https://servicodados.ibge.gov.br/api/v3/noticias/?q=" + keyword;

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ApiResponse apiResponse = gson.fromJson(response.body(), ApiResponse.class);

            if (apiResponse.items.isEmpty()) {
                System.out.println("Nenhuma notícia encontrada.");
                return;
            }

            int i = 1;
            for (News news : apiResponse.items) {
                System.out.println("\n[" + i + "] " + news.title);
                System.out.println("Introdução: " + news.intro);
                System.out.println("Data: " + news.publicationDate);
                System.out.println("Link: " + news.link);
                System.out.println("Tipo: " + news.type);
                i++;
            }

            System.out.print("\nDigite o número da notícia para gerenciar ou 0 para voltar: ");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice > 0 && choice <= apiResponse.items.size()) {
                News selected = apiResponse.items.get(choice - 1);
                handleNewsOptions(selected, scanner);
            }

        } catch (Exception e) {
            System.out.println("Erro ao buscar notícias: " + e.getMessage());
        }
    }

    private void handleNewsOptions(News news, Scanner scanner) {
        boolean managing = true;
        while (managing) {
            System.out.println("\n[1] Adicionar aos Favoritos");
            System.out.println("[2] Marcar como Lida");
            System.out.println("[3] Adicionar para Ler Depois");
            System.out.println("[0] Voltar");
            System.out.print("Escolha: ");
            int option = Integer.parseInt(scanner.nextLine());

            switch (option) {
                case 1 -> {
                    if (!favorites.contains(news)) {
                        favorites.add(news);
                        System.out.println("Adicionado aos favoritos.");
                    } else {
                        favorites.remove(news);
                        System.out.println("Removido dos favoritos.");
                    }
                }
                case 2 -> {
                    if (!readLater.contains(news)) {
                        readLater.add(news);
                        System.out.println("Marcado como lida.");
                    } else {
                        readLater.remove(news);
                        System.out.println("Removido da lista de lidas.");
                    }
                }
                case 3 -> {
                    if (!toReadLater.contains(news)) {
                        toReadLater.add(news);
                        System.out.println("Adicionado para ler depois.");
                    } else {
                        toReadLater.remove(news);
                        System.out.println("Removido da lista para ler depois.");
                    }
                }
                case 0 -> managing = false;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    public void showList(List<News> list, Scanner scanner, String listName) {
        if (list.isEmpty()) {
            System.out.println("Lista " + listName + " está vazia.");
            return;
        }

        list.sort(Comparator.comparing(n -> n.title));

        int i = 1;
        for (News news : list) {
            System.out.println("\n[" + i + "] " + news.title);
            System.out.println("Introdução: " + news.intro);
            System.out.println("Data: " + news.publicationDate);
            System.out.println("Link: " + news.link);
            System.out.println("Tipo: " + news.type);
            i++;
        }
    }

    public void saveData() {
        try (FileWriter writer = new FileWriter("data.json")) {
            Map<String, List<News>> data = new HashMap<>();
            data.put("favorites", favorites);
            data.put("readLater", readLater);
            data.put("toReadLater", toReadLater);
            gson.toJson(data, writer);
            System.out.println("Dados salvos com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    public void loadData() {
        try (FileReader reader = new FileReader("data.json")) {
            Type type = new TypeToken<Map<String, List<News>>>() {}.getType();
            Map<String, List<News>> data = gson.fromJson(reader, type);
            favorites = data.getOrDefault("favorites", new ArrayList<>());
            readLater = data.getOrDefault("readLater", new ArrayList<>());
            toReadLater = data.getOrDefault("toReadLater", new ArrayList<>());
            System.out.println("Dados carregados com sucesso.");
        } catch (Exception e) {
            System.out.println("Nenhum dado salvo encontrado, começando do zero.");
        }
    }
}
