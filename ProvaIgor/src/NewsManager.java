import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class NewsManager {
    private final List<News> favorites = new ArrayList<>();
    private final List<News> readLater = new ArrayList<>();
    private final List<News> readNews = new ArrayList<>();
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final String FAVORITES_FILE = "favorites.json";
    private final String READLATER_FILE = "readlater.json";
    private final String READ_FILE = "read.json";

    public NewsManager() {
        loadLists();
    }

    // Busca notícias pela API do IBGE, filtrando por modo
    public List<News> searchNews(String query, String mode) throws Exception {
    String url = "https://servicodados.ibge.gov.br/api/v3/noticias/?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
        ApiResponse apiResponse = gson.fromJson(response.body(), ApiResponse.class);
        if (apiResponse == null || apiResponse.items == null) {
            System.out.println("Nenhuma notícia encontrada.");
            return Collections.emptyList();
        }

        List<News> results = apiResponse.items;

        if (mode.equalsIgnoreCase("date")) {
            results = results.stream()
                    .filter(n -> n.publicationDate != null && n.publicationDate.contains(query))
                    .collect(Collectors.toList());
        } else if (mode.equalsIgnoreCase("keyword")) {
            results = results.stream()
                    .filter(n -> (n.title != null && n.title.toLowerCase().contains(query.toLowerCase())) ||
                                 (n.intro != null && n.intro.toLowerCase().contains(query.toLowerCase())))
                    .collect(Collectors.toList());
        } else { // título
            results = results.stream()
                    .filter(n -> n.title != null && n.title.toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return results;
    } else {
        System.out.println("Erro ao buscar notícias: HTTP " + response.statusCode());
        return Collections.emptyList();
    }
}


    public void addFavorite(News news) {
        if (!favorites.contains(news)) {
            favorites.add(news);
            saveList(FAVORITES_FILE, favorites);
            System.out.println("Adicionado aos favoritos!");
        } else {
            System.out.println("Já está nos favoritos.");
        }
    }

    public void markAsRead(News news) {
        if (!readNews.contains(news)) {
            readNews.add(news);
            saveList(READ_FILE, readNews);
            System.out.println("Marcado como lido!");
        } else {
            System.out.println("Já estava marcado como lido.");
        }
    }

    public void addReadLater(News news) {
        if (!readLater.contains(news)) {
            readLater.add(news);
            saveList(READLATER_FILE, readLater);
            System.out.println("Adicionado para ler depois!");
        } else {
            System.out.println("Já estava na lista.");
        }
    }

    public void showList(String listName) {
        List<News> list;
        switch (listName) {
            case "favorites" -> list = favorites;
            case "readlater" -> list = readLater;
            case "read" -> list = readNews;
            default -> {
                System.out.println("Lista inválida.");
                return;
            }
        }

        if (list.isEmpty()) {
            System.out.println("Lista vazia.");
            return;
        }

        list.forEach(this::printNews);
    }

    public void sortList(String listName, String sortBy) {
        List<News> list;
        switch (listName) {
            case "favorites" -> list = favorites;
            case "readlater" -> list = readLater;
            case "read" -> list = readNews;
            default -> {
                System.out.println("Lista inválida.");
                return;
            }
        }

        switch (sortBy) {
            case "title" -> list.sort(Comparator.comparing(n -> n.title != null ? n.title : ""));
            case "date" -> list.sort(Comparator.comparing(n -> n.publicationDate != null ? n.publicationDate : ""));
            case "type" -> list.sort(Comparator.comparing(n -> n.type != null ? n.type : ""));
            default -> System.out.println("Ordenação inválida.");
        }

        list.forEach(this::printNews);
    }

    public void printNews(News news) {
        System.out.println("[" + news.id + "] " + news.title);
        System.out.println("Introdução: " + news.intro);
        System.out.println("Data: " + news.publicationDate);
        System.out.println("Link: " + news.link);
        System.out.println("Tipo: " + news.type);
        System.out.println("------------------------------");
    }

    private void saveList(String fileName, List<News> list) {
        try (Writer writer = new FileWriter(fileName)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            System.out.println("Erro ao salvar lista: " + e.getMessage());
        }
    }

    private void loadLists() {
        favorites.addAll(loadList(FAVORITES_FILE));
        readLater.addAll(loadList(READLATER_FILE));
        readNews.addAll(loadList(READ_FILE));
    }

    private List<News> loadList(String fileName) {
        if (!Files.exists(Paths.get(fileName))) return new ArrayList<>();
        try (Reader reader = new FileReader(fileName)) {
            Type listType = new TypeToken<List<News>>(){}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            System.out.println("Erro ao carregar lista " + fileName);
            return new ArrayList<>();
        }
    }


    private static class ApiResponse {
        List<News> items;
    }
}
