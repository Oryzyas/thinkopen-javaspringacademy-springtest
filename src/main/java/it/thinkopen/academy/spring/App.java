package it.thinkopen.academy.spring;

import it.thinkopen.academy.spring.config.MainConfig;
import it.thinkopen.academy.spring.entity.ToDo;
import it.thinkopen.academy.spring.service.ToDoService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Hello world!
 *
 */
public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfig.class);
    private static final ToDoService toDoService = context.getBean(ToDoService.class);

    /*public static void main( String[] args ) {
        final LocalDateTime date = LocalDateTime.now();
        final long dateInMillis = Utils.toSeconds(date);

        ToDo toDo1 = new ToDo();
        toDo1.setTitle("TITOLO 1");
        toDo1.setContent("CONTENUTO 1");
        toDo1.setDate(dateInMillis);
        toDoService.create(toDo1);

        ToDo toDo2 = new ToDo();
        toDo2.setTitle("TITOLO 2");
        toDo2.setContent("CONTENUTO 2");
        toDo2.setDate(dateInMillis);
        toDo2.setExpiration(Utils.toSeconds(date.plusDays(1)));
        toDoService.create(toDo2);

        ToDo toDo3 = new ToDo();
        toDo3.setTitle("TITOLO 3");
        toDo3.setContent("CONTENUTO 3");
        toDo3.setDate(dateInMillis);
        toDo3.setExpiration(Utils.toSeconds(date.plusMonths(1)));
        toDoService.create(toDo3);

        toDoService.getAllExpired().stream().forEach(System.out::println);
        System.out.println();

        toDo2.setExpiration(null);
        toDo2.setContent("CONTENUTO NUOVO");
        toDoService.update(toDo2);

        toDoService.getAllExpired().stream().forEach(System.out::println);
        System.out.println();

        toDoService.delete(toDo2);
        toDoService.getAllExpired().stream().forEach(System.out::println);

        context.close();
    }*/

    public static void main(String[] args) {

        terminate:
        while(true) {
            int resp = runMenu("Benvenuto", "Risposta","Crea Nuovo TODO", "Mostra i TODO", "Esci");

            switch (resp) {
                case 1:
                    createToDo();
                    break;
                case 2:
                    showToDos();
                    break;
                case 3:
                    break terminate;
            }
        }

        context.close();
    }

    private static void createToDo() {
        final ToDo todo = new ToDo();
        final LocalDateTime now = LocalDateTime.now();

        todo.setTitle(read("Titolo", t -> t.length() > 0 && t.length() < 32, "Titolo non valido."));
        todo.setContent(read("Contenuto", t -> t.length() > 0 && t.length() < 128, "Contenuto non valido."));
        todo.setDate(Utils.toSeconds(now));
        todo.setExpiration(Utils.toSeconds(read("Scadenza", exp -> Utils.parseExpirationDate(now, exp),
                exp -> exp == null || now.isBefore(exp), "Data non valida.")));
        todo.setDone(false);

        toDoService.create(todo);

        showSuccess("TODO creato con successo: " + todo);
    }

    private static String chooseFilteredToDos() {
        int resp = runMenu("Lista Filtri", "Filtro", "A - Tutti", "D - Fatti", "E - Scaduti", "ED - Scaduti e Fatti", "<Indietro>");

        switch (resp) {
            case 1:
                return "A";
            case 2:
                return "D";
            case 3:
                return "E";
            case 4:
                return "ED";
        }

        return null;
    }

    private static void showToDos() {
        final String filter = chooseFilteredToDos();
        Boolean expired = null, done = null;

        switch (filter) {
            case "D":
                done = true;
                break;
            case "E":
                expired = true;
                break;
            case "ED":
                done = true;
                expired = true;
                break;
        }

        while(true) {
            final List<ToDo> todos = toDoService.getAll(expired, done);

            final List<Object> objs = new ArrayList<>();
            objs.addAll(todos);
            objs.add("<Indietro>");

            final int resp = runMenu("Tutti i TODO", "Seleziona", objs);

            if(resp > todos.size()) // Turn back
                break;

            showTodoActions(todos.get(resp - 1));
        }
    }

    private static void showTodoActions(final ToDo todo) {
        back:
        while(true) {
            final String markAsText = todo.isDone() ? "Segna come 'da fare'" : "Segna come 'già fatto'";
            final int resp = runMenu("[SELEZIONATO] " + todo, "Cosa vuoi fare?", "Modifica", "Elimina", markAsText, "<Indietro>");

            switch (resp) {
                case 1:
                    editToDo(todo);
                    break;
                case 2:
                    if(deleteToDo(todo))
                        break back;
                    break;
                case 3:
                    todo.setDone(!todo.isDone());
                    toDoService.update(todo);
                    break;
                case 4:
                    break back;
            }
        }
    }

    private static void editToDo(final ToDo todo) {
        back:
        while(true) {
            final int resp = runMenu("[MODIFICA] " + todo, "Cosa vuoi modificare?", "Titolo", "Contenuto", "Scadenza", "<Indietro>");

            switch (resp) {
                case 1:
                    todo.setTitle(read("Titolo", t -> t.length() > 0 && t.length() < 32, "Titolo non valido."));
                    break;
                case 2:
                    todo.setContent(read("Contenuto", t -> t.length() > 0 && t.length() < 128, "Contenuto non valido."));
                    break;
                case 3:
                    LocalDateTime now = Utils.toLocalDateTime(todo.getDate());
                    todo.setExpiration(Utils.toSeconds(read("Scadenza", exp -> Utils.parseExpirationDate(now, exp),
                            exp -> exp == null || now.isBefore(exp), "Data non valida.")));
                    break;
                case 4:
                    break back;
            }
        }

        toDoService.update(todo);
    }

    private static boolean deleteToDo(final ToDo todo) {
        boolean resp = read(String.format("Vuoi davvero eliminare il TODO '%s'? [true/false]", todo),
                Boolean::parseBoolean, r -> true, "Risposta non valida");

        if(resp) {
            toDoService.delete(todo);
            showSuccess("TODO eliminato con successo: " + todo);
            return true;
        }

        return false;
    }

    private static int runMenu(String title, String request, String ... menuItems) {
        final String menu = buildMenu(title, request, menuItems);
        return read(menu, Integer::parseInt, r -> r > 0 && r <= menuItems.length, "Risposta non valida.");
    }

    private static int runMenu(String title, String request, List<? extends Object> menuItems) {
        final String menu = buildMenu(title, request, menuItems);
        return read(menu, Integer::parseInt, r -> r > 0 && r <= menuItems.size(), "Risposta non valida.");
    }

    private static String buildMenu(String title, String request, String ... items) {
        return buildMenu(title, request, Arrays.asList(items));
    }

    private static String buildMenu(String title, String request, List<? extends Object> items) {
        final StringBuilder sb = new StringBuilder();

        sb.append(title);
        sb.append('\n');

        for(int i = 0; i < items.size(); ++i) {
            sb.append('\n');
            sb.append(i + 1);
            sb.append(" - ");
            sb.append(items.get(i));
            sb.append(" ;");
        }

        sb.append("\n\n");
        sb.append(request);

        return sb.toString();
    }

    private static String read(String msg, Predicate<String> test, String errMsg) {
        return read(msg, str -> str, test, errMsg);
    }

    private static <T> T read(String msg, Function<String, T> f, Predicate<T> test, String errMsg) {
        while (true) {
            try {
                System.out.print(msg + ": ");
                String input = scanner.nextLine();
                T t = f.apply(input);

                if (!test.test(t))
                    throw new Exception();

                System.out.println();

                return t;
            } catch (Exception e) {
                showError(errMsg);
            }

        }
    }

    private static void showSuccess(String msg) {
        System.out.println("\n\t[ SUCCESS ]: " + msg + "\n");
        scanner.nextLine();
    }

    private static void showError(String errMsg) {
        System.out.println("\n\t[ ERRORE ]: " + errMsg + "\n");
        scanner.nextLine();
    }
}
