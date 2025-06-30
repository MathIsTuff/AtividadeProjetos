import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserManager {
    private static final String USER_FILE = "user.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Scanner scanner = new Scanner(System.in);
    private List<User> users = new ArrayList<>();
    private User currentUser;

    public UserManager() {
        loadUsers();
        chooseOrCreateUser();
    }

    private void loadUsers() {
        File file = new File(USER_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(USER_FILE)) {
                Type listType = new TypeToken<List<User>>() {}.getType();
                users = gson.fromJson(reader, listType);
            } catch (IOException e) {
                System.out.println("Erro ao carregar usuários.");
                users = new ArrayList<>();
            }
        } else {
            users = new ArrayList<>();
        }
    }

    private void chooseOrCreateUser() {
        if (users.isEmpty()) {
            System.out.println("Nenhum usuário encontrado. Vamos criar um!");
            createUser();
        } else {
            System.out.println("Usuários salvos:");
            for (int i = 0; i < users.size(); i++) {
                System.out.println("[" + (i + 1) + "] " + users.get(i).getUsername());
            }
            System.out.print("Escolha o número do usuário ou 0 para criar novo: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // limpa o buffer

            if (choice > 0 && choice <= users.size()) {
                currentUser = users.get(choice - 1);
                System.out.println("\nBem-vindo de volta, " + currentUser.getUsername() + "!\n");
            } else {
                createUser();
            }
        }
    }

    private void createUser() {
        System.out.print("Digite seu nome ou apelido: ");
        String name = scanner.nextLine();
        currentUser = new User(name);
        users.add(currentUser);
        saveUsers();
        System.out.println("\nOlá, " + currentUser.getUsername() + "! Bem-vindo ao Blog IBGE.\n");
    }

    private void saveUsers() {
        try (Writer writer = new FileWriter(USER_FILE)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.out.println("Erro ao salvar usuários.");
        }
    }

    public String getUsername() {
        return currentUser.getUsername();
    }
}
