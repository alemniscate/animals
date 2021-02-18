import java.util.Arrays;
import java.util.ListResourceBundle;
import java.util.function.BiFunction;
import java.util.function.Function;

public class App extends ListResourceBundle {

    String toCapital(String word) {
        String firstChar = word.charAt(0) + "";
        firstChar = firstChar.toUpperCase();
        return firstChar + word.substring(1);
    }

    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                {"", ""},
                {"it", "it"},
                {"Good evening!", "Good evening!"},
                {"Good afternoon!", "Good afternoon!"},
                {"Good morning!", "Good morning!"},
                {"Midnight now aren'you!", "Midnight now aren'you!"},
                {"It was good day!", "It was good day!"},
                {"Good bye!", "Good bye!"},
                {"See you again!", "See you again!"},
                {"I want to learn about animals.", "I want to learn about animals."},
                {"Which animal do you like most?", "Which animal do you like most?"},
                {"Welcome to the animal expert system!", "Welcome to the animal expert system!"},
                {"Welcome to the animal expert system!2", "Welcome to the animal expert system!"},
                {"Wonderful! I've learned so much about animals!","Wonderful! I've learned so much about animals!"},
                {"What do you want to do:", "What do you want to do:"},
                {"1. Play the guessing game", "1. Play the guessing game"},
                {"2. List of all animals", "2. List of all animals"},
                {"3. Search for an animal", "3. Search for an animal"},
                {"4. Calculate statistics", "4. Calculate statistics"},
                {"5. Print the Knowledge Tree", "5. Print the Knowledge Tree"},
                {"0. Exit", "0. Exit"},
                {"Would you like to play again?", "Would you like to play again?"},
                {"You think of an animal, and I guess it.", "You think of an animal, and I guess it."},
                {"Press enter when you're ready.", "Press enter when you're ready."},
                {"I give up. What animal do you have in mind?", "I give up. What animal do you have in mind?"},
                {"Come on, yes or no?", "Come on, yes or no?"},
                {"Please reply yes or no?", "Please reply yes or no?"},
                {"You shuld say yes or no?", "You shuld say yes or no?"},
                {"The sentence should satisfy one of the following templates:", "The sentence should satisfy one of the following templates:"},
                {"- It can ...", "- It can ..."},
                {"- It has ...", "- It has ..."},
                {"- It is a/an ...", "- It is a/an ..."},
                {"The sentence should be of the format: 'It can/has/is ...'.", "The sentence should be of the format: 'It can/has/is ...'."},
                {"examples of a statement.", "examples of a statement."},
                {"It is a mammal", "It is a mammal"},
                {"It can barks", "It can barks"},
                {"I have learned the following facts about animals:\n", "I have learned the following facts about animals:\n"},
                {"I can distinguish these animals by asking the question:\n", "I can distinguish these animals by asking the question:\n"},
                {"Nice! I've learned so much about animals!", "Nice! I've learned so much about animals!"},
                {"Here are the animals I know:", "Here are the animals I know:"},
                {"Enter the animal:", "Enter the animal:"},
                {"The Knowledge Tree stats", "The Knowledge Tree stats"},
                {" - root node\t\t\t", " - root node\t\t\t"},
                {" - total number of nodes\t", " - total number of nodes\t"},
                {" - total number of animals\t", " - total number of animals\t"},
                {" - total number of statements\t" ," - total number of statements\t"},
                {" - height of the tree\t\t", " - height of the tree\t\t"},
                {" - minimum depth\t\t", " - minimum depth\t\t"},
                {" - average depth\t\t", " - average depth\t\t"},
                {"Specify a fact that distinguishes %s from %s.", (BiFunction<String, String, String>) (animal1, animal2) -> {
                    return String.format("Specify a fact that distinguishes %s from %s.", animal1, animal2);
                }},
                {"cv.askSV", (BiFunction<String, String, String>) (subject, verb) -> {
                    String askSV = "";

                    switch (verb) {
                        case "can":
                            askSV = "Can " + subject.toLowerCase();
                            break;
                        case "has":
                            askSV = "Does " + subject.toLowerCase() + " have";
                            break;
                        case "is":
                            askSV = "Is " + subject.toLowerCase();
                            break;
                    }

                    return askSV;
                }},
                {"cv.askSVO", (Function<String[], String>) a -> {
                    String subject = a[0];
                    String verb = a[1];
                    String object = a[2];

                    String askSVO = "";

                    switch (verb) {
                        case "can":
                            askSVO = "Can " + subject.toLowerCase();
                            break;
                        case "has":
                            askSVO = "Does " + subject.toLowerCase() + " have";
                            break;
                        case "is":
                            askSVO = "Is " + subject.toLowerCase();
                            break;
                    }

                    askSVO += " " + object + "?";

                    return askSVO;
                }},
                {"cv.replyAnalize", (Function<String, String>) replyInput -> {
                    String reply = replyInput.toLowerCase();
                    if ("i don't".equals(reply) || "i no".equals(reply)) {
                        return "";
                    }

                    if (reply.endsWith("..") ||
                            reply.endsWith("...") ||
                            reply.endsWith("!!") ||
                            reply.endsWith("!!!") ||
                            reply.endsWith(".!") ||
                            reply.endsWith("!.")) {

                        return "";
                    }

                    reply = reply.replace(".", "").replace("!", "");
                    long yesCount = Arrays.asList(reply.split("\\s+|,")).stream()
                            .filter(s ->
                                    "yes".equals(s) ||
                                            "sure".equals(s) ||
                                            "right".equals(s) ||
                                            "correct".equals(s) ||
                                            "affirmative".equals(s) ||
                                            "indeed".equals(s) ||
                                            "y".equals(s) ||
                                            "yep".equals(s) ||
                                            "bet".equals(s) ||
                                            "exactly".equals(s) ||
                                            "said".equals(s) ||
                                            "yeah".equals(s))
                            .count();

                    long noCount = Arrays.asList(reply.split("\\s+")).stream()
                            .filter(s ->
                                    "no".equals(s) ||
                                            "not".equals(s) ||
                                            "n".equals(s) ||
                                            "nah".equals(s) ||
                                            "nope".equals(s) ||
                                            "negative".equals(s) ||
                                            "wa".equals(s) ||
                                            s.matches(".*don't.*"))
                            .count();

                    if (noCount == 1) {
                        return "no";
                    }

                    if (yesCount == 1) {
                        return "yes";
                    }

                    return "";
                }},
                {"ac.getDifferenc.CheckAnswer", (Function<String, Boolean>) difference -> {
                    boolean moreFlag = false;
                    String[] words = difference.split("\\s+");
                    if (words.length > 2) {
                        String subject = words[0];
                        String verb = words[1];

                        if (!"it".equals(subject)) {
                            moreFlag = true;
                        }
                        if (!"can".equals(verb) && !"has".equals(verb) && !"is".equals(verb)) {
                            moreFlag = true;
                        }
                    } else {
                        moreFlag = true;
                    }
                    return moreFlag;
                }},
                {"Is the statement correct for %s?", (Function<String, String>) animal -> {
                    return String.format("Is the statement correct for %s?", animal);
                }},
                {"knowledge.affirmStatement", (Function<String, String[]>) affirmSentence -> {
                    String[] statement = new String[3];
                    String[] words = affirmSentence.split("\\s+");
                    String subject = words[0];
                    String verb = words[1];
                    int index = affirmSentence.indexOf(verb);
                    String fact = affirmSentence.substring(index);
                    fact = fact.replace(verb, "").trim();
                    if (fact.endsWith(".")) {
                        fact = fact.substring(0, fact.length() - 1);
                    }
                    statement[0] = subject;
                    statement[1] = verb;
                    statement[2] = fact;

                    return statement;
                }},
                {"knowledge.trueFact", (Function<String[], String>) SVO -> {
                    String subject = SVO[0];
                    String verb = SVO[1];
                    String fact = SVO[2];
                    String trueFact = "The " + subject + " " + verb + " " + fact + ".";
                    return trueFact;
                }},
                {"knowledge.falseFact", (Function<String[], String>) SVO -> {
                    String subject = SVO[0];
                    String denyVerb = SVO[1];
                    String fact = SVO[2];
                    String denyFact = "The " + subject + " " + denyVerb + " " + fact + ".";
                    return denyFact;
                }},
                {"cv.denyV", (Function<String, String>) verb -> {
                    String denyV = "";
                    switch (verb) {
                        case "can":
                            denyV = "can't";
                            break;
                        case "has":
                            denyV = "doesn't have";
                            break;
                        case "is":
                            denyV = "isn't";
                            break;
                    }
                    return denyV;
                }},
                {"cv.denySV", (BiFunction<String, String, String>) (subject, denyVerb) -> {
                    return toCapital(subject) + " " + denyVerb;
                }},
                {"cv.affirmSVO", (Function<String[], String>) SVO -> {
                    String subject = SVO[0];
                    String verb = SVO[1];
                    String fact = SVO[2];
                    return toCapital(subject) + " " + verb + " " + fact + ".";
                }},
                {"cv.denySVO", (Function<String[], String>) SVO -> {
                    String subject = SVO[0];
                    String denyVerb = SVO[1];
                    String fact = SVO[2];
                    return toCapital(subject) + " " + denyVerb + " " + fact + ".";
                }},
                {"knowledge.animal", (Function<String, String[]>) animal -> {
                    String[] SVO = new String[3];
                    String subject = "it";
                    String verb = "is";
                    SVO[0] = subject;
                    SVO[1] = verb;
                    SVO[2] = animal;
                    return SVO;
                }},
                {"animal.new", (Function<String, String[]>) animal -> {
                    String[] words = animal.split("\\s+");
                    String name = "";
                    String article = "";
                    String aname = "";

                    if (words.length == 1) {
                        name = words[0];
                        if (name.matches("^[aiueoy].*")) {
                            article = "an";
                        } else {
                            article = "a";
                        }
                        aname = article + " " + name;
                    } else if ( words.length == 2) {
                        article = words[0];
                        name = words[1];
                        if ("a".equals(article) || "an".equals(article)) {
                            aname = article + " " + name;
                        } else {
                            aname = "a " + article + " " + name;
                        }
                    }

                    String[] animalFields = new String[3];
                    animalFields[0] = name;
                    animalFields[1] = article;
                    animalFields[2] = aname;
                    return animalFields;
                }},
                {"Facts about the %s:", (Function<String, String>) animal -> {
                    return String.format("Facts about the %s:", animal);
                }},
                {"No facts about the %s.", (Function<String, String>) animal -> {
                    return String.format("No facts about the %s.", animal);
                }},
                {"animal.aname", (BiFunction<String, String, String>) (name, aname) -> aname},
                {"animal.name", (BiFunction<String, String, String>) (name, aname) -> name}
        };
    }
}
