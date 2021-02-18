package animals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class Main {

    static String type = "json";
    static String fileName = "animals";

    public static void main(String[] args) {

//        Locale .setDefault(new Locale("eo"));
        String locale = Locale.getDefault().toString();
        if ("eo".equals(locale)) {
            fileName = fileName + "_" + locale;
        }
//        System.out.println(locale);
        ResourceBundle rb = ResourceBundle.getBundle("App");

        Animal.rb = rb;
        Conversation.rb = rb;
        Action.rb = rb;
        Knowledge.rb = rb;

        List<String> argList = Arrays.asList(args);
        Map<String, String> argMap = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            argMap.put(argList.get(i), argList.get(i + 1));
        }
        if (!argMap.isEmpty()) {
            if (argMap.get("-type") != null) {
                type = argMap.get("-type");
//                System.out.println(type);
            }
        }

        fileName += "." + type;

        DataBase db = new DataBase(type);

        Conversation cv = new Conversation();

//        cv.sayGreeting();
//        cv.say();

        Node root;

        if (ReadText.isExist(fileName)) {
            // cv.say("I know a lot about animals.");
            root = db.load(fileName);
            cv.say("Welcome to the animal expert system!");
        } else {
//            cv.say("I want to learn about animals.");
            cv.say("Which animal do you like most?");
            cv.say();
            Animal animal = new Animal(cv.hear());
            root = new Node(animal, null);
//            cv.say("Wonderful! I've learned so much about animals!");
//            cv.say();
            cv.say("Welcome to the animal expert system!2");
        }

//        cv.say();

        Action ac = new Action(cv, root);

        while (true) {
//           cv.say("What do you want to do:");
//           cv.say();
            cv.say("1. Play the guessing game");
            cv.say("2. List of all animals");
            cv.say("3. Search for an animal");
            cv.say("4. Calculate statistics");
            cv.say("5. Print the Knowledge Tree");
            cv.say("0. Exit");
            int menuno = cv.hearNumber();
            if (menuno == 0) {
                break;
            }

            switch (menuno) {
                case 1:
                    ac.playGame();
                    break;
                case 2:
                    ac.listAnimal();
                    break;
                case 3:
                    ac.searchAnimal();
                    break;
                case 4:
                    ac.calcStatistcs();
                    break;
                case 5:
                    ac.printTree();
            }
        }

        db.save(root, fileName);
        cv.sayGoodBye();
        cv.finish();
    }

}

class Action {

    static ResourceBundle rb;
    Conversation cv;
    Node root;

    Action(Conversation cv, Node root) {
        this.cv = cv;
        this.root = root;
    }

    void playGame() {
        while (true) {
            playOneGame();
            cv.say();
            cv.say("Would you like to play again?");
            if ("no".equals(cv.hearYesNo())) {
                break;
            }
        }
    }

    void playOneGame() {

        // cv.say("Let's play a game!");
        cv.say("You think of an animal, and I guess it.");
        cv.say("Press enter when you're ready.");
        cv.hear();

        Node node = root;
        while (!node.isLeaf()) {
            cv.sayS(cv.ask(node.getKnowledge().getStatement()));
            if ("no".equals(cv.hearYesNo())) {
                node = node.getLeft();
            } else {
                node = node.getRight();
            }
        }
        cv.sayS(cv.ask(node.getKnowledge().getStatement()));
        Animal animal1 = node.getKnowledge().getAnimal();
        if ("no".equals(cv.hearYesNo())) {
            cv.say("I give up. What animal do you have in mind?");
        } else {
            return;
        }

        Animal animal2 = new Animal(cv.hear());

        cv.sayS(((BiFunction<String, String, String>) rb.getObject("Specify a fact that distinguishes %s from %s.")).apply(animal1.getAName(), animal2.getAName()));
        cv.say("The sentence should satisfy one of the following templates:");
        cv.say("- It can ...");
        cv.say("- It has ...");
        cv.say("- It is a/an ...");
        cv.say();

        String difference = getDifference(animal1, animal2);
        cv.sayS(((Function<String, String>) rb.getObject("Is the statement correct for %s?")).apply(animal2.getAName()));
        String yesno = cv.hearYesNo();

        Knowledge knowledge = new Knowledge(cv, animal1, animal2, difference, yesno);
        cv.sayS(knowledge.getMessage());
        storeKnowledge(node, knowledge);
    }

    void storeKnowledge(Node node, Knowledge knowledge) {

        node.setKnowledge(knowledge);
        node.setRight(new Node(knowledge.getTrueAnimal(), node));
        node.setLeft(new Node(knowledge.getFalseAnimal(), node));
    }

    String getDifference(Animal animal1, Animal animal2) {

        String difference = "";
        boolean moreFlag = true;
        while (moreFlag) {

            difference = cv.hear();
            moreFlag = ((Function<String, Boolean>) rb.getObject("ac.getDifferenc.CheckAnswer")).apply(difference);

            if (moreFlag) {
                cv.sayS(((BiFunction<String, String, String>) rb.getObject("Specify a fact that distinguishes %s from %s.")).apply(animal1.getAName(), animal2.getAName()));
                cv.say("The sentence should be of the format: 'It can/has/is ...'.");
                cv.say("examples of a statement.");
                cv.say("It is a mammal");
                cv.say("It can barks");
                cv.say();
            }
        }

        return difference;
    }

    void listAnimal() {
        List<Animal> animals = new ArrayList<>();
        getAllAnimals(root, animals);
        cv.say("Here are the animals I know:");
        animals.stream().sorted((a1, a2) -> a1.getName().compareTo(a2.getName()))
                .forEach(a -> cv.sayS(" - " + a.getName()));
    }

    void getAllAnimals(Node node, List<Animal> animals) {

        if (node.isLeaf()) {
            animals.add(node.getKnowledge().getAnimal());
            return;
        }

        getAllAnimals(node.getLeft(), animals);
        getAllAnimals(node.getRight(), animals);
    }

    void searchAnimal() {
        // - It is a mammal.
        // - It is living in the forest.
        // - It doesn't have a long bushy tail.
        // - It is a shy animal.

        cv.say("Enter the animal:");
        Animal targetAnimal = new Animal(cv.hear());
        cv.sayS(((Function<String, String>) rb.getObject("Facts about the %s:")).apply(targetAnimal.getName()));
        List<String> facts = new ArrayList<>();
        searchPath(root, targetAnimal, facts);
        if (facts.isEmpty()) {
            cv.sayS(((Function<String, String>) rb.getObject("No facts about the %s.")).apply(targetAnimal.getName()));
        } else {
            Collections.reverse(facts);
            facts.stream().forEach(s -> cv.sayS(" - " + s));
        }
    }

    boolean searchPath(Node node, Animal targetAnimal, List<String> facts) {

        if (node.isLeaf()) {
            if (targetAnimal.equals(node.getKnowledge().getAnimal())) {
                return true;
            } else {
                return false;
            }
        }

        if (searchPath(node.getLeft(), targetAnimal, facts)) {
            facts.add(cv.deny(node.getKnowledge().getStatement()));
            return true;
        }

        if (searchPath(node.getRight(), targetAnimal, facts)) {
            facts.add(cv.affirm(node.getKnowledge().getStatement()));
            return true;
        }

        return false;
    }

    void calcStatistcs() {
        // The Knowledge Tree stats

        // - root node It is a mammal
        // - total number of nodes 11
        // - total number of animals 6
        // - total number of statements 5
        // - height of the tree 4
        // - minimum depth 1
        // - average depth 3.0

        cv.say("The Knowledge Tree stats");
        cv.say();
        cv.sayS(cv.sayM(" - root node\t\t\t") + cv.affirm(root.getKnowledge().getStatement()));
        cv.sayS(cv.sayM(" - total number of nodes\t") + getNodesCount());
        cv.sayS(cv.sayM(" - total number of animals\t") + getLeafCount());
        cv.sayS(cv.sayM(" - total number of statements\t") + getStatementCount());
        cv.sayS(cv.sayM(" - height of the tree\t\t") + getHeight());
        cv.sayS(cv.sayM(" - minimum depth\t\t") + getMinDepth());
        cv.sayS(cv.sayM(" - average depth\t\t") + String.format("%.1f", getAveDepth()));
    }

    int getNodesCount() {
        return countNode(root);
    }

    int countNode(Node node) {

        if (node.isLeaf()) {
            return 1;
        }

        int leftCount = countNode(node.getLeft());
        int rightCount = countNode(node.getRight());
        return leftCount + rightCount + 1;
    }

    int getLeafCount() {
        return countLeaf(root);
    }

    int countLeaf(Node node) {
        if (node.isLeaf()) {
            return 1;
        }

        int leftCount = countLeaf(node.getLeft());
        int rightCount = countLeaf(node.getRight());
        return leftCount + rightCount;
    }

    int getStatementCount() {
        return countStatement(root);
    }

    int countStatement(Node node) {
        if (node.isLeaf()) {
            return 0;
        }

        int leftCount = countStatement(node.getLeft());
        int rightCount = countStatement(node.getRight());
        return leftCount + rightCount + 1;
    }

    int getHeight() {
        List<Integer> depths = new ArrayList<>();
        countHeight(root, 0, depths);
        return depths.stream().max((i1, i2) -> i1 - i2).get();
    }

    void countHeight(Node node, int depth, List<Integer> depths) {
        if (node.isLeaf()) {
            depths.add(depth);
            return;
        }

        countHeight(node.getLeft(), depth + 1, depths);
        countHeight(node.getRight(), depth + 1, depths);
    }

    int getMinDepth() {
        List<Integer> depths = new ArrayList<>();
        countHeight(root, 0, depths);
        return depths.stream().min((i1, i2) -> i1 - i2).get();
    }

    double getAveDepth() {
        List<Integer> depths = new ArrayList<>();
        countHeight(root, 0, depths);
        return depths.stream().mapToDouble(i -> i).average().getAsDouble();
    }

    void printTree() {
//└ Is it a mammal?
//  ├ Is it living in the forest?
//  │├ Is it a shy animal?
//  ││├ a hare
//  ││└ a wolf
//  │└ Can it climb tree?
//  │ ├ a cat
//  │ └ a dog
//  └ a shark
        List<PrintItem> printItems = new ArrayList<>();
        getPrintNode(root, 0, printItems);
        int height = getHeight();
        List<String> ruledLines = new ArrayList<>();
        for (int i = 0; i < printItems.size(); i++) {
            if (i == 0) {
                ruledLines.add("└");
            } else {
                ruledLines.add(" ");
            }
        }
        for (int level = 1; level < height + 1; level++) {
            setRuledLine(level, printItems, ruledLines);
        }
        for (int i = 0; i < printItems.size(); i++) {
            System.out.println(ruledLines.get(i) + printItems.get(i).getSentence());
        }
    }

    void setRuledLine(int level, List<PrintItem> printItems, List<String> ruledLines) {

        for (int i = 0; i < printItems.size(); i++) {
            String ruledLine = ruledLines.get(i);
            int nextDepth = 0;
            if (i != printItems.size() - 1) {
                nextDepth = printItems.get(i + 1).getDepth();
            }
            int depth = printItems.get(i).getDepth();
            int endIndex = -1;
            for (int j = i + 1 ; j < printItems.size(); j++) {
                if (printItems.get(j).getDepth() < level) {
                    break;
                }

                if (printItems.get(j).getDepth() == level) {
                    endIndex = j;
                }
            }
            if (depth == level) {
                if (nextDepth < depth || endIndex == -1) {
                    ruledLine += "└";
                } else {
                    ruledLine += "├";
                }
            } else if (depth > level) {
                if (endIndex == -1) {
                    ruledLine += " ";
                } else {
                    ruledLine += "|";
                }
            }
            ruledLines.set(i, ruledLine);
        }
    }

    void getPrintNode(Node node, int depth, List<PrintItem> printItems) {

        if (node.isLeaf()) {
            printItems.add(new PrintItem(depth, node.getKnowledge().getAnimal().getAName()));
            return;
        }

        printItems.add(new PrintItem(depth, cv.affirm(node.getKnowledge().getStatement())));

        getPrintNode(node.getRight(), depth + 1, printItems);
        getPrintNode(node.getLeft(), depth + 1, printItems);
    }
}

class PrintItem {

    int depth;
    String sentence;

    PrintItem(int depth, String sentence) {
        this.depth = depth;
        this.sentence = sentence;
    }

    int getDepth() {
        return depth;
    }

    String getSentence() {
        return sentence;
    }
}

class Statement {

    public String subject;
    public String verb;
    public String fact;

    Statement() {}

    Statement(String subject, String verb, String fact) {
        this.subject = subject;
        this.verb = verb;
        this.fact = fact;
    }

    String getSubject() {
        return subject;
    }

    String getVerb() {
        return verb;
    }

    String getFact() {
        return fact;
    }

    String toCsv() {
        return subject + "," + verb + "," + fact;
    }

    void toObj(String csv) {
        String[] strs = csv.split(",");
        subject = strs[0];
        verb = strs[1];
        fact = strs[2];
    }
}

class Knowledge {

    static ResourceBundle rb;
    public Statement statement;
    public Animal animal;
    public String message;
    public Animal trueAnimal;
    public Animal falseAnimal;

    Knowledge() {}

    Knowledge(Statement statement, Animal animal) {
        this.statement = statement;
        this.animal = animal;
    }

    Knowledge(Animal animal) {
        String[] SVO = ((Function<String, String[]>) rb.getObject("knowledge.animal")).apply(animal.getAName());
        String subject = SVO[0];
        String verb = SVO[1];
        String fact = SVO[2];
        statement = new Statement(subject, verb, fact);

        this. animal = animal;
    }

    Knowledge(Conversation cv, Animal animal1, Animal animal2, String differenceStr, String yesno) {

        if ("yes".equals(yesno)) {
            trueAnimal = animal2;
            falseAnimal = animal1;
        } else {
            trueAnimal = animal1;
            falseAnimal = animal2;
        }

        String[] SVO = ((Function<String, String[]>) rb.getObject("knowledge.affirmStatement")).apply(differenceStr);
        String subject = SVO[0];
        String verb = SVO[1];
        String fact = SVO[2];


        String[] trueSVO = new String[3];
        trueSVO[0] = trueAnimal.getName();
        trueSVO[1] = verb;
        trueSVO[2] = fact;
        String trueFact = ((Function<String[], String>) rb.getObject("knowledge.trueFact")).apply(trueSVO);

        String[] falseSVO = new String[3];
        falseSVO[0] = falseAnimal.getName();
        falseSVO[1] = cv.deny(verb);
        falseSVO[2] = fact;
        String falseFact = ((Function<String[], String>) rb.getObject("knowledge.falseFact")).apply(falseSVO);

        message = "";
        message += cv.sayM("I have learned the following facts about animals:\n");
        if (animal1 == trueAnimal) {
            message += "- " + trueFact + "\n";
            message += "- " + falseFact + "\n";
        } else {
            message += "- " + falseFact + "\n";
            message += "- " + trueFact + "\n";
        }
        message += cv.sayM("I can distinguish these animals by asking the question:\n");
        message += "- " + cv.ask(cv.sayM("it"), verb, fact) + "\n";

        message += cv.sayM("Nice! I've learned so much about animals!");

        statement = new Statement(cv.sayM("it"), verb, fact);
    }

    String getMessage() {
        return message;
    }

    Statement getStatement() {
        return statement;
    }

    Animal getAnimal() {
        return animal;
    }

    Animal getTrueAnimal() {
        return trueAnimal;
    }

    Animal getFalseAnimal() {
        return falseAnimal;
    }

    String toCsv(boolean leafFlag) {
        if (leafFlag) {
            return statement.toCsv() + "|" + animal.toCsv();
        } else {
            return statement.toCsv();
        }
    }

    void toObj(String csv) {
        String[] strs = csv.split("|");
        if (strs.length == 2) {
            statement = new Statement();
            statement.toObj(strs[0]);
            animal = new Animal();
            animal.toObj(strs[1]);
        } else {
            statement = new Statement();
            statement.toObj(csv);
        }
    }
}

class Animal {

    static ResourceBundle rb;
    public String name;
    public String article;
    public String aname;

    Animal() {}

    Animal(String animal) {
        String[] animalFields = ((Function<String, String[]>) rb.getObject("animal.new")).apply(animal);
        name = animalFields[0];
        article = animalFields[1];
        aname = animalFields[2];
    }

    String getAName() {
        return ((BiFunction<String, String, String>) rb.getObject("animal.aname")).apply(name, aname);
    }

    String getName() {
        return ((BiFunction<String, String, String>) rb.getObject("animal.name")).apply(name, aname);
    }

    boolean equals(Animal other) {
        if (this.getName().equals(other.getName())) {
            return true;
        } else {
            return false;
        }
    }

    String toCsv() {
        return name + "," + article + "," + aname;
    }

    void toObj(String csv) {
        String[] strs = csv.split(",");
        name = strs[0];
        article = strs[1];
        aname = strs[2];
    }
}

class Conversation {

    static ResourceBundle rb;
    Scanner scanner;

    Conversation() {
        scanner = new Scanner(System.in);
    }

    void say(String talk) {
        System.out.println(rb.getString(talk));
    }

    void say() {
        say("");
    }

    void sayS(String talk) {
        System.out.println(talk);
    }

    String sayM(String talk) {
        return rb.getString(talk);
    }

    String hear() {
        return scanner.nextLine().trim().toLowerCase();
    }

    int hearNumber() {
        return Integer.parseInt(hear());
    }

    void sayGreeting() {
        LocalDateTime dateTime = LocalDateTime.now();
        int hour = dateTime.getHour();
        if (hour >= 18) {
            say("Good evening!");
        } else if (hour >= 12) {
            say("Good afternoon!");
        } else if (hour >= 5) {
            say("Good morning!");
        } else {
            say("Midnight now aren'you!");
        }
    }

    void sayGoodBye() {
        say();

        LocalDateTime dateTime = LocalDateTime.now();
        int nano = dateTime.getNano();
        if (nano % 3 == 0) {
            say("It was good day!");
        }
        if (nano % 3 == 1) {
            say("Good bye!");
        }
        if (nano % 3 == 2) {
            say("See you again!");
        }
    }

    void finish() {
        scanner.close();
    }

    String hearYesNo() {
        while (true) {
            String reply = hear();
            String judge = replyAnalize(reply);
            if ("yes".equals(judge)) {
                return "yes";
            } else if ("no".equals(judge)) {
                return "no";
            } else {
                sayYesNoClarification();
            }
        }
    }

    void sayYesNoClarification() {
        LocalDateTime dateTime = LocalDateTime.now();
        int nano = dateTime.getNano();
        if (nano % 3 == 0) {
            say("Come on, yes or no?");
        }
        if (nano % 3 == 1) {
            say("Please reply yes or no?");
        }
        if (nano % 3 == 2) {
            say("You shuld say yes or no?");
        }
    }

    String replyAnalize(String replyInput) {
        return ((Function<String, String>) rb.getObject("cv.replyAnalize")).apply(replyInput);
    }

    String deny(String verb) {
        return  ((Function<String, String>) rb.getObject("cv.denyV")).apply(verb);
    }

    String deny(String subject, String verb) {
        return  ((BiFunction<String, String, String>) rb.getObject("cv.denySV")).apply(subject, deny(verb));
    }

    String deny(Statement statement) {
        String[] SVO = new String[3];
        SVO[0] = statement.getSubject();
        SVO[1] = deny(statement.getVerb());
        SVO[2] = statement.getFact();
        return ((Function<String[], String>) rb.getObject("cv.denySVO")).apply(SVO);
    }

    String affirm(Statement statement) {
        String[] SVO = new String[3];
        SVO[0] = statement.getSubject();
        SVO[1] = statement.getVerb();
        SVO[2] = statement.getFact();
        return ((Function<String[], String>) rb.getObject("cv.affirmSVO")).apply(SVO);
    }

    String ask(String subject, String verb) {
        return  ((BiFunction<String, String, String>) rb.getObject("cv.askSV")).apply(subject, verb);
    }

    String ask(String subject, String verb, String fact) {
        String[] SVO = new String[3];
        SVO[0] = subject;
        SVO[1] = verb;
        SVO[2] = fact;
        return ((Function<String[], String>) rb.getObject("cv.askSVO")).apply(SVO);
    }

    String ask(Statement statement) {
        String[] SVO = new String[3];
        SVO[0] = statement.getSubject();
        SVO[1] = statement.getVerb();
        SVO[2] = statement.getFact();
        return ((Function<String[], String>) rb.getObject("cv.askSVO")).apply(SVO);
    }

    String toCapital(String word) {
        int code = (int) word.charAt(0);
        if (code < 97 && code > 122) {
            return word;
        }
        code -= 32;
        char ch = (char)code;
        return ch + word.substring(1);
    }

    String toSmall(String word) {
        return word.toLowerCase();
    }
}

class Node {

    public Knowledge knowledge;
    public Node left;
    public Node right;
    Node parent;

    Node () {}

    Node (Knowledge knowledge , Node parent) {
        this.knowledge = knowledge;
        this.parent = parent;
    }

    Node (Animal animal, Node parent) {
        knowledge = new Knowledge(animal);
        this.parent = parent;
    }

    Knowledge getKnowledge() {
        return knowledge;
    }

    Node getLeft() {
        return left;
    }

    Node getRight() {
        return right;
    }

    Node getParent() {
        return parent;
    }

    void setKnowledge(Knowledge knowledge) {
        this.knowledge = knowledge;
    }

    void setLeft(Node left) {
        this.left = left;
    }

    void setRight(Node right) {
        this.right = right;
    }

    void setParent(Node parent) {
        this.parent = parent;
    }

    boolean isLeaf() {
        return knowledge.getAnimal() != null ? true: false;
    }
}

class DataBase {

    String type;
    ObjectMapper mapper;

    DataBase(String type) {
        this.type = type;
        switch (type) {
            case "xml":
                mapper = new XmlMapper();
                break;
            case "yaml":
                mapper = new YAMLMapper();
                break;
            default:
                mapper = new JsonMapper();
                break;
        }
    }

    void save(Node root, String fileName) {
        try {
            String text = mapper.writeValueAsString(root);
   //         System.out.println(ReadText.getAbsolutePath(fileName));
            WriteText.writeAll(fileName, text);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    Node load(String fileName) {
        try {
//            System.out.println(ReadText.getAbsolutePath(fileName));
            String text = ReadText.readAll(fileName);
            Node root = mapper.readValue(text, Node.class);
            return root;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}

class ReadText {

    static boolean isExist(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    static String getAbsolutePath(String fileName) {
        File file = new File(fileName);
        return file.getAbsolutePath();
    }

    static String readAllWithoutEol(String fileName) {
        String text = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            text =  br.lines().collect(Collectors.joining());
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return text;
    }

    static List<String> readLines(String fileName) {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            lines =  br.lines().collect(Collectors.toList());
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return lines;
    }

    static String readAll(String fileName) {
        char[] cbuf = new char[4096];
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while (true) {
                int length = br.read(cbuf, 0, cbuf.length);
                if (length != -1) {
                    sb.append(cbuf, 0, length);
                }
                if (length < cbuf.length) {
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }
}

class WriteText {

    static void writeAll(String fileName, String text) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            bw.write(text, 0, text.length());
            bw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}